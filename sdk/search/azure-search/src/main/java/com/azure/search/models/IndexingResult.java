// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is
// regenerated.

package com.azure.search.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Status of an indexing operation for a single document.
 */
@Fluent
public final class IndexingResult implements Serializable {
    private static final long serialVersionUID = -8604424005271188140L;
    /*
     * The key of a document that was in the indexing request.
     */
    @JsonProperty(value = "key", required = true, access = JsonProperty.Access.WRITE_ONLY)
    private String key;

    /*
     * The error message explaining why the indexing operation failed for the
     * document identified by the key; null if indexing succeeded.
     */
    @JsonProperty(value = "errorMessage", access = JsonProperty.Access.WRITE_ONLY)
    private String errorMessage;

    /*
     * A value indicating whether the indexing operation succeeded for the
     * document identified by the key.
     */
    @JsonProperty(value = "status", required = true, access = JsonProperty.Access.WRITE_ONLY)
    private boolean succeeded;

    /*
     * The status code of the indexing operation. Possible values include: 200
     * for a successful update or delete, 201 for successful document creation,
     * 400 for a malformed input document, 404 for document not found, 409 for
     * a version conflict, 422 when the index is temporarily unavailable, or
     * 503 for when the service is too busy.
     */
    @JsonProperty(value = "statusCode", required = true, access = JsonProperty.Access.WRITE_ONLY)
    private int statusCode;

    /**
     * Get the key property: The key of a document that was in the indexing
     * request.
     *
     * @return the key value.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get the errorMessage property: The error message explaining why the
     * indexing operation failed for the document identified by the key; null
     * if indexing succeeded.
     *
     * @return the errorMessage value.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Get the succeeded property: A value indicating whether the indexing
     * operation succeeded for the document identified by the key.
     *
     * @return the succeeded value.
     */
    public boolean isSucceeded() {
        return this.succeeded;
    }

    /**
     * Get the statusCode property: The status code of the indexing operation.
     * Possible values include: 200 for a successful update or delete, 201 for
     * successful document creation, 400 for a malformed input document, 404
     * for document not found, 409 for a version conflict, 422 when the index
     * is temporarily unavailable, or 503 for when the service is too busy.
     *
     * @return the statusCode value.
     */
    public int getStatusCode() {
        return this.statusCode;
    }
}
