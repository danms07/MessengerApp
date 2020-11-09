package com.hms.demo.messengerapp

import android.content.Context
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.io.*
import java.nio.ByteBuffer

class HQUICClient(context: Context, private val requestId:Int=0) : UrlRequest.Callback() {

    var hquicService: HQUICService? = null
    val CAPACITY = 10240
    val TAG="QUICClient"
    val response=ByteArrayOutputStream()
    var listener: HQUICClientListener?=null

    init {
        hquicService = HQUICService(context)
        hquicService?.setCallback(this)
    }

    fun makeRequest(url: String, method: String, headers: HashMap<String, String>?=null,body:ByteArray?=null){
        hquicService?.sendRequest(url,method,headers,body)
    }

    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        val byteBuffer = ByteBuffer.allocateDirect(CAPACITY)
        request?.read(byteBuffer)
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        byteBuffer?.apply {
            response.write(array(),arrayOffset(),position())
            response.flush()
        }
        request?.read(ByteBuffer.allocateDirect(CAPACITY))
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        listener?.onSuccess(requestId,response.toByteArray())
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        listener?.apply { onFailure(requestId,error.toString()) }
    }

    interface HQUICClientListener{
        fun onSuccess(requestId: Int,response: ByteArray)
        fun onFailure(requestId: Int,error: String)
    }
}