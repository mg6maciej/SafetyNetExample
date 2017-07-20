package pl.mg6.safetynet

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ProtectedApi {

    @GET("protected/secret")
    fun getSecret(@Query("token") token: String): Single<String>

    companion object {

        fun get() = RetrofitProvider.get().create(ProtectedApi::class.java)
    }
}
