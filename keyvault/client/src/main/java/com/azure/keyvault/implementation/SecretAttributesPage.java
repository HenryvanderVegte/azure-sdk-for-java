// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.keyvault.implementation;

import com.azure.common.http.rest.Page;
import com.azure.keyvault.models.SecretAttributes;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A page of Azure App Configuration {@link SecretAttributes} resources and a link to get the next page of
 * resources, if any.
 */
public final class SecretAttributesPage implements Page<SecretAttributes> {

    /**
     * The link to the next page.
     */
    @JsonProperty("nextLink")
    private String nextLink;

    /**
     * The list of items.
     */
    @JsonProperty("value")
    private List<SecretAttributes> items;

    /**
     * Gets the link to the next page. Or {@code null} if there are no more resources to fetch.
     *
     * @return The link to the next page.
     */
    @Override
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Gets the list of {@link SecretAttributes SecretAttributes} on this page.
     *
     * @return The list of items in {@link List}.
     */
    @Override
    public List<SecretAttributes> items() {
        return items;
    }
}