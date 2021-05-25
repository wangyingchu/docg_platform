package sample.remote.transformation

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

object LocalApp extends App {


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
  implicit val system = ActorSystem("LocalDemoSystem",config)

  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")

  localActor ! Init
  //localActor ! SendNoReturn

  //localActor ! SendNoReturn
  //localActor ! SendNoReturn
  //localActor ! SendNoReturn
  //localActor ! SendNoReturn
  //localActor ! SendHasReturn

  //localActor ! SendNoReturn
  localActor ! SendNoReturn
  /*
  for( a <- 1 to 1000000){
    localActor ! SendNoReturn
  }
  */

  //implicit val timeout = Timeout(10.seconds)

  //system.terminate()
}
