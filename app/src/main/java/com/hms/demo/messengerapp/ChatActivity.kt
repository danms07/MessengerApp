package com.hms.demo.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.hms.demo.messengerapp.databinding.ChatBinding

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle=intent.extras
        val nickname=bundle?.getString(ProfileUtils.NICKNAME,"")

        val binding=ChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProviders.of(this).get(ChatVM::class.java)
        nickname?.let{
            viewModel.nickname=it
        }
        supportActionBar?.title=viewModel.nickname
        binding.chatVM=viewModel
    }

    override fun onBackPressed() {
        finish()
    }
}