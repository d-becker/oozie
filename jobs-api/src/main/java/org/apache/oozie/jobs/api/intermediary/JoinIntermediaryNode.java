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

public class JoinIntermediaryNode extends IntermediaryNode {
    private final List<IntermediaryNode> parents;
    private IntermediaryNode child;

    public JoinIntermediaryNode(final String name) {
        super(name);
        parents = new ArrayList<>();
    }

    public List<IntermediaryNode> getParents() {
        return Collections.unmodifiableList(parents);
    }

    @Override
    public void addParent(final IntermediaryNode parent) {
        if (parent != null) {
            parent.addChild(this);
        }

        parents.add(parent);
    }

    @Override
    public void removeParent(final IntermediaryNode parent) {
        if (!parents.remove(parent)) {
            throw new IllegalArgumentException("Trying to remove a nonexistent parent");
        }

        parent.removeChild(this);
    }

    public IntermediaryNode getChild() {
        return child;
    }

    @Override
    protected void addChild(final IntermediaryNode child) {
        if (this.child != null) {
            throw new IllegalStateException("Join nodes cannot have multiple children.");
        }

        this.child = child;
    }

    @Override
    protected void removeChild(final IntermediaryNode child) {
        if (this.child == child) {
            this.child = null;
        } else {
            throw new IllegalArgumentException("Trying to remove a nonexistent child.");
        }
    }
}
