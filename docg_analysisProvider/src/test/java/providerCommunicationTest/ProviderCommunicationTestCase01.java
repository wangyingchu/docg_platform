package providerCommunicationTest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.viewfunction.docg.analysisProvider.communication.CommunicationActor;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ProviderCommunicationTestCase01 {
    public static void main(String[] args) {
        String configStr = "akka{" +
                        "actor {"+
                            "provider = cluster," +
                            "allow-java-serialization = on" +
                        "},"+
                        "serializers {" +
                            "kryo = \"com.twitter.chill.akka.AkkaSerializer\"" +
                        "}," +
                        "serialization-bindings {" +
                            "java.io.Serializable = none," +
                            "scala.Product = kryo" +
                        "}," +
                        "remote {" +
                            "artery {" +
                                "transport = tcp," +
                                "canonical.hostname = \"127.0.0.1\"," +
                                "canonical.port = 9901" +
                            "}" +
                        "}," +
                        "loglevel=INFO" +
                "}";
        Config config = ConfigFactory.parseString(configStr);
        ActorSystem actorSystem = ActorSystem.create("EngineCommunicationTestSystem", config);
        ActorRef localCommunicationActor = actorSystem.actorOf(Props.create(CommunicationActor.class), "localCommunicationActor");
        System.out.println("localCommunicationActor path  "+localCommunicationActor.path());

        AnalyzeTreesCrownAreaInSection analyzeTreesCrownAreaInSection = new AnalyzeTreesCrownAreaInSection("treeType001",12345);
        localCommunicationActor.tell(analyzeTreesCrownAreaInSection,localCommunicationActor);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actorSystem.terminate();
    }
}
