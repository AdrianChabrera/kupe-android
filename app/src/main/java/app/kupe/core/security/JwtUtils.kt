package app.kupe.core.security

import android.util.Base64
import app.kupe.core.network.ApiJson
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JwtUtils {
    private val USER_ID_CLAIMS = listOf(
        "http://schemas.xmlsoap.org/ws/2005/12/identity/claims/nameidentifier",
        "nameid",
        "sub",
    )

    fun userId(jwt: String): String? = runCatching {
        val payload = jwt.split('.')[1]
        val json = String(
            Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP),
            Charsets.UTF_8,
        )
        val claims = ApiJson.parseToJsonElement(json).jsonObject
        USER_ID_CLAIMS.firstNotNullOfOrNull { claims[it]?.jsonPrimitive?.contentOrNull }
    }.getOrNull()
}
