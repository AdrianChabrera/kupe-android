package app.kupe.ui.register

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.kupe.R

enum class TravelInterestUi(
    val code: String,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int? = null, // TODO: add illustrations
) {
    ARCHITECTURE("architecture", R.string.interest_architecture),
    ART("art", R.string.interest_art),
    BEACHES("beaches", R.string.interest_beaches),
    CASTLES("castles", R.string.interest_castles),
    CAVES("caves", R.string.interest_caves),
    CITIES("cities", R.string.interest_cities),
    FESTIVALS("festivals", R.string.interest_festivals),
    FOOD("food", R.string.interest_food),
    HIKING("hiking", R.string.interest_hiking),
    LUXURY("luxury", R.string.interest_luxury),
    MUSEUMS("museums", R.string.interest_museums),
    PARTIES("parties", R.string.interest_parties),
    PHOTOGRAPHY("photography", R.string.interest_photography),
    ROAD_TRIPS("road_trips", R.string.interest_road_trips),
    RUINS("ruins", R.string.interest_ruins),
    SHOPS("shops", R.string.interest_shops),
    SPORTS("sports", R.string.interest_sports),
    TEMPLES("temples", R.string.interest_temples),
    THEME_PARKS("theme_parks", R.string.interest_theme_parks),
    TOWNS("towns", R.string.interest_towns),
    TRADITIONS("traditions", R.string.interest_traditions);

    companion object {
        fun fromCode(code: String): TravelInterestUi? = entries.firstOrNull { it.code == code }
    }
}
