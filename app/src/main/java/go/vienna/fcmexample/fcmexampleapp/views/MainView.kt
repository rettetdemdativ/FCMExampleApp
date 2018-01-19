package go.vienna.fcmexample.fcmexampleapp.views

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
interface MainView {
    fun displayFCMText(text: String)
    fun displayFCMToken(token: String)
    fun displayNoFCMToken()
}