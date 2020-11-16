package com.wy.akka

case class RegisterWorker(val id: String, val workerHost: String, val memory: String, val cores: String)

case class HeartBeat(val workid: String)

case class CheckOfTimeOutWorker()

case class RegisteredWorker(val workerHost: String)

case class SendHeartBeat()