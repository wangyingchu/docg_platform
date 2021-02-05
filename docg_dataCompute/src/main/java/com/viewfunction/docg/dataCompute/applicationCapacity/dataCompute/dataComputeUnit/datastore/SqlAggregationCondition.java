package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import org.apache.ignite.cache.query.SqlFieldsQuery;

public class SqlAggregationCondition {

    private SqlFieldsQuery igniteSqlFieldsQuery;

    public SqlFieldsQuery getIgniteSqlFieldsQuery() {
        return igniteSqlFieldsQuery;
    }

    public SqlAggregationCondition(String sql) {
        this.igniteSqlFieldsQuery=new SqlFieldsQuery(sql);
    }

    public SqlAggregationCondition(String sql, boolean collocated) {
        this.igniteSqlFieldsQuery=new SqlFieldsQuery(sql,collocated);
    }

    public void setQueryParameters(Object... args){
        this.igniteSqlFieldsQuery.setArgs(args);
    }
}
