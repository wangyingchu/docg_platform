package com.viewfunction.docg.realmExample.generator;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
/*
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

        ConceptionEntitiesRetrieveResult _SongResult= _SongConceptionKind1.getEntities(queryParameters2);
        Map<String,String> idUIDMapping_Song = new HashMap();
        for(ConceptionEntity currentSongConceptionEntity : _SongResult.getConceptionEntities()){
            String uid = currentSongConceptionEntity.getConceptionEntityUID();
            String idValue = currentSongConceptionEntity.getAttribute(SongId).getAttributeValue().toString();
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
*/

        coreRealm.openGlobalSession();

        ConceptionKind _SongConceptionKind2 = coreRealm.getConceptionKind(SongConceptionType);
        QueryParameters queryParameters2_1 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem2_1 = new NullValueFilteringItem(SongId);
        defaultFilterItem2_1.reverseCondition();
        queryParameters2_1.setDefaultFilteringItem(defaultFilterItem2_1);

        ConceptionEntitiesRetrieveResult _SongResult2= _SongConceptionKind2.getEntities(queryParameters2_1);
        Map<String,String> idUIDMapping_Song2 = new HashMap();
        for(ConceptionEntity currentSongConceptionEntity : _SongResult2.getConceptionEntities()){
            String uid = currentSongConceptionEntity.getConceptionEntityUID();
            String idValue = currentSongConceptionEntity.getAttribute(SongId).getAttributeValue().toString();
            idUIDMapping_Song2.put(idValue,uid);
        }

        ConceptionKind _PlaylistConceptionKind1 = coreRealm.getConceptionKind(PlaylistConceptionType);
        QueryParameters queryParameters3 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem3 = new NullValueFilteringItem(PlaylistId);
        defaultFilterItem3.reverseCondition();
        queryParameters3.setDefaultFilteringItem(defaultFilterItem3);
        queryParameters3.setResultNumber(10000000);

        ConceptionEntitiesRetrieveResult _PlaylistResult = _PlaylistConceptionKind1.getEntities(queryParameters3);
        List<ConceptionEntity> allPlaylistResultList = _PlaylistResult.getConceptionEntities();

        class LinkPlaylistRecordThread implements Runnable{
            private List<ConceptionEntity> conceptionEntityList;

            public LinkPlaylistRecordThread(List<ConceptionEntity> conceptionEntityList){
                this.conceptionEntityList = conceptionEntityList;
            }
            @Override
            public void run(){
               for(ConceptionEntity currentConceptionEntity:conceptionEntityList){
                String playlistContent = currentConceptionEntity.getAttribute(PlaylistContent).getAttributeValue().toString();

                String[] dataItems = playlistContent.split(" ");
                for(String currentSongIdValue:dataItems){
                    String currentSongId = currentSongIdValue.trim();
                    try {
                        linkPlaylistToSongItem(currentConceptionEntity,idUIDMapping_Song2,currentSongId);
                    } catch (CoreRealmServiceRuntimeException e) {
                        e.printStackTrace();
                    }
                }
               }
            }
        }

        List<List<ConceptionEntity>> playlistEntityRsList = Lists.partition(allPlaylistResultList, 10000);

        ExecutorService executor2 = Executors.newFixedThreadPool(playlistEntityRsList.size());

        for (List<ConceptionEntity> currentConceptionEntityList : playlistEntityRsList) {
            LinkPlaylistRecordThread linkPlaylistRecordThread = new LinkPlaylistRecordThread(currentConceptionEntityList);
            executor2.execute(linkPlaylistRecordThread);
        }
        executor2.shutdown();

        coreRealm.closeGlobalSession();
    }

    private static void linkSongToTagItem(ConceptionEntity _SongEntity,Map<String,String> idUIDMapping_Song,Map<String,String> idUIDMapping_MusicTag,
                                String musicTagId) throws CoreRealmServiceRuntimeException {
        String _musicTagEntityUID = idUIDMapping_MusicTag.get(musicTagId);
        if(_musicTagEntityUID != null){
            _SongEntity.attachFromRelation(_musicTagEntityUID,"belongsToMusicType",null,false);
        }
    }

    private static void linkPlaylistToSongItem(ConceptionEntity _PlaylistEntity,Map<String,String> idUIDMapping_Song,String songId) throws CoreRealmServiceRuntimeException {
        String _songEntityUID = idUIDMapping_Song.get(songId);
        if(_songEntityUID != null){
            //System.out.println("DOLINK..................");
            _PlaylistEntity.attachToRelation(_songEntityUID,"playedInList",null,false);
        }
    }
}
