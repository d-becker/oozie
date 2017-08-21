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

/**
 * This class represents a joining point where two or more (but not necessarily all) conditional branches originating
 * from the same decision node meet.
 * This class will NOT be mapped to JAXB classes and XML as decision nodes don't need to be joined is Oozie, this class
 * only exists to make implementing the algorithms easier.
 */
public class DecisionJoin extends JoiningNodeBase<Decision> {

    public DecisionJoin(final String name, final Decision branching) {
        super(name, branching);
    }
}
