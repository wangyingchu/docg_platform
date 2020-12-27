package com.viewfunction.docg.realmExample.generator;

import com.google.common.collect.Lists;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SongPlaylists_Realm_Generator {

    private static final String SongConceptionType = "Song";
    private static final String SongId = "songId";
    private static final String SongTitle = "songTitle";
    private static final String SongArtist = "songArtist";

    private static final String MusicTagConceptionType = "MusicTag";
    private static final String TagId = "tagId";
    private static final String TagName = "tagName";

    private static final String PlaylistConceptionType = "Playlist";
    private static final String PlaylistId = "playlistId";
    private static final String PlaylistContent = "playlistContent";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        //Part 1
        ConceptionKind _MusicTagConceptionKind = coreRealm.getConceptionKind(MusicTagConceptionType);
        if(_MusicTagConceptionKind != null){
            coreRealm.removeConceptionKind(MusicTagConceptionType,true);
        }
        _MusicTagConceptionKind = coreRealm.getConceptionKind(MusicTagConceptionType);
        if(_MusicTagConceptionKind == null){
            _MusicTagConceptionKind = coreRealm.createConceptionKind(MusicTagConceptionType,"音乐分类");
        }

        List<ConceptionEntityValue> musicTagEntityValueList = new ArrayList<>();
        File file = new File("realmExampleData/song_playlists/tag_hash.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {

                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split(", ");
                    String musicTagId = dataItems[0].trim();
                    String musicTagName = dataItems[1].trim();
                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(TagId,musicTagId);
                    newEntityValueMap.put(TagName,musicTagName);
                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    musicTagEntityValueList.add(conceptionEntityValue);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        _MusicTagConceptionKind.newEntities(musicTagEntityValueList,false);

        ConceptionKind _SongConceptionKind = coreRealm.getConceptionKind(SongConceptionType);
        if(_SongConceptionKind != null){
            coreRealm.removeConceptionKind(SongConceptionType,true);
        }
        _SongConceptionKind = coreRealm.getConceptionKind(SongConceptionType);
        if(_SongConceptionKind == null){
            _SongConceptionKind = coreRealm.createConceptionKind(SongConceptionType,"歌曲");
        }

        List<ConceptionEntityValue> songEntityValueList = new ArrayList<>();
        File file2 = new File("realmExampleData/song_playlists/song_hash.txt");
        BufferedReader reader2 = null;
        try {
            reader2 = new BufferedReader(new FileReader(file2));
            String tempStr;
            while ((tempStr = reader2.readLine()) != null) {
                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split("\t");
                    String songId = dataItems[0].trim();
                    String songTitle = dataItems[1].trim();
                    String songArtist = dataItems[2].trim();

                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(SongId,songId);
                    newEntityValueMap.put(SongTitle,songTitle);
                    newEntityValueMap.put(SongArtist,songArtist);
                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    songEntityValueList.add(conceptionEntityValue);
                }
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        _SongConceptionKind.newEntities(songEntityValueList,false);

        coreRealm.openGlobalSession();

        ConceptionKind _MusicTagConceptionKind1 = coreRealm.getConceptionKind(MusicTagConceptionType);

        QueryParameters queryParameters1 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem1 = new NullValueFilteringItem(TagId);
        defaultFilterItem1.reverseCondition();
        queryParameters1.setDefaultFilteringItem(defaultFilterItem1);

        ConceptionEntitiesRetrieveResult _MusicTagResult= _MusicTagConceptionKind1.getEntities(queryParameters1);
        Map<String,String> idUIDMapping_MusicTag = new HashMap();
        for(ConceptionEntity currentMusicTagConceptionEntity : _MusicTagResult.getConceptionEntities()){
            String uid = currentMusicTagConceptionEntity.getConceptionEntityUID();
            String idValue = currentMusicTagConceptionEntity.getAttribute(TagId).getAttributeValue().toString();
            idUIDMapping_MusicTag.put(idValue,uid);
        }

        ConceptionKind _SongConceptionKind1 = coreRealm.getConceptionKind(SongConceptionType);

        QueryParameters queryParameters2 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem2 = new NullValueFilteringItem(SongId);
        defaultFilterItem2.reverseCondition();
        queryParameters2.setDefaultFilteringItem(defaultFilterItem2);

        List<String> attributeNamesList0 = new ArrayList<>();
        attributeNamesList0.add(SongId);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult0 = _SongConceptionKind1.getSingleValueEntityAttributesByAttributeNames(attributeNamesList0,queryParameters2);
        List<ConceptionEntityValue> conceptionEntityValueList0 = conceptionEntitiesAttributesRetrieveResult0.getConceptionEntityValues();

        Map<String,String> idUIDMapping_Song = new HashMap();
        for(ConceptionEntityValue currentSongConceptionEntityValue : conceptionEntityValueList0){
            String uid = currentSongConceptionEntityValue.getConceptionEntityUID();
            String idValue = currentSongConceptionEntityValue.getEntityAttributesValue().get(SongId).toString();
            idUIDMapping_Song.put(idValue,uid);
        }

        File file3 = new File("realmExampleData/song_playlists/tags.txt");
        BufferedReader reader3 = null;
        try {
            reader3 = new BufferedReader(new FileReader(file3));
            String tempStr;
            int keyIndex = 0;
            while ((tempStr = reader3.readLine()) != null) {
                String currentSongId = ""+keyIndex;
                keyIndex ++;
                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String songEntityUID = idUIDMapping_Song.get(currentSongId);
                    ConceptionEntity _SongEntity = _SongConceptionKind1.getEntityByUID(songEntityUID);
                    String[] dataItems =  currentLine.split(" ");
                    for(String currentTagId:dataItems){
                        linkSongToTagItem(_SongEntity,idUIDMapping_Song,idUIDMapping_MusicTag,currentTagId);
                    }
                }
            }
            reader3.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader3 != null) {
                try {
                    reader3.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        coreRealm.closeGlobalSession();

        ConceptionKind _PlaylistConceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);
        if(_PlaylistConceptionKind != null){
            coreRealm.removeConceptionKind(PlaylistConceptionType,true);
        }
        _PlaylistConceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);
        if(_PlaylistConceptionKind == null){
            _PlaylistConceptionKind = coreRealm.createConceptionKind(PlaylistConceptionType,"播放列表");
        }

        List<ConceptionEntityValue> playlistEntityValueList = Lists.newArrayList();
        File file4 = new File("realmExampleData/song_playlists/test.txt");
        BufferedReader reader4 = null;
        try {
            reader4 = new BufferedReader(new FileReader(file4));
            String tempStr;
            int keyIndex = 0;
            while ((tempStr = reader4.readLine()) != null) {
                String currentPlayListId = ""+keyIndex;
                keyIndex ++;
                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(PlaylistId,currentPlayListId);
                    newEntityValueMap.put(PlaylistContent,currentLine);
                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    playlistEntityValueList.add(conceptionEntityValue);
                }
            }
            reader4.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader4 != null) {
                try {
                    reader4.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        class InsertRecordThread implements Runnable{
            private List<ConceptionEntityValue> conceptionEntityValueList;
            private ConceptionKind conceptionKind;

            public InsertRecordThread(ConceptionKind conceptionKind,List<ConceptionEntityValue> conceptionEntityValueList){
                this.conceptionEntityValueList = conceptionEntityValueList;
                this.conceptionKind = conceptionKind;
            }
            @Override
            public void run(){
                this.conceptionKind.newEntities(conceptionEntityValueList,false);
            }
        }
        List<List<ConceptionEntityValue>> rsList = Lists.partition(playlistEntityValueList, 10000);

        ExecutorService executor1 = Executors.newFixedThreadPool(rsList.size());

        for (List<ConceptionEntityValue> currentConceptionEntityValueList : rsList) {
            ConceptionKind conceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);
            InsertRecordThread insertRecordThread = new InsertRecordThread(conceptionKind,currentConceptionEntityValueList);
            executor1.execute(insertRecordThread);
        }
        executor1.shutdown();

        //PART2

        coreRealm.openGlobalSession();

        ConceptionKind _SongConceptionKind2 = coreRealm.getConceptionKind(SongConceptionType);
        QueryParameters queryParameters2_1 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem2_1 = new NullValueFilteringItem(SongId);
        defaultFilterItem2_1.reverseCondition();
        queryParameters2_1.setDefaultFilteringItem(defaultFilterItem2_1);

        List<String> attributeNamesList1 = new ArrayList<>();
        attributeNamesList0.add(SongId);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult1 = _SongConceptionKind2.getSingleValueEntityAttributesByAttributeNames(attributeNamesList1,queryParameters2_1);
        List<ConceptionEntityValue> conceptionEntityValueList1 = conceptionEntitiesAttributesRetrieveResult1.getConceptionEntityValues();

        Map<String,String> idUIDMapping_Song2 = new HashMap();
        for(ConceptionEntityValue currentSongConceptionEntityValue : conceptionEntityValueList1){
            String uid = currentSongConceptionEntityValue.getConceptionEntityUID();
            String idValue = currentSongConceptionEntityValue.getEntityAttributesValue().get(SongId).toString();
            idUIDMapping_Song2.put(idValue,uid);
        }

        ConceptionKind _PlaylistConceptionKind1 = coreRealm.getConceptionKind(PlaylistConceptionType);
        QueryParameters queryParameters3 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem3 = new NullValueFilteringItem(PlaylistId);
        defaultFilterItem3.reverseCondition();
        queryParameters3.setDefaultFilteringItem(defaultFilterItem3);
        queryParameters3.setResultNumber(10000000);

        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(PlaylistContent);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult = _PlaylistConceptionKind1.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters3);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();

        Map<String,String> idContentMapping_Playlist = new HashMap();
        for(ConceptionEntityValue currentConceptionEntityValue : conceptionEntityValueList){
            String uid = currentConceptionEntityValue.getConceptionEntityUID();
            String playlistContentValue = currentConceptionEntityValue.getEntityAttributesValue().get(PlaylistContent).toString();
            idContentMapping_Playlist.put(uid,playlistContentValue);
        }
        coreRealm.closeGlobalSession();

        Iterator<Map.Entry<String, String>> iterator = idContentMapping_Playlist.entrySet().iterator();
        Map<String, String> mapThread0 = new HashMap<>();
        Map<String, String> mapThread1 = new HashMap<>();
        Map<String, String> mapThread2 = new HashMap<>();
        Map<String, String> mapThread3 = new HashMap<>();
        Map<String, String> mapThread4 = new HashMap<>();
        Map<String, String> mapThread5 = new HashMap<>();
        Map<String, String> mapThread6 = new HashMap<>();
        Map<String, String> mapThread7 = new HashMap<>();
        Map<String, String> mapThread_default = new HashMap<>();

        List<Map<String, String>> mapThreadList = new ArrayList<>();
        mapThreadList.add(mapThread0);
        mapThreadList.add(mapThread1);
        mapThreadList.add(mapThread2);
        mapThreadList.add(mapThread3);
        mapThreadList.add(mapThread4);
        mapThreadList.add(mapThread5);
        mapThreadList.add(mapThread6);
        mapThreadList.add(mapThread7);
        mapThreadList.add(mapThread_default);

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String.valueOf(key);
            int hashCode = Math.abs(String.valueOf(key).hashCode());

            switch (hashCode % 8) {
                case 0 :
                    mapThread0.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 1 :
                    mapThread1.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 2 :
                    mapThread2.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 3 :
                    mapThread3.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 4 :
                    mapThread4.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 5 :
                    mapThread5.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 6 :
                    mapThread6.put(key, idContentMapping_Playlist.get(key));
                    break;
                case 7 :
                    mapThread7.put(key, idContentMapping_Playlist.get(key));
                    break;
                default:
                    mapThread_default.put(key, idContentMapping_Playlist.get(key));
                    break;
            }
        }

        class LinkPlaylistAndSongThread implements Runnable{

            private Map<String,String> playlistDataMap;
            private Map<String,String> songIdRIDMapping;

            public LinkPlaylistAndSongThread(Map<String,String> playlistDataMap,Map<String,String> songIdRIDMapping){
                this.playlistDataMap = playlistDataMap;
                this.songIdRIDMapping = songIdRIDMapping;
            }

            @Override
            public void run() {
                CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
                coreRealm.openGlobalSession();
                ConceptionKind _PlaylistConceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);

                Set<String> playlistUIDKeySet = this.playlistDataMap.keySet();
                for(String currentUID :playlistUIDKeySet){
                    String uid = currentUID;
                    String playlistContent = this.playlistDataMap.get(uid);

                    String[] dataItems = playlistContent.split(" ");
                    for(String currentSongIdValue:dataItems){
                        String currentSongId = currentSongIdValue.trim();
                        try {
                            ConceptionEntity currentConceptionEntity = _PlaylistConceptionKind.getEntityByUID(uid);
                            String _songEntityUID = songIdRIDMapping.get(currentSongId);
                            if(_songEntityUID != null){
                                currentConceptionEntity.attachToRelation(_songEntityUID,"playedInList",null,false);
                            }

                        } catch (CoreRealmServiceRuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                coreRealm.closeGlobalSession();
            }
        }

        ExecutorService executor2 = Executors.newFixedThreadPool(mapThreadList.size());
        for(Map<String, String> currentMapThread:mapThreadList){
            LinkPlaylistAndSongThread linkPlaylistAndSongThread1 = new LinkPlaylistAndSongThread(currentMapThread,idUIDMapping_Song2);
            executor2.execute(linkPlaylistAndSongThread1);
        }
        executor2.shutdown();
    }

    private static void linkSongToTagItem(ConceptionEntity _SongEntity,Map<String,String> idUIDMapping_Song,Map<String,String> idUIDMapping_MusicTag,
                                String musicTagId) throws CoreRealmServiceRuntimeException {
        String _musicTagEntityUID = idUIDMapping_MusicTag.get(musicTagId);
        if(_musicTagEntityUID != null){
            _SongEntity.attachFromRelation(_musicTagEntityUID,"belongsToMusicType",null,false);
        }
    }
}
