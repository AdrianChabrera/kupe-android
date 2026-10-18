package app.kupe.data.remote

object Flags {
    const val HOST = "flagcdn.com"
    private val VALID_CODE = Regex("^[a-z]{2}(-[a-z]{2,3})?$")

    fun forCountry(iso2: String): String? = url(iso2.lowercase())

    fun forLanguage(code: String): String? {
        val normalized = code.lowercase().substringBefore('-').substringBefore('_')
        return LANGUAGE_TO_FLAG[normalized]?.let(::url)
    }

    private fun url(code: String): String? =
        code.takeIf { VALID_CODE.matches(it) }?.let { "https://$HOST/w160/$it.png" }

    private val LANGUAGE_TO_FLAG = mapOf(
        "en" to "gb", "eng" to "gb", "es" to "es", "spa" to "es", "fr" to "fr", "fra" to "fr", "de" to "de", "deu" to "de",
        "it" to "it", "ita" to "it", "pt" to "pt", "por" to "pt", "nl" to "nl", "nld" to "nl", "ru" to "ru", "rus" to "ru",
        "zh" to "cn", "zho" to "cn", "cmn" to "cn", "ja" to "jp", "jpn" to "jp", "ko" to "kr", "kor" to "kr",
        "ar" to "sa", "ara" to "sa", "hi" to "in", "hin" to "in", "bn" to "bd", "ben" to "bd", "pl" to "pl", "tr" to "tr",
        "sv" to "se", "da" to "dk", "no" to "no", "nb" to "no", "nn" to "no", "fi" to "fi", "el" to "gr", "cs" to "cz",
        "sk" to "sk", "hu" to "hu", "ro" to "ro", "bg" to "bg", "uk" to "ua", "he" to "il", "th" to "th", "vi" to "vn",
        "id" to "id", "ms" to "my", "fa" to "ir", "ur" to "pk", "ca" to "es-ct", "gl" to "es-ga", "eu" to "es-pv",
        "hr" to "hr", "sr" to "rs", "sl" to "si", "lt" to "lt", "lv" to "lv", "et" to "ee", "is" to "is", "ga" to "ie",
        "cy" to "gb-wls", "sw" to "tz", "af" to "za", "zu" to "za", "xh" to "za", "am" to "et", "ka" to "ge", "hy" to "am",
        "az" to "az", "kk" to "kz", "uz" to "uz", "mn" to "mn", "ne" to "np", "si" to "lk", "ta" to "in", "te" to "in",
        "mr" to "in", "gu" to "in", "pa" to "in", "ml" to "in", "kn" to "in", "my" to "mm", "km" to "kh", "lo" to "la",
        "tl" to "ph", "fil" to "ph", "mt" to "mt", "sq" to "al", "mk" to "mk", "bs" to "ba", "be" to "by", "lb" to "lu",
        "la" to "va", "ps" to "af", "so" to "so", "ha" to "ng", "yo" to "ng",
    )
}
