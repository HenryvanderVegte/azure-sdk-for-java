/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.kusto.v2019_05_15;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.microsoft.rest.ExpandableStringEnum;

/**
 * Defines values for AzureSkuName.
 */
public final class AzureSkuName extends ExpandableStringEnum<AzureSkuName> {
    /** Static value Standard_DS13_v2+1TB_PS for AzureSkuName. */
    public static final AzureSkuName STANDARD_DS13_V21TB_PS = fromString("Standard_DS13_v2+1TB_PS");

    /** Static value Standard_DS13_v2+2TB_PS for AzureSkuName. */
    public static final AzureSkuName STANDARD_DS13_V22TB_PS = fromString("Standard_DS13_v2+2TB_PS");

    /** Static value Standard_DS14_v2+3TB_PS for AzureSkuName. */
    public static final AzureSkuName STANDARD_DS14_V23TB_PS = fromString("Standard_DS14_v2+3TB_PS");

    /** Static value Standard_DS14_v2+4TB_PS for AzureSkuName. */
    public static final AzureSkuName STANDARD_DS14_V24TB_PS = fromString("Standard_DS14_v2+4TB_PS");

    /** Static value Standard_D13_v2 for AzureSkuName. */
    public static final AzureSkuName STANDARD_D13_V2 = fromString("Standard_D13_v2");

    /** Static value Standard_D14_v2 for AzureSkuName. */
    public static final AzureSkuName STANDARD_D14_V2 = fromString("Standard_D14_v2");

    /** Static value Standard_L8s for AzureSkuName. */
    public static final AzureSkuName STANDARD_L8S = fromString("Standard_L8s");

    /** Static value Standard_L16s for AzureSkuName. */
    public static final AzureSkuName STANDARD_L16S = fromString("Standard_L16s");

    /** Static value Standard_D11_v2 for AzureSkuName. */
    public static final AzureSkuName STANDARD_D11_V2 = fromString("Standard_D11_v2");

    /** Static value Standard_D12_v2 for AzureSkuName. */
    public static final AzureSkuName STANDARD_D12_V2 = fromString("Standard_D12_v2");

    /** Static value Standard_L4s for AzureSkuName. */
    public static final AzureSkuName STANDARD_L4S = fromString("Standard_L4s");

    /** Static value Dev(No SLA)_Standard_D11_v2 for AzureSkuName. */
    public static final AzureSkuName DEV_NO_SLA_STANDARD_D11_V2 = fromString("Dev(No SLA)_Standard_D11_v2");

    /**
     * Creates or finds a AzureSkuName from its string representation.
     * @param name a name to look for
     * @return the corresponding AzureSkuName
     */
    @JsonCreator
    public static AzureSkuName fromString(String name) {
        return fromString(name, AzureSkuName.class);
    }

    /**
     * @return known AzureSkuName values
     */
    public static Collection<AzureSkuName> values() {
        return values(AzureSkuName.class);
    }
}
