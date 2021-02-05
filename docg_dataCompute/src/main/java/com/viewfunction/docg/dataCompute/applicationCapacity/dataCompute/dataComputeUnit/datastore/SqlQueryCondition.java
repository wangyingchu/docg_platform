package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import org.apache.ignite.cache.query.SqlQuery;


public class SqlQueryCondition<K, V>{

    private SqlQuery igniteSqlQuery;

    public SqlQueryCondition(String type, String sql) {
        this.igniteSqlQuery=new SqlQuery(type, sql);
    }

    public SqlQueryCondition(Class<?> type, String sql) {
        this.igniteSqlQuery=new SqlQuery(type, sql);
    }

    public SqlQuery getIgniteSqlQuery() {
        return igniteSqlQuery;
    }

    public void setQueryParameters(Object... args){
        this.igniteSqlQuery.setArgs(args);
    }
}
