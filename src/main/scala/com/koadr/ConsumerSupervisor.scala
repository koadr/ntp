package com.koadr


import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import scala.util.Random

class ConsumerSupervisor(producer: ActorRef) extends Actor with ActorLogging {

  import ConsumerSupervisor._

  override def receive: Receive = {
    case CreateConsumer => context.actorOf(Props(classOf[Consumer],producer,Random.nextInt(13)), s"consumer:${Utils.guid}")
  }
}

object ConsumerSupervisor {
  case object CreateConsumer
}
