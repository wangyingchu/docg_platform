package engineCommunicationTest

import akka.actor.Actor
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

class EngineCommunicationTestActor extends Actor{

  val path = "akka://DOCGAnalysisProviderCommunicationSystem@127.0.0.1:8084/user/communicationRouter"
  implicit val timeout = Timeout(4.seconds)
  val remoteActor = context.actorSelection(path)

  def receive: Receive = {
    //case SendNoReturn => remoteActor ! "hello remote actor"
    case msg: String =>
      println(s"LocalActor received message '$msg'")
      println(sender)
  }
}
