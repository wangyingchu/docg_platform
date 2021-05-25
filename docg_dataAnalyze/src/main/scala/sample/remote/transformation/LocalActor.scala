package sample.remote.transformation

import akka.actor.Actor
import akka.pattern.extended.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

case object Init
case object SendNoReturn

case object SendHasReturn

class LocalActor extends Actor{

  val path = "akka.tcp://RemoteDemoSystem@127.0.0.1:8084/user/RemoteActor"
  implicit val timeout = Timeout(4.seconds)
  val remoteActor = context.actorSelection(path)

  def receive: Receive = {
    case Init => "init local actor"
    case SendNoReturn => remoteActor ! "hello remote actor"
    case SendHasReturn => //remoteActor.ask(message =>
    case msg: String =>
      println(s"LocalActor received message '$msg'")
      println(sender)
  }

  //https://www.cnblogs.com/shoutn/p/8717750.html
  //https://www.cnblogs.com/jiaan-geng/p/8876822.html
  //https://doc.akka.io/docs/akka/2.6.14/general/jmm.html
}
