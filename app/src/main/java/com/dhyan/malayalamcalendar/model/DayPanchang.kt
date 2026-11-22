package com.dhyan.malayalamcalendar.model

data class DayPanchang(
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val fullDateString: String = "",
    val localDate: String = "",         // Malayalam day of month, e.g. "17"
    val malayalamMonth: String = "",    // Malayalam month name, e.g. "ധനു"
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
    val location: String = "Kerala, India"
)
