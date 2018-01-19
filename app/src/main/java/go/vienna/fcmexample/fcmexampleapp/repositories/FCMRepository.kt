package go.vienna.fcmexample.fcmexampleapp.repositories

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
interface FCMRepository {
    fun getFCMToken(): String?
}