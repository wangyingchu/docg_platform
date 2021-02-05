package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import org.apache.ignite.cache.query.TextQuery;

public class TextContainsQueryCondition<K, V> {

    private TextQuery igniteTextQuery;

    public TextContainsQueryCondition(String type, String txt) {
        this.igniteTextQuery=new TextQuery(type,txt);

    }

    public TextContainsQueryCondition(Class<?> type, String txt) {
        this.igniteTextQuery=new TextQuery(type,txt);
    }

    public TextQuery getIgniteTextQuery() {
        return igniteTextQuery;
    }
}
