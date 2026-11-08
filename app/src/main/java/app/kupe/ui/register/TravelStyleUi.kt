package app.kupe.ui.register

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.kupe.R

enum class TravelStyleUi(
    val code: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int? = null,
) {
    ADVENTURER("adventurer", R.string.style_adventurer_name, R.string.style_adventurer_desc, R.drawable.travel_style_adventurer),
    PLANNER("planner", R.string.style_planner_name, R.string.style_planner_desc, R.drawable.travel_style_planner),
    RELAXER("relaxer", R.string.style_relaxer_name, R.string.style_relaxer_desc, R.drawable.travel_style_relaxer),
    IMPROVISER("improviser", R.string.style_improviser_name, R.string.style_improviser_desc, R.drawable.travel_style_improviser);

    companion object {
        fun fromCode(code: String): TravelStyleUi? = entries.firstOrNull { it.code == code }
    }
}