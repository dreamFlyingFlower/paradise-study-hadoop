package com.wy.akka

class WorkerInfo(val id: String, val workerHost: String, val memory: String, val cores: String) {
  var lastHeartbeat: Long = System.currentTimeMillis()

  override def toString = s"WorkerInfo($id, $workerHost, $memory, $cores)"
}