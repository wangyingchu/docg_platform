import com.docg.ai.llm.naturalLanguageAnalysis.util.Text2QueryUtil;

public class Text2QueryUtilTest {
    public static void main(String[] args){
        //String cql = Text2QueryUtil.generateQueryCypher("返回连接在一起的水系实体，返回100个");
        //String cql = Text2QueryUtil.generateQueryCypher("不考虑连接方向，返回通过多跳连接在一起的河流系统线和河流系统面实体组成的路径，返回300个结果");
        //String cql = Text2QueryUtil.generateQueryCypher("返回关联地铁线路最多的地铁站实体和关联的地铁线路数量");
        //String cql = Text2QueryUtil.generateQueryCypher("返回可以通往地址在三丰北里2号楼悠唐生活广场F3的公司的地铁站实体");
        String cql = Text2QueryUtil.generateQueryCypher("返回从地铁站西二旗到达可以前往地址在三丰北里2号楼悠唐生活广场F3的公司的地铁站实体的最短路径，只返回1条路径，请使用WHERE条件执行地铁站过滤");
        //String cql = Text2QueryUtil.generateQueryCypher("周边医疗设施最多的地铁站有哪些，请列举20个，返回地铁站的实体，相同station名称的站点只返回一个");
        System.out.println(cql);
    }
}
