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

public class KillBuilder extends NodeBuilderBaseImpl<KillBuilder> implements Builder<Kill> {
    private final ModifyOnce<String> message;

    public KillBuilder() {
        message = new ModifyOnce<>();
    }

    public KillBuilder(Kill kill) {
        super(kill);
        message = new ModifyOnce<>(kill.getMessage());
    }

    public KillBuilder withMessage(String message) {
        this.message.set(message);
        return this;
    }

    @Override
    public Kill build() {
        ImmutableList<Node> parentsList = new ImmutableList.Builder<Node>().addAll(parents).build();
        Kill instance = new Kill(name.get(), parentsList, message.get());

        if (parentsList != null) {
            for (Node parent : parentsList) {
                parent.addChild(instance);
            }
        }

        return instance;
    }

    @Override
    protected KillBuilder getThis() {
        return this;
    }
}
