package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class HiveAction extends PigAction {
    private final String query;

    HiveAction(final ConstructionData constructionData,
               final ActionAttributes attributes,
               final String script,
               final String query,
               final ImmutableList<String> params) {
        super(constructionData,
              attributes,
              script,
              params);

        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}