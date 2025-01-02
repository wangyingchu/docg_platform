package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ExternalAttributesValueAccessible;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityExternalAttributesValueRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface Neo4JExternalAttributesValueAccessible extends ExternalAttributesValueAccessible,Neo4JKeyResourcesRetrievable{

    public default Set<AttributesViewKind> getAvailableExternalValueAttributesViewKinds(){
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String cypherProcedureString1 = "MATCH (targetNode) WHERE id(targetNode) = " + this.getEntityUID()+"\n"+
                        "RETURN targetNode as "+CypherBuilder.operationResultName;

                DataTransformer<List<String>> dataTransformer1 = new DataTransformer<List<String>>() {
                    @Override
                    public List<String> transformResult(Result result) {

                        if(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord != null){
                                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                                return allConceptionKindNames;
                            }
                        }
                        return null;
                    }
                };
                Object conceptionKindNameListObj = workingGraphOperationExecutor.executeRead(dataTransformer1,cypherProcedureString1);
                List<String> conceptionKindNameList = conceptionKindNameListObj!= null ? (List<String>)conceptionKindNameListObj : null;

                String conceptionKindNameListString = "";
                for(int i=0;i<conceptionKindNameList.size();i++){
                    String currentString = conceptionKindNameList.get(i);
                    conceptionKindNameListString = conceptionKindNameListString +"\""+currentString+"\"";
                    if(i!= conceptionKindNameList.size()-1){
                        conceptionKindNameListString = conceptionKindNameListString +",";
                    }
                }
                String finalConceptionKindNamesStr = "["+conceptionKindNameListString+"]";
                String cypherProcedureString2 = "MATCH (targetNodes:"+ RealmConstant.ConceptionKindClass+") WHERE targetNodes.name IN " + finalConceptionKindNamesStr+"\n"+
                        "RETURN targetNodes as "+CypherBuilder.operationResultName;
                GetListConceptionKindTransformer getListConceptionKindTransformer = new GetListConceptionKindTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object conceptionListRes = workingGraphOperationExecutor.executeRead(getListConceptionKindTransformer,cypherProcedureString2);
                List<ConceptionKind> conceptionKindList = conceptionListRes != null ? (List<ConceptionKind>)conceptionListRes : null;

                Set<AttributesViewKind> availableAttributesViewKinds = Sets.newHashSet();
                if(conceptionKindList!= null){
                    List<String> attributesViewKindUIDList = new ArrayList<>();
                    for(ConceptionKind currentConceptionKind:conceptionKindList){
                        List<AttributesViewKind> attributesViewKindList = currentConceptionKind.getContainsAttributesViewKinds();
                        if(attributesViewKindList!= null){
                            for(AttributesViewKind currentAttributesViewKind:attributesViewKindList){
                                if( AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE.equals(currentAttributesViewKind.getAttributesViewKindDataForm())){
                                    if(!attributesViewKindUIDList.contains(currentAttributesViewKind.getAttributesViewKindUID())){
                                        availableAttributesViewKinds.add(currentAttributesViewKind);
                                        attributesViewKindUIDList.add(currentAttributesViewKind.getAttributesViewKindUID());
                                    }
                                }
                            }
                        }
                    }
                }
                return availableAttributesViewKinds;
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    public default ConceptionEntityExternalAttributesValueRetrieveResult getEntityExternalAttributesValues(
            AttributesViewKind attributesViewKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException{return null;}

    public default Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters){return null;}
}
