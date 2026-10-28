package app.kupe.ui.register

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.kupe.R

enum class TravelStyleUi(
    val code: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int? = null, // TODO: add ilustrations
) {
    ADVENTURER("adventurer", R.string.style_adventurer_name, R.string.style_adventurer_desc),
    PLANNER("planner", R.string.style_planner_name, R.string.style_planner_desc),
    RELAXER("relaxer", R.string.style_relaxer_name, R.string.style_relaxer_desc),
    IMPROVISER("improviser", R.string.style_improviser_name, R.string.style_improviser_desc);

    companion object {
        fun fromCode(code: String): TravelStyleUi? = entries.firstOrNull { it.code == code }
    }
}