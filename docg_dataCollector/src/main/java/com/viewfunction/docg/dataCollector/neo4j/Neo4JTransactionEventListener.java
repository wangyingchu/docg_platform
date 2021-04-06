package com.viewfunction.docg.dataCollector.neo4j;

import com.viewfunction.docg.dataCollector.neo4j.dataGenerator.CoreRealmDataGenerator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventListener;
import org.neo4j.logging.internal.LogService;

public class Neo4JTransactionEventListener implements TransactionEventListener<Object>{
    GraphDatabaseService db;
    LogService log;

    public Neo4JTransactionEventListener(final GraphDatabaseService graphDatabaseService, final LogService logsvc){
        this.db = graphDatabaseService;
        this.log = logsvc;
    }

    public Object beforeCommit(final TransactionData data, final Transaction transaction, final GraphDatabaseService databaseService){
        return null;
    }

    public void afterCommit(final TransactionData data, final Object state, final GraphDatabaseService databaseService){
        CoreRealmDataGenerator.generateCoreRealmDataPayload(data,state,this.log.getUserLog(String.class));
    }

    public void afterRollback(final TransactionData data, final Object state, final GraphDatabaseService databaseService){
    }
}
