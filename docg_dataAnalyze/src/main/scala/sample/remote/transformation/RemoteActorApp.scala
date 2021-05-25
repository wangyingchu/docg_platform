package sample.remote.transformation

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object RemoteActorApp extends App{

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
            port = 8084
          }
          log-sent-messages = on
          log-received-messages = on
        }
      }
     """)
  val system = ActorSystem("RemoteDemoSystem",config)
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  //remoteActor ! "The RemoteActor is alive"
}
