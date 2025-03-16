package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.SortingItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

import static org.jooq.impl.DSL.*;

public class RelationDBQueryBuilder {

    private static Logger logger = LoggerFactory.getLogger(RelationDBQueryBuilder.class);

    public static String buildSelectQuerySQL(String dataTableName, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        //RelationDB in DOCG mainly use Doris as its underlying database
        DSLContext create = DSL.using((Connection) null, SQLDialect.MYSQL);
        Query query = null;
        if(queryParameters != null){
            int startPage = queryParameters.getStartPage();
            int endPage = queryParameters.getEndPage();
            int pageSize = queryParameters.getPageSize();
            int resultNumber = queryParameters.getResultNumber();

            int defaultReturnRecordNumber = 10000000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

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

            SelectConditionStep selectConditionStep = null;

            FilteringItem defaultFilteringItem = queryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = queryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = queryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }else{
                    if(queryParameters.isDistinctMode()){
                        query = create.selectDistinct(field("*")).from(table(dataTableName)).limit(limitRecordNumber).offset(skipRecordNumber);
                    }else{
                        query = create.select(field("*")).from(table(dataTableName)).limit(limitRecordNumber).offset(skipRecordNumber);
                    }
                    String sql = query.getSQL(ParamType.NAMED_OR_INLINED);
                    logger.debug("Generated SQL Statement: {}", sql);
                    return sql;
                }
            }else{
                Condition defaultCondition = generateQueryCondition(defaultFilteringItem);
                if(andFilteringItemList != null && andFilteringItemList.size() > 0){
                    for(FilteringItem currentAndFilteringItem:andFilteringItemList){
                        Condition currentAndCondition = generateQueryCondition(currentAndFilteringItem);
                        if(currentAndCondition != null){
                            defaultCondition = defaultCondition.and(currentAndCondition);
                        }
                    }
                }
                if(orFilteringItemList != null && orFilteringItemList.size() > 0){
                    for(FilteringItem currentOrFilteringItem:orFilteringItemList){
                        Condition currentOrCondition = generateQueryCondition(currentOrFilteringItem);
                        if(currentOrCondition != null){
                            defaultCondition = defaultCondition.or(currentOrCondition);
                        }
                    }
                }

                if(queryParameters.isDistinctMode()){
                    selectConditionStep = create.selectDistinct(field("*")).from(table(dataTableName)).where(defaultCondition);
                }else{
                    selectConditionStep = create.select(field("*")).from(table(dataTableName)).where(defaultCondition);
                }

                List<SortingItem> sortingItemList = queryParameters.getSortingItems();
                if(sortingItemList != null && sortingItemList.size() > 0){
                    for(SortingItem currentSortingItem:sortingItemList){
                        String fieldName = currentSortingItem.getAttributeName();
                        QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                        switch (sortingLogic){
                            case ASC:
                                selectConditionStep.orderBy(field(fieldName).asc());
                                break;
                            case DESC:
                                selectConditionStep.orderBy(field(fieldName).desc());
                        }
                    }
                }
            }
            selectConditionStep.limit(limitRecordNumber).offset(skipRecordNumber);
            query = selectConditionStep.getQuery();
        }else{
            query = create.select(field("*")).from(table(dataTableName));
        }
        String sql = query.getSQL(ParamType.NAMED_OR_INLINED);
        logger.debug("Generated SQL Statement: {}", sql);
        return sql;
    }

    public static String buildCountQuerySQL(String dataTableName, AttributesParameters attributesParameters) throws CoreRealmServiceEntityExploreException{
        //RelationDB in DOCG mainly use Doris as its underlying database
        DSLContext create = DSL.using((Connection) null, SQLDialect.MYSQL);
        Query query = null;
        if(attributesParameters != null){
            FilteringItem defaultFilteringItem = attributesParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = attributesParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = attributesParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }else{
                    query = create.select(count(field("*"))).from(table(dataTableName));
                }
            }else{
                Condition defaultCondition = generateQueryCondition(defaultFilteringItem);
                if(andFilteringItemList != null && andFilteringItemList.size() > 0){
                    for(FilteringItem currentAndFilteringItem:andFilteringItemList){
                        Condition currentAndCondition = generateQueryCondition(currentAndFilteringItem);
                        if(currentAndCondition != null){
                            defaultCondition = defaultCondition.and(currentAndCondition);
                        }
                    }
                }
                if(orFilteringItemList != null && orFilteringItemList.size() > 0){
                    for(FilteringItem currentOrFilteringItem:orFilteringItemList){
                        Condition currentOrCondition = generateQueryCondition(currentOrFilteringItem);
                        if(currentOrCondition != null){
                            defaultCondition = defaultCondition.or(currentOrCondition);
                        }
                    }
                }
                SelectConditionStep selectConditionStep = create.select(count(field("*"))).from(table(dataTableName)).where(defaultCondition);
                query = selectConditionStep.getQuery();
            }
        }else{
            query = create.select(count(field("*"))).from(table(dataTableName));
        }

        String sql = query.getSQL(ParamType.NAMED_OR_INLINED);
        logger.debug("Generated SQL Statement: {}", sql);
        return sql;
    }

    public static String buildDeleteQuerySQL(String dataTableName, AttributesParameters attributesParameters) throws CoreRealmServiceEntityExploreException{
        //RelationDB in DOCG mainly use Doris as its underlying database
        DSLContext create = DSL.using((Connection) null, SQLDialect.MYSQL);
        Query query = null;
        if(attributesParameters != null){
            FilteringItem defaultFilteringItem = attributesParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = attributesParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = attributesParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }else{
                    query = create.deleteFrom(table(dataTableName)).returning();
                }
            }else{
                Condition defaultCondition = generateQueryCondition(defaultFilteringItem);
                if(andFilteringItemList != null && andFilteringItemList.size() > 0){
                    for(FilteringItem currentAndFilteringItem:andFilteringItemList){
                        Condition currentAndCondition = generateQueryCondition(currentAndFilteringItem);
                        if(currentAndCondition != null){
                            defaultCondition = defaultCondition.and(currentAndCondition);
                        }
                    }
                }
                if(orFilteringItemList != null && orFilteringItemList.size() > 0){
                    for(FilteringItem currentOrFilteringItem:orFilteringItemList){
                        Condition currentOrCondition = generateQueryCondition(currentOrFilteringItem);
                        if(currentOrCondition != null){
                            defaultCondition = defaultCondition.or(currentOrCondition);
                        }
                    }
                }
                DeleteConditionStep deleteConditionStep = create.deleteFrom(table(dataTableName)).where(defaultCondition);
                query = deleteConditionStep.returning();
            }
        }else{
            query = create.deleteFrom(table(dataTableName)) .returning();
        }
        String sql = query.getSQL(ParamType.NAMED_OR_INLINED);
        logger.debug("Generated SQL Statement: {}", sql);
        return sql;
    }

    private static Condition generateQueryCondition(FilteringItem filteringItem){
        Condition currentQueryCondition = null;
        if(filteringItem instanceof EqualFilteringItem){
            EqualFilteringItem currentFilteringItem = (EqualFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).notEqual(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).equal(propertyValue);
                }
            }
        }
        if(filteringItem instanceof BetweenFilteringItem){
            BetweenFilteringItem currentFilteringItem = (BetweenFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyFromValue = currentFilteringItem.getAttributeFromValue();
            Object propertyToValue = currentFilteringItem.getAttributeToValue();
            if(propertyName != null & propertyFromValue != null & propertyToValue !=null){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).notBetween(propertyFromValue,propertyToValue);
                }else{
                    currentQueryCondition = field(propertyName).between(propertyFromValue,propertyToValue);
                }
            }
        }
        if(filteringItem instanceof GreaterThanEqualFilteringItem){
            GreaterThanEqualFilteringItem currentFilteringItem = (GreaterThanEqualFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).lessThan(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).greaterOrEqual(propertyValue);
                }
            }
        }
        if(filteringItem instanceof GreaterThanFilteringItem){
            GreaterThanFilteringItem currentFilteringItem = (GreaterThanFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).lessOrEqual(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).greaterThan(propertyValue);
                }
            }
        }
        if(filteringItem instanceof InValueFilteringItem){
            InValueFilteringItem currentFilteringItem = (InValueFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            List<Object> propertyValues = currentFilteringItem.getAttributeValues();
            if(propertyName != null & propertyValues != null & propertyValues.size() > 0){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).notIn(propertyValues.toArray());
                }else{
                    currentQueryCondition = field(propertyName).in(propertyValues.toArray());
                }
            }
        }
        if(filteringItem instanceof LessThanEqualFilteringItem){
            LessThanEqualFilteringItem currentFilteringItem = (LessThanEqualFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).greaterThan(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).lessOrEqual(propertyValue);
                }
            }
        }
        if(filteringItem instanceof LessThanFilteringItem){
            LessThanFilteringItem currentFilteringItem = (LessThanFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).greaterOrEqual(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).lessThan(propertyValue);
                }
            }
        }
        if(filteringItem instanceof NotEqualFilteringItem){
            NotEqualFilteringItem currentFilteringItem = (NotEqualFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue != null ){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).equal(propertyValue);
                }else{
                    currentQueryCondition = field(propertyName).notEqual(propertyValue);
                }
            }
        }
        if(filteringItem instanceof NullValueFilteringItem){
            NullValueFilteringItem currentFilteringItem = (NullValueFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            if(propertyName != null){
                if(currentFilteringItem.isReversedCondition()){
                    currentQueryCondition = field(propertyName).isNotNull();
                }else{
                    currentQueryCondition = field(propertyName).isNull();
                }
            }
        }
        if(filteringItem instanceof SimilarFilteringItem){
            SimilarFilteringItem currentFilteringItem = (SimilarFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            SimilarFilteringItem.MatchingType matchingType = currentFilteringItem.getMatchingType();
            if(propertyName != null & propertyValue != null & matchingType!= null){
                if(currentFilteringItem.isReversedCondition()){
                    switch(matchingType){
                        case BeginWith:
                            currentQueryCondition = field(propertyName).notLike(propertyValue.toString()+"%");
                            break;
                        case EndWith:
                            currentQueryCondition = field(propertyName).notLike("%"+propertyValue.toString());
                            break;
                        case Contain:
                            currentQueryCondition = field(propertyName).notLike("%"+propertyValue.toString()+"%");
                            break;
                    }
                }else{
                    switch(matchingType){
                        case BeginWith:
                            currentQueryCondition = field(propertyName).like(propertyValue.toString()+"%");
                            break;
                        case EndWith:
                            currentQueryCondition = field(propertyName).like("%"+propertyValue.toString());
                            break;
                        case Contain:
                            currentQueryCondition = field(propertyName).like("%"+propertyValue.toString()+"%");
                            break;
                    }
                }
            }
        }
        return currentQueryCondition;
    }
}
