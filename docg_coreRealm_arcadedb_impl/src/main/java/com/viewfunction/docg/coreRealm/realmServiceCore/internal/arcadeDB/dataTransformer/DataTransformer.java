package com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB.dataTransformer;

import com.arcadedb.query.sql.executor.ResultSet;

public interface DataTransformer<T>{
    public T transformResult(ResultSet resultSet);
}
