package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;

public class GraphOperationExecutorHelper {

    private GraphOperationExecutor globalGraphOperationExecutor;
    private GraphOperationExecutor innerGraphOperationExecutor;

    public GraphOperationExecutorHelper(GraphOperationExecutor globalGraphOperationExecutor){
        this.globalGraphOperationExecutor = globalGraphOperationExecutor;
    }

    public GraphOperationExecutorHelper(){}

    public boolean isUsingGlobalGraphOperationExecutor(){
        if(this.globalGraphOperationExecutor != null){
            return true;
        }else{
            return false;
        }
    }

    public GraphOperationExecutor getWorkingGraphOperationExecutor(){
        if(this.globalGraphOperationExecutor != null){
            return this.globalGraphOperationExecutor;
        }else{
            innerGraphOperationExecutor = new GraphOperationExecutor();
            return innerGraphOperationExecutor;
        }
    }

    public GraphOperationExecutor getGlobalGraphOperationExecutor(){
        return this.globalGraphOperationExecutor;
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor globalGraphOperationExecutor){
        this.globalGraphOperationExecutor = globalGraphOperationExecutor;
    }

    public void closeWorkingGraphOperationExecutor(){
        if(this.innerGraphOperationExecutor != null){
            this.innerGraphOperationExecutor.close();
        }
    }
}
