package sample.remote.transformation

import akka.actor.Actor
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

class RemoteActor extends Actor {
  def receive = {
    case msg: String =>
      println(s"RemoteActor received message '$msg'")
      println(sender)
      //sender ! "Hello2 from the RemoteActor"
      implicit val timeout = Timeout(10.seconds)
      sender.tell("this is return from RemoteActor",self)
  }
}