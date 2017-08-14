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

public class EmailActionBuilder extends ActionBuilderBaseImpl<EmailActionBuilder> implements Builder<EmailAction> {
    private final ModifyOnce<String> to;
    private final ModifyOnce<String> cc;
    private final ModifyOnce<String> bcc;
    private final ModifyOnce<String> subject;
    private final ModifyOnce<String> body;
    private final ModifyOnce<String> contentType;
    private final ModifyOnce<String> attachment;

    public EmailActionBuilder() {
        super();
        to = new ModifyOnce<>();
        cc = new ModifyOnce<>();
        bcc = new ModifyOnce<>();
        subject = new ModifyOnce<>();
        body = new ModifyOnce<>();
        contentType = new ModifyOnce<>();
        attachment = new ModifyOnce<>();
    }

    public EmailActionBuilder(final EmailAction action) {
        super(action);
        to = new ModifyOnce<>(action.getRecipient());
        cc = new ModifyOnce<>(action.getCc());
        bcc = new ModifyOnce<>(action.getBcc());
        subject = new ModifyOnce<>(action.getSubject());
        body = new ModifyOnce<>(action.getBody());
        contentType = new ModifyOnce<>(action.getContentType());
        attachment = new ModifyOnce<>(action.getAttachment());
    }

    public EmailActionBuilder withRecipient(final String to) {
        this.to.set(to);
        return this;
    }

    public EmailActionBuilder withCc(final String cc) {
        this.cc.set(cc);
        return this;
    }

    public EmailActionBuilder withBcc(final String bcc) {
        this.bcc.set(bcc);
        return this;
    }

    public EmailActionBuilder withSubject(final String subject) {
        this.subject.set(subject);
        return this;
    }

    public EmailActionBuilder withBody(final String body) {
        this.body.set(body);
        return this;
    }

    public EmailActionBuilder withContentType(final String contentType) {
        this.contentType.set(contentType);
        return this;
    }

    public EmailActionBuilder withAttachment(final String attachment) {
        this.attachment.set(attachment);
        return this;
    }

    @Override
    public EmailAction build() {
        final Action.ConstructionData constructionData = getConstructionData();

        final EmailAction instance = new EmailAction(
                constructionData,
                to.get(),
                cc.get(),
                bcc.get(),
                subject.get(),
                body.get(),
                contentType.get(),
                attachment.get());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected EmailActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
