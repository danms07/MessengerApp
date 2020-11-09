package com.hms.demo.messengerapp

import android.app.Application

class MessengerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AccountPushReceiver().register(this)
    }
}