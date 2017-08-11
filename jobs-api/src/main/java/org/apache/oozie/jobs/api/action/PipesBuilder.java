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

public class PipesBuilder {
    private final ModifyOnce<String> map;
    private final ModifyOnce<String> reduce;
    private final ModifyOnce<String> inputformat;
    private final ModifyOnce<String> partitioner;
    private final ModifyOnce<String> writer;
    private final ModifyOnce<String> program;

    public PipesBuilder() {
        map = new ModifyOnce<>();
        reduce = new ModifyOnce<>();
        inputformat = new ModifyOnce<>();
        partitioner = new ModifyOnce<>();
        writer = new ModifyOnce<>();
        program = new ModifyOnce<>();
    }

    public PipesBuilder withMap(final String map) {
        this.map.set(map);
        return this;
    }

    public PipesBuilder withReduce(final String reduce) {
        this.reduce.set(reduce);
        return this;
    }

    public PipesBuilder withInputformat(final String inputformat) {
        this.inputformat.set(inputformat);
        return this;
    }

    public PipesBuilder withPartitioner(final String partitioner) {
        this.partitioner.set(partitioner);
        return this;
    }

    public PipesBuilder withWriter(final String writer) {
        this.writer.set(writer);
        return this;
    }

    public PipesBuilder withProgram(final String program) {
        this.program.set(program);
        return this;
    }

    public Pipes build() {
        return new Pipes(map.get(), reduce.get(), inputformat.get(), partitioner.get(), writer.get(), program.get());
    }
}
