package com.hms.demo.messengerapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.input_dialog.view.*

class InputDialog(context: Context) : Dialog(context) {
    var inputListener:OnInputListener?=null
    init {
        val layoutInflater=LayoutInflater.from(context)
        val view=layoutInflater.inflate(R.layout.input_dialog,null)
        val editText=view.etInput
        val button=view.inputBtn
        button.setOnClickListener{
            Log.e("Dialog","onInput")
            inputListener?.onInput(editText.text.toString())
            dismiss()
        }
        setContentView(view)

    }
    interface OnInputListener{
        fun onInput(input:String)
    }

}