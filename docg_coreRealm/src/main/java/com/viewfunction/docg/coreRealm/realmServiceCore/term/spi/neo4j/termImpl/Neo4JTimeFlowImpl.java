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
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JTimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Neo4JTimeFlowImpl implements Neo4JTimeFlow {

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
    public boolean createTimeSpanEntities(int fromYear, int toYear, boolean createMinuteData) throws CoreRealmServiceRuntimeException {
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
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor,getTimeFlowName(),fromYear,toYear,createMinuteData);
            String linkYearsCql = "MATCH (timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"}),(year:DOCG_TS_Year{timeFlow:\""+getTimeFlowName()+"\"}) WHERE year.year in range("+fromYear+","+toYear+")\n" +
                    "MERGE (timeFlow)-[r:DOCG_TS_Contains]->(year) return count(r) as operationResult";
            logger.debug("Generated Cypher Statement: {}", linkYearsCql);
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Boolean transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        int resultNumber = nodeRecord.get(CypherBuilder.operationResultName).asInt();
                        return resultNumber == toYear-fromYear+1;
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
    public boolean createTimeSpanEntities(int targetYear, boolean createMinuteData) throws CoreRealmServiceRuntimeException {
        List<Integer> availableTimeSpanYears = getAvailableTimeSpanYears();
        if(availableTimeSpanYears.contains(targetYear)){
            logger.error("Year {} already initialized in TimeFlow {}.", targetYear, getTimeFlowName());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Year "+targetYear+" already initialized in TimeFlow "+getTimeFlowName()+".");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor,getTimeFlowName(),targetYear,createMinuteData);
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
            return resultRes != null ? true : false;
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
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException {
        if(toYear<=fromYear){
            logger.error("To Year {} must great than From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }
        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year) WHERE year.year in range("+fromYear+","+toYear+") RETURN year as operationResult ORDER BY year.year";
        return getListTimeScaleEntity(queryCql);
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

        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year) WHERE year.year in "+yearPartString.toString()+" RETURN year as operationResult";
        return getArrayTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity getMonthEntity(int year, int month) {
        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"}) RETURN month as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment) throws CoreRealmServiceRuntimeException {
        int fromYear = fromMonthMoment.getYear();
        int fromMonth = fromMonthMoment.getMonth();
        int toYear = toMonthMoment.getYear();
        int toMonth = toMonthMoment.getMonth();

        if(toYear<=fromYear){
            logger.error("To Year {} must great than From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }

        String queryCql = null;

        //for from part year
        queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month) WHERE month.month in range("+fromMonth+",12) RETURN month as operationResult ORDER BY year.year, month.month";

        //for middle whole years
        int yearSpan = toYear - fromYear;
        if(yearSpan > 1 ){
            if(yearSpan == 2){
                String yearRange =""+(fromYear+1);
                String wholeYearPartQuery = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year)-[:DOCG_TS_Contains]->(month:DOCG_TS_Month) WHERE year.year ="+yearRange+" RETURN month as operationResult ORDER BY year.year, month.month";
                queryCql = queryCql +" UNION "+"\n" + wholeYearPartQuery;
            }else{
                String yearRange ="range("+(fromYear+1)+","+(fromYear+yearSpan-1)+")";
                String wholeYearPartQuery = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year)-[:DOCG_TS_Contains]->(month:DOCG_TS_Month) WHERE year.year in "+yearRange+" RETURN month as operationResult ORDER BY year.year, month.month";
                queryCql = queryCql +" UNION "+"\n" + wholeYearPartQuery;
            }
        }
        //for to part year
        queryCql = queryCql +" UNION "+"\n" +
                "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month) WHERE month.month in range(1,"+toMonth+") RETURN month as operationResult ORDER BY year.year, month.month";
        return getListTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity[] getSpecificMonthEntities(TimeScaleMoment... monthMoments) {
        String queryCql = buildUnionMonthEntitiesCql(monthMoments);
        return getArrayTimeScaleEntity(queryCql);
    }

    private String buildUnionMonthEntitiesCql(TimeScaleMoment... monthMoments){
        StringBuffer cqlBuffer = new StringBuffer();
        for(int i = 0; i< monthMoments.length;i++){
            TimeScaleMoment currentTimeScaleMoment = monthMoments[i];
            String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+currentTimeScaleMoment.getYear()+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+currentTimeScaleMoment.getMonth()+"}) RETURN month as operationResult";
            cqlBuffer.append(queryCql);
            if(i !=monthMoments.length-1){
                cqlBuffer.append(" UNION"+"\n");
            }
        }
        return cqlBuffer.toString();
    }

    @Override
    public TimeScaleEntity getDayEntity(int year, int month, int day) {
        String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"}) RETURN day as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment) throws CoreRealmServiceRuntimeException {
        int fromYear = fromDayMoment.getYear();
        int fromMonth = fromDayMoment.getMonth();
        int fromDay = fromDayMoment.getDay();
        int toYear = toDayMoment.getYear();
        int toMonth = toDayMoment.getMonth();
        int toDay = toDayMoment.getDay();

        if(toYear < fromYear){
            logger.error("To Year {} must great than or equal From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }

        String queryCql = null;

        if(fromYear == toYear && fromMonth == toMonth){
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE day.day in range("+fromDay+","+toDay+") RETURN day as operationResult ORDER BY year.year, month.month, day.day";
        }else{
            //for from part day
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE day.day in range("+fromDay+",31) RETURN day as operationResult ORDER BY year.year, month.month, day.day";
            if(fromYear == toYear){
                String monthRange ="range("+(fromMonth+1)+","+(toMonth-1)+")";
                String wholeYearPartQuery = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE month.month in "+monthRange+" RETURN day as operationResult ORDER BY year.year, month.month, day.day";
                queryCql = queryCql +" UNION "+"\n" + wholeYearPartQuery;
            }else{
                //for middle whole years
                int yearSpan = toYear - fromYear;
                if(yearSpan > 1 ){
                    String yearRange ="range("+(fromYear+1)+","+(fromYear+yearSpan-1)+")";
                    String wholeYearPartQuery = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year)-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE year.year in "+yearRange+" RETURN month as operationResult ORDER BY year.year, month.month, day.day";
                    queryCql = queryCql +" UNION "+"\n" + wholeYearPartQuery;
                }
            }
            //for to part day
            if(toYear != fromYear){
                queryCql = queryCql +" UNION "+"\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE month.month in range(1,"+(toMonth-1)+") RETURN day as operationResult ORDER BY year.year, month.month, day.day";
            }
            queryCql = queryCql +" UNION "+"\n" +
                    "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day) WHERE day.day in range(1,"+toDay+") RETURN day as operationResult ORDER BY year.year, month.month, day.day";
        }
        return getListTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity[] getSpecificDayEntities(TimeScaleMoment... dayMoments) {
        String queryCql = buildUnionDayEntitiesCql(dayMoments);
        return getArrayTimeScaleEntity(queryCql);
    }

    private String buildUnionDayEntitiesCql(TimeScaleMoment... monthMoments){
        StringBuffer cqlBuffer = new StringBuffer();
        for(int i = 0; i< monthMoments.length;i++){
            TimeScaleMoment currentTimeScaleMoment = monthMoments[i];
            String queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+currentTimeScaleMoment.getYear()+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+currentTimeScaleMoment.getMonth()+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+currentTimeScaleMoment.getDay()+"}) RETURN day as operationResult";
            cqlBuffer.append(queryCql);
            if(i !=monthMoments.length-1){
                cqlBuffer.append(" UNION"+"\n");
            }
        }
        return cqlBuffer.toString();
    }

    @Override
    public TimeScaleEntity getHourEntity(int year, int month, int day, int hour) {
        String queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+hour+"}) RETURN hour as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment) throws CoreRealmServiceRuntimeException {
        int fromYear = fromHourMoment.getYear();
        int fromMonth = fromHourMoment.getMonth();
        int fromDay = fromHourMoment.getDay();
        int fromHour = fromHourMoment.getHour();
        int toYear = toHourMoment.getYear();
        int toMonth = toHourMoment.getMonth();
        int toDay = toHourMoment.getDay();
        int toHour = toHourMoment.getHour();

        if(toYear < fromYear){
            logger.error("To Year {} must great than or equal From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }

        String queryCql = null;
        if(fromYear == toYear && fromMonth == toMonth && fromDay == toDay) {
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE hour.hour in range("+fromHour+","+toHour+") RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";
        }else {
            //for from part hour
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE hour.hour in range("+fromHour+",23) RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";

            //for middle whole days
            if(fromYear == toYear && fromMonth == toMonth && toDay > fromDay) {
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\"" + getTimeFlowName() + "\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:" + toYear + "})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:" + toMonth + "})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE day.day in range(" + (fromDay + 1) + "," + (toDay - 1) + ") RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";
            }else if(fromYear == toYear && toMonth > fromMonth){
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\"" + getTimeFlowName() + "\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:" + toYear + "})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE month.month in range(" + (fromMonth + 1) + "," + (toMonth - 1) + ") RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";
            }else if(toYear > fromYear){
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\"" + getTimeFlowName() + "\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year)-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE year.year in range(" + (fromYear + 1) + "," + (toYear - 1) + ") RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";
            }

            //for to part hour
            queryCql = queryCql +" UNION "+"\n" +
                "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+toDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour) WHERE hour.hour in range(0,"+toHour+") RETURN hour as operationResult ORDER BY year.year, month.month, day.day, hour.hour\n";
        }
        return getListTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity[] getSpecificHourEntities(TimeScaleMoment... hourMoments) {
        String queryCql = buildUnionHourEntitiesCql(hourMoments);
        return getArrayTimeScaleEntity(queryCql);
    }

    private String buildUnionHourEntitiesCql(TimeScaleMoment... monthMoments){
        StringBuffer cqlBuffer = new StringBuffer();
        for(int i = 0; i< monthMoments.length;i++){
            TimeScaleMoment currentTimeScaleMoment = monthMoments[i];
            String queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+currentTimeScaleMoment.getYear()+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+currentTimeScaleMoment.getMonth()+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+currentTimeScaleMoment.getDay()+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+currentTimeScaleMoment.getHour()+"}) RETURN hour as operationResult";
            cqlBuffer.append(queryCql);
            if(i !=monthMoments.length-1){
                cqlBuffer.append(" UNION"+"\n");
            }
        }
        return cqlBuffer.toString();
    }

    @Override
    public TimeScaleEntity getMinuteEntity(int year, int month, int day, int hour, int minute) {
        String queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+hour+"})-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute{minute:"+minute+"}) RETURN minute as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment) throws CoreRealmServiceRuntimeException {

        int fromYear = fromMinuteMoment.getYear();
        int fromMonth = fromMinuteMoment.getMonth();
        int fromDay = fromMinuteMoment.getDay();
        int fromHour = fromMinuteMoment.getHour();
        int fromMinute = fromMinuteMoment.getMinute();
        int toYear = toMinuteMoment.getYear();
        int toMonth = toMinuteMoment.getMonth();
        int toDay = toMinuteMoment.getDay();
        int toHour = toMinuteMoment.getHour();
        int toMinute = toMinuteMoment.getMinute();

        if(toYear < fromYear){
            logger.error("To Year {} must great than or equal From Year {}.", toYear, fromYear);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("To Year "+toYear+" must great than From Year "+fromYear+".");
            throw exception;
        }

        String queryCql = null;
        if(fromYear == toYear && fromMonth == toMonth && fromDay == toDay && fromHour == toHour) {
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+fromHour+"})-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE minute.minute in range("+fromMinute+","+toMinute+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
        }else{
            //for from part minutes
            queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+fromHour+"})-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE minute.minute in range("+fromMinute+",59) RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";

            //for middle whole minutes
            if(fromYear == toYear && fromMonth == toMonth && fromDay == toDay && toHour > fromHour) {
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE hour.hour in range("+(fromHour+1)+","+(toHour-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
            }else if(fromYear == toYear && fromMonth == toMonth && toDay > fromDay) {
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+fromDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE hour.hour in range("+(fromHour+1)+",23) RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE day.day in range("+(fromDay+1)+","+(toDay-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+toDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE hour.hour in range(0,"+(toHour-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
            }else if(fromYear == toYear && toMonth > fromMonth) {
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE day.day in range("+(fromDay+1)+",31) RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE month.month in range("+(fromMonth+1)+","+(toMonth-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE day.day in range(1,"+(toDay-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+toDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE hour.hour in range(0,"+(toHour-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
            }else if(toYear > fromYear) {
                queryCql = queryCql + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+fromMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE day.day in range("+(fromDay+1)+",31) RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+fromYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE month.month in range("+(fromMonth+1)+",12) RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year)-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE year.year in range("+(fromYear+1)+","+(toYear-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month)-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE month.month in range(1,"+(toMonth-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day)-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE day.day in range(1,"+(toDay-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n"
                        + " UNION " + "\n" +
                        "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+toDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour)-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE hour.hour in range(0,"+(toHour-1)+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
            }

            //for to part minutes
            queryCql = queryCql +" UNION "+"\n" +
                    "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+toYear+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+toMonth+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+toDay+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+toHour+"})-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute) WHERE minute.minute in range(0,"+toMinute+") RETURN minute as operationResult ORDER BY year.year, month.month, day.day, hour.hour, minute.minute\n";
        }
        return getListTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity[] getSpecificMinuteEntities(TimeScaleMoment... minuteMoments) {
        String queryCql = buildUnionMinuteEntitiesCql(minuteMoments);
        return getArrayTimeScaleEntity(queryCql);
    }

    private String buildUnionMinuteEntitiesCql(TimeScaleMoment... monthMoments){
        StringBuffer cqlBuffer = new StringBuffer();
        for(int i = 0; i< monthMoments.length;i++){
            TimeScaleMoment currentTimeScaleMoment = monthMoments[i];
            String queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+getTimeFlowName()+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+currentTimeScaleMoment.getYear()+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+currentTimeScaleMoment.getMonth()+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+currentTimeScaleMoment.getDay()+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+currentTimeScaleMoment.getHour()+"})-[:DOCG_TS_Contains]->(minute:DOCG_TS_Minute{minute:"+currentTimeScaleMoment.getMinute()+"}) RETURN minute as operationResult";
            cqlBuffer.append(queryCql);
            if(i !=monthMoments.length-1){
                cqlBuffer.append(" UNION"+"\n");
            }
        }
        return cqlBuffer.toString();
    }

    @Override
    public TimeScaleEntity getSecondEntity(int year, int month, int day, int hour, int minute, int second) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSpecificSecondEntities(TimeScaleMoment... secondMoments) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getChildEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade) {
        TimeScaleEntity targetEntity = getSpecialTimeScaleEntity(timeScaleMoment,timeScaleGrade);
        if (targetEntity != null){
            return targetEntity.getChildEntities();
        }
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getFellowEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade) {
        TimeScaleEntity targetEntity = getSpecialTimeScaleEntity(timeScaleMoment,timeScaleGrade);
        if (targetEntity != null){
            return targetEntity.getFellowEntities();
        }
        return null;
    }

    @Override
    public InheritanceTree<TimeScaleEntity> getOffspringEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade) {
        TimeScaleEntity targetEntity = getSpecialTimeScaleEntity(timeScaleMoment,timeScaleGrade);
        if (targetEntity != null){
            return targetEntity.getOffspringEntities();
        }
        return null;
    }

    @Override
    public long removeRefersTimeScaleEvents() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String deleteEntitiesCql = "CALL apoc.periodic.commit(\"MATCH (n:"+RealmConstant.TimeScaleEventClass+") WHERE n."+RealmConstant._TimeScaleEventTimeFlow+"='"+this.timeFlowName+"' WITH n LIMIT $limit DETACH DELETE n RETURN count(*)\",{limit: 10000}) YIELD updates, executions, runtime, batches RETURN updates, executions, runtime, batches";
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

    private TimeScaleEntity getSpecialTimeScaleEntity(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade){
        int targetYear = timeScaleMoment.getYear();
        int targetMonth = timeScaleMoment.getMonth();
        int targetDay = timeScaleMoment.getDay();
        int targetHour = timeScaleMoment.getHour();
        int targetMinute = timeScaleMoment.getMinute();
        int targetSecond = timeScaleMoment.getSecond();

        TimeScaleEntity targetTimeScaleEntity = null;

        switch(timeScaleGrade){
            case SECOND:
                targetTimeScaleEntity = getSecondEntity(targetYear,targetMonth,targetDay,targetHour,targetMinute,targetSecond);
                break;
            case MINUTE:
                targetTimeScaleEntity = getMinuteEntity(targetYear,targetMonth,targetDay,targetHour,targetMinute);
                break;
            case HOUR:
                targetTimeScaleEntity = getHourEntity(targetYear,targetMonth,targetDay,targetHour);
                break;
            case DAY:
                targetTimeScaleEntity = getDayEntity(targetYear,targetMonth,targetDay);
                break;
            case MONTH:
                targetTimeScaleEntity = getMonthEntity(targetYear,targetMonth);
                break;
            case YEAR:
                targetTimeScaleEntity = getYearEntity(targetYear);
                break;
        }
        return targetTimeScaleEntity;
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

    private LinkedList<TimeScaleEntity> getListTimeScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
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

    private TimeScaleEntity[] getArrayTimeScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
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
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
