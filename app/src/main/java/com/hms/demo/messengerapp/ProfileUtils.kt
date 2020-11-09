package com.hms.demo.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileUtils(private val callback:ProfileCallback) :HQUICClient.HQUICClientListener{
    companion object {
        const val TAG="ProfileUtils"
        const val PROFILE_URL = "https://qz41hkiav2.execute-api.us-east-2.amazonaws.com/Prod/profile/"
        const val NICKNAME_RESOURCE = "nickname/"
        const val UID = "uid"
        const val NICKNAME="nickname"
        const val CHECK_NICKNAME=200
        const val UPDATE_NICKNAME=300
    }


    fun checkNickname(context: Context,uid: String) {
        Log.e(TAG,"Checking Nickname")
        publishUid(context,uid)
        val apiURL = "$PROFILE_URL?$UID=$uid"
        CoroutineScope(Dispatchers.IO).launch {
            context.let {
                HQUICClient(it, CHECK_NICKNAME).apply{
                    listener=this@ProfileUtils
                    makeRequest(apiURL,"GET")
                }

            }
        }
    }

    private fun publishUid(context: Context, uid: String) {
        val intent= Intent().apply {
            action = AccountPushReceiver.ACTION
            putExtra(AccountPushReceiver.PARAM,AccountPushReceiver.UID)
            putExtra(AccountPushReceiver.UID,uid)
        }
        context.sendBroadcast(intent)
    }

    fun updateNickname(context:Context,uid:String,nickname:String){
        Log.e(TAG,"Updating Nickname")
        //publishUid(context,uid)
        val url="$PROFILE_URL$NICKNAME_RESOURCE"
        val params=JSONObject().apply {
            put(UID,uid)
            put(NICKNAME,nickname)
        }
        val headers=HashMap<String,String>()
        headers["Content-Type"]= "application/json"
        CoroutineScope(Dispatchers.IO).launch {
            context.let {
                HQUICClient(it, UPDATE_NICKNAME).apply{
                    listener=this@ProfileUtils
                    makeRequest(url,"POST",headers,params.toString().toByteArray())
                }

            }
        }
    }

    override fun onSuccess(requestId:Int,response: ByteArray) {
        val result= String(response)
        Log.e(TAG,"RequestID: $requestId $result")
        val json=JSONObject(result)
        when(requestId){
            CHECK_NICKNAME ->handleNicknameCheck(json)

            UPDATE_NICKNAME ->handleNicknameUpdate(json)
        }
    }

    private fun handleNicknameCheck(json:JSONObject){
        val body=json.getJSONObject("body")
        if(body.has("nickname")){
            val nickname=body.getString("nickname")
            callback.onNicknameResult(ProfileCallback.NICKNAME_RETRIEVED,nickname)
        }else callback.onNicknameResult(ProfileCallback.NICKNAME_EMPTY,"")

    }

    private fun handleNicknameUpdate(json:JSONObject){
        val body=json.getJSONObject("body")
        when(json.getInt("responseCode")){
            ProfileCallback.NICKNAME_USED ->{
                callback.onNicknameResult(ProfileCallback.NICKNAME_USED,body.getString("message"))
            }
            ProfileCallback.NICKNAME_INSERTED->{
                callback.onNicknameResult(ProfileCallback.NICKNAME_USED,body.getString("nickname"))
            }
        }
    }

    override fun onFailure(requestId:Int,error: String) {
    }

    interface  ProfileCallback{
        companion object{
            const val NICKNAME_USED=1
            const val NICKNAME_INSERTED=2
            const val NICKNAME_RETRIEVED=3
            const val NICKNAME_EMPTY=4
        }
        fun onNicknameResult(resultCode:Int,data: String)
    }


}