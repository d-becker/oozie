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

import com.google.common.base.Preconditions;

public class ErrorHandler {
    private final Node handlerNode;

    public static ErrorHandler buildAsErrorHandler(final Builder<? extends Node> builder) {
        final Node handlerNode = builder.build();
        return new ErrorHandler(handlerNode);
    }

    private ErrorHandler(final Node handlerNode) {
        final boolean hasParents = !handlerNode.getAllParents().isEmpty();
        final boolean hasChildren = !handlerNode.getAllChildren().isEmpty();
        Preconditions.checkState(!hasParents && !hasChildren, "Error handler nodes cannot have parents or children.");

        this.handlerNode = handlerNode;
    }

    public String getName() {
        return handlerNode.getName();
    }

    public Node getHandlerNode() {
        return handlerNode;
    }
}
