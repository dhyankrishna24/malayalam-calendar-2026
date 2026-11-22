package com.dhyan.malayalamcalendar.data

import android.content.Context
import android.util.Base64
import com.dhyan.malayalamcalendar.model.DayPanchang
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Calendar

data class PrayerTimes(
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val subhi: String,
    val udayam: String
)

object CalendarRepository {
    private val cache = mutableMapOf<String, List<DayPanchang>>()
    private val holidaysCache = mutableMapOf<String, Map<String, List<Pair<Int, String>>>>()

    private val malayalamMonthPairs = listOf(
        "ധനു - മകരം",    // Jan (1)
        "മകരം - കുംഭം",   // Feb (2)
        "കുംഭം - മീനം",   // Mar (3)
        "മീനം - മേടം",    // Apr (4)
        "മേടം - ഇടവം",    // May (5)
        "ഇടവം - മിഥുനം",   // Jun (6)
        "മിഥുനം - കർക്കടകം",// Jul (7)
        "കർക്കടകം - ചിങ്ങം",// Aug (8)
        "ചിങ്ങം - കന്നി",   // Sep (9)
        "കന്നി - തുലാം",    // Oct (10)
        "തുലാം - വൃശ്ചികം", // Nov (11)
        "വൃശ്ചികം - ധനു"    // Dec (12)
    )

    private val monthFirstHalf = listOf(
        "ധനു", "മകരം", "കുംഭം", "മീനം", "മേടം", "ഇടവം",
        "മിഥുനം", "കർക്കടകം", "ചിങ്ങം", "കന്നി", "തുലാം", "വൃശ്ചികം"
    )

    private val monthSecondHalf = listOf(
        "മകരം", "കുംഭം", "മീനം", "മേടം", "ഇടവം", "മിഥുനം",
        "കർക്കടകം", "ചിങ്ങം", "കന്നി", "തുലാം", "വൃശ്ചികം", "ധനു"
    )

    private val sakaMonthNames = listOf(
        "ചൈത്രം", "വൈശാഖം", "ജ്യേഷ്ഠം", "ആഷാഢം",
        "ശ്രാവണം", "ഭാദ്രപദം", "ആശ്വിനം", "കാർത്തികം",
        "മാർഗ്ഗശീർഷം", "പൗഷം", "മാഘം", "ഫാൽഗുനം"
    )

    private val hijriMonthNames = listOf(
        "മുഹറം", "സഫർ", "റബീഉൽ അവ്വൽ", "റബീഉൽ ആഖിർ",
        "ജമാദുൽ അവ്വൽ", "ജമാദുൽ ആഖിർ", "റജബ്", "ശഅ്ബാൻ",
        "റമദാൻ", "ശവ്വാൽ", "ദുൽഖഅ്ദ്", "ദുൽഹിജ്ജ"
    )

    fun getMonthMalayalamName(month: Int): String {
        return if (month in 1..12) malayalamMonthPairs[month - 1] else ""
    }

    fun getKollamSubtitle(year: Int, month: Int): String {
        if (month !in 1..12) return ""
        val pair = malayalamMonthPairs[month - 1]
        return when (year) {
            2026 -> {
                when {
                    month < 8 -> "1201 $pair"
                    month == 8 -> "1201 കർക്കടകം - 1202 ചിങ്ങം"
                    else -> "1202 $pair"
                }
            }
            2027 -> {
                when {
                    month < 8 -> "1202 $pair"
                    month == 8 -> "1202 കർക്കടകം - 1203 ചിങ്ങം"
                    else -> "1203 $pair"
                }
            }
            else -> "1202 $pair"
        }
    }

    private fun loadHolidaysForYear(context: Context, year: Int, isMalayalam: Boolean): Map<String, List<Pair<Int, String>>> {
        val cacheKey = "holidays-$year-$isMalayalam"
        holidaysCache[cacheKey]?.let { return it }

        val langFolder = if (isMalayalam) "ml" else "en"
        val path = "$year/$langFolder/data/Holidays/holidays.kjs"
        val result = mutableMapOf<String, List<Pair<Int, String>>>()

        try {
            val inputStream = context.assets.open(path)
            val base64Str = inputStream.bufferedReader().use { it.readText() }
            val jsonStr = String(Base64.decode(base64Str, Base64.DEFAULT), StandardCharsets.UTF_8)
            val jsonRoot = JSONObject(jsonStr)

            val categories = listOf("Government", "Hindu", "Islamic", "Christian")
            for (cat in categories) {
                val catArr = jsonRoot.optJSONArray(cat) ?: continue
                val items = mutableListOf<Pair<Int, String>>()
                for (m in 0 until catArr.length()) {
                    val mArr = catArr.optJSONArray(m) ?: continue
                    for (i in 0 until mArr.length()) {
                        val item = mArr.optJSONObject(i) ?: continue
                        val title = item.optString("title", "").trim()
                        val dateStr = item.optString("date", "") // e.g. "Fri Jan 02 2026"
                        val parts = dateStr.split(" ")
                        if (parts.size >= 3) {
                            val dayNum = parts[2].toIntOrNull() ?: 0
                            val monthIndex = when (parts[1].take(3).lowercase()) {
                                "jan" -> 1; "feb" -> 2; "mar" -> 3; "apr" -> 4; "may" -> 5; "jun" -> 6
                                "jul" -> 7; "aug" -> 8; "sep" -> 9; "oct" -> 10; "nov" -> 11; "dec" -> 12
                                else -> m + 1
                            }
                            if (dayNum > 0) {
                                val monthKey = "$cat-$monthIndex"
                                val existing = result[monthKey]?.toMutableList() ?: mutableListOf()
                                existing.add(dayNum to title)
                                result[monthKey] = existing
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holidaysCache[cacheKey] = result
        return result
    }

    fun loadMonthDays(context: Context, year: Int, month: Int, isMalayalam: Boolean = true): List<DayPanchang> {
        val cacheKey = "$year-$month-$isMalayalam"
        cache[cacheKey]?.let { return it }

        val langFolder = if (isMalayalam) "ml" else "en"
        val assetPath = "$year/$langFolder/day_view/$month.kjs"

        val holidaysMap = loadHolidaysForYear(context, year, isMalayalam)
        val govHolidaysForMonth = holidaysMap["Government-$month"] ?: emptyList()
        val hinduHolidaysForMonth = holidaysMap["Hindu-$month"] ?: emptyList()
        val islamicHolidaysForMonth = holidaysMap["Islamic-$month"] ?: emptyList()
        val christianHolidaysForMonth = holidaysMap["Christian-$month"] ?: emptyList()

        val daysList = mutableListOf<DayPanchang>()

        try {
            val inputStream = context.assets.open(assetPath)
            val base64Str = inputStream.bufferedReader().use { it.readText() }
            val jsonStr = String(Base64.decode(base64Str, Base64.DEFAULT), StandardCharsets.UTF_8)
            val jsonRoot = JSONObject(jsonStr)

            val keys = jsonRoot.keys()
            val sortedKeys = mutableListOf<String>()
            while (keys.hasNext()) {
                sortedKeys.add(keys.next())
            }
            sortedKeys.sortBy { key ->
                key.trim().take(2).toIntOrNull() ?: 0
            }

            var hasCrossedSankranti = false

            val calHelper = Calendar.getInstance()
            calHelper.set(year, month - 1, 1)
            val firstDayOfWeek = calHelper.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 7 = Sat

            for (key in sortedKeys) {
                val dayNum = key.trim().take(2).toIntOrNull() ?: 1
                val array = jsonRoot.optJSONArray(key) ?: continue
                if (array.length() == 0) continue
                val dayObj = array.getJSONObject(0)

                val localDateArr = dayObj.optJSONArray("local_date")
                val localDateStr = if (localDateArr != null && localDateArr.length() > 0) {
                    localDateArr.getString(0)
                } else ""

                if (localDateStr == "1" && dayNum > 1) {
                    hasCrossedSankranti = true
                }

                val currentMalMonth = if (hasCrossedSankranti) {
                    monthSecondHalf[month - 1]
                } else {
                    monthFirstHalf[month - 1]
                }

                // Determine Day of Week (1 = Sunday, 7 = Saturday)
                val currentDayOfWeek = ((firstDayOfWeek - 1 + (dayNum - 1)) % 7) + 1
                val isSunday = currentDayOfWeek == 1

                // Determine Kollam Year
                val kollamYear = when (year) {
                    2026 -> {
                        if (month < 8) 1201
                        else if (month == 8) {
                            if (hasCrossedSankranti || dayNum >= 17) 1202 else 1201
                        } else 1202
                    }
                    2027 -> {
                        if (month < 8) 1202
                        else if (month == 8) {
                            if (hasCrossedSankranti || dayNum >= 17) 1203 else 1202
                        } else 1203
                    }
                    else -> 1202
                }

                // Collect Festivals & Observances
                val festivalsArr = dayObj.optJSONArray("festivals")
                val festivals = mutableListOf<String>()
                if (festivalsArr != null) {
                    for (i in 0 until festivalsArr.length()) {
                        val fest = festivalsArr.getString(i).trim()
                        if (fest.isNotEmpty() && fest != "NA") {
                            festivals.add(fest)
                        }
                    }
                }

                // Add from holiday lists
                var isGovHoliday = false
                govHolidaysForMonth.filter { it.first == dayNum }.forEach {
                    isGovHoliday = true
                    if (!festivals.contains(it.second)) festivals.add(it.second)
                }
                hinduHolidaysForMonth.filter { it.first == dayNum }.forEach {
                    if (!festivals.contains(it.second)) festivals.add(it.second)
                }
                islamicHolidaysForMonth.filter { it.first == dayNum }.forEach {
                    if (!festivals.contains(it.second)) festivals.add(it.second)
                }
                christianHolidaysForMonth.filter { it.first == dayNum }.forEach {
                    if (!festivals.contains(it.second)) festivals.add(it.second)
                }

                // Bank Holiday Calculation:
                // Sunday, 2nd Saturday, 4th Saturday, or Government Holiday
                val isSaturday = currentDayOfWeek == 7
                val saturdayOrdinal = if (isSaturday) ((dayNum - 1) / 7) + 1 else 0
                val isBankHoliday = isSunday || (isSaturday && (saturdayOrdinal == 2 || saturdayOrdinal == 4)) || isGovHoliday

                fun getList(field: String): List<String> {
                    val arr = dayObj.optJSONArray(field) ?: return emptyList()
                    val list = mutableListOf<String>()
                    for (i in 0 until arr.length()) {
                        val v = arr.getString(i).trim()
                        if (v.isNotEmpty() && v != "NA") list.add(v)
                    }
                    return list
                }

                fun getFirst(field: String): String {
                    val list = getList(field)
                    return if (list.isNotEmpty()) list[0] else ""
                }

                val tithiList = getList("Tithi")

                // Moon Phase: Amavasya (⚫), Pournami (⚪)
                val moonPhase = when {
                    tithiList.any { it.contains("അമാവാസി") || it.contains("Amavasya") } -> "⚫"
                    tithiList.any { it.contains("പൌർണമി") || it.contains("പൂർണ്ണിമ") || it.contains("Pournami") || it.contains("Purnima") } -> "⚪"
                    else -> null
                }

                val dayPanchang = DayPanchang(
                    gregorianDay = dayNum,
                    gregorianMonth = month,
                    gregorianYear = year,
                    fullDateString = dayObj.optString("date", key),
                    localDate = localDateStr,
                    malayalamMonth = currentMalMonth,
                    kollamYear = kollamYear,
                    dayOfWeek = currentDayOfWeek,
                    nakshathram = getList("Nakshathram"),
                    tithi = tithiList,
                    sunrise = getFirst("Sunrise"),
                    sunset = getFirst("Sunset"),
                    moonrise = getFirst("Moonrise"),
                    moonset = getFirst("Moonset"),
                    rahuKalam = getStandardRahuTiming(currentDayOfWeek),
                    gulikaiKalam = getStandardGulikaTiming(currentDayOfWeek),
                    yamaganda = getFirst("Yamaganda"),
                    abhijit = getFirst("Abhijit"),
                    brahmaMuhurta = getFirst("Brahma Muhurta"),
                    amritKalam = getFirst("Amrit Kalam"),
                    festivals = festivals,
                    isHoliday = isSunday || isBankHoliday || festivals.isNotEmpty(),
                    isBankHoliday = isBankHoliday,
                    isSunday = isSunday,
                    moonPhase = moonPhase,
                    shakaDate = getSakaDate(year, month, dayNum),
                    hijriDate = getHijriDate(year, month, dayNum),
                    location = dayObj.optString("location", "Kerala, India")
                )
                daysList.add(dayPanchang)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cache[cacheKey] = daysList
        return daysList
    }

    // Traditional Standard Rahu Kalam (based on 6:00 AM Sunrise)
    fun getStandardRahuTiming(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "04.30 pm - 06.00 pm" // Sun
            2 -> "07.30 am - 09.00 am" // Mon
            3 -> "03.00 pm - 04.30 pm" // Tue
            4 -> "12.00 pm - 01.30 pm" // Wed
            5 -> "01.30 pm - 03.00 pm" // Thu
            6 -> "10.30 am - 12.00 pm" // Fri
            7 -> "09.00 am - 10.30 am" // Sat
            else -> "04.30 pm - 06.00 pm"
        }
    }

    // Traditional Standard Gulikai Kalam (based on 6:00 AM Sunrise)
    fun getStandardGulikaTiming(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "03.00 pm - 04.30 pm" // Sun
            2 -> "01.30 pm - 03.00 pm" // Mon
            3 -> "12.00 pm - 01.30 pm" // Tue
            4 -> "10.30 am - 12.00 pm" // Wed
            5 -> "09.00 am - 10.30 am" // Thu
            6 -> "07.30 am - 09.00 am" // Fri
            7 -> "06.00 am - 07.30 am" // Sat
            else -> "03.00 pm - 04.30 pm"
        }
    }

    // Muslim Prayer Times Calculation for Kozhikode (Calicut)
    fun getKozhikodePrayerTimes(month: Int, day: Int): PrayerTimes {
        val cal = Calendar.getInstance()
        cal.set(2026, month - 1, day)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        val b = 2 * Math.PI * (dayOfYear - 81) / 365.0
        val eot = 9.87 * Math.sin(2 * b) - 7.53 * Math.cos(b) - 1.5 * Math.sin(b) // minutes
        val decl = 23.45 * Math.sin(b) * Math.PI / 180.0

        val lat = 11.25 * Math.PI / 180.0
        val lon = 75.78

        // Solar noon in IST (UTC+5.5 -> 82.5 deg)
        val solarNoonMinutes = 12 * 60 + (82.5 - lon) * 4 - eot

        // Sunrise & Sunset angle (-0.833 deg)
        val cosHaSun = (Math.sin(-0.833 * Math.PI / 180.0) - Math.sin(lat) * Math.sin(decl)) / (Math.cos(lat) * Math.cos(decl))
        val haSun = Math.acos(Math.max(-1.0, Math.min(1.0, cosHaSun))) * 180.0 / Math.PI

        val sunriseMin = solarNoonMinutes - haSun * 4
        val sunsetMin = solarNoonMinutes + haSun * 4

        // Fajr (-18 deg)
        val cosHaFajr = (Math.sin(-18.0 * Math.PI / 180.0) - Math.sin(lat) * Math.sin(decl)) / (Math.cos(lat) * Math.cos(decl))
        val haFajr = Math.acos(Math.max(-1.0, Math.min(1.0, cosHaFajr))) * 180.0 / Math.PI
        val fajrMin = solarNoonMinutes - haFajr * 4

        // Isha (-17.5 deg)
        val cosHaIsha = (Math.sin(-17.5 * Math.PI / 180.0) - Math.sin(lat) * Math.sin(decl)) / (Math.cos(lat) * Math.cos(decl))
        val haIsha = Math.acos(Math.max(-1.0, Math.min(1.0, cosHaIsha))) * 180.0 / Math.PI
        val ishaMin = solarNoonMinutes + haIsha * 4

        // Asr
        val noonAlt = Math.PI / 2 - Math.abs(lat - decl)
        val shadowNoon = 1.0 / Math.tan(noonAlt)
        val asrAlt = Math.atan(1.0 / (shadowNoon + 1.0))
        val cosHaAsr = (Math.sin(asrAlt) - Math.sin(lat) * Math.sin(decl)) / (Math.cos(lat) * Math.cos(decl))
        val haAsr = Math.acos(Math.max(-1.0, Math.min(1.0, cosHaAsr))) * 180.0 / Math.PI
        val asrMin = solarNoonMinutes + haAsr * 4

        fun formatTime(minutes: Double, isPm: Boolean): String {
            val total = Math.round(minutes).toInt()
            var h = total / 60
            val m = total % 60
            if (isPm && h > 12) h -= 12
            return String.format("%d.%02d", h, m)
        }

        return PrayerTimes(
            dhuhr = formatTime(solarNoonMinutes, true),
            asr = formatTime(asrMin, true),
            maghrib = formatTime(sunsetMin, true),
            isha = formatTime(ishaMin, true),
            subhi = formatTime(fajrMin, false),
            udayam = formatTime(sunriseMin, false)
        )
    }

    // Indian National Saka Calendar Conversion
    fun getSakaDate(year: Int, month: Int, day: Int): String {
        // Starts with Chaitra 1 on March 22
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        // Mar 22 is day 81 in non-leap year
        val sakaYear = if (dayOfYear >= 81) year - 78 else year - 79
        val sakaDayOffset = if (dayOfYear >= 81) dayOfYear - 81 else (dayOfYear + 365 - 81)

        val monthLengths = listOf(30, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30)
        var remainingDays = sakaDayOffset
        var sakaMonthIdx = 0

        for (i in monthLengths.indices) {
            if (remainingDays < monthLengths[i]) {
                sakaMonthIdx = i
                break
            }
            remainingDays -= monthLengths[i]
        }

        val sakaDay = remainingDays + 1
        val sakaMonth = sakaMonthNames[sakaMonthIdx]
        return "$sakaMonth $sakaDay, $sakaYear"
    }

    // Islamic Hijri Calendar Conversion
    fun getHijriDate(year: Int, month: Int, day: Int): String {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        var l = jdn - 1948440 + 10632
        val n = (l - 1) / 10631
        l = l - 10631 * n + 354
        val j = ((10985 - l) / 5316) * ((50 * l) / 17719) + (l / 5670) * ((43 * l) / 15238)
        l = l - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val mH = (24 * l) / 709
        val dH = l - (709 * mH) / 24
        val yH = 30 * n + j - 30

        val monthIdx = (mH - 1).coerceIn(0, 11)
        val monthName = hijriMonthNames[monthIdx]
        return "$monthName $dH, $yH"
    }
}
