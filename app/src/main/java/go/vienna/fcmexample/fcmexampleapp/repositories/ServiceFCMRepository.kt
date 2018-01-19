package go.vienna.fcmexample.fcmexampleapp.repositories

import com.google.firebase.iid.FirebaseInstanceId

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
class ServiceFCMRepository: FCMRepository {
    override fun getFCMToken(): String? {
        return FirebaseInstanceId.getInstance().token
    }
}