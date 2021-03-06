/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.vmwarecloudsimple.v2019_04_01;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User name model.
 */
public class UsageName {
    /**
     * e.g. "Virtual Machines".
     */
    @JsonProperty(value = "localizedValue")
    private String localizedValue;

    /**
     * resource type or resource type sku name, e.g. virtualMachines.
     */
    @JsonProperty(value = "value")
    private String value;

    /**
     * Get e.g. "Virtual Machines".
     *
     * @return the localizedValue value
     */
    public String localizedValue() {
        return this.localizedValue;
    }

    /**
     * Set e.g. "Virtual Machines".
     *
     * @param localizedValue the localizedValue value to set
     * @return the UsageName object itself.
     */
    public UsageName withLocalizedValue(String localizedValue) {
        this.localizedValue = localizedValue;
        return this;
    }

    /**
     * Get resource type or resource type sku name, e.g. virtualMachines.
     *
     * @return the value value
     */
    public String value() {
        return this.value;
    }

    /**
     * Set resource type or resource type sku name, e.g. virtualMachines.
     *
     * @param value the value value to set
     * @return the UsageName object itself.
     */
    public UsageName withValue(String value) {
        this.value = value;
        return this;
    }

}
