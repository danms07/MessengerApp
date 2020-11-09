package com.hms.demo.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hms.demo.messengerapp.databinding.LoginBinding


class LoginActivity : AppCompatActivity(), LoginViewModel.ViewNavigator {
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.navigator = this
        binding.loginVM = viewModel
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(this, requestCode, resultCode, data)

    }

    override fun navigateForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    override fun navigateToMessenger() {
        startActivity(Intent(this, MessengerActivity::class.java))
        finish()
    }

}
