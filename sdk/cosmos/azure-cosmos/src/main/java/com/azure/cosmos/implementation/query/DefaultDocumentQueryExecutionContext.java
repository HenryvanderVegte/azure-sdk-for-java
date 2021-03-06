// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation.query;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.model.FeedOptions;
import com.azure.cosmos.model.FeedResponse;
import com.azure.cosmos.model.Resource;
import com.azure.cosmos.model.SqlQuerySpec;
import com.azure.cosmos.implementation.BackoffRetryUtility;
import com.azure.cosmos.implementation.Constants;
import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.implementation.InvalidPartitionExceptionRetryPolicy;
import com.azure.cosmos.implementation.PartitionKeyRange;
import com.azure.cosmos.implementation.PartitionKeyRangeGoneRetryPolicy;
import com.azure.cosmos.implementation.PathsHelper;
import com.azure.cosmos.implementation.QueryMetrics;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.RxDocumentServiceRequest;
import com.azure.cosmos.implementation.Strings;
import com.azure.cosmos.implementation.Utils.ValueHolder;
import com.azure.cosmos.implementation.caches.IPartitionKeyRangeCache;
import com.azure.cosmos.implementation.caches.RxCollectionCache;
import com.azure.cosmos.implementation.query.metrics.ClientSideMetrics;
import com.azure.cosmos.implementation.query.metrics.FetchExecutionRangeAccumulator;
import com.azure.cosmos.implementation.query.metrics.SchedulingStopwatch;
import com.azure.cosmos.implementation.query.metrics.SchedulingTimeSpan;
import com.azure.cosmos.implementation.routing.PartitionKeyInternal;
import com.azure.cosmos.implementation.routing.PartitionKeyRangeIdentity;
import com.azure.cosmos.implementation.routing.Range;
import com.azure.cosmos.implementation.routing.RoutingMapProviderHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.azure.cosmos.model.ModelBridgeInternal.partitionKeyRangeIdInternal;

/**
 * While this class is public, but it is not part of our published public APIs.
 * This is meant to be internally used only by our sdk.
 */
public class DefaultDocumentQueryExecutionContext<T extends Resource> extends DocumentQueryExecutionContextBase<T> {

    private boolean isContinuationExpected;
    private volatile int retries = -1;

    private final SchedulingStopwatch fetchSchedulingMetrics;
    private final FetchExecutionRangeAccumulator fetchExecutionRangeAccumulator;
    private static final String DEFAULT_PARTITION_KEY_RANGE_ID = "0";

    public DefaultDocumentQueryExecutionContext(IDocumentQueryClient client, ResourceType resourceTypeEnum,
            Class<T> resourceType, SqlQuerySpec query, FeedOptions feedOptions, String resourceLink,
            UUID correlatedActivityId, boolean isContinuationExpected) {

        super(client,
                resourceTypeEnum,
                resourceType,
                query,
                feedOptions,
                resourceLink,
                false,
                correlatedActivityId);

        this.isContinuationExpected = isContinuationExpected;
        this.fetchSchedulingMetrics = new SchedulingStopwatch();
        this.fetchSchedulingMetrics.ready();
        this.fetchExecutionRangeAccumulator = new FetchExecutionRangeAccumulator(DEFAULT_PARTITION_KEY_RANGE_ID);
    }

    protected PartitionKeyInternal getPartitionKeyInternal() {
        return this.feedOptions.getPartitionKey() == null ? null : BridgeInternal.getPartitionKeyInternal(feedOptions.getPartitionKey());
    }

    @Override
    public Flux<FeedResponse<T>> executeAsync() {

        if (feedOptions == null) {
            feedOptions = new FeedOptions();
        }

        FeedOptions newFeedOptions = new FeedOptions(feedOptions);

        // We can not go to backend with the composite continuation token,
        // but we still need the gateway for the query plan.
        // The workaround is to try and parse the continuation token as a composite continuation token.
        // If it is, then we send the query to the gateway with max degree of parallelism to force getting back the query plan

        String originalContinuation = newFeedOptions.getRequestContinuation();

        if (isClientSideContinuationToken(originalContinuation)) {
            // At this point we know we want back a query plan
            newFeedOptions.setRequestContinuation(null);
            newFeedOptions.setMaxDegreeOfParallelism(Integer.MAX_VALUE);
        }

        int maxPageSize = newFeedOptions.getMaxItemCount() != null ? newFeedOptions.getMaxItemCount() : Constants.Properties.DEFAULT_MAX_PAGE_SIZE;

        BiFunction<String, Integer, RxDocumentServiceRequest> createRequestFunc = (continuationToken, pageSize) -> this.createRequestAsync(continuationToken, pageSize);

        // TODO: clean up if we want to use single vs observable.
        Function<RxDocumentServiceRequest, Mono<FeedResponse<T>>> executeFunc = executeInternalAsyncFunc();

        return Paginator
    			.getPaginatedQueryResultAsObservable(newFeedOptions, createRequestFunc, executeFunc, resourceType, maxPageSize);
    }

    public Mono<List<PartitionKeyRange>> getTargetPartitionKeyRanges(String resourceId, List<Range<String>> queryRanges) {
        return RoutingMapProviderHelper.getOverlappingRanges(client.getPartitionKeyRangeCache(), resourceId, queryRanges);
    }

    public Mono<List<PartitionKeyRange>> getTargetPartitionKeyRangesById(String resourceId,
                                                                                      String partitionKeyRangeIdInternal) {
        return client.getPartitionKeyRangeCache()
                   .tryGetPartitionKeyRangeByIdAsync(resourceId,
                                                     partitionKeyRangeIdInternal,
                                                     false,
                                                     null)
                   .flatMap(partitionKeyRange -> Mono.just(Collections.singletonList(partitionKeyRange.v)));
    }

    protected Function<RxDocumentServiceRequest, Mono<FeedResponse<T>>> executeInternalAsyncFunc() {
        RxCollectionCache collectionCache = this.client.getCollectionCache();
        IPartitionKeyRangeCache partitionKeyRangeCache =  this.client.getPartitionKeyRangeCache();
        DocumentClientRetryPolicy retryPolicyInstance = this.client.getResetSessionTokenRetryPolicy().getRequestPolicy();

        retryPolicyInstance = new InvalidPartitionExceptionRetryPolicy(collectionCache, retryPolicyInstance, resourceLink, feedOptions);
        if (super.resourceTypeEnum.isPartitioned()) {
            retryPolicyInstance = new PartitionKeyRangeGoneRetryPolicy(
                    collectionCache,
                    partitionKeyRangeCache,
                    PathsHelper.getCollectionPath(super.resourceLink),
                    retryPolicyInstance,
                    feedOptions);
        }

        final DocumentClientRetryPolicy finalRetryPolicyInstance = retryPolicyInstance;

        return req -> {
            finalRetryPolicyInstance.onBeforeSendRequest(req);
            this.fetchExecutionRangeAccumulator.beginFetchRange();
            this.fetchSchedulingMetrics.start();
            return BackoffRetryUtility.executeRetry(() -> {
                ++this.retries;
                return executeRequestAsync(req);
            }, finalRetryPolicyInstance)
                    .map(tFeedResponse -> {
                        this.fetchSchedulingMetrics.stop();
                        this.fetchExecutionRangeAccumulator.endFetchRange(tFeedResponse.getActivityId(),
                                tFeedResponse.getResults().size(),
                                this.retries);
                        ImmutablePair<String, SchedulingTimeSpan> schedulingTimeSpanMap =
                                new ImmutablePair<>(DEFAULT_PARTITION_KEY_RANGE_ID, this.fetchSchedulingMetrics.getElapsedTime());
                        if (!StringUtils.isEmpty(tFeedResponse.getResponseHeaders().get(HttpConstants.HttpHeaders.QUERY_METRICS))) {
                            QueryMetrics qm =
                                    BridgeInternal.createQueryMetricsFromDelimitedStringAndClientSideMetrics(tFeedResponse.getResponseHeaders()
                                                    .get(HttpConstants.HttpHeaders.QUERY_METRICS),
                                            new ClientSideMetrics(this.retries,
                                                    tFeedResponse.getRequestCharge(),
                                                    this.fetchExecutionRangeAccumulator.getExecutionRanges(),
                                                    Arrays.asList(schedulingTimeSpanMap)),
                                            tFeedResponse.getActivityId());
                            BridgeInternal.putQueryMetricsIntoMap(tFeedResponse, DEFAULT_PARTITION_KEY_RANGE_ID, qm);
                        }
                        return tFeedResponse;
                    });
        };
    }

    private Mono<FeedResponse<T>> executeOnceAsync(DocumentClientRetryPolicy retryPolicyInstance, String continuationToken) {
        // Don't reuse request, as the rest of client SDK doesn't reuse requests between retries.
        // The code leaves some temporary garbage in request (in RequestContext etc.),
        // which shold be erased during retries.

        RxDocumentServiceRequest request = this.createRequestAsync(continuationToken, this.feedOptions.getMaxItemCount());
        if (retryPolicyInstance != null) {
            retryPolicyInstance.onBeforeSendRequest(request);
        }

        if (!Strings.isNullOrEmpty(request.getHeaders().get(HttpConstants.HttpHeaders.PARTITION_KEY))
                || !request.getResourceType().isPartitioned()) {
            return this.executeRequestAsync(request);
        }


        // TODO: remove this as partition key range id is not relevant
        // TODO; has to be rx async
        //CollectionCache collectionCache =  this.client.getCollectionCache();

        // TODO: has to be rx async
        //DocumentCollection collection =
        //        collectionCache.resolveCollection(request);

        // TODO: this code is not relevant because partition key range id should not be exposed
        //            if (!Strings.isNullOrEmpty(super.getPartitionKeyId()))
        //            {
        //                request.RouteTo(new PartitionKeyRangeIdentity(collection.ResourceId, base.PartitionKeyRangeId));
        //                return await this.ExecuteRequestAsync(request);
        //            }

        request.UseGatewayMode = true;
        return this.executeRequestAsync(request);
    }

    public RxDocumentServiceRequest createRequestAsync(String continuationToken, Integer maxPageSize) {

        // TODO this should be async
        Map<String, String> requestHeaders = this.createCommonHeadersAsync(
                this.getFeedOptions(continuationToken, maxPageSize));

        // TODO: add support for simple continuation for single partition query
        //requestHeaders.put(keyHttpConstants.HttpHeaders.IsContinuationExpected, isContinuationExpected.ToString())

        RxDocumentServiceRequest request = this.createDocumentServiceRequest(
                requestHeaders,
                this.query,
                this.getPartitionKeyInternal());

        if (!StringUtils.isEmpty(partitionKeyRangeIdInternal(feedOptions))) {
            request.routeTo(new PartitionKeyRangeIdentity(partitionKeyRangeIdInternal(feedOptions)));
        }

        return request;
    }

    private static boolean isClientSideContinuationToken(String continuationToken) {
        if (continuationToken != null) {
            ValueHolder<CompositeContinuationToken> outCompositeContinuationToken = new ValueHolder<CompositeContinuationToken>();
            if (CompositeContinuationToken.tryParse(continuationToken, outCompositeContinuationToken)) {
                return true;
            }

            ValueHolder<OrderByContinuationToken> outOrderByContinuationToken = new ValueHolder<OrderByContinuationToken>();
            if (OrderByContinuationToken.tryParse(continuationToken, outOrderByContinuationToken)) {
                return true;
            }

            ValueHolder<TakeContinuationToken> outTakeContinuationToken = new ValueHolder<TakeContinuationToken>();
            if (TakeContinuationToken.tryParse(continuationToken, outTakeContinuationToken)) {
                return true;
            }
        }

        return false;
    }
}

