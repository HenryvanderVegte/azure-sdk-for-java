/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.datafactory.v2018_06_01;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.microsoft.rest.serializer.JsonFlatten;
import com.microsoft.azure.management.datafactory.v2018_06_01.implementation.DatasetInner;

/**
 * The Salesforce Service Cloud object dataset.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = SalesforceServiceCloudObjectDataset.class)
@JsonTypeName("SalesforceServiceCloudObject")
@JsonFlatten
public class SalesforceServiceCloudObjectDataset extends DatasetInner {
    /**
     * The Salesforce Service Cloud object API name. Type: string (or
     * Expression with resultType string).
     */
    @JsonProperty(value = "typeProperties.objectApiName")
    private Object objectApiName;

    /**
     * Get the Salesforce Service Cloud object API name. Type: string (or Expression with resultType string).
     *
     * @return the objectApiName value
     */
    public Object objectApiName() {
        return this.objectApiName;
    }

    /**
     * Set the Salesforce Service Cloud object API name. Type: string (or Expression with resultType string).
     *
     * @param objectApiName the objectApiName value to set
     * @return the SalesforceServiceCloudObjectDataset object itself.
     */
    public SalesforceServiceCloudObjectDataset withObjectApiName(Object objectApiName) {
        this.objectApiName = objectApiName;
        return this;
    }

}
