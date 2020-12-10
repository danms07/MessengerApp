package com.hms.demo.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.hms.demo.messengerapp.databinding.ActivityMessengerBinding

class MessengerActivity : AppCompatActivity(), MessengerVM.MessengerNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMessengerBinding.inflate(layoutInflater)
        val viewModel= ViewModelProviders.of(this).get(MessengerVM::class.java)
        viewModel.navigator=this
        binding.messengerVM=viewModel
        setContentView(binding.root)

    }

    override fun showInputDialog(dialog: InputDialog) {
        dialog.show()
    }

    override fun navigateToChat(nickname: String) {
        Intent(this,ChatActivity::class.java).let{
            it.putExtra(ProfileUtils.NICKNAME,nickname)
            startActivity(it)
        }
    }

    override fun displayError(error:String) {
        runOnUiThread{Toast.makeText(this,error,Toast.LENGTH_SHORT).show()}
    }

}