package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;

import java.util.Map;

public interface Neo4JStatisticalAndEvaluable extends StatisticalAndEvaluable,Neo4JKeyResourcesRetrievable{

    default Map<String,Double> statisticNumericalAttributes(QueryParameters queryParameters, Map<String, StatisticFunction> statisticCondition){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {







            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        System.out.println("statisticNumericalAttributes");
        return null;
    }

            /*
                case COUNT:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.count(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.count(m.property(additionalPropertyName))).build();
                    }
                case AVG:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.avg(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.avg(m.property(additionalPropertyName))).build();
                    }
                case MAX:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.max(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.max(m.property(additionalPropertyName))).build();
                    }
                case MIN:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.min(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.min(m.property(additionalPropertyName))).build();
                    }
                case STDEV:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.stDev(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.stDev(m.property(additionalPropertyName))).build();
                    }
                case SUM:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.sum(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.sum(m.property(additionalPropertyName))).build();
                    }
            */

}
