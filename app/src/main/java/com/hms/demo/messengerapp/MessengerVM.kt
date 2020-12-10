package com.hms.demo.messengerapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.*

class MessengerVM: ViewModel(),InputDialog.OnInputListener,ProfileUtils.ProfileCallback{
    private val TAG="MessengerVM"
    var navigator:MessengerNavigator?=null
    var context:Context?=null

    fun onNewChat(context: Context){
        this.context=context
        val dialog=InputDialog(context).also { it.inputListener=this }
        navigator?.showInputDialog(dialog)
    }

    override fun onInput(input: String) {
        Log.e(TAG,input)
        context?.let {
            ProfileUtils(this).findUser(it,input)
        }

    }

    override fun onNicknameResult(resultCode: Int, data: String) {
        when (resultCode){
            ProfileUtils.ProfileCallback.NICKNAME_USED ->{//the remote nickname exists, we can start the chat
                navigator?.navigateToChat(data)
            }
            else ->{
                navigator?.displayError(data)
            }
        }
    }




    interface MessengerNavigator{
        fun showInputDialog(dialog:InputDialog)
        fun navigateToChat(nickname: String)
        fun displayError(error: String)
    }
}