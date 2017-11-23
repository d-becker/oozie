package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Hive2Action extends HiveAction {
    private final String jdbcUrl;
    private final String password;

    public Hive2Action(final ConstructionData constructionData,
                       final ActionAttributes attributes,
                       final String jdbcUrl,
                       final String password,
                       final String script,
                       final String query,
                       final ImmutableList<String> params) {
        super(constructionData,
              attributes,
              script,
              query,
              params);

        this.jdbcUrl = jdbcUrl;
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getPassword() {
        return password;
    }
}