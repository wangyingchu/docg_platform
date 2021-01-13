package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLinkedListTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.TimeScaleOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Neo4JTimeFlowImpl implements TimeFlow {

    private static Logger logger = LoggerFactory.getLogger(Neo4JTimeFlowImpl.class);
    private String coreRealmName;
    private String timeFlowName;
    private String timeFlowUID;

    public Neo4JTimeFlowImpl(String coreRealmName, String timeFlowName,String timeFlowUID){
        this.coreRealmName = coreRealmName;
        this.timeFlowName = timeFlowName;
        this.timeFlowUID = timeFlowUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getTimeFlowUID() {
        return this.timeFlowUID;
    }

    @Override
    public String getTimeFlowName() {
        return this.timeFlowName;
    }

    @Override
    public boolean createTimeSpanEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException {
        if(toYear<=fromYear){
            logger.error("To Year {} must great than From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }
        List<Integer> availableTimeSpanYears = getAvailableTimeSpanYears();
        if(availableTimeSpanYears.contains(fromYear) ){
            logger.error("Year {} already initialized in TimeFlow {}.", fromYear, getTimeFlowName());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Year "+fromYear+" already initialized in TimeFlow "+getTimeFlowName()+".");
            throw exception;
        }
        if(availableTimeSpanYears.contains(toYear)){
            logger.error("Year {} already initialized in TimeFlow {}.", toYear, getTimeFlowName());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Year "+toYear+" already initialized in TimeFlow "+getTimeFlowName()+".");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor,getTimeFlowName(),fromYear,toYear);
            String linkYearsCql = "MATCH (timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"}),(year:DOCG_TS_Year{timeFlow:\""+getTimeFlowName()+"\"}) WHERE year.year in range("+fromYear+","+toYear+")\n" +
                    "MERGE (timeFlow)-[r:DOCG_TS_Contains]->(year) return count(r) as operationResult";
            logger.debug("Generated Cypher Statement: {}", linkYearsCql);
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Boolean transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        int resultNumber = nodeRecord.get(CypherBuilder.operationResultName).asInt();
                        return resultNumber == toYear-fromYear;
                    }
                    return false;
                }
            };
            Object resultRes = workingGraphOperationExecutor.executeWrite(dataTransformer,linkYearsCql);
            return (Boolean)resultRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean createTimeSpanEntities(int targetYear) throws CoreRealmServiceRuntimeException {
        List<Integer> availableTimeSpanYears = getAvailableTimeSpanYears();
        if(availableTimeSpanYears.contains(targetYear)){
            logger.error("Year {} already initialized in TimeFlow {}.", targetYear, getTimeFlowName());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Year "+targetYear+" already initialized in TimeFlow "+getTimeFlowName()+".");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor,getTimeFlowName(),targetYear);
            String linkYearCql = "MATCH (timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"}),(year:DOCG_TS_Year{timeFlow:\""+getTimeFlowName()+"\"}) WHERE year.year ="+targetYear+"\n" +
                    "MERGE (timeFlow)-[r:DOCG_TS_Contains]->(year) return count(r) as operationResult";
            logger.debug("Generated Cypher Statement: {}", linkYearCql);
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Boolean transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        int resultNumber = nodeRecord.get(CypherBuilder.operationResultName).asInt();
                        return resultNumber == 1;
                    }
                    return false;
                }
            };
            Object resultRes = workingGraphOperationExecutor.executeWrite(dataTransformer,linkYearCql);
            return (Boolean)resultRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<Integer> getAvailableTimeSpanYears() {
        List<Integer> availableTimeSpanYearList = new ArrayList<>();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(timeFlowUID),
                    RealmConstant.TimeScaleYearEntityClass,RealmConstant.TimeScale_ContainsRelationClass, RelationDirection.TO, null);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            availableTimeSpanYearList.add(resultNode.get("year").asInt());
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        Collections.sort(availableTimeSpanYearList);
        return availableTimeSpanYearList;
    }

    @Override
    public TimeScaleEntity getYearEntity(int year) {
        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"}) RETURN year as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }


    @Override
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year) WHERE year.year in range("+fromYear+","+toYear+") RETURN year as operationResult ORDER BY year.year";
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetLinkedListTimeScaleEntityTransformer getLinkedListTimeScaleEntityTransformer =
                    new GetLinkedListTimeScaleEntityTransformer(this.coreRealmName,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getLinkedListTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (LinkedList<TimeScaleEntity>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificYearEntities(int... year) {
        StringBuffer yearPartString = new StringBuffer();
        yearPartString.append("[");
        for(int i=0 ; i<year.length ; i++){
            int currentYear = year[i];
            yearPartString.append(currentYear);
            if(i!=year.length-1){
                yearPartString.append(",");
            }
        }
        yearPartString.append("]");

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year) WHERE year.year in "+yearPartString.toString()+" RETURN year as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetLinkedListTimeScaleEntityTransformer getLinkedListTimeScaleEntityTransformer =
                    new GetLinkedListTimeScaleEntityTransformer(this.coreRealmName,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getLinkedListTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                List<TimeScaleEntity> timeScaleEntityList = (LinkedList<TimeScaleEntity>)queryRes;
                TimeScaleEntity[] timeScaleEntityArray = new TimeScaleEntity[timeScaleEntityList.size()];
                for(int i=0 ; i< timeScaleEntityList.size() ; i++){
                    timeScaleEntityArray[i] = timeScaleEntityList.get(i);
                }
                return timeScaleEntityArray;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getMonthEntity(int year, int month) {
        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"}) RETURN month as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment) {
        int fromYear = fromMonthMoment.getYear();
        int fromMonth = fromMonthMoment.getMonth();
        int toYear = toMonthMoment.getYear();
        int toMonth = toMonthMoment.getMonth();

        try {
            TimeScaleOperationUtil.getMonths(fromYear,fromMonth,toYear,toMonth);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificMonthEntities(TimeScaleMoment... monthMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getDayEntity(int year, int month, int day) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificDayEntities(TimeScaleMoment... dayMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getHourEntity(int year, int month, int day, int hour) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificHourEntities(TimeScaleMoment... hourMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getMinuteEntity(int year, int month, int day, int hour, int minute) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificMinuteEntities(TimeScaleMoment... minuteMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getSecondEntity(int year, int month, int day, int hour, int minute, int second) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificSecondEntities(TimeScaleMoment... secondMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public LinkedList<TimeScaleEntity> getChildEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getFellowEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public TimeScaleEntity getFirstChildEntity(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public TimeScaleEntity getLastChildEntity(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public InheritanceTree<TimeScaleEntity> getOffspringEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    private TimeScaleEntity getSingleTimeScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleTimeScaleEntityTransformer getSingleTimeScaleEntityTransformer =
                    new GetSingleTimeScaleEntityTransformer(this.coreRealmName,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (TimeScaleEntity)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
