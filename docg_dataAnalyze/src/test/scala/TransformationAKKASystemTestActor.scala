import akka.actor.Actor
import akka.util.Timeout
import com.viewfunction.docg.dataAnalyze.feature.transformation.messagePayload.AnalyzeTreesCrownAreaInSection

import scala.concurrent.duration.DurationInt

case object SendNoReturn

class TransformationAKKASystemTestActor extends Actor{

  val path = "akka.tcp://DataAnalyzeTransformationRouterSystem@127.0.0.1:8084/user/TransformationRouter"
  implicit val timeout = Timeout(4.seconds)
  val remoteActor = context.actorSelection(path)

  def receive: Receive = {
    case SendNoReturn => remoteActor ! "hello remote actor"
    case msg: AnalyzeTreesCrownAreaInSection => remoteActor ! msg
    case msg: String =>
      println(s"LocalActor received message '$msg'")
      println(sender)
  }
}
