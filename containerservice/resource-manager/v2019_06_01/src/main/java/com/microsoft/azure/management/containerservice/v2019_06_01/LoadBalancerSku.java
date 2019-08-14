/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.containerservice.v2019_06_01;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.microsoft.rest.ExpandableStringEnum;

/**
 * Defines values for LoadBalancerSku.
 */
public final class LoadBalancerSku extends ExpandableStringEnum<LoadBalancerSku> {
    /** Static value standard for LoadBalancerSku. */
    public static final LoadBalancerSku STANDARD = fromString("standard");

    /** Static value basic for LoadBalancerSku. */
    public static final LoadBalancerSku BASIC = fromString("basic");

    /**
     * Creates or finds a LoadBalancerSku from its string representation.
     * @param name a name to look for
     * @return the corresponding LoadBalancerSku
     */
    @JsonCreator
    public static LoadBalancerSku fromString(String name) {
        return fromString(name, LoadBalancerSku.class);
    }

    /**
     * @return known LoadBalancerSku values
     */
    public static Collection<LoadBalancerSku> values() {
        return values(LoadBalancerSku.class);
    }
}
