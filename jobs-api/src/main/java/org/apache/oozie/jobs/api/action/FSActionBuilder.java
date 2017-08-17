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
    private final ModifyOnce<String> nameNode;
    private final List<String> jobXmls;
    private final ConfigurationHandlerBuilder configurationHandlerBuilder;

    private final List<Delete> deletes;
    private final List<Mkdir> mkdirs;
    private final List<Move> moves;
    private final List<Chmod> chmods;
    private final List<Touchz> touchzs;
    private final List<Chgrp> chgrps;
    private final List<Setrep> setreps;

    public static FSActionBuilder create() {
        final ModifyOnce<String> nameNode = new ModifyOnce<>();
        final List<String> jobXmls = new ArrayList<>();
        final ConfigurationHandlerBuilder configurationHandlerBuilder = new ConfigurationHandlerBuilder();

        final List<Delete> deletes = new ArrayList<>();
        final List<Mkdir> mkdirs = new ArrayList<>();
        final List<Move> moves = new ArrayList<>();
        final List<Chmod> chmods = new ArrayList<>();
        final List<Touchz> touchzs = new ArrayList<>();
        final List<Chgrp> chgrps = new ArrayList<>();
        final List<Setrep> setreps = new ArrayList<>();

        return new FSActionBuilder(
                null,
                nameNode,
                jobXmls,
                configurationHandlerBuilder,
                deletes,
                mkdirs,
                moves,
                chmods,
                touchzs,
                chgrps,
                setreps);
    }

    public static FSActionBuilder createFromExistingAction(final FSAction action) {
        final ModifyOnce<String> nameNode = new ModifyOnce<>(action.getNameNode());
        final List<String> jobXmls = new ArrayList<>(action.getJobXmls());
        final ConfigurationHandlerBuilder configurationHandlerBuilder = new ConfigurationHandlerBuilder(action.getConfiguration());

        final List<Delete> deletes = new ArrayList<>(action.getDeletes());
        final List<Mkdir> mkdirs = new ArrayList<>(action.getMkdirs());
        final List<Move> moves = new ArrayList<>(action.getMoves());
        final List<Chmod> chmods = new ArrayList<>(action.getChmods());
        final List<Touchz> touchzs = new ArrayList<>(action.getTouchzs());
        final List<Chgrp> chgrps = new ArrayList<>(action.getChgrps());
        final List<Setrep> setreps = new ArrayList<>(action.getSetreps());

        return new FSActionBuilder(
                action,
                nameNode,
                jobXmls,
                configurationHandlerBuilder,
                deletes,
                mkdirs,
                moves,
                chmods,
                touchzs,
                chgrps,
                setreps);
    }

    FSActionBuilder(final FSAction action,
                    final ModifyOnce<String> nameNode,
                    final List<String> jobXmls,
                    final ConfigurationHandlerBuilder configurationHandlerBuilder,
                    final List<Delete> deletes,
                    final List<Mkdir> mkdirs,
                    final List<Move> moves,
                    final List<Chmod> chmods,
                    final List<Touchz> touchzs,
                    final List<Chgrp> chgrps,
                    final List<Setrep> setreps) {
        super(action);

        this.nameNode = nameNode;
        this.jobXmls = jobXmls;
        this.configurationHandlerBuilder = configurationHandlerBuilder;

        this.deletes = deletes;
        this.mkdirs = mkdirs;
        this.moves = moves;
        this.chmods = chmods;
        this.touchzs = touchzs;
        this.chgrps = chgrps;
        this.setreps = setreps;
    }

    public FSActionBuilder withNameNode(final String nameNode) {
        this.nameNode.set(nameNode);
        return this;
    }

    public FSActionBuilder withJobXml(final String jobXml) {
        this.jobXmls.add(jobXml);
        return this;
    }

    public FSActionBuilder withoutJobXml(final String jobXml) {
        jobXmls.remove(jobXml);
        return this;
    }

    public FSActionBuilder clearJobXmls() {
        jobXmls.clear();
        return this;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public FSActionBuilder withConfigProperty(final String key, final String value) {
        configurationHandlerBuilder.withConfigProperty(key, value);
        return this;
    }

    public FSActionBuilder withDelete(final Delete delete) {
        this.deletes.add(delete);
        return this;
    }

    public FSActionBuilder withoutDelete(final Delete delete) {
        deletes.remove(delete);
        return this;
    }

    public FSActionBuilder clearDeletes() {
        deletes.clear();
        return this;
    }

    public FSActionBuilder withMkdir(final Mkdir mkdir) {
        this.mkdirs.add(mkdir);
        return this;
    }

    public FSActionBuilder withoutMkdir(final Mkdir mkdir) {
        mkdirs.remove(mkdir);
        return this;
    }

    public FSActionBuilder clearMkdirs() {
        mkdirs.clear();
        return this;
    }

    public FSActionBuilder withMove(final Move move) {
        this.moves.add(move);
        return this;
    }

    public FSActionBuilder withoutMove(final Move move) {
        moves.remove(move);
        return this;
    }

    public FSActionBuilder clearMoves() {
        moves.clear();
        return this;
    }

    public FSActionBuilder withChmod(final Chmod chmod) {
        this.chmods.add(chmod);
        return this;
    }

    public FSActionBuilder withoutChmod(final Chmod chmod) {
        chmods.remove(chmod);
        return this;
    }

    public FSActionBuilder clearChmods() {
        chmods.clear();
        return this;
    }

    public FSActionBuilder withTouchz(final Touchz touchz) {
        this.touchzs.add(touchz);
        return this;
    }

    public FSActionBuilder withoutTouchz(final Touchz touchz) {
        touchzs.remove(touchz);
        return this;
    }

    public FSActionBuilder clearTouchzs() {
        touchzs.clear();
        return this;
    }

    public FSActionBuilder withChgrp(final Chgrp chgrp) {
        this.chgrps.add(chgrp);
        return this;
    }

    public FSActionBuilder withoutChgrp(final Chgrp chgrp) {
        chgrps.remove(chgrp);
        return this;
    }

    public FSActionBuilder clearChgrps() {
        chgrps.clear();
        return this;
    }

    public FSActionBuilder withSetrep(final Setrep setrep) {
        this.setreps.add(setrep);
        return this;
    }

    public FSActionBuilder withoutSetrep(final Setrep setrep) {
        setreps.remove(setrep);
        return this;
    }

    public FSActionBuilder clearSetreps() {
        setreps.clear();
        return this;
    }

    @Override
    public FSAction build() {
        final Action.ConstructionData constructionData = getConstructionData();
        final String nameNodeActual = nameNode.get();
        final ImmutableList<String> jobXmlsList = ImmutableList.copyOf(jobXmls);
        final ConfigurationHandler configurationHandler = configurationHandlerBuilder.build();

        final ImmutableList<Delete> deletesList = ImmutableList.copyOf(deletes);
        final ImmutableList<Mkdir> mkdirsList = ImmutableList.copyOf(mkdirs);
        final ImmutableList<Move> movesList = ImmutableList.copyOf(moves);
        final ImmutableList<Chmod> chmodsList = ImmutableList.copyOf(chmods);
        final ImmutableList<Touchz> touchzsList = ImmutableList.copyOf(touchzs);
        final ImmutableList<Chgrp> chgrpsList = ImmutableList.copyOf(chgrps);
        final ImmutableList<Setrep> setrepsList = ImmutableList.copyOf(setreps);

        final FSAction instance = new FSAction(
                constructionData,
                nameNodeActual,
                jobXmlsList,
                configurationHandler,
                deletesList,
                mkdirsList,
                movesList,
                chmodsList,
                touchzsList,
                chgrpsList,
                setrepsList);

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected FSActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
