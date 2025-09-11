package org.company.app.domain.model

@Suppress("INTEGER_OVERFLOW")
enum class Period(val title: String, val days: Int) {
    ONE_HOUR("1H", 1),
    ONE_DAY("1D", 1),
    ONE_WEEK("1W", 7),
    ONE_MONTH("1M", 30),
    ONE_YEAR("1Y", 365);

    companion object {
        fun Period.toTimeMillis(): Long = when(this) {
            ONE_HOUR -> 60 * 60 * 1000
            ONE_DAY -> 24 * 60 * 60 * 1000
            ONE_WEEK -> 7 * 24 * 60 * 60 * 1000
            ONE_MONTH -> 30 * 24 * 60 * 60 * 1000
            ONE_YEAR -> 365 * 24 * 60 * 60 * 1000
        }
    }
}