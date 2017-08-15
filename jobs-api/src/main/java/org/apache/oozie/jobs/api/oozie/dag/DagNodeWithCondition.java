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

package org.apache.oozie.jobs.api.oozie.dag;

import java.util.Collection;

public class DagNodeWithCondition {
    private final NodeBase node;
    private final String condition;

    public static boolean removeFromCollection(final Collection<DagNodeWithCondition> collection, final NodeBase node) {
        DagNodeWithCondition element = null;
        for (DagNodeWithCondition nodeWithCondition : collection) {
            if (node.equals(nodeWithCondition.getNode())) {
                element = nodeWithCondition;
            }
        }

        if (element != null) {
            collection.remove(element);
        }

        return element != null;
    }

    // TODO: remove this.
    public DagNodeWithCondition() {
        node = null;
        condition = null;
    }

    public DagNodeWithCondition(final NodeBase node,
                                final String condition) {
        this.node = node;
        this.condition = condition;
    }

    public NodeBase getNode() {
        return node;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DagNodeWithCondition that = (DagNodeWithCondition) o;

        if (node != null ? !node.equals(that.node) : that.node != null) return false;
        return condition != null ? condition.equals(that.condition) : that.condition == null;
    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }
}
