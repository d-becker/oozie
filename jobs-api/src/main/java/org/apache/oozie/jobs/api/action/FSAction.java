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

import java.util.List;
import java.util.Map;

public class FSAction extends Action {
    private final String nameNode;
    private final ImmutableList<String> jobXmls;
    private final ConfigurationHandler configurationHandler;

    private final ImmutableList<Delete> deletes;
    private final ImmutableList<Mkdir> mkdirs;
    private final ImmutableList<Move> moves;
    private final ImmutableList<Chmod> chmods;
    private final ImmutableList<Touchz> touchzs;
    private final ImmutableList<Chgrp> chgrps;
    private final ImmutableList<Setrep> setreps;

    FSAction(final Action.ConstructionData constructionData,
             final String nameNode,
             final ImmutableList<String> jobXmls,
             final ConfigurationHandler configurationHandler,
             final ImmutableList<Delete> deletes,
             final ImmutableList<Mkdir> mkdirs,
             final ImmutableList<Move> moves,
             final ImmutableList<Chmod> chmods,
             final ImmutableList<Touchz> touchzs,
             final ImmutableList<Chgrp> chgrps,
             final ImmutableList<Setrep> setreps) {
        super(constructionData);

        this.nameNode = nameNode;
        this.jobXmls = jobXmls;
        this.configurationHandler = configurationHandler;
        this.deletes = deletes;
        this.mkdirs = mkdirs;
        this.moves = moves;
        this.chmods = chmods;
        this.touchzs = touchzs;
        this.chgrps = chgrps;
        this.setreps = setreps;
    }

    public String getNameNode() {
        return nameNode;
    }

    public List<String> getJobXmls() {
        return jobXmls;
    }

    public String getConfigProperty(final String property) {
        return configurationHandler.getConfigProperty(property);
    }

    public Map<String, String> getConfiguration() {
        return configurationHandler.getConfiguration();
    }

    public List<Delete> getDeletes() {
        return deletes;
    }

    public List<Mkdir> getMkdirs() {
        return mkdirs;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public List<Chmod> getChmods() {
        return chmods;
    }

    public List<Touchz> getTouchzs() {
        return touchzs;
    }

    public List<Chgrp> getChgrps() {
        return chgrps;
    }

    public List<Setrep> getSetreps() {
        return setreps;
    }
}
