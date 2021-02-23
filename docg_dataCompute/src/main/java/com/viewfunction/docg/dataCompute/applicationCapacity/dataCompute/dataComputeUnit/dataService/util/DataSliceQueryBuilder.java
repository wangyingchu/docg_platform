package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.util;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem.FilteringItem;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceQueryStructureException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class DataSliceQueryBuilder {

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
                    //logger.error("Default Filtering Item is required");
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
        return sql;
    }

    private static Condition generateQueryCondition(FilteringItem filteringItem){
        Condition currentQueryCondition = null;
        if(filteringItem instanceof EqualFilteringItem){
            EqualFilteringItem currentFilteringItem = (EqualFilteringItem)filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = currentFilteringItem.getAttributeValue();
            if(propertyName != null & propertyValue !=null ){
                currentQueryCondition = field(propertyName).eq(propertyValue);
            }
        }
        return currentQueryCondition;
    }
}
