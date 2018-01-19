package go.vienna.fcmexample.fcmexampleapp.retrofit

import go.vienna.fcmexample.fcmexampleapp.retrofit.messages.TokenMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
interface ServerService {
    @POST("/fcm/register")
    fun registerFCMID(@Body tokenMessage: TokenMessage): Call<Void>
}