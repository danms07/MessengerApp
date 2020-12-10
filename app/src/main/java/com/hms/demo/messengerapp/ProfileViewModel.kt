package com.hms.demo.messengerapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_INSERTED
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_RETRIEVED
import com.hms.demo.messengerapp.ProfileUtils.ProfileCallback.Companion.NICKNAME_USED
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL

class ProfileViewModel: ViewModel(), ProfileUtils.ProfileCallback {
    companion object{
        const val TAG="ProfileViewModel"
    }

    var user: AGConnectUser =AGConnectAuth.getInstance().currentUser
    var nickname:String=""
    private val _liveBitmap=MutableLiveData<Bitmap>().apply { value=null }
    val liveBitmap:LiveData<Bitmap> = _liveBitmap
    var navigator:ProfileNavigator?=null

    private fun checkNickname(context: Context,uid:String?){
        uid?.let{
            ProfileUtils(this).checkNickname(context,it)
        }
    }

    public fun loadProfile(context: Context){
        user.uid?.let { checkNickname(context,it) }
        user.photoUrl?.let { loadProfilePic(it) }

    }

    fun updateNickname(context: Context,value:Editable){
        user.uid?.let {
            Log.e(TAG,"UpdateNickname")
            ProfileUtils(this).updateNickname(context,it,value.toString()) }

    }

    override fun onNicknameResult(resultCode: Int, data: String) {
        when(resultCode){
            NICKNAME_RETRIEVED ->this.nickname=data
            NICKNAME_USED ->{
                Log.e(TAG,"Nickname used")
                navigator?.displayMessage(data)
            }
            NICKNAME_INSERTED ->{
                this.nickname=data
                Log.e(TAG,"Nickname inserted")
                navigator?.navigateToMessenger()
            }
        }
    }

    private fun loadProfilePic(avatarUriString: String?) {
        CoroutineScope(IO).launch {
            val bitmap=getBitmap(avatarUriString)
            if(bitmap!=null){
                val resizedBitmap=getResizedBitmap(bitmap,480,480)
                _liveBitmap.postValue(resizedBitmap)
            }
        }
    }

    private suspend fun getBitmap(avatarUriString: String?):Bitmap?{
        try {
            val url= URL(avatarUriString)
            val connection=url.openConnection()
            connection.doInput=true
            connection.connect()
            val input: InputStream = connection.getInputStream()

            return BitmapFactory.decodeStream(input)
        }catch (e: Exception){
            return null
        }
    }

    private suspend fun getResizedBitmap(bitmap:Bitmap, newHeight:Int,newWidth:Int):Bitmap{
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat()  / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP

        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
            matrix, false)
    }

    interface ProfileNavigator{
        fun navigateToMessenger()
        fun displayMessage(message:String)
    }
}