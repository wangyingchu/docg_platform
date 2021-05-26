package sample.jvm.transformation

import akka.actor.Actor
import akka.event.Logging

class RobotActor extends Actor {
  val log = Logging(context.system, this)
  def receive: Receive = { //机器人接受指令
    case t: TurnOnLight => log.info(s"${t.message} after ${t.time} hour")
    case b: BoilWater => log.info(s"${b.message} after ${b.time} hour")
    case _ => log.info("I can not handle this message")
  }
}
