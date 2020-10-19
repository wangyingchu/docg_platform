package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

public interface StatisticalAndEvaluable {

    public enum StatisticAndEvaluateFunction {
        COUNT,AVG,MAX,MIN,STDEV,SUM
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
