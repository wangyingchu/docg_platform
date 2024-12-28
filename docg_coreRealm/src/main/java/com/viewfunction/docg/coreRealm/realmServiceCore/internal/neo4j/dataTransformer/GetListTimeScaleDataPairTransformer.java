package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetListTimeScaleDataPairTransformer implements DataTransformer<List<TimeScaleDataPair>>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetListTimeScaleDataPairTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<TimeScaleDataPair> transformResult(Result result) {
        List<TimeScaleDataPair> timeScaleDataPairList = new ArrayList<>();
        while(result.hasNext()) {
            Record record = result.next();

            Neo4JTimeScaleEntityImpl neo4JTimeScaleEntityImpl = null;
            Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = null;

            Node timeScaleEntityNode = record.get("timeScaleEntities").asNode();
            List<String> allLabelNames = Lists.newArrayList(timeScaleEntityNode.labels());
            boolean isMatchedEntity = true;
            if (allLabelNames.size() > 0) {
                isMatchedEntity = allLabelNames.contains(RealmConstant.TimeScaleEntityClass);
            }
            if (isMatchedEntity) {
                TimeFlow.TimeScaleGrade timeScaleGrade = null;
                long nodeUID = timeScaleEntityNode.id();
                String entityUID = "" + nodeUID;
                int value = timeScaleEntityNode.get("id").asInt();
                String timeFlowName = timeScaleEntityNode.get("timeFlow").asString();

                String timeScaleEntityDesc = null;
                if(allLabelNames.contains(RealmConstant.TimeScaleYearEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                    if(timeScaleEntityNode.containsKey("year")){
                        timeScaleEntityDesc = ""+timeScaleEntityNode.get("year").asInt();
                    }
                }else if(allLabelNames.contains(RealmConstant.TimeScaleMonthEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                    if(timeScaleEntityNode.containsKey("year") && timeScaleEntityNode.containsKey("month")){
                        timeScaleEntityDesc = ""+timeScaleEntityNode.get("year").asInt()+
                                "-"+timeScaleEntityNode.get("month").asInt();
                    }
                }else if(allLabelNames.contains(RealmConstant.TimeScaleDayEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                    if(timeScaleEntityNode.containsKey("year") && timeScaleEntityNode.containsKey("month")
                            && timeScaleEntityNode.containsKey("day")){
                        timeScaleEntityDesc = ""+timeScaleEntityNode.get("year").asInt()+
                                "-"+timeScaleEntityNode.get("month").asInt()+
                                "-"+timeScaleEntityNode.get("day").asInt();
                    }
                }else if(allLabelNames.contains(RealmConstant.TimeScaleHourEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                    if(timeScaleEntityNode.containsKey("year") && timeScaleEntityNode.containsKey("month")
                            && timeScaleEntityNode.containsKey("day") && timeScaleEntityNode.containsKey("hour")){
                        timeScaleEntityDesc = ""+timeScaleEntityNode.get("year").asInt()+
                                "-"+timeScaleEntityNode.get("month").asInt()+
                                "-"+timeScaleEntityNode.get("day").asInt()+
                                " "+timeScaleEntityNode.get("hour").asInt();
                    }
                }else if(allLabelNames.contains(RealmConstant.TimeScaleMinuteEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                    if(timeScaleEntityNode.containsKey("year") && timeScaleEntityNode.containsKey("month")
                            && timeScaleEntityNode.containsKey("day") && timeScaleEntityNode.containsKey("hour")
                            && timeScaleEntityNode.containsKey("minute")){
                        timeScaleEntityDesc = ""+timeScaleEntityNode.get("year").asInt()+
                                "-"+timeScaleEntityNode.get("month").asInt()+
                                "-"+timeScaleEntityNode.get("day").asInt()+
                                " "+timeScaleEntityNode.get("hour").asInt()+
                                ":"+timeScaleEntityNode.get("minute").asInt();
                    }
                }

                neo4JTimeScaleEntityImpl = new Neo4JTimeScaleEntityImpl(
                        null, timeFlowName, entityUID, timeScaleGrade, value);
                neo4JTimeScaleEntityImpl.setEntityDescription(timeScaleEntityDesc);
                neo4JTimeScaleEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            }

            Node timeScaleEventNode = record.get("timeScaleEvents").asNode();
            List<String> allConceptionKindNames = Lists.newArrayList(timeScaleEventNode.labels());
            boolean isMatchedEvent = false;
            if(allConceptionKindNames.size()>0){
                isMatchedEvent = allConceptionKindNames.contains(RealmConstant.TimeScaleEventClass);
            }
            if(isMatchedEvent){
                long nodeUID = timeScaleEventNode.id();
                String timeScaleEventUID = ""+nodeUID;
                String eventComment = timeScaleEventNode.get(RealmConstant._TimeScaleEventComment).asString();
                String timeScaleGrade = timeScaleEventNode.get(RealmConstant._TimeScaleEventScaleGrade).asString();
                LocalDateTime referTime = timeScaleEventNode.get(RealmConstant._TimeScaleEventReferTime).asLocalDateTime();
                neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(null,eventComment,referTime,getTimeScaleGrade(timeScaleGrade),timeScaleEventUID);
                neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            }

            if(neo4JTimeScaleEntityImpl != null && neo4JTimeScaleEventImpl != null){
                timeScaleDataPairList.add(
                        new TimeScaleDataPair(neo4JTimeScaleEventImpl,neo4JTimeScaleEntityImpl)
                );
            }
        }
        return timeScaleDataPairList;
    }

    private TimeFlow.TimeScaleGrade getTimeScaleGrade(String timeScaleGradeValue){
        if(timeScaleGradeValue.equals("YEAR")){
            return TimeFlow.TimeScaleGrade.YEAR;
        }else if(timeScaleGradeValue.equals("MONTH")){
            return TimeFlow.TimeScaleGrade.MONTH;
        }else if(timeScaleGradeValue.equals("DAY")){
            return TimeFlow.TimeScaleGrade.DAY;
        }else if(timeScaleGradeValue.equals("HOUR")){
            return TimeFlow.TimeScaleGrade.HOUR;
        }else if(timeScaleGradeValue.equals("MINUTE")){
            return TimeFlow.TimeScaleGrade.MINUTE;
        }else if(timeScaleGradeValue.equals("SECOND")){
            return TimeFlow.TimeScaleGrade.SECOND;
        }
        return null;
    }
}
