package com.koadr

import akka.actor.{Cancellable, ActorRef, ActorLogging, Actor}
import scala.concurrent.duration._
import java.text.SimpleDateFormat

class Consumer(producer: ActorRef, maxNumberOfAcks: Int) extends Actor with ActorLogging {
  import Producer._
  import Consumer._

  val ackFrequency = 5 seconds

  implicit val ec = context.dispatcher

  private var cancellable: Option[Cancellable] = None

  private var counter = 0


  override def receive: Receive = {
    case Time(time) => {
      // Log Current Time
      val prettyT = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date(time))
      log.info(s"Current Time is $prettyT")
    }

  }

  override def preStart() {
    setup()
  }

  def setup() {
    // Register Consumer
    producer ! Register
    // Send KeepAlive
    sendKeepAlive()
  }

  private def sendKeepAlive() {
    // Send periodic acks and track cancellable callback for stopping scheduler so long as max keepalive messages are greater than 0.
    if (cancellable.isEmpty && maxNumberOfAcks > 0)
      cancellable = Some(context.system.scheduler.schedule(0 seconds, ackFrequency, new Runnable {
        override def run(): Unit = {
          producer ! KeepAlive

          // Stop Sending acks if excededed max
          if (counter >= maxNumberOfAcks) {
            log.error("Exceeded max KeepAlive acknowledgements")
            cancellable.foreach(_.cancel())
          } else counter = counter + 1
        }
      }))
  }
}

object Consumer {
  case object Register
  case object KeepAlive
}
