package pl.mg6.safetynet

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.webtoken.JsonWebSignature
import com.google.api.client.util.Base64
import org.apache.http.conn.ssl.DefaultHostnameVerifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.System.currentTimeMillis
import java.security.SecureRandom

@RestController
class TheController {

    val logins = mutableMapOf<String, Login>()

    @GetMapping("/login/nonce")
    fun nonce(@RequestParam("name") name: String, @RequestParam("password") password: String): String {
        val key = "$name:$password"
        if (key !in credentials) {
            error("Invalid credentials")
        }
        val nonce = generateRandomToken()
        logins.put(key, Login(nonce, currentTimeMillis()))
        return nonce
    }

    @GetMapping("/login/verify")
    fun verify(@RequestParam("jwsResult") jwsResult: String): String {
        val jws = JsonWebSignature.parser(JacksonFactory.getDefaultInstance())
                .setPayloadClass(AttestationStatement::class.java)
                .parse(jwsResult)
        val cert = jws.verifySignature()
                ?: error("Invalid signature")
        hostnameVerifier.verify("attest.android.com", cert)
        val statement = jws.payload as AttestationStatement
        if (!statement.ctsProfileMatch) {
            // NOTE: emulator has ctsProfileMatch == false
            //error("You shall not pass")
        }
        if (statement.apkPackageName != "pl.mg6.safetynet") {
            error("Wrong APK package name")
        }
        if (statement.apkCertificateDigestSha256[0]
                // NOTE: change to SHA256 in base64 of your certificate
                // keytool -list -v -keystore ~/.android/debug.keystore
                // password: android
                // online converter http://tomeko.net/online_tools/hex_to_base64.php?lang=en
                != "MD7VgYhe4S02QWijuO8TtKbs5BLGTNnZOacdaWt6hGk=") {
            error("Wrong APK certificate")
        }
        val login = logins.values.single { it.nonce == statement.nonce }
        if (login.statement != null) {
            error("Nonce can only be used once")
        }
        if (currentTimeMillis() - login.createdAt > 2 * 60 * 1000) {
            error("Nonce is too old")
        }
        login.statement = statement
        val token = generateRandomToken()
        login.token = token
        return token
    }

    @GetMapping("/protected/secret")
    fun secret(@RequestParam("token") token: String): String {
        val login = logins.entries.single { it.value.token == token }
        return "SECRET ${login.key.substringBefore(':')} ${currentTimeMillis()}"
    }

    companion object {

        val credentials = listOf("mg6:pass", "user:feeds")
        val random = SecureRandom()
        val hostnameVerifier = DefaultHostnameVerifier()

        private fun generateRandomToken(): String {
            val array = ByteArray(42)
            random.nextBytes(array)
            return Base64.encodeBase64String(array)
        }
    }
}
