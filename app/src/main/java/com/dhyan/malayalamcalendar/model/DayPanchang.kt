package com.dhyan.malayalamcalendar.model

data class DayPanchang(
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val fullDateString: String = "",
    val localDate: String = "",         // Malayalam day of month, e.g. "17"
    val malayalamMonth: String = "",    // Malayalam month name, e.g. "ധനു"
    val kollamYear: Int = 1201,         // e.g. 1201 or 1202
    val dayOfWeek: Int = 1,             // 1 = Sunday, 7 = Saturday
    val nakshathram: List<String> = emptyList(),
    val tithi: List<String> = emptyList(),
    val sunrise: String = "",
    val sunset: String = "",
    val moonrise: String = "",
    val moonset: String = "",
    val rahuKalam: String = "",
    val gulikaiKalam: String = "",
    val yamaganda: String = "",
    val abhijit: String = "",
    val brahmaMuhurta: String = "",
    val amritKalam: String = "",
    val festivals: List<String> = emptyList(),
    val isHoliday: Boolean = false,
    val isBankHoliday: Boolean = false,
    val isSunday: Boolean = false,
    val moonPhase: String? = null,      // "⚫" for Amavasya, "⚪" for Pournami
    val shakaDate: String = "",         // e.g. "ശ്രാവണം 25, 1948"
    val hijriDate: String = "",         // e.g. "റബീഉൽ അവ്വൽ 2, 1448"
    val location: String = "Kerala, India"
)
