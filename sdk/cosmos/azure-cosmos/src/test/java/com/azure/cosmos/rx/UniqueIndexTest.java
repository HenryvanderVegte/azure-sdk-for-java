// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.rx;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.ConnectionPolicy;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosClientException;
import com.azure.cosmos.model.CosmosContainerProperties;
import com.azure.cosmos.CosmosDatabaseForTest;
import com.azure.cosmos.implementation.CosmosItemProperties;
import com.azure.cosmos.model.CosmosItemRequestOptions;
import com.azure.cosmos.model.DataType;
import com.azure.cosmos.model.ExcludedPath;
import com.azure.cosmos.model.HashIndex;
import com.azure.cosmos.model.IncludedPath;
import com.azure.cosmos.model.IndexingMode;
import com.azure.cosmos.model.IndexingPolicy;
import com.azure.cosmos.model.PartitionKey;
import com.azure.cosmos.model.PartitionKeyDefinition;
import com.azure.cosmos.model.UniqueKey;
import com.azure.cosmos.model.UniqueKeyPolicy;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.implementation.TestConfigurations;
import com.azure.cosmos.implementation.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UniqueIndexTest extends TestSuiteBase {
    protected static final int TIMEOUT = 30000;
    protected static final int SETUP_TIMEOUT = 20000;
    protected static final int SHUTDOWN_TIMEOUT = 20000;

    private final String databaseId = CosmosDatabaseForTest.generateId();
    private CosmosAsyncClient client;
    private CosmosAsyncDatabase database;

    private CosmosAsyncContainer collection;

    @Test(groups = { "long" }, timeOut = TIMEOUT)
    public void insertWithUniqueIndex() throws Exception {
        PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
        ArrayList<String> paths = new ArrayList<String>();
        paths.add("/mypk");
        partitionKeyDef.setPaths(paths);

        CosmosContainerProperties collectionDefinition = new CosmosContainerProperties(UUID.randomUUID().toString(), partitionKeyDef);
        UniqueKeyPolicy uniqueKeyPolicy = new UniqueKeyPolicy();
        UniqueKey uniqueKey = new UniqueKey();
        uniqueKey.setPaths(ImmutableList.of("/name", "/description"));
        uniqueKeyPolicy.setUniqueKeys(Lists.newArrayList(uniqueKey));
        collectionDefinition.setUniqueKeyPolicy(uniqueKeyPolicy);

        IndexingPolicy indexingPolicy = new IndexingPolicy();
        indexingPolicy.setIndexingMode(IndexingMode.CONSISTENT);
        ExcludedPath excludedPath = new ExcludedPath();
        excludedPath.setPath("/*");
        indexingPolicy.setExcludedPaths(Collections.singletonList(excludedPath));

        IncludedPath includedPath1 = new IncludedPath();
        includedPath1.setPath("/name/?");
        includedPath1.setIndexes(Collections.singletonList(new HashIndex(DataType.STRING, 7)));
        includedPath1.setIndexes(Collections.singletonList(new HashIndex(DataType.STRING, 7)));

        IncludedPath includedPath2 = new IncludedPath();
        includedPath2.setPath("/description/?");
        includedPath2.setIndexes(Collections.singletonList(new HashIndex(DataType.STRING, 7)));
        indexingPolicy.setIncludedPaths(ImmutableList.of(includedPath1, includedPath2));
        collectionDefinition.setIndexingPolicy(indexingPolicy);

        ObjectMapper om = new ObjectMapper();

        JsonNode doc1 = om.readValue("{\"name\":\"Alexander Pushkin\",\"description\":\"poet\",\"id\": \""+ UUID.randomUUID().toString() +"\"}", JsonNode.class);
        JsonNode doc2 = om.readValue("{\"name\":\"Alexander Pushkin\",\"description\":\"playwright\",\"id\": \"" + UUID.randomUUID().toString() + "\"}", JsonNode.class);
        JsonNode doc3 = om.readValue("{\"name\":\"حافظ شیرازی\",\"description\":\"poet\",\"id\": \"" + UUID.randomUUID().toString() + "\"}", JsonNode.class);

        collection = database.createContainer(collectionDefinition).block().getContainer();

        CosmosItemProperties properties = BridgeInternal.getProperties(collection.createItem(doc1).block());

        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        CosmosItemProperties itemSettings =
            BridgeInternal.getProperties(
                collection.readItem(properties.getId(), PartitionKey.NONE, options, CosmosItemProperties.class)
                                             .block());
        assertThat(itemSettings.getId()).isEqualTo(doc1.get("id").textValue());

        try {
            collection.createItem(doc1).block();
            fail("Did not throw due to unique constraint (create)");
        } catch (RuntimeException e) {
            assertThat(getDocumentClientException(e).getStatusCode()).isEqualTo(HttpConstants.StatusCodes.CONFLICT);
        }

        collection.createItem(doc2).block();
        collection.createItem(doc3).block();
    }

    @Test(groups = { "long" }, timeOut = TIMEOUT * 1000)
    public void replaceAndDeleteWithUniqueIndex() throws Exception {
        PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
        ArrayList<String> paths = new ArrayList<String>();
        paths.add("/mypk");
        partitionKeyDef.setPaths(paths);

        CosmosContainerProperties collectionDefinition = new CosmosContainerProperties(UUID.randomUUID().toString(), partitionKeyDef);
        UniqueKeyPolicy uniqueKeyPolicy = new UniqueKeyPolicy();
        UniqueKey uniqueKey = new UniqueKey();
        uniqueKey.setPaths(ImmutableList.of("/name", "/description"));
        uniqueKeyPolicy.setUniqueKeys(Lists.newArrayList(uniqueKey));
        collectionDefinition.setUniqueKeyPolicy(uniqueKeyPolicy);

        collection = database.createContainer(collectionDefinition).block().getContainer();

        ObjectMapper om = new ObjectMapper();

        ObjectNode doc1 = om.readValue("{\"name\":\"عمر خیّام\",\"description\":\"poet\",\"id\": \""+ UUID.randomUUID().toString() +"\"}", ObjectNode.class);
        ObjectNode doc3 = om.readValue("{\"name\":\"Rabindranath Tagore\",\"description\":\"poet\",\"id\": \""+ UUID.randomUUID().toString() +"\"}", ObjectNode.class);
        ObjectNode doc2 = om.readValue("{\"name\":\"عمر خیّام\",\"description\":\"mathematician\",\"id\": \""+ UUID.randomUUID().toString() +"\"}", ObjectNode.class);

        CosmosItemProperties doc1Inserted =
            BridgeInternal.getProperties(collection.createItem(doc1, new CosmosItemRequestOptions()).block());

        BridgeInternal.getProperties(collection.replaceItem(doc1Inserted, doc1.get("id").asText(), PartitionKey.NONE, new CosmosItemRequestOptions())
            .block());     // REPLACE with same values -- OK.

        CosmosItemProperties doc2Inserted = BridgeInternal.getProperties(collection
                                                                             .createItem(doc2, new CosmosItemRequestOptions())
                                                                             .block());
        CosmosItemProperties doc2Replacement = new CosmosItemProperties(doc1Inserted.toJson());
        doc2Replacement.setId( doc2Inserted.getId());

        try {
            collection.replaceItem(doc2Replacement, doc2Inserted.getId(), PartitionKey.NONE,
                               new CosmosItemRequestOptions()).block(); // REPLACE doc2 with values from doc1 -- Conflict.
            fail("Did not throw due to unique constraint");
        }
        catch (RuntimeException ex) {
            assertThat(getDocumentClientException(ex).getStatusCode()).isEqualTo(HttpConstants.StatusCodes.CONFLICT);
        }

        doc3.put("id", doc1Inserted.getId());
        collection.replaceItem(doc3, doc1Inserted.getId(), PartitionKey.NONE).block();             // REPLACE with values from doc3 -- OK.

        collection.deleteItem(doc1Inserted.getId(), PartitionKey.NONE).block();
        collection.createItem(doc1, new CosmosItemRequestOptions()).block();
    }

    @Test(groups = { "long" }, timeOut = TIMEOUT)
    public void uniqueKeySerializationDeserialization() {
        PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
        ArrayList<String> paths = new ArrayList<String>();
        paths.add("/mypk");
        partitionKeyDef.setPaths(paths);

        CosmosContainerProperties collectionDefinition = new CosmosContainerProperties(UUID.randomUUID().toString(), partitionKeyDef);
        UniqueKeyPolicy uniqueKeyPolicy = new UniqueKeyPolicy();
        UniqueKey uniqueKey = new UniqueKey();
        uniqueKey.setPaths(ImmutableList.of("/name", "/description"));
        uniqueKeyPolicy.setUniqueKeys(Lists.newArrayList(uniqueKey));
        collectionDefinition.setUniqueKeyPolicy(uniqueKeyPolicy);

        IndexingPolicy indexingPolicy = new IndexingPolicy();
        indexingPolicy.setIndexingMode(IndexingMode.CONSISTENT);
        ExcludedPath excludedPath = new ExcludedPath();
        excludedPath.setPath("/*");
        indexingPolicy.setExcludedPaths(Collections.singletonList(excludedPath));

        IncludedPath includedPath1 = new IncludedPath();
        includedPath1.setPath("/name/?");
        includedPath1.setIndexes(Collections.singletonList(new HashIndex(DataType.STRING, 7)));

        IncludedPath includedPath2 = new IncludedPath();
        includedPath2.setPath("/description/?");
        includedPath2.setIndexes(Collections.singletonList(new HashIndex(DataType.STRING, 7)));
        indexingPolicy.setIncludedPaths(ImmutableList.of(includedPath1, includedPath2));

        collectionDefinition.setIndexingPolicy(indexingPolicy);

        CosmosAsyncContainer createdCollection = database.createContainer(collectionDefinition).block().getContainer();

        CosmosContainerProperties collection = createdCollection.read().block().getProperties();

        assertThat(collection.getUniqueKeyPolicy()).isNotNull();
        assertThat(collection.getUniqueKeyPolicy().getUniqueKeys()).isNotNull();
        assertThat(collection.getUniqueKeyPolicy().getUniqueKeys())
                .hasSameSizeAs(collectionDefinition.getUniqueKeyPolicy().getUniqueKeys());
        assertThat(collection.getUniqueKeyPolicy().getUniqueKeys()
                .stream().map(ui -> ui.getPaths()).collect(Collectors.toList()))
                .containsExactlyElementsOf(
                        ImmutableList.of(ImmutableList.of("/name", "/description")));
    }

    private CosmosClientException getDocumentClientException(RuntimeException e) {
        CosmosClientException dce = Utils.as(e, CosmosClientException.class);
        assertThat(dce).isNotNull();
        return dce;
    }

    @BeforeClass(groups = { "long" }, timeOut = SETUP_TIMEOUT)
    public void before_UniqueIndexTest() {
        // set up the client
        client = new CosmosClientBuilder()
                .setEndpoint(TestConfigurations.HOST)
                .setKey(TestConfigurations.MASTER_KEY)
                .setConnectionPolicy(ConnectionPolicy.getDefaultPolicy())
                .setConsistencyLevel(ConsistencyLevel.SESSION).buildAsyncClient();

        database = createDatabase(client, databaseId);
    }

    @AfterClass(groups = { "long" }, timeOut = SHUTDOWN_TIMEOUT, alwaysRun = true)
    public void afterClass() {
        safeDeleteDatabase(database);
        safeClose(client);
    }
}
