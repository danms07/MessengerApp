package com.hms.demo.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.hms.demo.messengerapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() ,ProfileViewModel.ProfileNavigator{
    lateinit var viewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel =ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.loadProfile(this)
        viewModel.navigator=this
        binding.profileVM=viewModel

    }

    override fun onBackPressed() {
        if(viewModel.nickname == ""){
            displayMessage(getString(R.string.forceNickname))
        }else navigateToMessenger()
    }

    override fun navigateToMessenger() {
        startActivity(Intent(this,MessengerActivity::class.java))
        finish()
    }

    override fun displayMessage(message: String) {
        runOnUiThread{
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
    }
}