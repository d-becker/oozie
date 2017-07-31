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
import java.util.Arrays;
import java.util.List;

public class EndIntermediaryNode extends IntermediaryNode {
    private IntermediaryNode parent;

    public EndIntermediaryNode(final String name) {
        super(name);
    }

    public IntermediaryNode getParent() {
        return parent;
    }

    @Override
    public void addParent(final IntermediaryNode parent) {
        if (this.parent != null) {
            throw new IllegalStateException("End nodes cannot have multiple parents.");
        }

        this.parent = parent;
        this.parent.addChild(this);
    }

    @Override
    public void removeParent(final IntermediaryNode parent) {
        if (this.parent != parent) {
            throw new IllegalArgumentException("Trying to remove a nonexistent parent.");
        } else {
            if (this.parent != null) {
                this.parent.removeChild(this);
            }

            this.parent = null;
        }
    }

    @Override
    public List<IntermediaryNode> getChildren() {
        return Arrays.asList();
    }

    @Override
    protected void addChild(final IntermediaryNode child) {
        throw new IllegalStateException("End nodes cannot have children.");
    }

    @Override
    protected void removeChild(final IntermediaryNode child) {
        throw new IllegalStateException("End nodes cannot have children.");
    }
}
