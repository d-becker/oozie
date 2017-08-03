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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node {
    private final String name;
    private final ImmutableList<Node> parents;
    private final List<Node> children; // MUTABLE!

    Node(final String name,
         final ImmutableList<Node> parents)
    {
        this.name = name;
        this.parents = parents;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Node> getParents() {
        return parents;
    }

    void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Returns an unmodifiable view of list of the children of this <code>Action</code>.
     * @return An unmodifiable view of list of the children of this <code>Action</code>.
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
