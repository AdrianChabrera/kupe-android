package app.kupe.core.network

import app.kupe.data.remote.ErrorCodes
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException

class ApiException(
    val kind: Kind,
    val errorCode: String? = null,
    val status: Int? = null,
) : Exception("${kind.name}${errorCode?.let { ": $it" } ?: ""}") {

    enum class Kind { NETWORK, VALIDATION, UNAUTHORIZED, CONFLICT, NOT_FOUND, SERVER, UNKNOWN }

    fun hasCode(code: String) = errorCode == code
}

suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: ApiException) {
    Result.failure(e)
} catch (e: HttpException) {
    Result.failure(e.toApiException())
} catch (e: IOException) {
    Result.failure(ApiException(ApiException.Kind.NETWORK))
} catch (e: Exception) {
    Result.failure(ApiException(ApiException.Kind.UNKNOWN))
}

private fun HttpException.toApiException(): ApiException {
    val status = code()
    val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
    val code = body
        ?.let { runCatching { ApiJson.parseToJsonElement(it).jsonObject["errorCode"]?.jsonPrimitive?.contentOrNull }.getOrNull() }
        ?.takeIf { it in ErrorCodes.ALL }
    val kind = when {
        status == 400 && code == null -> ApiException.Kind.VALIDATION
        status == 401 -> ApiException.Kind.UNAUTHORIZED
        status == 404 -> ApiException.Kind.NOT_FOUND
        status == 409 -> ApiException.Kind.CONFLICT
        status >= 500 -> ApiException.Kind.SERVER
        else -> ApiException.Kind.UNKNOWN
    }
    return ApiException(kind, code, status)
}
