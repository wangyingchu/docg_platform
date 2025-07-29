import com.docg.ai.llm.naturalLanguageAnalysis.util.Text2QueryUtil;

public class Text2QueryUtilTest {
    public static void main(String[] args){
        String cql = Text2QueryUtil.generateQueryCypher("论文鬼灭之刃的作者是谁");
        System.out.println(cql);
    }
}
