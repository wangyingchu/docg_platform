import com.docg.ai.llm.naturalLanguageAnalysis.util.Text2QueryUtil;

public class Text2QueryUtilTest {
    public static void main(String[] args){
        //String cql = Text2QueryUtil.generateQueryCypher("返回连接在一起的水系实体，返回100个");
        String cql = Text2QueryUtil.generateQueryCypher("返回连接在一起的河流系统线和河流系统面实体组成的路径，返回300个结果");
        //String cql = Text2QueryUtil.generateQueryCypher("返回关联地铁线路最多的地铁站实体和关联的地铁线路数量");
        //String cql = Text2QueryUtil.generateQueryCypher("周边医疗设施最多的地铁站有哪些，请列举20个，返回地铁站的实体，相同station名称的站点只返回一个");
        System.out.println(cql);
    }
}
