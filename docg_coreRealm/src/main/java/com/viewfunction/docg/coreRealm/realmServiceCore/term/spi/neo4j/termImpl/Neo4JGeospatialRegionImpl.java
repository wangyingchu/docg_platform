package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.SimilarFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GeospatialScaleOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialRegionRuntimeStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialRegionSummaryStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Neo4JGeospatialRegionImpl implements Neo4JGeospatialRegion {

    private static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialRegionImpl.class);
    private String coreRealmName;
    private String geospatialRegionName;
    private String geospatialRegionUID;

    public Neo4JGeospatialRegionImpl(String coreRealmName, String geospatialRegionName, String geospatialRegionUID){
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
        this.geospatialRegionUID = geospatialRegionUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getGeospatialRegionName() {
        return this.geospatialRegionName;
    }

    @Override
    public boolean createGeospatialScaleEntities() {
        try{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            return GeospatialScaleOperationUtil.generateGeospatialScaleEntities(workingGraphOperationExecutor,this.geospatialRegionName);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public GeospatialScaleEntity getEntityByGeospatialCode(String geospatialCode) {
        if(geospatialCode != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,geospatialCode), QueryParameters.FilteringLogic.AND);
                queryParameters.setDistinctMode(true);
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer = new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes != null){
                    return (GeospatialScaleEntity)resultEntityRes;
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listContinentEntities() {
        try{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleContinentEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,100);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return new ArrayList<>();
    }

    @Override
    public GeospatialScaleEntity getContinentEntity(GeospatialProperty geospatialProperty, String continentValue) {
        if(continentValue != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,continentValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialChineseNameProperty,continentValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialEnglishNameProperty,continentValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                }
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleContinentEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer =
                        new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes != null){
                    return (GeospatialScaleEntity)resultEntityRes;
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listCountryRegionEntities(GeospatialProperty geospatialProperty, String continentValue, String countryValue) {
        if(continentValue != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,continentValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialChineseNameProperty,continentValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialEnglishNameProperty,continentValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                }
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleContinentEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer =
                        new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes != null){
                    List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = ((GeospatialScaleEntity)resultEntityRes).getChildEntities();
                    return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,countryValue);
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listCountryRegionEntities(GeospatialProperty geospatialProperty, String countryValue) {
        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(500);
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty, geospatialRegionName));
            if(countryValue != null) {
                switch (geospatialProperty) {
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialCodeProperty, countryValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialChineseNameProperty, countryValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialEnglishNameProperty, countryValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                }
            }

            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleCountryRegionEntityClass, queryParameters, null);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName, this.geospatialRegionName, workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer, queryCql);
            if (resultEntityList != null) {
                return (List<GeospatialScaleEntity>) resultEntityList;
            }

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public GeospatialScaleEntity getCountryRegionEntity(GeospatialProperty geospatialProperty, String countryValue) {
        if(countryValue != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialChineseNameProperty,countryValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialEnglishNameProperty,countryValue,
                                SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
                        break;
                }
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleCountryRegionEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer = new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes != null){
                    return (GeospatialScaleEntity)resultEntityRes;
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listProvinceEntities(GeospatialProperty geospatialProperty, String countryValue, String provinceValue) throws CoreRealmServiceRuntimeException {
        if(countryValue == null & provinceValue == null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleProvinceEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,1000000);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }
        if(countryValue != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialChineseNameProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialEnglishNameProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                }
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleCountryRegionEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer = new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes == null){
                    logger.error("COUNTRY_REGION GeospatialScaleEntity with {} = {} doesn't exist.", ""+geospatialProperty, countryValue);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("COUNTRY_REGION GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+" doesn't exist.");
                    throw exception;
                }else{
                    GeospatialScaleEntity targetCountryRegionEntity = (GeospatialScaleEntity)resultEntityRes;
                    List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = targetCountryRegionEntity.getChildEntities();
                    return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,provinceValue);
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public GeospatialScaleEntity getProvinceEntity(GeospatialProperty geospatialProperty, String countryValue, String provinceValue) throws CoreRealmServiceRuntimeException{
        if(countryValue != null && provinceValue != null){
            try {
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialChineseNameProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialEnglishNameProperty,countryValue),QueryParameters.FilteringLogic.AND);
                        break;
                }
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleCountryRegionEntityClass,queryParameters,null);
                GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer = new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityRes == null){
                    logger.error("COUNTRY_REGION GeospatialScaleEntity with {} = {} doesn't exist.", ""+geospatialProperty, countryValue);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("COUNTRY_REGION GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+" doesn't exist.");
                    throw exception;
                }else{
                    GeospatialScaleEntity targetCountryRegionEntity = (GeospatialScaleEntity)resultEntityRes;
                    List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = targetCountryRegionEntity.getChildEntities();
                    return getFullMatchedEntity(allChildrenGeospatialScaleEntities,geospatialProperty,provinceValue);
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listPrefectureEntities(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue) throws CoreRealmServiceRuntimeException{
        if(countryValue == null & provinceValue == null & prefectureValue == null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScalePrefectureEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,1000000);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }
        GeospatialScaleEntity provinceGeospatialScaleEntity = getProvinceEntity(geospatialProperty,countryValue,provinceValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("PROVINCE GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("PROVINCE GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,prefectureValue);
        }
    }

    @Override
    public GeospatialScaleEntity getPrefectureEntity(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue) throws CoreRealmServiceRuntimeException{
        GeospatialScaleEntity provinceGeospatialScaleEntity = getProvinceEntity(geospatialProperty,countryValue,provinceValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("PROVINCE GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("PROVINCE GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntity(allChildrenGeospatialScaleEntities,geospatialProperty,prefectureValue);
        }
    }

    @Override
    public List<GeospatialScaleEntity> listCountyEntities(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException{
        if(countryValue == null & provinceValue == null & prefectureValue == null & countyValue == null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleCountyEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,100000000);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }
        GeospatialScaleEntity provinceGeospatialScaleEntity = getPrefectureEntity(geospatialProperty,countryValue,provinceValue,prefectureValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("PREFECTURE GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("PREFECTURE GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,countyValue);
        }
    }

    @Override
    public GeospatialScaleEntity getCountyEntity(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException {
        GeospatialScaleEntity provinceGeospatialScaleEntity = getPrefectureEntity(geospatialProperty,countryValue,provinceValue,prefectureValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("PREFECTURE GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("PREFECTURE GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntity(allChildrenGeospatialScaleEntities,geospatialProperty,countyValue);
        }
    }

    @Override
    public List<GeospatialScaleEntity> listTownshipEntities(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue, String townshipValue) throws CoreRealmServiceRuntimeException {
        if(countryValue == null & provinceValue == null & prefectureValue == null & countyValue == null & townshipValue == null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleTownshipEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,100000000);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }
        GeospatialScaleEntity provinceGeospatialScaleEntity = getCountyEntity(geospatialProperty,countryValue,provinceValue,prefectureValue,countyValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("COUNTY GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue+","+countyValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("COUNTY GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+","+countyValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,townshipValue);
        }
    }

    @Override
    public GeospatialScaleEntity getTownshipEntity(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue, String townshipValue) throws CoreRealmServiceRuntimeException {
        GeospatialScaleEntity provinceGeospatialScaleEntity = getCountyEntity(geospatialProperty,countryValue,provinceValue,prefectureValue,countyValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("COUNTY GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue+","+countyValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("COUNTY GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+","+countyValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntity(allChildrenGeospatialScaleEntities,geospatialProperty,townshipValue);
        }
    }

    @Override
    public List<GeospatialScaleEntity> listVillageEntities(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue, String townshipValue, String villageValue) throws CoreRealmServiceRuntimeException {
        if(countryValue == null & provinceValue == null & prefectureValue == null & countyValue == null & townshipValue == null & villageValue == null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleVillageEntityClass,
                    RealmConstant.GeospatialRegionProperty,geospatialRegionName,100000000);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
            Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(resultEntityList != null){
                return (List<GeospatialScaleEntity>)resultEntityList;
            }
        }
        GeospatialScaleEntity provinceGeospatialScaleEntity = getTownshipEntity(geospatialProperty,countryValue,provinceValue,prefectureValue,countyValue,townshipValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("TOWNSHIP GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue+","+countyValue+","+townshipValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("TOWNSHIP GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+","+countyValue+","+townshipValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntitiesList(allChildrenGeospatialScaleEntities,geospatialProperty,villageValue);
        }
    }

    @Override
    public GeospatialScaleEntity getVillageEntity(GeospatialProperty geospatialProperty, String countryValue, String provinceValue, String prefectureValue, String countyValue, String townshipValue, String villageValue) throws CoreRealmServiceRuntimeException {
        GeospatialScaleEntity provinceGeospatialScaleEntity = getTownshipEntity(geospatialProperty,countryValue,provinceValue,prefectureValue,countyValue,townshipValue);
        if(provinceGeospatialScaleEntity == null){
            logger.error("TOWNSHIP GeospatialScaleEntity with {} = {} , doesn't exist.", ""+geospatialProperty, countryValue+","+provinceValue+","+prefectureValue+","+countyValue+","+townshipValue);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("TOWNSHIP GeospatialScaleEntity with "+geospatialProperty+" = "+countryValue+","+provinceValue+","+prefectureValue+","+countyValue+","+townshipValue+" doesn't exist.");
            throw exception;
        }else{
            ((Neo4JGeospatialScaleEntityImpl)provinceGeospatialScaleEntity).setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor());
            List<GeospatialScaleEntity> allChildrenGeospatialScaleEntities = provinceGeospatialScaleEntity.getChildEntities();
            return getFullMatchedEntity(allChildrenGeospatialScaleEntities,geospatialProperty,villageValue);
        }
    }

    @Override
    public long removeRefersGeospatialScaleEvents() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String deleteEntitiesCql = "CALL apoc.periodic.commit(\"MATCH (n:"+RealmConstant.GeospatialScaleEventClass+") WHERE n."+RealmConstant._GeospatialScaleEventGeospatialRegion+"='"+this.geospatialRegionName+"' WITH n LIMIT $limit DETACH DELETE n RETURN count(*)\",{limit: 10000}) YIELD updates, executions, runtime, batches RETURN updates, executions, runtime, batches";
            logger.debug("Generated Cypher Statement: {}", deleteEntitiesCql);
            DataTransformer<Long> deleteTransformer = new DataTransformer() {
                @Override
                public Long transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Long deletedTimeScaleEntitiesNumber =  nodeRecord.get("updates").asLong();
                        return deletedTimeScaleEntitiesNumber;
                    }
                    return null;
                }
            };
            Object deleteEntitiesRes = workingGraphOperationExecutor.executeWrite(deleteTransformer,deleteEntitiesCql);
            long currentDeletedEntitiesCount = deleteEntitiesRes != null ? ((Long)deleteEntitiesRes).longValue():0;
            return currentDeletedEntitiesCount;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public GeospatialRegionRuntimeStatistics getGeospatialRegionRuntimeStatistics() {
        GeospatialRegionRuntimeStatistics geospatialRegionRuntimeStatistics = new GeospatialRegionRuntimeStatistics();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(5);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialRegionClass,queryParameters, CypherBuilder.CypherFunctionType.COUNT);
            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer0 = new GetLongFormatReturnValueTransformer("count(operationResult)");
            Object responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer0,queryCql);
            boolean hasOnlyOneGeospatialRegionFlag = (Long)responseObj == 1 ? true:false;

            String geospatialRegionNameFilter = hasOnlyOneGeospatialRegionFlag ? "":"{DOCG_GeospatialRegion:'"+getGeospatialRegionName()+"'}";

            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();

            queryCql ="MATCH (n:DOCG_GS_Continent"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsContinentScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_CountryRegion"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsCountry_RegionScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_Province"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsProvinceScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_Prefecture"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsPrefectureScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_County"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsCountyScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_Township"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsTownshipScaleTimeScaleEntityCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GS_Village"+geospatialRegionNameFilter+") RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setContainsVillageScaleTimeScaleEntityCount((Long)responseObj);
            }

            geospatialRegionRuntimeStatistics.setContainsTotalGeospatialScaleEntityCount(
                    geospatialRegionRuntimeStatistics.getContainsContinentScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsCountry_RegionScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsProvinceScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsPrefectureScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsCountyScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsTownshipScaleTimeScaleEntityCount()+
                            geospatialRegionRuntimeStatistics.getContainsVillageScaleTimeScaleEntityCount()
            );

            String geospatialRegionNameFilter2 = hasOnlyOneGeospatialRegionFlag ? "":"DOCG_GeospatialScaleEventGeospatialRegion:'"+getGeospatialRegionName()+"',";

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent {"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'CONTINENT'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersContinentScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'COUNTRY_REGION'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersCountry_RegionScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'PROVINCE'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersProvinceScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'PREFECTURE'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersPrefectureScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'COUNTY'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersCountyScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'TOWNSHIP'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersTownshipScaleTimeScaleEventCount((Long)responseObj);
            }

            queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{"+geospatialRegionNameFilter2+"DOCG_GeospatialScaleEventScaleGrade:'VILLAGE'}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionRuntimeStatistics.setRefersVillageScaleTimeScaleEventCount((Long)responseObj);
            }

            geospatialRegionRuntimeStatistics.setRefersTotalGeospatialScaleEventCount(
                    geospatialRegionRuntimeStatistics.getRefersContinentScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersCountry_RegionScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersProvinceScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersPrefectureScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersCountyScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersTownshipScaleTimeScaleEventCount()+
                            geospatialRegionRuntimeStatistics.getRefersVillageScaleTimeScaleEventCount()
            );
        } catch (CoreRealmServiceEntityExploreException e) {
            throw new RuntimeException(e);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return geospatialRegionRuntimeStatistics;
    }

    @Override
    public GeospatialRegionSummaryStatistics getGeospatialRegionSummaryStatistics() {
        GeospatialRegionSummaryStatistics geospatialRegionSummaryStatistics = new GeospatialRegionSummaryStatistics();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(5);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialRegionClass,queryParameters, CypherBuilder.CypherFunctionType.COUNT);
            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer0 = new GetLongFormatReturnValueTransformer("count(operationResult)");
            Object responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer0,queryCql);
            boolean hasOnlyOneGeospatialRegionFlag = (Long)responseObj == 1 ? true:false;

            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();

            if(hasOnlyOneGeospatialRegionFlag){
                //ignore GeospatialRegion filter for better performance
                queryCql ="MATCH (n:DOCG_GeospatialScaleEntity) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            }else{
                queryCql ="MATCH (n:DOCG_GeospatialScaleEntity{DOCG_GeospatialRegion:\""+getGeospatialRegionName()+"\"}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionSummaryStatistics.setContainsTotalGeospatialScaleEntityCount((Long)responseObj);
            }

            if(hasOnlyOneGeospatialRegionFlag){
                //ignore DOCG_GeospatialScaleEventGeospatialRegion filter for better performance
                queryCql ="MATCH (n:DOCG_GeospatialScaleEvent) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            }else{
                queryCql ="MATCH (n:DOCG_GeospatialScaleEvent{DOCG_GeospatialScaleEventGeospatialRegion:\""+getGeospatialRegionName()+"\"}) RETURN COUNT(n) AS "+CypherBuilder.operationResultName;
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);
            responseObj = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
            if(responseObj != null){
                geospatialRegionSummaryStatistics.setRefersTotalGeospatialScaleEventCount((Long)responseObj);
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            throw new RuntimeException(e);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return geospatialRegionSummaryStatistics;
    }

    @Override
    public List<GeospatialScaleEntity> listGeospatialScaleEntities(GeospatialScaleGrade geospatialScaleGrade, GeospatialProperty geospatialProperty, String propertyValue) {
        if(geospatialScaleGrade != null & geospatialProperty != null & propertyValue != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String geospatialScaleGradeValue = null;
                switch (geospatialScaleGrade) {
                    case CONTINENT -> geospatialScaleGradeValue = RealmConstant.GeospatialScaleContinentEntityClass;
                    case COUNTRY_REGION ->
                            geospatialScaleGradeValue = RealmConstant.GeospatialScaleCountryRegionEntityClass;
                    case PROVINCE -> geospatialScaleGradeValue = RealmConstant.GeospatialScaleProvinceEntityClass;
                    case PREFECTURE -> geospatialScaleGradeValue = RealmConstant.GeospatialScalePrefectureEntityClass;
                    case COUNTY -> geospatialScaleGradeValue = RealmConstant.GeospatialScaleCountyEntityClass;
                    case TOWNSHIP -> geospatialScaleGradeValue = RealmConstant.GeospatialScaleTownshipEntityClass;
                    case VILLAGE -> geospatialScaleGradeValue = RealmConstant.GeospatialScaleVillageEntityClass;
                }

                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(100000000);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                switch(geospatialProperty){
                    case GeospatialCode:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialCodeProperty,propertyValue,SimilarFilteringItem.MatchingType.BeginWith),QueryParameters.FilteringLogic.AND);
                        break;
                    case ChineseName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialChineseNameProperty,propertyValue,SimilarFilteringItem.MatchingType.BeginWith),QueryParameters.FilteringLogic.AND);
                        break;
                    case EnglishName:
                        queryParameters.addFilteringItem(new SimilarFilteringItem(RealmConstant.GeospatialEnglishNameProperty,propertyValue,SimilarFilteringItem.MatchingType.BeginWith),QueryParameters.FilteringLogic.AND);
                        break;
                }

                String queryCql = CypherBuilder.matchNodesWithQueryParameters(geospatialScaleGradeValue,queryParameters,null);

                GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                        new GetListGeospatialScaleEntityTransformer(this.coreRealmName,this.geospatialRegionName,workingGraphOperationExecutor);
                Object resultEntityList = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
                if(resultEntityList != null){
                    return (List<GeospatialScaleEntity>)resultEntityList;
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                throw new RuntimeException(e);
            } finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public GeospatialScaleEvent getGeospatialScaleEventByUID(String geospatialScaleEventUID) {
        String queryCql =
                "MATCH (docg_GeospatialScaleEvent:DOCG_GeospatialScaleEvent{DOCG_GeospatialScaleEventGeospatialRegion:\""+getGeospatialRegionName()+"\"}) WHERE id(docg_GeospatialScaleEvent) = "+geospatialScaleEventUID+" RETURN docg_GeospatialScaleEvent as operationResult";
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleGeospatialScaleEventTransformer getSingleGeospatialScaleEventTransformer =
                    new GetSingleGeospatialScaleEventTransformer(getGeospatialRegionName(),graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEventTransformer,queryCql);
            if(queryRes != null){
                return (GeospatialScaleEvent)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    private GeospatialScaleEntity getFullMatchedEntity(List<GeospatialScaleEntity> geospatialScaleEntitiesList,
                                                       GeospatialProperty geospatialProperty,String propertyValue){
        for(GeospatialScaleEntity currentChildEntity:geospatialScaleEntitiesList){
            switch(geospatialProperty){
                case GeospatialCode:
                    if(currentChildEntity.getGeospatialCode().equals(propertyValue)){
                        return currentChildEntity;
                    }
                    break;
                case ChineseName:
                    if(currentChildEntity.getChineseName() != null && currentChildEntity.getChineseName().equals(propertyValue)){
                        return currentChildEntity;
                    }
                    break;
                case EnglishName:
                    if(currentChildEntity.getEnglishName() != null && currentChildEntity.getEnglishName().equals(propertyValue)){
                        return currentChildEntity;
                    }
                    break;
            }
        }
        return null;
    }

    private List<GeospatialScaleEntity> getFullMatchedEntitiesList(List<GeospatialScaleEntity> geospatialScaleEntitiesList,
                                                                   GeospatialProperty geospatialProperty,String propertyValue){
        if(propertyValue == null){
            return geospatialScaleEntitiesList;
        }
        List<GeospatialScaleEntity> matchedProvinceEntities = new ArrayList<>();
        for(GeospatialScaleEntity currentChildEntity:geospatialScaleEntitiesList){
            switch(geospatialProperty){
                case GeospatialCode:
                    if(currentChildEntity.getGeospatialCode().startsWith(propertyValue)){
                        matchedProvinceEntities.add(currentChildEntity);
                    }
                    break;
                case ChineseName:
                    if(currentChildEntity.getChineseName() != null && currentChildEntity.getChineseName().startsWith(propertyValue)){
                        matchedProvinceEntities.add(currentChildEntity);
                    }
                    break;
                case EnglishName:
                    if(currentChildEntity.getEnglishName() != null && currentChildEntity.getEnglishName().startsWith(propertyValue)){
                        matchedProvinceEntities.add(currentChildEntity);
                    }
                    break;
            }
        }
        return matchedProvinceEntities;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
