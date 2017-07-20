package pl.mg6.safetynet

import com.google.api.client.json.webtoken.JsonWebToken
import com.google.api.client.util.Key

class AttestationStatement(
        @Key var nonce: String,
        @Key var timestampMs: Long,
        @Key var apkPackageName: String,
        @Key var apkCertificateDigestSha256: Array<String>,
        @Key var apkDigestSha256: String,
        @Key var ctsProfileMatch: Boolean,
        @Key var basicIntegrity: Boolean) : JsonWebToken.Payload() {

    constructor() : this("", 0, "", arrayOf<String>(), "", false, false)
}
