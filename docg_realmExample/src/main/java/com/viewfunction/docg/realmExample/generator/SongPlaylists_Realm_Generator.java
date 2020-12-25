package com.viewfunction.docg.realmExample.generator;

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
*/
/*
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
*/
/*
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
*/







        ConceptionKind _PlaylistConceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);
        if(_PlaylistConceptionKind != null){
            coreRealm.removeConceptionKind(PlaylistConceptionType,true);
        }
        _PlaylistConceptionKind = coreRealm.getConceptionKind(PlaylistConceptionType);
        if(_PlaylistConceptionKind == null){
            _PlaylistConceptionKind = coreRealm.createConceptionKind(PlaylistConceptionType,"播放列表");
        }

        List<ConceptionEntityValue> playlistEntityValueList = new ArrayList<>();
        File file4 = new File("realmExampleData/song_playlists/test.txt");
        BufferedReader reader3 = null;
        try {
            reader3 = new BufferedReader(new FileReader(file4));
            String tempStr;
            int keyIndex = 0;
            while ((tempStr = reader3.readLine()) != null) {
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


        _PlaylistConceptionKind.newEntities(playlistEntityValueList,false);

















    }

    private static void linkSongToTagItem(ConceptionEntity _SongEntity,Map<String,String> idUIDMapping_Song,Map<String,String> idUIDMapping_MusicTag,
                                String musicTagId) throws CoreRealmServiceRuntimeException {
        String _musicTagEntityUID = idUIDMapping_MusicTag.get(musicTagId);
        if(_musicTagEntityUID != null){
            _SongEntity.attachFromRelation(_musicTagEntityUID,"belongsToMusicType",null,false);
        }
    }
}
