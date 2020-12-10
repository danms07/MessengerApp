package com.hms.demo.messengerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AccountPushReceiver: BroadcastReceiver(),HQUICClient.HQUICClientListener{

    companion object {
        private const val TAG="Receiver"
        const val PARAM="param"
        const val UID="uid"
        const val TOKEN="pushToken"
        const val ACTION="ACTION_REGISTER_USER"
        private const val SERVER="https://qz41hkiav2.execute-api.us-east-2.amazonaws.com/Prod/profile"
    }

    private var uid:String?=null
    private var token:String?=null
    private var context:Context?=null

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context=context
        intent?.extras?.let {
            val key=it.getString(PARAM)
            val value=it.getString(key)
            checkParamValues(key!!,value!!)
        }
    }

    fun register(context:Context){
        Log.d(TAG,"Registering Receiver")
        context.registerReceiver(this, IntentFilter(ACTION))
    }

    private fun unregister(){
        Log.d(TAG,"UnregisteringReceiver")
        context?.unregisterReceiver(this)
    }

    private fun checkParamValues(key: String, value: String) {
        Log.e(TAG,"Received $key")
        when(key){
            UID -> {
                uid=value
                token?.let{registerInServer(value,it)}
            }
            TOKEN -> {
                token=value
                uid?.let { registerInServer(it,value) }
            }
        }
    }

    private fun registerInServer(mail:String,token:String){
        //perform registerOperation
        CoroutineScope(Dispatchers.IO).launch {
            context?.let {
                val map=HashMap<String,String>()
                map["Content-Type"] = "application/json"
                val body= JSONObject().apply {
                    put(UID,mail)
                    put(TOKEN,token)
                }
                HQUICClient(it,300).apply{
                    listener=this@AccountPushReceiver
                    makeRequest(SERVER,"POST",map,body.toString().toByteArray())
                }

            }
        }

    }

    override fun onSuccess(requestId:Int,response: ByteArray) {
        Log.e(TAG, String(response))
        unregister()
    }

    override fun onFailure(requestId:Int ,error: String) {
        Log.e(TAG, error)
        unregister()
    }
}