/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.sql.v2017_10_01_preview.implementation;

import com.microsoft.azure.management.sql.v2017_10_01_preview.ElasticPoolOperation;
import com.microsoft.azure.arm.model.implementation.WrapperImpl;
import rx.Observable;
import org.joda.time.DateTime;

class ElasticPoolOperationImpl extends WrapperImpl<ElasticPoolOperationInner> implements ElasticPoolOperation {
    private final SqlManager manager;

    ElasticPoolOperationImpl(ElasticPoolOperationInner inner,  SqlManager manager) {
        super(inner);
        this.manager = manager;
    }

    @Override
    public SqlManager manager() {
        return this.manager;
    }



    @Override
    public String description() {
        return this.inner().description();
    }

    @Override
    public String elasticPoolName() {
        return this.inner().elasticPoolName();
    }

    @Override
    public Integer errorCode() {
        return this.inner().errorCode();
    }

    @Override
    public String errorDescription() {
        return this.inner().errorDescription();
    }

    @Override
    public Integer errorSeverity() {
        return this.inner().errorSeverity();
    }

    @Override
    public DateTime estimatedCompletionTime() {
        return this.inner().estimatedCompletionTime();
    }

    @Override
    public String id() {
        return this.inner().id();
    }

    @Override
    public Boolean isCancellable() {
        return this.inner().isCancellable();
    }

    @Override
    public Boolean isUserError() {
        return this.inner().isUserError();
    }

    @Override
    public String name() {
        return this.inner().name();
    }

    @Override
    public String operation() {
        return this.inner().operation();
    }

    @Override
    public String operationFriendlyName() {
        return this.inner().operationFriendlyName();
    }

    @Override
    public Integer percentComplete() {
        return this.inner().percentComplete();
    }

    @Override
    public String serverName() {
        return this.inner().serverName();
    }

    @Override
    public DateTime startTime() {
        return this.inner().startTime();
    }

    @Override
    public String state() {
        return this.inner().state();
    }

    @Override
    public String type() {
        return this.inner().type();
    }

}
