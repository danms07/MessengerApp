package com.hms.demo.messengerapp

import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.huawei.agconnect.auth.*
import com.huawei.hmf.tasks.Task

class MailPhoneVM : ViewModel() {
    companion object{
        val mailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        val phoneRegex=Regex("^\\+[1-9]{1}[0-9]{3,14}\$\n")
        const val ACCOUNT_TYPE="account_type"
        const val VALUE="value"
        const val MAIL="mail"
        const val PHONE="phone"
    }
    var navigator:MailPhoneNavigator?=null
    private var dataType=MAIL
    private var value=""

    fun getCode(editable: Editable){

        if (mailRegex.matches(editable.toString())) {
            //is mail
            getMailCode(editable.toString())
        }
        else if (phoneRegex.matches(editable.toString())){
            //is phone
        }
    }

    private fun getMailCode(mail: String) {
        dataType= MAIL
        value=mail
        val settings = VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
            .sendInterval(30) // Minimum sending interval, ranging from 30s to 120s.
            .build()

        val task: Task<VerifyCodeResult> = EmailAuthProvider.requestVerifyCode(
            mail,
            settings
        )
        task.addOnSuccessListener {
            //The verification code application is successful.
        }
            .addOnFailureListener {
                Log.e("EmailAuth", it.toString())
            }
    }

    private fun getMobileCode(phone:String){
        dataType= PHONE
        value=phone
    }

    fun verifyCode(editable: Editable){
        when(dataType){
            PHONE -> verifyPhone(value,editable.toString())
            MAIL -> verifyMail(value,editable.toString())
        }
    }

    private fun verifyMail(email: String,code:String) {
        val emailUser = EmailUser.Builder()
            .setEmail(email)
            .setVerifyCode(code)
            .build()

        AGConnectAuth.getInstance().createUser(emailUser)
            .addOnSuccessListener{
                navigator?.sendResult(200,it.user)
            }
            .addOnFailureListener{
                Log.e("AuthSevice","Email Sign in failed $it")
            }

    }

    private fun verifyPhone(phone: String,code:String) {
    }

    interface MailPhoneNavigator{
        fun sendResult(result:Int,user:AGConnectUser)
    }
}