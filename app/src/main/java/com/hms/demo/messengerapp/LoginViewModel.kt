package com.hms.demo.messengerapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_EMPTY
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_RETRIEVED
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper

class LoginViewModel : ViewModel(), ProfileUtils.ProfileCallback {
    companion object{
        const val HW_ID=100
        const val MAIL_PHONE=200
    }

    var navigator:ViewNavigator?=null

    public fun huaweiIdLogin(context: Context){
        val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setAuthorizationCode()
            .setAccessToken()
            .createParams()
        val service = HuaweiIdAuthManager.getService(context, authParams)
        navigator?.navigateForResult(service.signInIntent, HW_ID)

    }

    public fun mailPhoneLogin(context: Context){
        val intent=Intent(context, MailPhoneLoginActivity::class.java)
        navigator?.navigateForResult(intent,MAIL_PHONE)
    }

    fun onActivityResult(context:Context,requestCode: Int, resultCode: Int, data: Intent?){
        when(requestCode){
            HW_ID -> handleHuaweiID(context,data)
            MAIL_PHONE -> handleMailPhoneUser(context,resultCode,data)
        }
    }

    private fun handleHuaweiID(context: Context,data: Intent?) {
        val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
        if (authHuaweiIdTask.isSuccessful) {
            val huaweiAccount = authHuaweiIdTask.result
            val credential = HwIdAuthProvider.credentialWithToken(huaweiAccount.accessToken)
            AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener{
                askForNickName(context,it.user.uid)
            }
        }
    }

    private fun handleMailPhoneUser(context: Context,resultCode: Int,data:Intent?) {
        if(resultCode==200){
            data?.let {
                askForNickName(context,it.getStringExtra(ProfileUtils.UID))
            }

        }

    }

    private fun askForNickName(context: Context,uid:String?) {
        uid?.let {
            ProfileUtils(this).checkNickname(context,it)
        }
    }

    interface ViewNavigator{
        fun navigateForResult(intent: Intent,requestCode:Int)
        fun navigateToProfile()
        fun navigateToMessenger()
    }

    override fun onNicknameResult(resultCode: Int, data: String) {
        when(resultCode){
            NICKNAME_EMPTY -> navigator?.navigateToProfile()
            NICKNAME_RETRIEVED ->navigator?.navigateToMessenger()
        }
    }

}