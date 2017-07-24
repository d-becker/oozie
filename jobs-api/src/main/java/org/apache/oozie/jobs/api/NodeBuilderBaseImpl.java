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

package org.apache.oozie.jobs.api;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeBuilderBaseImpl <BUILDER_T extends NodeBuilderBaseImpl<BUILDER_T>> {
    protected final ModifyOnce<String> name;
    protected final List<Node> parents;

    protected final BUILDER_T concreteThis;

    protected NodeBuilderBaseImpl() {
        parents = new ArrayList<>();
        name = new ModifyOnce<>();

        concreteThis = checkThis();
    }

    public NodeBuilderBaseImpl(final Node node) {
        parents = new ArrayList<>(node.getParents());
        name = new ModifyOnce<>(node.getName());

        concreteThis = checkThis();
    }

    public BUILDER_T withName(String name) {
        this.name.set(name);
        return concreteThis;
    }

    public BUILDER_T withParent(Node action) {
        parents.add(action);
        return concreteThis;
    }

    public BUILDER_T withoutParent(Node parent) {
        parents.remove(parent);
        return concreteThis;
    }

    public BUILDER_T clearParents() {
        parents.clear();
        return concreteThis;
    }

    protected final BUILDER_T checkThis() {
        BUILDER_T concrete = getThis();
        if (concrete != this) {
            throw new IllegalStateException(
                    "The concrete builder type BUILDER_T doesn't extend ActionBuilderBaseImpl<BUILDER_T>.");
        }

        return concrete;
    }

    protected abstract BUILDER_T getThis();
}
