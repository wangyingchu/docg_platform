package com.viewfunction.docg.dataCollector.neo4j;

import org.neo4j.annotations.service.ServiceProvider;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.kernel.availability.AvailabilityGuard;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.Log;
import org.neo4j.logging.internal.LogService;

@ServiceProvider
public class Neo4JTransactionEventListenerExtensionFactory extends ExtensionFactory<Neo4JTransactionEventListenerExtensionFactory.Dependencies>{

    public Neo4JTransactionEventListenerExtensionFactory(){
        super(ExtensionType.DATABASE, "neo4JTransactionEventHandler");
    }

    @Override
    public Lifecycle newInstance(final ExtensionContext extensionContext, final Dependencies dependencies){
        final GraphDatabaseAPI db = dependencies.graphdatabaseAPI();
        final LogService log = dependencies.log();
        final DatabaseManagementService databaseManagementService = dependencies.databaseManagementService();
        return new CustomGraphDatabaseLifecycle(log, db, dependencies, databaseManagementService);
    }

    interface Dependencies {
        GraphDatabaseAPI graphdatabaseAPI();
        DatabaseManagementService databaseManagementService();
        AvailabilityGuard availabilityGuard();
        LogService log();
    }

    public static class CustomGraphDatabaseLifecycle extends LifecycleAdapter{
        private final GraphDatabaseAPI db;
        private final Dependencies dependencies;
        private Neo4JTransactionEventListener transactionEventHandler;
        private LogService log;
        private final DatabaseManagementService databaseManagementService;

        public CustomGraphDatabaseLifecycle(final LogService log, final GraphDatabaseAPI db, final Dependencies dependencies,final DatabaseManagementService databaseManagementService){
            this.log = log;
            this.db = db;
            this.dependencies = dependencies;
            this.databaseManagementService = databaseManagementService;
        }

        @Override
        public void start(){
            if (this.db.databaseName().compareTo("system") != 0){
                Log userLog = this.log.getUserLog(String.class);
                userLog.info("-> Register CoreRealm Data Collector Listener for Database: "+this.db.databaseName());
                this.transactionEventHandler = new Neo4JTransactionEventListener(this.db, this.log);
                this.databaseManagementService.registerTransactionEventListener(this.db.databaseName(), this.transactionEventHandler);
            }
        }

        @Override
        public void shutdown(){
            if (this.db.databaseName().compareTo("system") != 0){
                Log userLog = this.log.getUserLog(String.class);
                userLog.info("-> Unregister CoreRealm Data Collector Listener for Database: "+this.db.databaseName());
                this.databaseManagementService.unregisterTransactionEventListener(this.db.databaseName(), this.transactionEventHandler);
            }
        }
    }
}

