package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificationSessionExample {


    public static void main(String[] args) throws CoreRealmServiceRuntimeException {
        manner1();
        //manner2();
    }

    public static void manner1() throws CoreRealmServiceRuntimeException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();

        String classificationName01 = "classification1";
        Classification _Classification01 = coreRealm.getClassification(classificationName01);

        Assert.assertFalse(coreRealm.removeClassification(null));
        if(_Classification01 != null){
            coreRealm.removeClassificationWithOffspring(classificationName01);
        }
        coreRealm.createClassification(classificationName01,classificationName01+"Desc");

        for(int i=0;i<100;i++){
            Classification currentChildClassification = coreRealm.createClassification("childClassificationName"+i,classificationName01+"Desc",classificationName01);
            currentChildClassification.addAttribute("color","color"+i);
        }

        Classification classification = coreRealm.getClassification("classification1");
        List<Classification> childClassificationList = classification.getChildClassifications();

        for (Classification childClassification : childClassificationList) {
          //String color = childClassification.getAttribute("color").getAttributeValue().toString();
            String color = "";
            List<AttributeValue> AttributeValues = childClassification.getAttributes();
            for (AttributeValue attributeValue : AttributeValues) {
                String attributeValueName = attributeValue.getAttributeName();
                if ("color".equals(attributeValueName)) {
                    color = attributeValue.getAttributeValue().toString();
                    System.out.println(color);
                    break;
                }
            }
            String childClassificationName = childClassification.getClassificationName();
        }
        coreRealm.closeGlobalSession();
    }


    public static void manner2() throws CoreRealmServiceRuntimeException {

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        ((Neo4JCoreRealmImpl)coreRealm).setGlobalGraphOperationExecutor(graphOperationExecutor);

        String classificationName01 = "classification1";
        Classification _Classification01 = coreRealm.getClassification(classificationName01);

        Assert.assertFalse(coreRealm.removeClassification(null));
        if(_Classification01 != null){
            coreRealm.removeClassificationWithOffspring(classificationName01);
        }
        coreRealm.createClassification(classificationName01,classificationName01+"Desc");

        for(int i=0;i<100;i++){
            Classification currentChildClassification = coreRealm.createClassification("childClassificationName"+i,classificationName01+"Desc",classificationName01);
            currentChildClassification.addAttribute("color","color"+i);
        }

        Classification classification = coreRealm.getClassification("classification1");
        List<Classification> childClassificationList = classification.getChildClassifications();

        for (Classification childClassification : childClassificationList) {
            //String color = childClassification.getAttribute("color").getAttributeValue().toString();
            String color = "";
            List<AttributeValue> AttributeValues = childClassification.getAttributes();
            for (AttributeValue attributeValue : AttributeValues) {
                String attributeValueName = attributeValue.getAttributeName();
                if ("color".equals(attributeValueName)) {
                    color = attributeValue.getAttributeValue().toString();
                    System.out.println(color);
                    break;
                }
            }
            String childClassificationName = childClassification.getClassificationName();
        }

        graphOperationExecutor.close();
    }

    public Map<String, String> getZoneColor(String zone) {
        Map<String, String> map = new HashMap<>();
        char[] ch = zone.toCharArray();
        ch[0] = (char) (ch[0] - 32);
        String classificationName = new String(ch);

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        Classification classification = coreRealm.getClassification(classificationName);
        if (null == classification) {
            return map;
        }
        List<Classification> childClassificationList = classification.getChildClassifications();

        for (Classification childClassification : childClassificationList) {
//            String color = childClassification.getAttribute("color").getAttributeValue().toString();
            String color = "";
            List<AttributeValue> AttributeValues = childClassification.getAttributes();
            for (AttributeValue attributeValue : AttributeValues) {
                String attributeValueName = attributeValue.getAttributeName();
                if ("color".equals(attributeValueName)) {
                    color = attributeValue.getAttributeValue().toString();
                    break;
                }
            }
            String childClassificationName = childClassification.getClassificationName();
            map.put(childClassificationName, color);
        }
        return map;
    }

}
