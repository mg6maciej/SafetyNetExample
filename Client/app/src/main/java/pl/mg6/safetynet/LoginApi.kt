package pl.mg6.safetynet

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApi {

    @GET("login/nonce")
    fun requestNonce(@Query("name") name: String, @Query("password") password: String): Single<String>

    @GET("login/verify")
    fun verify(@Query("jwsResult") jwsResult: String): Single<String>

    companion object {

        fun get() = RetrofitProvider.get().create(LoginApi::class.java)
    }
}
