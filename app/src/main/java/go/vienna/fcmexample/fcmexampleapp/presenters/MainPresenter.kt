package go.vienna.fcmexample.fcmexampleapp.presenters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import go.vienna.fcmexample.fcmexampleapp.repositories.ServiceFCMRepository
import go.vienna.fcmexample.fcmexampleapp.retrofit.ServerConstants
import go.vienna.fcmexample.fcmexampleapp.retrofit.ServerService
import go.vienna.fcmexample.fcmexampleapp.retrofit.messages.TokenMessage
import go.vienna.fcmexample.fcmexampleapp.services.MyFirebaseMessagingService
import go.vienna.fcmexample.fcmexampleapp.views.MainView
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
class MainPresenter(private val view: MainView, private val fcmRepository: ServiceFCMRepository) {

    private var fcmMessageBroadcastReceiver: BroadcastReceiver? = null

    init {
        // Initialize the BroadcastReceiver that waits for new messages from Firebase Cloud
        // Messaging to arrive.
        fcmMessageBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                try {
                    val message = intent.getStringExtra("message")
                    view.displayFCMText(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun registerBroadcastReceiver(localBroadcastManager: LocalBroadcastManager) {
        localBroadcastManager.registerReceiver(fcmMessageBroadcastReceiver, IntentFilter(MyFirebaseMessagingService.FCM_MSG))
    }

    fun unregisterBroadcastReceiver(localBroadcastManager: LocalBroadcastManager) {
        localBroadcastManager.unregisterReceiver(fcmMessageBroadcastReceiver)
    }

    /**
     * Initialize the FCM connection by getting a token and sending it to the application server.
     * The token will also be displayed in the main view.
     */
    fun getFCMToken() {
        val currentToken = fcmRepository.getFCMToken()
        if (currentToken != null) {
            sendToServer(currentToken)
            view.displayFCMToken(currentToken)
        } else {
            view.displayNoFCMToken()
        }
    }

    /**
     * Sends the Firebase Cloud Messaging token to /fcm/register.
     */
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
        private const val TAG = "MainPresenter"
    }
}