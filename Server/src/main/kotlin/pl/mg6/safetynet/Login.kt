package pl.mg6.safetynet

class Login(
        val nonce: String,
        val createdAt: Long,
        var statement: AttestationStatement? = null,
        var token: String? = null)
