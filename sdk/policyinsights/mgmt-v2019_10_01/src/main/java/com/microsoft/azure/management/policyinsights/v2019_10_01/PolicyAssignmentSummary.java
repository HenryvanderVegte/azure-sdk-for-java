/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.policyinsights.v2019_10_01;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Policy assignment summary.
 */
public class PolicyAssignmentSummary {
    /**
     * Policy assignment ID.
     */
    @JsonProperty(value = "policyAssignmentId")
    private String policyAssignmentId;

    /**
     * Policy set definition ID, if the policy assignment is for a policy set.
     */
    @JsonProperty(value = "policySetDefinitionId")
    private String policySetDefinitionId;

    /**
     * Compliance summary for the policy assignment.
     */
    @JsonProperty(value = "results")
    private SummaryResults results;

    /**
     * Policy definitions summary.
     */
    @JsonProperty(value = "policyDefinitions")
    private List<PolicyDefinitionSummary> policyDefinitions;

    /**
     * Policy definition group summary.
     */
    @JsonProperty(value = "policyGroups")
    private List<PolicyGroupSummary> policyGroups;

    /**
     * Get policy assignment ID.
     *
     * @return the policyAssignmentId value
     */
    public String policyAssignmentId() {
        return this.policyAssignmentId;
    }

    /**
     * Set policy assignment ID.
     *
     * @param policyAssignmentId the policyAssignmentId value to set
     * @return the PolicyAssignmentSummary object itself.
     */
    public PolicyAssignmentSummary withPolicyAssignmentId(String policyAssignmentId) {
        this.policyAssignmentId = policyAssignmentId;
        return this;
    }

    /**
     * Get policy set definition ID, if the policy assignment is for a policy set.
     *
     * @return the policySetDefinitionId value
     */
    public String policySetDefinitionId() {
        return this.policySetDefinitionId;
    }

    /**
     * Set policy set definition ID, if the policy assignment is for a policy set.
     *
     * @param policySetDefinitionId the policySetDefinitionId value to set
     * @return the PolicyAssignmentSummary object itself.
     */
    public PolicyAssignmentSummary withPolicySetDefinitionId(String policySetDefinitionId) {
        this.policySetDefinitionId = policySetDefinitionId;
        return this;
    }

    /**
     * Get compliance summary for the policy assignment.
     *
     * @return the results value
     */
    public SummaryResults results() {
        return this.results;
    }

    /**
     * Set compliance summary for the policy assignment.
     *
     * @param results the results value to set
     * @return the PolicyAssignmentSummary object itself.
     */
    public PolicyAssignmentSummary withResults(SummaryResults results) {
        this.results = results;
        return this;
    }

    /**
     * Get policy definitions summary.
     *
     * @return the policyDefinitions value
     */
    public List<PolicyDefinitionSummary> policyDefinitions() {
        return this.policyDefinitions;
    }

    /**
     * Set policy definitions summary.
     *
     * @param policyDefinitions the policyDefinitions value to set
     * @return the PolicyAssignmentSummary object itself.
     */
    public PolicyAssignmentSummary withPolicyDefinitions(List<PolicyDefinitionSummary> policyDefinitions) {
        this.policyDefinitions = policyDefinitions;
        return this;
    }

    /**
     * Get policy definition group summary.
     *
     * @return the policyGroups value
     */
    public List<PolicyGroupSummary> policyGroups() {
        return this.policyGroups;
    }

    /**
     * Set policy definition group summary.
     *
     * @param policyGroups the policyGroups value to set
     * @return the PolicyAssignmentSummary object itself.
     */
    public PolicyAssignmentSummary withPolicyGroups(List<PolicyGroupSummary> policyGroups) {
        this.policyGroups = policyGroups;
        return this;
    }

}
