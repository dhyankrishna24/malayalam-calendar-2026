package com.dhyan.malayalamcalendar.data

import android.content.Context
import android.util.Base64
import com.dhyan.malayalamcalendar.model.DayPanchang
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object CalendarRepository {
    private val cache = mutableMapOf<String, List<DayPanchang>>()

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

    fun getMonthMalayalamName(month: Int): String {
        return if (month in 1..12) malayalamMonthPairs[month - 1] else ""
    }

    fun loadMonthDays(context: Context, year: Int, month: Int, isMalayalam: Boolean = true): List<DayPanchang> {
        val cacheKey = "$year-$month-$isMalayalam"
        cache[cacheKey]?.let { return it }

        val langFolder = if (isMalayalam) "ml" else "en"
        val assetPath = "$year/$langFolder/day_view/$month.kjs"

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

                val dayPanchang = DayPanchang(
                    gregorianDay = dayNum,
                    gregorianMonth = month,
                    gregorianYear = year,
                    fullDateString = dayObj.optString("date", key),
                    localDate = localDateStr,
                    malayalamMonth = currentMalMonth,
                    nakshathram = getList("Nakshathram"),
                    tithi = getList("Tithi"),
                    sunrise = getFirst("Sunrise"),
                    sunset = getFirst("Sunset"),
                    moonrise = getFirst("Moonrise"),
                    moonset = getFirst("Moonset"),
                    rahuKalam = getFirst("Rahu Kalam"),
                    gulikaiKalam = getFirst("Gulikai Kalam"),
                    yamaganda = getFirst("Yamaganda"),
                    abhijit = getFirst("Abhijit"),
                    brahmaMuhurta = getFirst("Brahma Muhurta"),
                    amritKalam = getFirst("Amrit Kalam"),
                    festivals = festivals,
                    isHoliday = festivals.isNotEmpty(),
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
}
