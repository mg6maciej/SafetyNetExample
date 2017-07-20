package pl.mg6.safetynet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_main.*
import pl.mg6.rxjava2.disposeondestroy.disposeOnDestroy

class MainActivity : AppCompatActivity() {

    private val loginApi = LoginApi.get()
    private val protectedApi = ProtectedApi.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submit.setOnClickListener {
            login(nameView.text.toString(), passwordView.text.toString())
        }
    }

    private fun login(name: String, password: String) {
        loginApi.requestNonce(name, password)
                .flatMap { attest(it.decodeFromBase64()) }
                .flatMap { loginApi.verify(it.jwsResult) }
                .flatMap { protectedApi.getSecret(it) }
                .subscribeOn(io())
                .observeOn(mainThread())
                .doOnSubscribe { loader.visibility = VISIBLE }
                .doFinally { loader.visibility = INVISIBLE }
                .disposeOnDestroy(this)
                .subscribe(this::onSecret, this::onError)
    }

    private fun attest(nonce: ByteArray): Single<SafetyNetApi.AttestationResponse> {
        return Single.create<SafetyNetApi.AttestationResponse> {
            SafetyNet.getClient(this).attest(nonce, apiKey)
                    .addOnSuccessListener(it::onSuccess)
                    .addOnFailureListener(it::onError)
        }
    }

    private fun String.decodeFromBase64() = Base64.decode(this, Base64.DEFAULT)

    private fun onSecret(secret: String) {
        secretView.text = "Got: $secret"
    }

    private fun onError(error: Throwable) {
        if (error is ApiException) {
            Log.e("TAG", "ApiException: ${error.statusCode} ${error.statusMessage}")
        }
        Log.e("TAG", "error", error)
        secretView.text = "Got: $error"
    }

    companion object {

        const val apiKey = "AIzaSyA3VTG6cRblrvHhnclrkDst1HcoG0-wMpE"
    }
}
