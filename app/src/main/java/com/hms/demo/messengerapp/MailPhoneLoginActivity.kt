package com.hms.demo.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.hms.demo.messengerapp.ProfileUtils.Companion.UID
import com.hms.demo.messengerapp.databinding.MailPhoneBinding
import com.huawei.agconnect.auth.AGConnectUser

class MailPhoneLoginActivity : AppCompatActivity(), MailPhoneVM.MailPhoneNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MailPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val vm=ViewModelProviders.of(this).get(MailPhoneVM::class.java)
        vm.navigator=this
        binding.viewModel=vm
    }

    override fun sendResult(result: Int,user: AGConnectUser) {
        val intent = Intent().apply { putExtra(UID,user.uid) }
        setResult(result,intent)
        finish()
    }
}