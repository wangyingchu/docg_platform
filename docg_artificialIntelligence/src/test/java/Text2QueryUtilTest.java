import com.docg.ai.llm.naturalLanguageAnalysis.util.Text2QueryUtil;

public class Text2QueryUtilTest {
    public static void main(String[] args){
        //String cql = Text2QueryUtil.generateQueryCypher("论文鬼灭之刃的作者是谁");
        String cql = Text2QueryUtil.generateQueryCypher("周边医疗设施最多的地铁站有哪些，请列举20个，返回地铁站的实体，相同station名称的站点只返回一个");
        System.out.println(cql);
    }
}
