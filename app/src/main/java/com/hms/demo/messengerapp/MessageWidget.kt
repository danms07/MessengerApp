package com.hms.demo.messengerapp


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.hms.demo.messengerapp.databinding.WidgetBinding

class MessageWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){
    var messageEventListener:OnMessageEventListener?=null
    var binding:WidgetBinding
    init{
        val layoutInflater=LayoutInflater.from(context)
        //val view=layoutInflater.inflate(R.layout.message_widget,this)
        binding=WidgetBinding.inflate(layoutInflater,this,true)
        binding.controller=this
    }

    fun onMessageSending(text: String){
        if(!text.isNullOrEmpty()){
            messageEventListener?.onMessageSending(text)
            binding.etMessage.setText("")
        }
    }

    interface OnMessageEventListener{
        fun onMessageSending(message:String)
    }

}