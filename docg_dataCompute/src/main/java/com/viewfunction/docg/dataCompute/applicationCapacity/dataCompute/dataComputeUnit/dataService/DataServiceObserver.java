package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

public class DataServiceObserver implements AutoCloseable{

    private IgniteClient igniteClient;
    public void openObserveSession(){
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10800").setPartitionAwarenessEnabled(true);
        this.igniteClient = Ignition.startClient(cfg);
    }

    public void closeObserveSession(){
        if(this.igniteClient != null){
            this.igniteClient.close();
        }
    }

    private DataServiceObserver(){}

    public static DataServiceObserver getObserverInstance(){
        DataServiceObserver dataServiceObserver = new DataServiceObserver();
        dataServiceObserver.openObserveSession();
        return dataServiceObserver;
    }

    @Override
    public void close() throws Exception {
        closeObserveSession();
    }
}
