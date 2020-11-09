package com.hms.demo.messengerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.auth.AGConnectAuth


class MainActivity : AppCompatActivity(), ProfileUtils.ProfileCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = AGConnectAuth.getInstance().currentUser
        if(user!=null)checkNickName(user.uid)
        else redirectToLogin()

    }

    private fun checkNickName(uid: String?) {
        uid?.let{
            ProfileUtils(this).checkNickname(this,it)
        }
    }


    private fun redirectToLogin() {
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }



    override fun onNicknameResult(resultCode: Int, data: String) {
        when(resultCode){
            ProfileUtils.ProfileCallback.NICKNAME_EMPTY ->{
                startActivity(Intent(this,ProfileActivity::class.java))
                finish()
            }

            ProfileUtils.ProfileCallback.NICKNAME_RETRIEVED ->{
                Toast.makeText(this,"Welcome $data",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MessengerActivity::class.java))
            }
        }
    }


}