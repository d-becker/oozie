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

package org.apache.oozie.jobs.api.intermediary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class IntermediaryNode {
    private final String name;
    private final List<IntermediaryNode> parents;
    private final List<IntermediaryNode> children;

    public IntermediaryNode(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public IntermediaryNode(String name, List<IntermediaryNode> parents, List<IntermediaryNode> children) {
        this(name);

        for (IntermediaryNode parent : parents) {
            addParent(parent);
        }

        for (IntermediaryNode child : children) {
            addChild(child);
        }
    }

    public String getName() {
        return name;
    }

    public List<IntermediaryNode> getParents() {
        return Collections.unmodifiableList(parents);
    }

    public List<IntermediaryNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addParent(IntermediaryNode parent) {
        this.parents.add(parent);

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void addChild(IntermediaryNode child) {
        this.children.add(child);

        if (child != null) {
            child.parents.add(this);
        }
    }
}
