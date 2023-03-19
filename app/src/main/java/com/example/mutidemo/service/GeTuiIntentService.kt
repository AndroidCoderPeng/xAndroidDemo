package com.example.mutidemo.service

import android.content.Context
import android.util.Log
import com.igexin.sdk.GTIntentService
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage

class GeTuiIntentService : GTIntentService() {

    private val kTag = "GeTuiIntentService"

    override fun onReceiveServicePid(context: Context, pid: Int) {}

    override fun onReceiveMessageData(context: Context, msg: GTTransmitMessage) {
        // 透传消息的处理，详看SDK demo
        val appid = msg.appid
        val taskid = msg.taskId
        val messageid = msg.messageId
        val payload = msg.payload
        val pkg = msg.pkgName
        val cid = msg.clientId
    }

    override fun onReceiveClientId(context: Context, clientid: String) {
        Log.e(kTag, "onReceiveClientId -> clientid = $clientid")
    }

    override fun onReceiveOnlineState(context: Context, online: Boolean) {}
    override fun onReceiveCommandResult(context: Context, cmdMessage: GTCmdMessage) {}
    override fun onNotificationMessageArrived(context: Context, msg: GTNotificationMessage) {}
    override fun onNotificationMessageClicked(context: Context, msg: GTNotificationMessage) {}
}