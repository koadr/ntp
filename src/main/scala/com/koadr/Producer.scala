package com.koadr

import akka.actor._
import scala.concurrent.duration._
import org.joda.time._


class Producer extends Actor with ActorLogging {
  import Producer._
  import Consumer._

  val ackTimeout = 10 seconds
  val timeStampDuration = 1 second

  implicit val ec = context.dispatcher

  // Keeps track of callback by which all scheduled messages per consumer can be stopped
  private var cancellablesByPub: Map[ActorRef, Cancellable] = Map[ActorRef, Cancellable]()

  // Keeps track of timestamp of last message per consumer
  private var lastMsgTimePerConsumer: Map[ActorRef,Long] = Map[ActorRef, Long]()

  // Cancellable for resolving if timeouts exceeded for KeepAlive from Consumer
  private var cancellable: Option[Cancellable] = None

  override def postStop() = cancellable.foreach(_.cancel())

  override def receive: Receive = {

    case Register => {
      val consumer = sender()
      log.info(s"Registered new client ${consumer.path.name}")

      // Track sender MsgTime
      lastMsgTimePerConsumer += consumer -> new DateTime().getMillis

      // Track cancellable callback for stopping scheduler.
      if (cancellable.isEmpty) cancellable = Some(context.system.scheduler.schedule(0 seconds, 0 seconds, self, Resolve ))

      // Start Sending Time
      sendTime(consumer)
    }

    // Set the timeout for 10 seconds on consumer ack
    case KeepAlive => {
      val now = new DateTime()
      lastMsgTimePerConsumer += sender() -> now.getMillis

      log.info(s"Heartbeat: ${sender().path.name}")
    }

    case Resolve => {
      /*
       * Stop Sending messages to Unreachable Consumers
       * 1) Check for all consumers that haven't sent KeepAlive within timeout
       * 2) Stop sending messages to those consumers
       */

      val deactivatedConsumers = lastMsgTimePerConsumer.filter {
        case (_, ts) =>
          new DateTime().
            withMillis(ts).
            plusSeconds(10).
            isBeforeNow
      }

      for {
        (ref,_) <- deactivatedConsumers
      } yield cancellablesByPub.get(ref).foreach {
        consumer =>
          log.info(s"Deactivating Messaging to ${ref.path.name}")
          consumer.cancel()
          cancellablesByPub -= ref
          lastMsgTimePerConsumer -= ref
      }
    }
  }

  private def sendTime(consumer: ActorRef) {
    cancellablesByPub += consumer -> context.system.scheduler.schedule(
      0 seconds, timeStampDuration, new Runnable {
        override def run(): Unit = consumer ! Time(System.currentTimeMillis())
      }
    )
  }
}

object Producer {
  case class Time(current: Long)
  case object Resolve
}
