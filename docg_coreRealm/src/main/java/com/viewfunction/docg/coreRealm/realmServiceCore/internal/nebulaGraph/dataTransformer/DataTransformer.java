package com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.dataTransformer;

import com.vesoft.nebula.client.graph.data.ResultSet;

public interface DataTransformer<T> {
    public T transformResult(ResultSet resultSet);
}
