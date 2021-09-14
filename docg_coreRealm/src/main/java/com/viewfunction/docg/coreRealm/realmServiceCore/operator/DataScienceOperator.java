package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;

import java.util.List;

public interface DataScienceOperator {
    //墨子悲丝 - 染于苍则苍，染于黄则黄。五入为五色，不可不慎也。非独染丝，治国亦然。

    public List<AnalyzableGraph> getAnalyzableGraphs();

    public boolean checkAnalyzableGraphExistence(String graphName);

    public AnalyzableGraph getAnalyzableGraph(String graphName);

    public boolean removeAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException;

    public AnalyzableGraph createAnalyzableGraph(String graphName,List<String> conceptionKindList,
                                                 List<String> relationKindList) throws CoreRealmServiceRuntimeException;
}
