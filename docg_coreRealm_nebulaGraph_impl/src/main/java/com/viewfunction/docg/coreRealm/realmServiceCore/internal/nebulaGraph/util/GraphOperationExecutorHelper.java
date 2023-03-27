package com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.GraphOperationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphOperationExecutorHelper {
    private static Logger logger = LoggerFactory.getLogger(GraphOperationExecutorHelper.class);
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
            try {
                this.innerGraphOperationExecutor.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
