package com.hms.demo.messengerapp

import android.content.Intent
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

class MyHmsMessageService: HmsMessageService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        token?.let {
            Log.d("onNewToken",it)
            publishToken(it)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
    }


    private fun publishToken(token:String) {
        val intent= Intent().apply {
            action = AccountPushReceiver.ACTION
            putExtra(AccountPushReceiver.PARAM,AccountPushReceiver.TOKEN)
            putExtra(AccountPushReceiver.TOKEN,token)
        }
        sendBroadcast(intent)
    }
}