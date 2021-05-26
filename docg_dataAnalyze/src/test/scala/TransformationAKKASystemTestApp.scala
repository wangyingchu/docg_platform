import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object TransformationAKKASystemTestApp extends App{
  val config = ConfigFactory.parseString("""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      serializers {
        kryo = "com.twitter.chill.akka.AkkaSerializer"
      }
      serialization-bindings {
        "java.io.Serializable" = none
        "scala.Product" = kryo
      }
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = 127.0.0.1
          port = 8088
        }
        log-sent-messages = on
        log-received-messages = on
      }
    }
     """)
  implicit val system = ActorSystem("LocalTestSystem",config)

  val localActor = system.actorOf(Props[TransformationAKKASystemTestActor], name = "LocalActor")

  for( a <- 1 to 1000000){
    localActor ! SendNoReturn
  }
}
