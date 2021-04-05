package com.viewfunction.docg.dataCollector.neo4j;

import org.apache.commons.io.FileUtils;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventListener;

import java.io.File;
import java.io.IOException;

public class coreRealmDataGenerator implements TransactionEventListener {

    private static final String DEFAULT_DATABASE_NAME =
            "ssssssssssss";

    public static void main(String[] args ) throws IOException
    {
        File HOME_DIRECTORY = new File("ddd");
        FileUtils.deleteDirectory( HOME_DIRECTORY );
        var managementService = new DatabaseManagementServiceBuilder( HOME_DIRECTORY ).build();
        var database = managementService.database( DEFAULT_DATABASE_NAME );

        var countingListener = new coreRealmDataGenerator();
        managementService.registerTransactionEventListener( DEFAULT_DATABASE_NAME, countingListener );

        var connectionType = RelationshipType.withName( "CONNECTS" );
        try ( var transaction = database.beginTx() )
        {
            var startNode = transaction.createNode();
            var endNode = transaction.createNode();
            startNode.createRelationshipTo( endNode, connectionType );
            transaction.commit();
        }
    }

    @Override
    public Object beforeCommit(TransactionData transactionData, Transaction transaction, GraphDatabaseService graphDatabaseService) throws Exception {
        return null;
    }

    @Override
    public void afterCommit(TransactionData transactionData, Object o, GraphDatabaseService graphDatabaseService) {
        transactionData.createdNodes();
        transactionData.deletedNodes();
        transactionData.createdRelationships();
        transactionData.deletedRelationships();
        System.out.println( "Number of created nodes: " + o );
    }

    @Override
    public void afterRollback(TransactionData transactionData, Object o, GraphDatabaseService graphDatabaseService) {

    }
}
