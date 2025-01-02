package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.SortingItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ExternalAttributesValueAccessible;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityExternalAttributesValueRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Neo4JExternalAttributesValueAccessible extends ExternalAttributesValueAccessible,Neo4JKeyResourcesRetrievable{

    public final String ExternalAttributesValueAccessProcessorID = "DOCG_ExternalAttributesValueAccessProcessorID";

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
                                if(AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE.equals(currentAttributesViewKind.getAttributesViewKindDataForm())){
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
            AttributesViewKind attributesViewKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if(queryParameters != null){
            checkQueryParametersValidStatus(queryParameters);
        }
        if(attributesViewKind != null){
            checkAttributesViewKindValidStatus(attributesViewKind);
            if(queryParameters != null){
                checkAttributesViewKindAndQueryParameterCompatibility(attributesViewKind,queryParameters);
            }

            Object _ExternalAttributesValueAccessProcessorID = attributesViewKind.getMetaConfigItem(ExternalAttributesValueAccessProcessorID);
            if(_ExternalAttributesValueAccessProcessorID == null){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("ExternalAttributesValueAccessProcessor is required");
                throw exception;
            }else{
                String externalAttributesValueAccessProcessorID = _ExternalAttributesValueAccessProcessorID.toString();
            }
        }
        return null;
    }

    public default Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters)
            throws CoreRealmServiceEntityExploreException{
        if(attributesViewKind != null){
            checkAttributesViewKindValidStatus(attributesViewKind);
            if(attributesParameters != null){
                checkAttributesViewKindAndAttributesParametersCompatibility(attributesViewKind,attributesParameters);
            }



        }
        return null;
    }

    private void checkQueryParametersValidStatus(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if (queryParameters != null) {
            int startPage = queryParameters.getStartPage();
            int endPage = queryParameters.getEndPage();
            int pageSize = queryParameters.getPageSize();
            int resultNumber = queryParameters.getResultNumber();

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (endPage != 0) {
                    //get data from start page to end page, each page has runtimePageSize number of record
                    if (endPage < 0 || endPage <= startPage) {
                        String exceptionMessage = "end page must great than start page";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                }
            } else {
                //if there is no page parameters,use resultNumber to control result information number
                if (resultNumber != 0) {
                    if (resultNumber < 0) {
                        String exceptionMessage = "result number must great then zero";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                }
            }
        }
    }

    private void checkAttributesViewKindAndQueryParameterCompatibility(AttributesViewKind attributesViewKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        Set<String> viewAttributesNameList = new HashSet<>();
        List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
        for(AttributeKind currentAttributeKind:attributeKindList){
            viewAttributesNameList.add(currentAttributeKind.getAttributeKindName());
        }

        String defaultFilterAttributeName = queryParameters.getDefaultFilteringItem() != null ? queryParameters.getDefaultFilteringItem().getAttributeName():null;
        if(!viewAttributesNameList.contains(defaultFilterAttributeName)){
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("attribute "+defaultFilterAttributeName + " not contained in AttributesViewKind");
            throw exception;
        }
        for(FilteringItem currentFilteringItem:queryParameters.getAndFilteringItemsList()){
            if(!viewAttributesNameList.contains(currentFilteringItem.getAttributeName())){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("attribute "+currentFilteringItem.getAttributeName() + " not contained in AttributesViewKind");
                throw exception;
            }
        }
        for(FilteringItem currentFilteringItem:queryParameters.getOrFilteringItemsList()){
            if(!viewAttributesNameList.contains(currentFilteringItem.getAttributeName())){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("attribute "+currentFilteringItem.getAttributeName() + " not contained in AttributesViewKind");
                throw exception;
            }
        }
        for(SortingItem currentSortingItem:queryParameters.getSortingItems()){
            if(!viewAttributesNameList.contains(currentSortingItem.getAttributeName())){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("attribute "+currentSortingItem.getAttributeName() + " not contained in AttributesViewKind");
                throw exception;
            }
        }
    }

    private void checkAttributesViewKindAndAttributesParametersCompatibility(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters) throws CoreRealmServiceEntityExploreException {
        Set<String> viewAttributesNameList = new HashSet<>();
        List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
        for(AttributeKind currentAttributeKind:attributeKindList){
            viewAttributesNameList.add(currentAttributeKind.getAttributeKindName());
        }

        String defaultFilterAttributeName = attributesParameters.getDefaultFilteringItem() != null ? attributesParameters.getDefaultFilteringItem().getAttributeName():null;
        if(!viewAttributesNameList.contains(defaultFilterAttributeName)){
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("attribute "+defaultFilterAttributeName + " not contained in AttributesViewKind");
            throw exception;
        }
        for(FilteringItem currentFilteringItem:attributesParameters.getAndFilteringItemsList()){
            if(!viewAttributesNameList.contains(currentFilteringItem.getAttributeName())){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("attribute "+currentFilteringItem.getAttributeName() + " not contained in AttributesViewKind");
                throw exception;
            }
        }
        for(FilteringItem currentFilteringItem:attributesParameters.getOrFilteringItemsList()){
            if(!viewAttributesNameList.contains(currentFilteringItem.getAttributeName())){
                CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                exception.setCauseMessage("attribute "+currentFilteringItem.getAttributeName() + " not contained in AttributesViewKind");
                throw exception;
            }
        }
    }

    private void checkAttributesViewKindValidStatus(AttributesViewKind attributesViewKind) throws CoreRealmServiceEntityExploreException {
        if(!AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE.equals(attributesViewKind.getAttributesViewKindDataForm())){
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("AttributesViewKindDataForm must be EXTERNAL_VALUE");
            throw exception;
        }
        boolean isValidAttributesViewKind = false;
        Set<AttributesViewKind> availableExternalValueAttributesViewKinds = getAvailableExternalValueAttributesViewKinds();
        for(AttributesViewKind currentAttributesViewKind:availableExternalValueAttributesViewKinds){
            if(currentAttributesViewKind.getAttributesViewKindUID().equals(attributesViewKind.getAttributesViewKindUID())){
                isValidAttributesViewKind = true;
                break;
            }
        }
        if(!isValidAttributesViewKind){
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("AttributesViewKind is invalid for Entity: "+this.getEntityUID());
            throw exception;
        }
        List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
        if(attributeKindList == null || attributeKindList.isEmpty()){
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("AttributesViewKind must contains AttributeKinds");
            throw exception;
        }
    }






    private void buildQueryIndex(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if (queryParameters != null) {
            int defaultReturnRecordNumber = 10000;

            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = queryParameters.getStartPage();
            int endPage = queryParameters.getEndPage();
            int pageSize = queryParameters.getPageSize();
            int resultNumber = queryParameters.getResultNumber();
            boolean isDistinctMode = queryParameters.isDistinctMode();
            List<SortingItem> sortingItemList = queryParameters.getSortingItems();

            if (sortingItemList.size() > 0) {
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            //
                            break;
                        case DESC:
                            //
                    }
                }
            }

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }

                int runtimePageSize = pageSize != 0 ? pageSize : 50;
                int runtimeStartPage = startPage - 1;

                if (endPage != 0) {
                    //get data from start page to end page, each page has runtimePageSize number of record
                    if (endPage < 0 || endPage <= startPage) {
                        String exceptionMessage = "end page must great than start page";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    int runtimeEndPage = endPage - 1;

                    skipRecordNumber = runtimePageSize * runtimeStartPage;
                    limitRecordNumber = (runtimeEndPage - runtimeStartPage) * runtimePageSize;
                } else {
                    //filter the data before the start page
                    limitRecordNumber = runtimePageSize * runtimeStartPage;
                }
            } else {
                //if there is no page parameters,use resultNumber to control result information number
                if (resultNumber != 0) {
                    if (resultNumber < 0) {
                        String exceptionMessage = "result number must great then zero";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    limitRecordNumber = resultNumber;
                }
            }

            if (limitRecordNumber == 0) {
                limitRecordNumber = defaultReturnRecordNumber;
            }
        }
    }

}
