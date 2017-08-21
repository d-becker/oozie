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

import com.google.common.base.Preconditions;

public class Condition {
    private final String condition;
    private final boolean isDefault;

    private Condition(final String condition, final boolean isDefault) {
        final boolean bothFieldsSet = condition == null && !isDefault;
        final boolean bothFieldsUnset = condition != null && isDefault;
        Preconditions.checkArgument(!bothFieldsSet && !bothFieldsUnset,
                "Exactly one of 'condition' and 'isDefault' must be non-null or true (respectively).");

        this.condition = condition;
        this.isDefault = isDefault;
    }

    public static Condition actualCondition(final String condition) {
        Preconditions.checkArgument(condition != null, "The argument 'condition' must not be null.");

        return new Condition(condition, false);
    }

    public static Condition defaultCondition() {
        return new Condition(null, true);
    }

    public String getCondition() {
        return condition;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Condition other = (Condition) o;

        if (isDefault != other.isDefault) {
            return false;
        }

        return condition != null ? condition.equals(other.condition) : other.condition == null;
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (isDefault ? 1 : 0);

        return result;
    }
}
