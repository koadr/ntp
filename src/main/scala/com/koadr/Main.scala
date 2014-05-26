package com.koadr

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import com.koadr.ConsumerSupervisor.CreateConsumer

/**
 * Created by Craig on 5/24/14.
 */
object Main {
  def main (args: Array[String]) {
    implicit val timeout = Timeout(5 seconds)
    val system = ActorSystem("Ntp")
    val numOfConsumers = if (args.length == 0) 15 else args(0).toInt
    val producer = system.actorOf(Props[Producer], "producer")

    // Create Multiple Consumers
    for {
      i <- 1 to numOfConsumers
    } system.actorOf(Props(classOf[ConsumerSupervisor],producer), s"consumer_supervisor:${Utils.guid}") ! CreateConsumer

    system.scheduler.scheduleOnce(120 seconds) { system.shutdown() }

  }
}
