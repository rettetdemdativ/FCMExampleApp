package go.vienna.fcmexample.fcmexampleapp.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import go.vienna.fcmexample.fcmexampleapp.retrofit.ServerConstants
import go.vienna.fcmexample.fcmexampleapp.retrofit.ServerService
import go.vienna.fcmexample.fcmexampleapp.retrofit.messages.TokenMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
class MyFirebaseInstanceIdService: FirebaseInstanceIdService(), FCMInstanceIdService {
    private var currentToken: String? = null

    override fun getFCMId(): String? {
        return currentToken
    }

    override fun onTokenRefresh() {
        currentToken = FirebaseInstanceId.getInstance().token
        sendToServer(currentToken)
    }

    private fun sendToServer(token: String?) {
        if (token != null) {
            val service = getServerService()
            val registerTokenCall = service.registerFCMID(TokenMessage(token))
            registerTokenCall.enqueue(object: Callback<Void> {

                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    Log.i(TAG, "Request to %s returned %s: %s".format(
                            response?.raw()?.request()?.url(),
                            response?.code(),
                            response?.message()
                    ))
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    Log.e(TAG, "Request returned error %s".format(t?.message))
                }

            })
        }
    }

    private fun getServerService(): ServerService {
        val retrofit = Retrofit.Builder()
                .baseUrl(ServerConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(ServerService::class.java)
    }

    companion object {
        private const val TAG = "MyFBInstanceIdService"
    }
}