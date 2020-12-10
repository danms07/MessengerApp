package com.hms.demo.messengerapp

import android.content.Context
import androidx.lifecycle.ViewModel

class ChatVM : ViewModel(), MessageWidget.OnMessageEventListener,ChatUtils.OnChatEventListener{
    var chatUtils:ChatUtils?=null
    var context:Context?=null
    var nickname:String=""

    fun load(context: Context){
        this.context=context
        chatUtils= ChatUtils(context).apply { chatEventListener=this@ChatVM }
    }
    override fun onMessageSending(message: String) {
        chatUtils?.sendMessage(nickname,message)
    }

    override fun onMessageSent(message: String) {
        TODO("Not yet implemented")
    }

    override fun onChatFailure(errorCode: Int) {
        TODO("Not yet implemented")
    }

}