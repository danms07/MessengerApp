package com.hms.demo.messengerapp

import android.content.Context
import android.util.Log
import com.huawei.hms.hquic.HQUICManager
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import java.net.MalformedURLException
import java.net.URL
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class HQUICService(val context: Context) {

    private val TAG = "HQUICService"

    private val DEFAULT_PORT = 443

    private val DEFAULT_ALTERNATEPORT = 443

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private var cronetEngine: CronetEngine? = null

    private var callback: UrlRequest.Callback? = null


    /**
     * Asynchronous initialization.
     */
    init {
        HQUICManager.asyncInit(
            context,
            object : HQUICManager.HQUICInitCallback {
                override fun onSuccess() {
                    Log.i(TAG, "HQUICManager asyncInit success")
                }

                override fun onFail(e: Exception?) {
                    Log.w(TAG, "HQUICManager asyncInit fail")
                }
            })
    }

    /**
     * Create a Cronet engine.
     *
     * @param url URL.
     * @return cronetEngine Cronet engine.
     */
    private fun createCronetEngine(url: String): CronetEngine? {
        if (cronetEngine != null) {
            return cronetEngine
        }
        val builder = CronetEngine.Builder(context)
        builder.enableQuic(true)
        builder.addQuicHint(getHost(url), DEFAULT_PORT, DEFAULT_ALTERNATEPORT)
        cronetEngine = builder.build()
        return cronetEngine
    }

    /**
     * Construct a request
     *
     * @param url Request URL.
     * @param method method Method type.
     * @return UrlRequest urlrequest instance.
     */
    private fun builRequest(
        url: String,
        method: String,
        headers: HashMap<String, String>?,
        body:ByteArray?
    ): UrlRequest? {
        val cronetEngine: CronetEngine? = createCronetEngine(url)
        val requestBuilder = cronetEngine?.newUrlRequestBuilder(url, callback, executor)
        requestBuilder?.apply {
            setHttpMethod(method)
            if(method=="POST"){
                body?.let {
                    setUploadDataProvider(UploadDataProviders.create(ByteBuffer.wrap(it)), executor) }
            }
            headers?.let{
                for (key in it.keys) {
                    addHeader(key, headers[key])
                }
            }
            return build()
        }
        return null
    }

    /**
     * Send a request to the URL.
     *
     * @param url Request URL.
     * @param method Request method type.
     */
    fun sendRequest(url: String, method: String, headers: HashMap<String, String>?=null,body:ByteArray?=null) {
        Log.i(TAG, "callURL: url is " + url + "and method is " + method)
        val urlRequest: UrlRequest? = builRequest(url, method, headers,body)
        urlRequest?.apply { urlRequest.start() }
    }

    /**
     * Parse the domain name to obtain the host name.
     *
     * @param url Request URL.
     * @return host Host name.
     */
    private fun getHost(url: String): String? {
        var host: String? = null
        try {
            val url1 = URL(url)
            host = url1.host
        } catch (e: MalformedURLException) {
            Log.e(TAG, "getHost: ", e)
        }
        return host
    }

    fun setCallback(mCallback: UrlRequest.Callback?) {
        callback = mCallback
    }


}