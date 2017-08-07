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

import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeBuilderBaseImpl <B extends NodeBuilderBaseImpl<B>> {
    protected final ModifyOnce<String> name;
    protected final List<Node> parents;

    NodeBuilderBaseImpl() {
        parents = new ArrayList<>();
        name = new ModifyOnce<>();
    }

    NodeBuilderBaseImpl(final Node node) {
        parents = new ArrayList<>(node.getParents());
        name = new ModifyOnce<>(node.getName());
    }

    public B withName(final String name) {
        this.name.set(name);
        return ensureRuntimeSelfReference();
    }

    public B withParent(final Node action) {
        parents.add(action);
        return ensureRuntimeSelfReference();
    }

    B withoutParent(final Node parent) {
        parents.remove(parent);
        return ensureRuntimeSelfReference();
    }

    public B clearParents() {
        parents.clear();
        return ensureRuntimeSelfReference();
    }

    final B ensureRuntimeSelfReference() {
        final B concrete = getRuntimeSelfReference();
        if (concrete != this) {
            throw new IllegalStateException(
                    "The builder type B doesn't extend ActionBuilderBaseImpl<B>.");
        }

        return concrete;
    }

    protected abstract B getRuntimeSelfReference();
}
