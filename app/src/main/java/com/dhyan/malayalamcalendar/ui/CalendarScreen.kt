package com.dhyan.malayalamcalendar.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhyan.malayalamcalendar.data.CalendarRepository
import com.dhyan.malayalamcalendar.model.DayPanchang
import com.dhyan.malayalamcalendar.ui.theme.*
import java.util.Calendar

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val todayCal = remember { Calendar.getInstance() }
    val initialYear = remember {
        val y = todayCal.get(Calendar.YEAR)
        if (y in 2026..2027) y else 2026
    }
    val initialMonth = remember {
        val y = todayCal.get(Calendar.YEAR)
        if (y in 2026..2027) todayCal.get(Calendar.MONTH) + 1 else 1
    }

    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) } // 1-12
    var selectedDay by remember { mutableStateOf<DayPanchang?>(null) }

    val monthNamesMl = remember {
        listOf("ജനുവരി", "ഫെബ്രുവരി", "മാർച്ച്", "ഏപ്രിൽ", "മേയ്", "ജൂൺ", "ജൂലൈ", "ഓഗസ്റ്റ്", "സെപ്റ്റംബർ", "ഒക്ടോബർ", "നവംബർ", "ഡിസംബർ")
    }
    val weekDaysFullMl = remember {
        listOf("ഞായർ", "തിങ്കൾ", "ചൊവ്വ", "ബുധൻ", "വ്യാഴം", "വെള്ളി", "ശനി")
    }
    val weekDaysShortEn = remember {
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    }

    val days = remember(selectedYear, selectedMonth) {
        CalendarRepository.loadMonthDays(context, selectedYear, selectedMonth, isMalayalam = true)
    }

    val startDayOfWeek = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth - 1, 1)
        cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
    }
    val leadingEmptyCount = startDayOfWeek - 1

    val daysInPrevMonth = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth - 1, 1)
        cal.add(Calendar.DATE, -1)
        cal.get(Calendar.DAY_OF_MONTH)
    }

    val totalGridCells = leadingEmptyCount + days.size
    val trailingEmptyCount = if (totalGridCells % 7 == 0) 0 else 7 - (totalGridCells % 7)

    // Handle physical / gesture back button to close Day Details
    BackHandler(enabled = selectedDay != null) {
        selectedDay = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CalendarPaperBg
    ) {
        if (selectedDay != null) {
            // Full Date Details Screen matching template
            DateDetailsView(
                day = selectedDay!!,
                monthNameMl = monthNamesMl[selectedDay!!.gregorianMonth - 1],
                weekDayNameMl = weekDaysFullMl[selectedDay!!.dayOfWeek - 1],
                onBack = { selectedDay = null }
            )
        } else {
            // Monthly Calendar Grid matching template
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Surface(
                    color = TempleMaroon,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "മലയാളം കലണ്ടർ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Jump to Today Button
                            val isAtToday = selectedYear == initialYear && selectedMonth == initialMonth
                            TopBarPill(
                                text = "ഇന്ന്",
                                isActive = isAtToday,
                                onClick = {
                                    selectedYear = initialYear
                                    selectedMonth = initialMonth
                                }
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // Year Selector: 2026 / 2027
                            TopBarPill(
                                text = "2026",
                                isActive = selectedYear == 2026,
                                onClick = { selectedYear = 2026 }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TopBarPill(
                                text = "2027",
                                isActive = selectedYear == 2027,
                                onClick = { selectedYear = 2027 }
                            )
                        }
                    }
                }

                // Month Navigation Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TempleMaroonDark)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Month Button (<)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    if (selectedMonth > 1) {
                                        selectedMonth--
                                    } else if (selectedYear > 2026) {
                                        selectedYear--
                                        selectedMonth = 12
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❮",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        // Centered Month & Kollam Subtitle
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val currentMonthName = monthNamesMl[selectedMonth - 1]
                            val kollamSubtitle = CalendarRepository.getKollamSubtitle(selectedYear, selectedMonth)

                            Text(
                                text = "$currentMonthName $selectedYear",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (kollamSubtitle.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = kollamSubtitle,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.95f)
                                )
                            }
                        }

                        // Next Month Button (>)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    if (selectedMonth < 12) {
                                        selectedMonth++
                                    } else if (selectedYear < 2027) {
                                        selectedYear++
                                        selectedMonth = 1
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❯",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 2-Line Weekday Header (SUN / MON / TUE ...)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WeekdayHeaderBg)
                        .border(1.dp, CalendarGridBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDaysShortEn.forEachIndexed { index, enName ->
                            val isSun = index == 0
                            val mlName = weekDaysFullMl[index]
                            val colColor = if (isSun) SundayRed else DetailsTextDark

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = enName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colColor
                                    )
                                    Text(
                                        text = mlName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Monthly Grid Table
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CalendarPaperBg),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // Leading days from previous month (Dimmed)
                    items(leadingEmptyCount) { index ->
                        val dayNum = daysInPrevMonth - leadingEmptyCount + 1 + index
                        DimmedDayCell(dayNumber = dayNum)
                    }

                    // Active Days of Current Month
                    itemsIndexed(days) { _, day ->
                        val isToday = (todayCal.get(Calendar.YEAR) == selectedYear &&
                                todayCal.get(Calendar.MONTH) + 1 == selectedMonth &&
                                todayCal.get(Calendar.DAY_OF_MONTH) == day.gregorianDay)

                        KeralaDayCell(
                            day = day,
                            isToday = isToday,
                            onClick = { selectedDay = day }
                        )
                    }

                    // Trailing days from next month (Dimmed)
                    items(trailingEmptyCount) { index ->
                        val dayNum = index + 1
                        DimmedDayCell(dayNumber = dayNum)
                    }
                }
            }
        }
    }
}

@Composable
fun KeralaDayCell(
    day: DayPanchang,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val isRedDay = day.isSunday || day.isBankHoliday
    val cellBorder = if (isToday) TodayBorder else CalendarGridBorder
    val borderWidth = if (isToday) 2.dp else 0.5.dp
    val cellBg = if (isToday) TodayBg else Color.White

    Box(
        modifier = Modifier
            .aspectRatio(0.80f)
            .background(cellBg)
            .border(borderWidth, cellBorder)
            .clickable { onClick() }
            .padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        // Top Row: Malayalam Date (Left) & Moon Phase / BH / TODAY (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Malayalam Date / Month Start
            if (day.localDate.isNotEmpty()) {
                val malText = if (day.localDate == "1" && day.gregorianDay > 1 && day.malayalamMonth.isNotEmpty()) {
                    "${day.malayalamMonth.take(3)}\n1"
                } else {
                    day.localDate
                }
                Text(
                    text = malText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetailsTextDark,
                    lineHeight = 10.sp
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Right indicators: Moon Phase & BH
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (day.moonPhase != null) {
                    Text(
                        text = day.moonPhase,
                        fontSize = 9.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                if (day.isBankHoliday && !day.isSunday) {
                    Text(
                        text = "BH",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SundayRed
                    )
                }
            }
        }

        // Center: Large Gregorian Date Number
        Text(
            text = "${day.gregorianDay}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRedDay) SundayRed else DetailsTextDark,
            modifier = Modifier.align(Alignment.Center)
        )

        // Bottom: Nakshathram short text
        val rawNakshatra = day.nakshathram.firstOrNull()?.split(" ")?.firstOrNull() ?: ""
        val shortNakshatra = if (rawNakshatra.length > 5) rawNakshatra.take(4) + "..." else rawNakshatra

        Text(
            text = shortNakshatra,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = DetailsTextDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun DimmedDayCell(dayNumber: Int) {
    Box(
        modifier = Modifier
            .aspectRatio(0.80f)
            .background(Color(0xFFFAFAFA))
            .border(0.5.dp, CalendarGridBorder),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$dayNumber",
            fontSize = 19.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFC0C0C0)
        )
    }
}

@Composable
fun TopBarPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(14.dp)
    val bg = if (isActive) Color.White else Color(0x33FFFFFF)
    val textCol = if (isActive) TempleMaroon else Color.White

    Box(
        modifier = Modifier
            .clip(pillShape)
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
    }
}

// -------------------------------------------------------------
// Day Details View matching unnamed (1), (2), (4).webp
// -------------------------------------------------------------

@Composable
fun DateDetailsView(
    day: DayPanchang,
    monthNameMl: String,
    weekDayNameMl: String,
    onBack: () -> Unit
) {
    val isRedDay = day.isSunday || day.isBankHoliday
    val prayerTimes = remember(day.gregorianMonth, day.gregorianDay) {
        CalendarRepository.getKozhikodePrayerTimes(day.gregorianMonth, day.gregorianDay)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Maroon Top Bar with Back Arrow and Title
        Surface(
            color = TempleMaroon,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(
                        text = "←",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "മലയാളം കലണ്ടർ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Scrollable Day Details Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month, Year Header (e.g. "ഓഗസ്റ്റ്, 2026")
            Text(
                text = "$monthNameMl, ${day.gregorianYear}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DetailsTextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Centered Square White Date Box
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .border(1.5.dp, DateBoxBorder, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${day.gregorianDay}",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRedDay) SundayRed else DetailsTextDark,
                        lineHeight = 56.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = weekDayNameMl,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailsTextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Malayalam Date Subtitle (e.g. "കർക്കടകം 31, 1201")
            val malayalamDateFull = if (day.malayalamMonth.isNotEmpty() && day.localDate.isNotEmpty()) {
                "${day.malayalamMonth} ${day.localDate}, ${day.kollamYear}"
            } else ""

            if (malayalamDateFull.isNotEmpty()) {
                Text(
                    text = malayalamDateFull,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetailsTextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Nakshathram & Tithi
            val nakshatraStr = day.nakshathram.firstOrNull()?.replace(" upto ", " ") ?: "-"
            val tithiStr = day.tithi.firstOrNull()?.replace(" upto ", " ") ?: "-"

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "നക്ഷത്രം : ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimingLabelRed
                )
                Text(
                    text = nakshatraStr,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DetailsTextDark
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "തിഥി : ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimingLabelRed
                )
                Text(
                    text = tithiStr,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DetailsTextDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Warm Golden Festival & Observances Card
            val festivalsText = if (day.festivals.isNotEmpty()) {
                day.festivals.joinToString(", ")
            } else {
                "പ്രത്യേക വിശേഷങ്ങൾ ഒന്നുമില്ല"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(FestivalGoldBg)
                    .border(1.dp, FestivalGoldBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = festivalsText,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FestivalText,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Auspicious & Conversion Timings (Rahu, Gulika, Shaka, Hijri)
            TimingDetailRow(label = "രാഹു :", value = day.rahuKalam)
            TimingDetailRow(label = "ഗുളി :", value = day.gulikaiKalam)

            if (day.shakaDate.isNotEmpty()) {
                TimingDetailRow(label = "ശക :", value = day.shakaDate)
            }
            if (day.hijriDate.isNotEmpty()) {
                TimingDetailRow(label = "ഹിജ്‌റ :", value = day.hijriDate)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Muslim Prayer Times Section (Kozhikode)
            Text(
                text = "മുസ്‌ലിം നമസ്കാര സമയം (കോഴിക്കോട്)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DetailsTextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Line 1: Dhuhr, Asr, Maghrib
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrayerTimeItem(label = "ളൂഹ്ർ", time = prayerTimes.dhuhr)
                Text(text = ",  ", color = DetailsTextDark)
                PrayerTimeItem(label = "അസർ", time = prayerTimes.asr)
                Text(text = ",  ", color = DetailsTextDark)
                PrayerTimeItem(label = "മഗ്‌രിബ്", time = prayerTimes.maghrib)
                Text(text = ",", color = DetailsTextDark)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Line 2: Isha, Subhi, Udayam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrayerTimeItem(label = "ഇശാ", time = prayerTimes.isha)
                Text(text = ",  ", color = DetailsTextDark)
                PrayerTimeItem(label = "സുബ്ഹി", time = prayerTimes.subhi)
                Text(text = ",  ", color = DetailsTextDark)
                PrayerTimeItem(label = "ഉദയം", time = prayerTimes.udayam)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Sun Illustration with Sunrise & Sunset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Illustrated Rising Sun on Left
                SunIllustration(
                    modifier = Modifier.size(width = 110.dp, height = 65.dp)
                )

                Spacer(modifier = Modifier.width(26.dp))

                // Sunrise & Sunset on Right
                Column {
                    val cleanSunrise = day.sunrise.replace(" എ എം", "").replace(" AM", "").trim()
                        .ifEmpty { prayerTimes.udayam }
                    val cleanSunset = day.sunset.replace(" പി എം", "").replace(" PM", "").trim()
                        .ifEmpty { prayerTimes.maghrib }

                    Text(
                        text = "സൂര്യോദയം : $cleanSunrise",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = DetailsTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "അസ്തമയം : $cleanSunset",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = DetailsTextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Footnote at Bottom
            Text(
                text = "ഉദയം ആറു മണിക്ക് എന്ന അടിസ്ഥാനത്തിലാണ് രാഹുകാല, ഗുളികകാല സമയം നൽകിയിട്ടുള്ളത്. ചന്ദ്രദർശനമനുസരിച്ചു മുസ്ലിം അവധിദിവസങ്ങളിൽ മാറ്റം വന്നേക്കാം.",
                fontSize = 12.sp,
                color = DetailsTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun TimingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TimingLabelRed
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DetailsTextDark
        )
    }
}

@Composable
fun PrayerTimeItem(label: String, time: String) {
    Text(
        text = "$label : ",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TimingLabelRed
    )
    Text(
        text = time,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = DetailsTextDark
    )
}

// -------------------------------------------------------------
// Canvas Sun Illustration matching template
// -------------------------------------------------------------

@Composable
fun SunIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val baseY = height * 0.82f
        val centerX = width * 0.5f
        val radius = height * 0.42f

        // Draw baseline
        drawLine(
            color = Color.Black,
            start = Offset(4f, baseY),
            end = Offset(width - 4f, baseY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Draw Sun semicircle fill
        val arcRect = Rect(centerX - radius, baseY - radius, centerX + radius, baseY + radius)
        drawArc(
            color = SunGold,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = arcRect.topLeft,
            size = arcRect.size
        )

        // Outline of Sun semicircle
        drawArc(
            color = Color.Black,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        // Radiating rays around the curved edge
        val rayAngles = listOf(195f, 215f, 235f, 255f, 275f, 295f, 315f, 335f, 345f)
        for (angleDeg in rayAngles) {
            val rad = Math.toRadians(angleDeg.toDouble())
            val cosA = Math.cos(rad).toFloat()
            val sinA = Math.sin(rad).toFloat()
            val startDist = radius + 6f
            val endDist = radius + 17f + if (angleDeg in 250f..290f) 5f else 0f
            drawLine(
                color = Color.Black,
                start = Offset(centerX + cosA * startDist, baseY + sinA * startDist),
                end = Offset(centerX + cosA * endDist, baseY + sinA * endDist),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
        }
    }
}
