package com.hms.demo.messengerapp

import android.content.Context
import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_INSERTED
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_RETRIEVED
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_USED
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser

class ProfileViewModel: ViewModel(), ProfileUtils.ProfileCallback {
    companion object{
        const val TAG="ProfileViewModel"
    }

    var user: AGConnectUser =AGConnectAuth.getInstance().currentUser
    var nickname:String=""
    var navigator:ProfileNavigator?=null

    private fun checkNickname(context: Context,uid:String?){
        uid?.let{
            ProfileUtils(this).checkNickname(context,it)
        }
    }

    public fun loadProfile(context: Context){
        user.uid?.let { checkNickname(context,it) }

    }

    fun updateNickname(context: Context,value:Editable){
        user.uid?.let {
            Log.e(TAG,"UpdateNickname")
            ProfileUtils(this).updateNickname(context,it,value.toString()) }

    }

    override fun onNicknameResult(resultCode: Int, data: String) {
        when(resultCode){
            NICKNAME_RETRIEVED ->this.nickname=data
            NICKNAME_USED ->{
                Log.e(TAG,"Nickname used")
                navigator?.displayMessage(data)
            }
            NICKNAME_INSERTED ->{
                this.nickname=data
                Log.e(TAG,"Nickname inserted")
                navigator?.navigateToMessenger()
            }
        }
    }

    interface ProfileNavigator{
        fun navigateToMessenger()
        fun displayMessage(message:String)
    }
}