package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.util;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem.*;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceQueryStructureException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class DataSliceQueryBuilder {

    private static Logger logger = LoggerFactory.getLogger(DataSliceQueryBuilder.class);

    public static String buildSelectQuerySQL(String dataSliceName, QueryParameters queryParameters) throws DataSliceQueryStructureException {
        DSLContext create = DSL.using((Connection) null, SQLDialect.SQL99);
        Query query = null;
        if(queryParameters != null){
            FilteringItem defaultFilteringItem = queryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = queryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = queryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    DataSliceQueryStructureException e = new DataSliceQueryStructureException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            }else{
                Condition defaultCondition = generateQueryCondition(defaultFilteringItem);
                query = create.select(field("*")).from(table(dataSliceName)).where(defaultCondition);
            }
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
            if(propertyName != null & propertyValues != null &propertyValues.size() > 0){
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
