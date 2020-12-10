package com.hms.demo.messengerapp

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatUtils (val context: Context): HQUICClient.HQUICClientListener{
    companion object{
        const val CHAT_URL="https://qz41hkiav2.execute-api.us-east-2.amazonaws.com/Prod/chat"
        const val MESSAGE="message"
    }
    var chatEventListener:OnChatEventListener?=null

    fun sendMessage(nickname:String, message:String){
        CoroutineScope(Dispatchers.IO).launch {
            val params= JSONObject().apply {
                put(MESSAGE,message)
                put(ProfileUtils.NICKNAME,nickname)
            }
            val headers=HashMap<String,String>()
            headers["Content-Type"]= "application/json"
                context.let {
                    HQUICClient(it, ProfileUtils.UPDATE_NICKNAME).apply{
                        listener=this@ChatUtils
                        makeRequest(CHAT_URL,"POST",headers,params.toString().toByteArray())
                    }

                }

        }
    }

    override fun onSuccess(requestId: Int, response: ByteArray) {

    }

    override fun onFailure(requestId: Int, error: String) {

    }

    interface OnChatEventListener{
        fun onMessageSent(message:String)
        fun onChatFailure(errorCode:Int)
    }
}