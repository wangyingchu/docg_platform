package engineCommunicationTest

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object EngineCommunicationTestApp extends App{
  val config = ConfigFactory.parseString("""
    akka {
      actor {
        # provider=remote is possible, but prefer cluster
        provider = cluster
        allow-java-serialization = on
      }
      serializers {
              kryo = "com.twitter.chill.akka.AkkaSerializer"
            }
            serialization-bindings {
              "java.io.Serializable" = none
              "scala.Product" = kryo
            }
      remote {
        artery {
          transport = tcp # See Selecting a transport below
          canonical.hostname = "127.0.0.1"
          canonical.port = 9901
        }
      }
    }
     """)
  implicit val system = ActorSystem("LocalTestSystem",config)

  val localActor = system.actorOf(Props[EngineCommunicationTestActor], name = "LocalActor")
  localActor ! "This is string message"
  Thread.sleep(10000)
  system.terminate()
}
