package go.vienna.fcmexample.fcmexampleapp.views

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import go.vienna.fcmexample.fcmexampleapp.R
import go.vienna.fcmexample.fcmexampleapp.presenters.MainPresenter
import go.vienna.fcmexample.fcmexampleapp.repositories.ServiceFCMRepository
import kotlinx.android.synthetic.main.activity_main.*

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
class MainActivity : AppCompatActivity(), MainView {
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val fcmRepository = ServiceFCMRepository()
        presenter = MainPresenter(this, fcmRepository)

        // Get the Firebase Cloud Messaging token here. The function will then call displayFCMToken
        // so the token will become visible.
        presenter.getFCMToken()
    }

    override fun onStart() {
        super.onStart()
        presenter.registerBroadcastReceiver(LocalBroadcastManager.getInstance(this))
    }

    override fun onStop() {
        super.onStop()
        presenter.unregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this))
    }

    override fun displayFCMText(text: String) {
        fcmTextView.append("\n" + resources.getString(R.string.fcm_message_text).format(text))
    }

    override fun displayFCMToken(token: String) {
        fcmTokenTextView.text = resources.getString(R.string.fcm_token_text).format(token)
        fcmTokenTextView.setTextColor(Color.BLACK)
    }

    override fun displayNoFCMToken() {
        fcmTokenTextView.text = ""
        fcmTokenTextView.setTextColor(Color.RED)
    }
}
