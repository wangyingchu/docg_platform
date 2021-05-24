package sample.cluster.transformation

import language.postfixOps
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object TransformationBackendApp {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "2553" else args(0)
    //val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
    //  withFallback(ConfigFactory.load())

    val config = ConfigFactory.parseString("""
      akka {
  actor {
    provider = "akka.cluster.ClusterActorRefProvider"
  }
  remote {
    log-remote-lifecycle-events = off
    netty.tcp {
      hostname = "127.0.0.1"
      port = 0
    }
  }

  cluster {
    seed-nodes = [
      "akka.tcp://ClusterSystem@127.0.0.1:2551"]

    roles = [backend]
    #//#snippet
    # excluded from snippet
    auto-down-unreachable-after = 10s
    #//#snippet
    # auto downing is NOT safe for production deployments.
    # you may want to use it during development, read more about it in the docs.
    #
  }

}
     """).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[TransformationBackend], name = "backend")
  }
}