package com.hms.demo.messengerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hms.demo.messengerapp.databinding.ActivityMainBinding
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import java.io.File
import com.huawei.hms.image.render.*


class MainActivity : AppCompatActivity(), ProfileUtils.ProfileCallback {
    private var contentView: FrameLayout? = null
    private var imageRenderAPI: ImageRenderImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(
        HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this)==ConnectionResult.SUCCESS
        ){

        }
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contentView=binding.content
        if(checkStoragePermission()){
            initImageRender()
        }else{
            requestStoragePermission()
        }
        val user = AGConnectAuth.getInstance().currentUser
        if(user!=null)checkNickName(user.uid)
        else redirectToLogin()

    }

    private fun initImageRender() {
        val sourcePath =
            filesDir.path + File.separator + SOURCE_PATH
        if (!AnimationUtils.createResourceDirs(sourcePath)) {
            Log.e(
                TAG,
                "Create dirs fail, please check permission"
            )
        }

        if (!AnimationUtils.copyAssetsFilesToDirs(
                this,
                "DropPhysicalView",
                sourcePath
            )
        ) {
            Log.e(
                TAG,
                "copy files failure, please check permissions"
            )

        }

        ImageRender.getInstance(this,object :ImageRender.RenderCallBack{
            override fun onSuccess(imageRender: ImageRenderImpl) {

                imageRenderAPI = imageRender
                val initResult=imageRenderAPI?.doInit(sourcePath, AnimationUtils.authJson)
                if (initResult == 0) {
                    // Obtain the rendered view.
                    val renderView = imageRenderAPI?.renderView
                    when (renderView?.resultCode){
                         ResultCode.SUCCEED -> {
                            val view = renderView.view
                            view?.let {
                                contentView?.addView(it)
                            }
                             startAnimation()

                        }
                        ResultCode.ERROR_GET_RENDER_VIEW_FAILURE -> {
                            Log.e(TAG, "GetRenderView fail")
                        }
                        ResultCode.ERROR_XSD_CHECK_FAILURE -> {
                            Log.e(
                                TAG,
                                "GetRenderView fail, resource file parameter error, please check resource file."
                            )
                        }
                        ResultCode.ERROR_VIEW_PARSE_FAILURE -> {
                            Log.e(
                                TAG,
                                "GetRenderView fail, resource file parsing failed, please check resource file."
                            )
                        }
                        ResultCode.ERROR_REMOTE -> {
                            Log.e(
                                TAG,
                                "GetRenderView fail, remote call failed, please check HMS service"
                            )
                        }
                        ResultCode.ERROR_DOINIT -> {
                            Log.e(
                                TAG,
                                "GetRenderView fail, init failed, please init again"
                            )
                        }
                    }
                }
            }

            override fun onFailure(i: Int) {
                Log.e(
                    TAG,
                    "getImageRenderAPI failure, errorCode = $i"
                )
            }
        })
    }

    private fun startAnimation() {
        // Play the rendered view.
        Log.e(TAG, "Start animation")
        imageRenderAPI?.apply{
            val playResult = imageRenderAPI!!.playAnimation()
            if (playResult == ResultCode.SUCCEED) {
                Log.i(
                    TAG,
                    "Start animation success"
                )
            } else {
                Log.e(
                    TAG,
                    "Start animation failure"
                )
            }
        }
    }

    private fun stopAnimation() {
        // Stop the renderView animation.
        Log.e(TAG, "Stop animation")
        if (null != imageRenderAPI) {
            val playResult = imageRenderAPI?.stopAnimation()
            if (playResult == ResultCode.SUCCEED) {
                Log.e(
                    TAG,
                    "Stop animation success"
                )
            } else {
                Log.e(
                    TAG,
                    "Stop animation failure"
                )
            }
        } else {
            Log.e(
                TAG,
                "Stop animation fail, please init first."
            )
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkStoragePermission(): Boolean {
        val check=checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return check==PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(checkStoragePermission()){
            initImageRender()
        }
    }

    private fun checkNickName(uid: String?) {
        uid?.let{
            ProfileUtils(this).checkNickname(this,it)
        }
    }


    private fun redirectToLogin() {
        redirect(Intent(this,LoginActivity::class.java))
    }

    private fun redirect(intent :Intent){
        runOnUiThread{
            Handler().postDelayed({
                stopAnimation()
                imageRenderAPI?.removeRenderView()
                startActivity(intent)
                finish()
            },2000)
        }
    }


    override fun onNicknameResult(resultCode: Int, data: String) {
        when(resultCode){
            ProfileUtils.ProfileCallback.NICKNAME_EMPTY ->{
                redirect(Intent(this,ProfileActivity::class.java))
            }

            ProfileUtils.ProfileCallback.NICKNAME_RETRIEVED ->{
                runOnUiThread{Toast.makeText(this,"Welcome $data",Toast.LENGTH_SHORT).show()}
                redirect(Intent(this,MessengerActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        imageRenderAPI?.removeRenderView()
        super.onDestroy()
    }

    companion object {
        /**
         * TAG
         */
        const val TAG = "ImageKitRenderDemo"

        /**
         * Resource folder, which can be set as you want.
         */
        const val SOURCE_PATH = "sources"

        /**
         * requestCode for applying for permissions.
         */
        const val PERMISSION_REQUEST_CODE = 0x01
    }
}