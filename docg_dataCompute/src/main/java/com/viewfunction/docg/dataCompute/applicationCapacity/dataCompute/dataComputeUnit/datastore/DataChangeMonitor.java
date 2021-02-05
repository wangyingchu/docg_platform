package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiPredicate;

import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryUpdatedListener;
import java.util.ArrayList;
import java.util.List;

public class DataChangeMonitor<K, V> {

    private ContinuousQuery<K, V> igniteContinuousQuery;
    private DataChangeActions dataChangeActions;

    public DataChangeMonitor(final GeneralDataMatchCondition<K, V> generalDataMatchCondition, final DataChangeActions dataChangeListener){
        this.igniteContinuousQuery=new ContinuousQuery();
        this.dataChangeActions=dataChangeListener;
        ScanQuery<K, V> scan=new ScanQuery<>(new IgniteBiPredicate<K, V>() {
            @Override public boolean apply(K key, V val) {

                return generalDataMatchCondition.match(key,val);
            }
        });
        this.igniteContinuousQuery.setInitialQuery(scan);
        this.setCacheEntryUpdatedListener(this.dataChangeActions);
        this.setRemoteFilterFactory(this.dataChangeActions);
    }

    public DataChangeMonitor(final SqlQueryCondition sqlQueryCondition, final DataChangeActions dataChangeListener){
        this.igniteContinuousQuery=new ContinuousQuery();
        this.dataChangeActions=dataChangeListener;
        this.igniteContinuousQuery.setInitialQuery(sqlQueryCondition.getIgniteSqlQuery());
        this.setCacheEntryUpdatedListener(this.dataChangeActions);
        this.setRemoteFilterFactory(this.dataChangeActions);
    }

    public DataChangeMonitor(final TextContainsQueryCondition textContainsQueryCondition, final DataChangeActions dataChangeListener){
        this.igniteContinuousQuery=new ContinuousQuery();
        this.dataChangeActions=dataChangeListener;
        this.igniteContinuousQuery.setInitialQuery(textContainsQueryCondition.getIgniteTextQuery());
        this.setCacheEntryUpdatedListener(this.dataChangeActions);
        this.setRemoteFilterFactory(this.dataChangeActions);
    }

    private void setCacheEntryUpdatedListener(final DataChangeActions dataChangeActions){
        CacheEntryUpdatedListener cacheEntryUpdatedListener=new CacheEntryUpdatedListener<Object,Object>() {

            @Override public void onUpdated(Iterable<CacheEntryEvent<? extends Object, ? extends Object>> evts) {
                List<DataObjectChangeRecord> changingDataObjectList=new ArrayList<>();
                for (CacheEntryEvent<? extends Object, ? extends Object> e : evts){
                    changingDataObjectList.add(new DataObjectChangeRecord(e.getKey(),e.getValue(),e.getOldValue(),e.getSource().getName(),e.getEventType().name()));
                }
                dataChangeActions.onDataChangeReceived(changingDataObjectList);
            }
        };
        this.igniteContinuousQuery.setLocalListener(cacheEntryUpdatedListener);
        this.igniteContinuousQuery.setAutoUnsubscribe(true);
    }

    private void setRemoteFilterFactory(final DataChangeActions dataChangeActions){
        Factory remoteFilterFactory=new Factory<CacheEntryEventFilter<Object, Object>>() {
            @Override public CacheEntryEventFilter<Object, Object> create() {
                return new CacheEntryEventFilter<Object, Object>() {
                    @Override public boolean evaluate(CacheEntryEvent<? extends Object, ? extends Object> e) {
                        DataObjectChangeRecord dataObjectChangeRecord=new DataObjectChangeRecord(e.getKey(),e.getValue(),e.getOldValue(),e.getSource().getName(),e.getEventType().name());
                        return dataChangeActions.isNeededDataChange(dataObjectChangeRecord);
                    }
                };
            }
        };
        this.igniteContinuousQuery.setRemoteFilterFactory(remoteFilterFactory);
    }

    public ContinuousQuery<K, V> getIgniteContinuousQuery() {
        return igniteContinuousQuery;
    }
}
