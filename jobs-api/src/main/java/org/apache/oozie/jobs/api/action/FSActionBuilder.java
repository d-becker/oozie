/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class FSActionBuilder extends ActionBuilderBaseImpl<FSActionBuilder> implements Builder<FSAction> {
    private final ActionAttributesBuilder attributesBuilder;

    public static FSActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();

        return new FSActionBuilder(
                null,
                builder);
    }

    public static FSActionBuilder createFromExistingAction(final FSAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());

        return new FSActionBuilder(
                action,
                builder);
    }

    FSActionBuilder(final FSAction action,
                    final ActionAttributesBuilder attributesBuilder) {
        super(action);

        this.attributesBuilder = attributesBuilder;
    }

    public FSActionBuilder withNameNode(final String nameNode) {
        attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public FSActionBuilder withJobXml(final String jobXml) {
        attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public FSActionBuilder withoutJobXml(final String jobXml) {
        attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public FSActionBuilder clearJobXmls() {
        attributesBuilder.clearJobXmls();
        return this;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public FSActionBuilder withConfigProperty(final String key, final String value) {
        attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public FSActionBuilder withDelete(final Delete delete) {
        attributesBuilder.withDelete(delete);
        return this;
    }

    public FSActionBuilder withoutDelete(final Delete delete) {
        attributesBuilder.withoutDelete(delete);
        return this;
    }

    public FSActionBuilder clearDeletes() {
        attributesBuilder.clearDeletes();
        return this;
    }

    public FSActionBuilder withMkdir(final Mkdir mkdir) {
        attributesBuilder.withMkdir(mkdir);
        return this;
    }

    public FSActionBuilder withoutMkdir(final Mkdir mkdir) {
        attributesBuilder.withoutMkdir(mkdir);
        return this;
    }

    public FSActionBuilder clearMkdirs() {
        attributesBuilder.clearMkdirs();
        return this;
    }

    public FSActionBuilder withMove(final Move move) {
        attributesBuilder.withMove(move);
        return this;
    }

    public FSActionBuilder withoutMove(final Move move) {
        attributesBuilder.withoutMove(move);
        return this;
    }

    public FSActionBuilder clearMoves() {
        attributesBuilder.clearMoves();
        return this;
    }

    public FSActionBuilder withChmod(final Chmod chmod) {
        attributesBuilder.withChmod(chmod);
        return this;
    }

    public FSActionBuilder withoutChmod(final Chmod chmod) {
        attributesBuilder.withoutChmod(chmod);
        return this;
    }

    public FSActionBuilder clearChmods() {
        attributesBuilder.clearChmods();
        return this;
    }

    public FSActionBuilder withTouchz(final Touchz touchz) {
        attributesBuilder.withTouchz(touchz);
        return this;
    }

    public FSActionBuilder withoutTouchz(final Touchz touchz) {
        attributesBuilder.withoutTouchz(touchz);
        return this;
    }

    public FSActionBuilder clearTouchzs() {
        attributesBuilder.clearTouchzs();
        return this;
    }

    public FSActionBuilder withChgrp(final Chgrp chgrp) {
        attributesBuilder.withChgrp(chgrp);
        return this;
    }

    public FSActionBuilder withoutChgrp(final Chgrp chgrp) {
        attributesBuilder.withoutChgrp(chgrp);
        return this;
    }

    public FSActionBuilder clearChgrps() {
        attributesBuilder.clearChgrps();
        return this;
    }

    public FSActionBuilder withSetrep(final Setrep setrep) {
        attributesBuilder.withSetrep(setrep);
        return this;
    }

    public FSActionBuilder withoutSetrep(final Setrep setrep) {
        attributesBuilder.withoutSetrep(setrep);
        return this;
    }

    public FSActionBuilder clearSetreps() {
        attributesBuilder.clearSetreps();
        return this;
    }

    @Override
    public FSAction build() {
        final Action.ConstructionData constructionData = getConstructionData();

        final FSAction instance = new FSAction(
                constructionData,
                attributesBuilder.build());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected FSActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
