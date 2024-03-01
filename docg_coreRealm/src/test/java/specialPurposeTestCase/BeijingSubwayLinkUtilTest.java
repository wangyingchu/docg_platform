package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.IOException;
import java.util.*;

public class BeijingSubwayLinkUtilTest {

    public static void main0(String[] args) throws IOException, CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {

        //CoordinateReferenceSystem crs = GeospatialOperationUtil.detectSHPFileCRSInfo(new File("/home/wangychu/Desktop/北京市/北京市.shp"),"UTF-8");
        //CoordinateReferenceSystem crs = GeospatialOperationUtil.detectSHPFileCRSInfo(new File("/home/wangychu/Desktop/beijingroad001/beijingroad001.shp"),"UTF-8");

        //System.out.println(crs.getName());

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        ConceptionKind lineKind = coreRealm.getConceptionKind("SubWay-Line");

        //Set<ConceptionEntity> targetEntities = lineKind.getRandomEntities(1);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000);
        queryParameters.setDefaultFilteringItem(new EqualFilteringItem("Line","8号线"));

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = lineKind.getEntities(queryParameters);
        List<ConceptionEntity> conceptionEntityList = conceptionEntitiesRetrieveResult.getConceptionEntities();

        for(ConceptionEntity currentLine:conceptionEntityList){
            System.out.println(currentLine);
            System.out.println(currentLine.getAttribute("Line").getAttributeValue());
            List<ConceptionEntity> stationEntitiesList = currentLine.getSpatialPredicateMatchedConceptionEntities("SubWay-Station",null, GeospatialScaleCalculable.SpatialPredicateType.Intersects, GeospatialScaleCalculable.SpatialScaleLevel.Global);
            System.out.println(stationEntitiesList);
            for(ConceptionEntity currentConceptionEntity:stationEntitiesList){
                System.out.println(currentConceptionEntity.getAttribute("station").getAttributeValue());
                //currentLine.attachToRelation(currentConceptionEntity.getConceptionEntityUID(),"CanAccessSubWayLine",null,false);
            }
        }

        coreRealm.closeGlobalSession();
    }

    public static void main(String[] args) throws IOException, CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        ConceptionKind lineKind = coreRealm.getConceptionKind("SubWay-Station");

        HashMap<String,List<ConceptionEntity>> stationNameMapping = new HashMap<>();

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000);

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = lineKind.getEntities(queryParameters);
        List<ConceptionEntity> conceptionEntityList = conceptionEntitiesRetrieveResult.getConceptionEntities();
        System.out.println(conceptionEntityList.size());
        for(ConceptionEntity currentStation:conceptionEntityList){
            System.out.println(currentStation);
            System.out.println(currentStation.getAttribute("station").getAttributeValue());

            String stationName = currentStation.getAttribute("station").getAttributeValue().toString();
            if(!stationNameMapping.containsKey(stationName)){
                stationNameMapping.put(stationName,new ArrayList<>());
            }
            stationNameMapping.get(stationName).add(currentStation);
        }

        //System.out.println(stationNameMapping);

        String[] line1Array = new String[]{"苹果园","古城","八角游乐园","八宝山","玉泉路","五棵松","万寿路","公主坟","军事博物馆","木樨地","南礼士路","复兴门","西单","天安门西","天安门东","王府井","东单","建国门","永安里","国贸","大望路","四惠","四惠东","高碑店","传媒大学","双桥","管庄","八里桥","通州北苑","果园","九棵树","梨园","临河里","土桥","花庄","环球度假区"};
        linkLineData(line1Array,stationNameMapping,"1号线/八通线");
        String[] line2Array = new String[]{"西直门","积水潭","鼓楼大街","安定门","雍和宫","东直门","东四十条","朝阳门","建国门","北京站","崇文门","前门","和平门","宣武门","长椿街","复兴门","阜成门","车公庄","西直门"};//环线
        linkLineData(line2Array,stationNameMapping,"2号线");
        String[] line4Array = new String[]{"天宫院","生物医药基地","义和庄","黄村火车站","黄村西大街","清源路","枣园","高米店南","高米店北","西红门","新宫","公益西桥","角门西","马家堡","北京南站","陶然亭","菜市口","宣武门","西单","灵境胡同","西四","平安里","新街口","西直门","动物园","国家图书馆","魏公村","人民大学","海淀黄庄","中关村","北京大学东门","圆明园","西苑","北宫门","安河桥北"};
        linkLineData(line4Array,stationNameMapping,"4号线");
        String[] line5Array = new String[]{"宋家庄","刘家窑","蒲黄榆","天坛东门","磁器口","崇文门","东单","灯市口","东四","张自忠路","北新桥","雍和宫","和平里北街","和平西桥","惠新西街南口","惠新西街北口","大屯路东","北苑路北","立水桥南","立水桥","天通苑南","天通苑","天通苑北"};
        linkLineData(line5Array,stationNameMapping,"5号线");
        String[] line6Array = new String[]{"金安桥","苹果园","杨庄","西黄村","廖公庄","田村","海淀五路居","慈寿寺","花园桥","白石桥南","二里沟","车公庄西","车公庄","平安里","北海北","南锣鼓巷","东四","朝阳门","东大桥","呼家楼","金台路","十里堡","青年路","褡裢坡","黄渠","常营","草房","物资学院路","通州北关","通运门","北运河西","北运河东","郝家府","东夏园","潞城"};
        linkLineData(line6Array,stationNameMapping,"6号线");
        String[] line7Array = new String[]{"北京西站","湾子","达官营","广安门内","菜市口","虎坊桥","珠市口","桥湾","磁器口","广渠门内","广渠门外","双井","九龙山","大郊亭","百子湾","化工","南楼梓庄","欢乐谷景区","垡头","双合","焦化厂","黄厂","郎辛庄","黑庄户","万盛西","万盛东","群芳","高楼金","花庄","环球度假区"};
        linkLineData(line7Array,stationNameMapping,"7号线");
        String[] line8Array = new String[]{"朱辛庄","育知路","平西府","回龙观东大街","霍营","育新","西小口","永泰庄","林萃桥","森林公园南门","奥林匹克公园","奥体中心","北土城","安华桥","安德里北街","鼓楼大街","什刹海","南锣鼓巷","中国美术馆","金鱼胡同","王府井","前门","珠市口","天桥","永定门外","木樨园","海户屯","大红门南","和义","东高地","火箭万源","五福堂","德茂","瀛海"};
        linkLineData(line8Array,stationNameMapping,"8号线");
        String[] line9Array = new String[]{"郭公庄","丰台科技园","科怡路","丰台南路","丰台东大街","七里庄","六里桥","六里桥东","北京西站","军事博物馆","白堆子","白石桥南","国家图书馆"};
        linkLineData(line9Array,stationNameMapping,"9号线");
        String[] line10Array = new String[]{"巴沟","苏州街","海淀黄庄","知春里","知春路","西土城","牡丹园","健德门","北土城","安贞门","惠新西街南口","芍药居","太阳宫","三元桥","亮马桥","农业展览馆","团结湖","呼家楼","金台夕照","国贸","双井","劲松","潘家园","十里河","分钟寺","成寿寺","宋家庄","石榴庄","大红门","角门东","角门西","草桥","纪家庙","首经贸","丰台站","泥洼","西局","六里桥","莲花桥","公主坟","西钓鱼台","慈寿寺","车道沟","长春桥","火器营","巴沟"};//环线
        linkLineData(line10Array,stationNameMapping,"10号线");
        //String[] line11Array = new String[]{"模式口","金安桥","北辛安","新首钢"}; 无此数据
        String[] line13Array = new String[]{"西直门","大钟寺","知春路","五道口","上地","清河","西二旗","龙泽","回龙观","霍营","立水桥","北苑","望京西","芍药居","光熙门","柳芳","东直门"};
        linkLineData(line13Array,stationNameMapping,"13号线");
        String[] line14Array = new String[]{"张郭庄","园博园","大瓦窑","郭庄子","大井","七里庄","西局","东管头","丽泽商务区","菜户营","西铁营","景风门","北京南站","陶然桥","永定门外","景泰","蒲黄榆","方庄","十里河","北工大西门","平乐园","九龙山","大望路","红庙","金台路","朝阳公园","枣营","东风北桥","将台","高家园","望京南","阜通","望京","东湖渠","来广营","善各庄"};
        linkLineData(line14Array,stationNameMapping,"14号线");
        String[] line15Array = new String[]{"俸伯","顺义","石门","南法信","后沙峪","花梨坎","国展","孙河","马泉营","崔各庄","望京东","望京","望京西","关庄","大屯路东","安立路","奥林匹克公园","北沙滩","六道口","清华东路西口"};
        linkLineData(line15Array,stationNameMapping,"15号线");
        String[] line16Array = new String[]{"北安河","温阳路","稻香湖路","屯佃","永丰","永丰南","西北旺","马连洼","农大南路","西苑","万泉河桥","苏州街","苏州桥","万寿寺","国家图书馆","二里沟","甘家口"};
        linkLineData(line16Array,stationNameMapping,"16号线");
        String[] line17Array = new String[]{"亦庄站前区南","次渠","次渠北","北神树", "朝阳港","十八里店","十里河","潘家园西","广渠门外","永安里","东大桥","工人体育场","香河园","西坝河","太阳宫","望京西","勇士营","清河营","天通苑东","未来科技城南","未来科技城北"};
        linkLineData(line17Array,stationNameMapping,"17号线");
        String[] line1昌平线Array = new String[]{"昌平西山口","十三陵景区","昌平","昌平东关","北邵洼","南邵","沙河高教园","沙河","巩华城","朱辛庄","生命科学园","西二旗","清河","上清桥","学清路","六道口","学院桥","西土城","蓟门桥"};
        linkLineData(line1昌平线Array,stationNameMapping,"昌平线");
        String[] line1亦庄线Array = new String[]{"宋家庄","肖村","小红门","旧宫","亦庄桥","亦庄文化园","万源街","荣京东街","荣昌东街","同济南路","经海路","次渠南","次渠","亦庄火车站"};
        linkLineData(line1亦庄线Array,stationNameMapping,"亦庄线");
        String[] line1房山线Array = new String[]{"郭公庄","大葆台","稻田","长阳","篱笆房","广阳城","良乡大学城北","良乡大学城","良乡大学城西","良乡南关","苏庄","阎村东"};
                        //new String[]{"东管头南","首经贸","花乡东桥","白盆窑","郭公庄","大葆台","稻田","长阳","篱笆房","广阳城","良乡大学城北","良乡大学城","良乡大学城西","良乡南关","苏庄","阎村东"};
        linkLineData(line1房山线Array,stationNameMapping,"房山线");
        String[] line1首都机场线Array = new String[]{"北新桥","东直门","三元桥","T3航站楼","T2航站楼"};
        linkLineData(line1首都机场线Array,stationNameMapping,"首都机场线");
        String[] line1S1线Array = new String[]{"苹果园","金安桥","四道桥","桥户营","上岸","栗园庄","小园","石厂"};
        linkLineData(line1S1线Array,stationNameMapping,"S1线");
        String[] line1西郊线Array = new String[]{"香山","植物园","万安","茶棚","颐和园西门","巴沟"};
        linkLineData(line1西郊线Array,stationNameMapping,"西郊线");
        String[] line1大兴机场线Array = new String[]{"大兴机场","大兴新城","草桥"};
        linkLineData(line1大兴机场线Array,stationNameMapping,"大兴机场线");
        String[] line1燕房线Array = new String[]{"燕山","房山城关","饶乐府","马各庄","大石河东","星城","阎村","紫草坞","阎村东"};
        linkLineData(line1燕房线Array,stationNameMapping,"燕房线");

        coreRealm.closeGlobalSession();
    }


    private static void linkLineData(String[] line1NameArray,HashMap<String,List<ConceptionEntity>> stationNameMapping,String lineName) throws CoreRealmServiceRuntimeException {

        String[] currentNameArray = line1NameArray;
        Collections.reverse(Arrays.asList(currentNameArray));

        for(int i=0;i<line1NameArray.length;i++){
            String currentStation = line1NameArray[i];
            System.out.println(currentStation);
            //System.out.println(stationNameMapping.get(currentStation));
            if(i!=line1NameArray.length-1){
                String nextStation = line1NameArray[i+1];
                System.out.println(nextStation);
                //System.out.println(stationNameMapping.get(nextStation));
                linkStations(stationNameMapping.get(currentStation),stationNameMapping.get(nextStation),lineName);
            }
            //System.out.println("=================================");
        }

        Collections.reverse(Arrays.asList(currentNameArray));
        //System.out.println(currentNameArray);
        for(int i=0;i<line1NameArray.length;i++){
            String currentStation = line1NameArray[i];
            //System.out.println(currentStation);
            //System.out.println(stationNameMapping.get(currentStation));
            if(i!=line1NameArray.length-1){
                String nextStation = line1NameArray[i+1];
                //System.out.println(nextStation);
                //System.out.println(stationNameMapping.get(nextStation));
                linkStations(stationNameMapping.get(currentStation),stationNameMapping.get(nextStation),lineName);
            }
            //System.out.println("=================================");
        }
    }

    private static void linkStations(List<ConceptionEntity> fromStations,List<ConceptionEntity> toStations,String lineName) throws CoreRealmServiceRuntimeException {
        for(ConceptionEntity currentFromStation:fromStations){
            List<String> nextStationUIDList = new ArrayList<>();
            for(ConceptionEntity currentNextStation:toStations){
                nextStationUIDList.add(currentNextStation.getConceptionEntityUID());
            }
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("lineName",lineName);
            currentFromStation.attachFromRelation(nextStationUIDList,"ConnectedBySubWayLine",dataMap,false);
        }
    }

    private static void linkSameLocationStations(HashMap<String,List<ConceptionEntity>> stationNameMapping){
        Collection<List<ConceptionEntity>> sameLocationStations =  stationNameMapping.values();
        for(List<ConceptionEntity> currentStationsList : sameLocationStations){
            if(currentStationsList.size()>1){

            }
        }
    }
}
