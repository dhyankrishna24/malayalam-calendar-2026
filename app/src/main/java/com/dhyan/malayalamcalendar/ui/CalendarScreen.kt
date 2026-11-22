package com.dhyan.malayalamcalendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val todayCal = remember { Calendar.getInstance() }

    var selectedYear by remember { mutableIntStateOf(2026) }
    var selectedMonth by remember { mutableIntStateOf(1) } // 1-12
    var selectedDay by remember { mutableStateOf<DayPanchang?>(null) }
    var isMalayalam by remember { mutableStateOf(true) }

    val monthNamesMl = remember {
        listOf("ജനുവരി", "ഫെബ്രുവരി", "മാർച്ച്", "ഏപ്രിൽ", "മേയ്", "ജൂൺ", "ജൂലൈ", "ഓഗസ്റ്റ്", "സെപ്റ്റംബർ", "ഒക്ടോബർ", "നവംബർ", "ഡിസംബർ")
    }
    val monthNamesEn = remember {
        listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    }
    val weekDaysMl = remember {
        listOf("ഞായർ", "തിങ്കൾ", "ചൊവ്വ", "ബുധൻ", "വ്യാഴം", "വെള്ളി", "ശനി")
    }
    val weekDaysEn = remember {
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    }

    val days = remember(selectedYear, selectedMonth, isMalayalam) {
        CalendarRepository.loadMonthDays(context, selectedYear, selectedMonth, isMalayalam)
    }

    val startDayOfWeek = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth - 1, 1)
        cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
    }

    val totalCells = (startDayOfWeek - 1) + days.size
    val trailingEmptyCells = if (totalCells % 7 == 0) 0 else 7 - (totalCells % 7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LiquidBgTop, LiquidBgMid, LiquidBgBottom)
                )
            )
    ) {
        // Ambient Liquid Light Glow Orbs (Underneath the glass)
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 180.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(LiquidOrbGold, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = 240.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(LiquidOrbRose, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 160.dp, y = 560.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(LiquidOrbCyan, Color.Transparent)))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Floating Liquid Glass Top App Bar
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassCardBrush)
                            .border(1.dp, GlassBorderBrush, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isMalayalam) "മലയാളം കലണ്ടർ" else "Malayalam Calendar",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLiquidPrimary
                                )
                                Text(
                                    text = "2026 - 2027 • പഞ്ചാംഗം",
                                    fontSize = 11.sp,
                                    color = TextLiquidGold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Language Switch Pill
                                GlassPill(
                                    text = if (isMalayalam) "EN" else "മല",
                                    isActive = true,
                                    onClick = { isMalayalam = !isMalayalam }
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Year Switch Pills
                                GlassPill(
                                    text = "2026",
                                    isActive = selectedYear == 2026,
                                    onClick = { selectedYear = 2026 }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                GlassPill(
                                    text = "2027",
                                    isActive = selectedYear == 2027,
                                    onClick = { selectedYear = 2027 }
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Floating Liquid Glass Month Navigation Controller
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GlassCardBrush)
                        .border(1.dp, GlassBorderBrush, RoundedCornerShape(22.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Month Button
                        GlassCircleButton(
                            symbol = "❮",
                            onClick = {
                                if (selectedMonth > 1) {
                                    selectedMonth--
                                } else if (selectedYear > 2026) {
                                    selectedYear--
                                    selectedMonth = 12
                                }
                            }
                        )

                        // Center Month & Year Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val currentMonthName = if (isMalayalam) monthNamesMl[selectedMonth - 1] else monthNamesEn[selectedMonth - 1]
                            val malayalamMonthName = CalendarRepository.getMonthMalayalamName(selectedMonth)

                            Text(
                                text = "$currentMonthName $selectedYear",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextLiquidPrimary
                            )
                            if (malayalamMonthName.isNotEmpty()) {
                                Text(
                                    text = malayalamMonthName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LiquidGold
                                )
                            }
                        }

                        // Next Month Button
                        GlassCircleButton(
                            symbol = "❯",
                            onClick = {
                                if (selectedMonth < 12) {
                                    selectedMonth++
                                } else if (selectedYear < 2027) {
                                    selectedYear++
                                    selectedMonth = 1
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Weekday Header in Liquid Glass Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassFillSubtle)
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val weekDays = if (isMalayalam) weekDaysMl else weekDaysEn
                        weekDays.forEachIndexed { index, dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index == 0) LiquidRose else TextLiquidSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Liquid Glass Calendar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Empty leading slots
                    items(startDayOfWeek - 1) {
                        Box(modifier = Modifier.aspectRatio(0.82f))
                    }

                    // Active Month Days
                    items(days) { day ->
                        val isSunday = (startDayOfWeek - 1 + day.gregorianDay - 1) % 7 == 0
                        val isToday = (todayCal.get(Calendar.YEAR) == selectedYear &&
                                todayCal.get(Calendar.MONTH) + 1 == selectedMonth &&
                                todayCal.get(Calendar.DAY_OF_MONTH) == day.gregorianDay)

                        LiquidGlassDayCell(
                            day = day,
                            isSunday = isSunday,
                            isToday = isToday,
                            isSelected = selectedDay?.gregorianDay == day.gregorianDay,
                            onClick = { selectedDay = day }
                        )
                    }

                    // Empty trailing slots
                    items(trailingEmptyCells) {
                        Box(modifier = Modifier.aspectRatio(0.82f))
                    }
                }
            }
        }

        // Liquid Glass Date Details Bottom Sheet
        selectedDay?.let { day ->
            LiquidGlassDetailsBottomSheet(
                day = day,
                isMalayalam = isMalayalam,
                onDismiss = { selectedDay = null }
            )
        }
    }
}

@Composable
fun LiquidGlassDayCell(
    day: DayPanchang,
    isSunday: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cellShape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .aspectRatio(0.82f)
            .padding(3.dp)
            .clip(cellShape)
            .background(
                if (isSelected) LiquidSelectedBrush
                else GlassGradientBrush
            )
            .border(
                width = if (isToday || isSelected) 1.5.dp else 1.dp,
                brush = if (isSelected) Brush.linearGradient(listOf(Color.White, LiquidGold))
                        else if (isToday) Brush.linearGradient(listOf(LiquidGold, Color.White.copy(0.4f)))
                        else GlassBorderBrush,
                shape = cellShape
            )
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        // Top Row: Malayalam Date (Left) and Festival/Holiday Dot (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (day.localDate.isNotEmpty()) {
                Text(
                    text = day.localDate,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextLiquidGold
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            if (day.festivals.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else LiquidRose)
                )
            }
        }

        // Center: Gregorian Day Number
        Text(
            text = "${day.gregorianDay}",
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) Color.White
                    else if (isSunday) LiquidRose
                    else TextLiquidPrimary,
            modifier = Modifier.align(Alignment.Center)
        )

        // Bottom: Nakshathram Name
        val nakshatraShort = day.nakshathram.firstOrNull()?.split(" ")?.firstOrNull() ?: ""
        Text(
            text = nakshatraShort,
            fontSize = 8.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextLiquidMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun GlassPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .clip(pillShape)
            .background(if (isActive) LiquidSelectedBrush else GlassFillSubtle)
            .border(1.dp, if (isActive) GlassBorderHighlight else GlassBorderSubtle, pillShape)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else TextLiquidSecondary
        )
    }
}

@Composable
fun GlassCircleButton(
    symbol: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(GlassFill)
            .border(1.dp, GlassBorderBrush, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextLiquidPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassDetailsBottomSheet(
    day: DayPanchang,
    isMalayalam: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF5180B14),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Glass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassGradientBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = day.fullDateString.ifEmpty { "${day.gregorianDay}/${day.gregorianMonth}/${day.gregorianYear}" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLiquidPrimary
                        )
                        if (day.localDate.isNotEmpty() && day.malayalamMonth.isNotEmpty()) {
                            Text(
                                text = "കൊല്ലവർഷം: ${day.localDate} ${day.malayalamMonth} 1201",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LiquidGold
                            )
                        }
                    }

                    GlassCircleButton(
                        symbol = "✕",
                        onClick = onDismiss
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Festivals / Holidays Glowing Glass Banner
            if (day.festivals.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0x35E11D48), Color(0x20F59E0B))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(LiquidRose.copy(0.6f), LiquidGold.copy(0.4f))),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = if (isMalayalam) "🎉 വിശേഷദിവസങ്ങൾ / വ്രതങ്ങൾ" else "🎉 Festivals & Observances",
                            fontWeight = FontWeight.Bold,
                            color = LiquidGold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        day.festivals.forEach { fest ->
                            Text(
                                text = "✨ $fest",
                                fontSize = 13.5.sp,
                                color = TextLiquidPrimary,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Section: Panchangam (Nakshathram & Tithi)
            LiquidGlassInfoCard(
                title = if (isMalayalam) "⭐ നക്ഷത്രവും തിഥിയും" else "⭐ Nakshathram & Tithi",
                items = listOf(
                    (if (isMalayalam) "നക്ഷത്രം" else "Nakshathram") to day.nakshathram.joinToString(", ").ifEmpty { "-" },
                    (if (isMalayalam) "തിഥി" else "Tithi") to day.tithi.joinToString(", ").ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Section: Sun & Moon Timings
            LiquidGlassInfoCard(
                title = if (isMalayalam) "☀️ സൂര്യ-ചന്ദ്ര സമയങ്ങൾ" else "☀️ Sun & Moon Timings",
                items = listOf(
                    (if (isMalayalam) "സൂര്യോദയം" else "Sunrise") to day.sunrise.ifEmpty { "-" },
                    (if (isMalayalam) "സൂര്യാസ്തമയം" else "Sunset") to day.sunset.ifEmpty { "-" },
                    (if (isMalayalam) "ചന്ദ്രോദയം" else "Moonrise") to day.moonrise.ifEmpty { "-" },
                    (if (isMalayalam) "ചന്ദ്രാസ്തമയം" else "Moonset") to day.moonset.ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Section: Auspicious & Inauspicious Times
            LiquidGlassInfoCard(
                title = if (isMalayalam) "⏳ ശുഭാശുഭ സമയങ്ങൾ" else "⏳ Auspicious & Inauspicious Times",
                items = listOf(
                    (if (isMalayalam) "🔴 രാഹുകാലം" else "🔴 Rahu Kalam") to day.rahuKalam.ifEmpty { "-" },
                    (if (isMalayalam) "🟡 ഗുളികകാലം" else "🟡 Gulikai Kalam") to day.gulikaiKalam.ifEmpty { "-" },
                    (if (isMalayalam) "⚪ യമഗണ്ഡം" else "⚪ Yamaganda") to day.yamaganda.ifEmpty { "-" },
                    (if (isMalayalam) "🟢 അഭിജിത്" else "🟢 Abhijit") to day.abhijit.ifEmpty { "-" },
                    (if (isMalayalam) "🟢 ബ്രഹ്മ മുഹൂർത്തം" else "🟢 Brahma Muhurta") to day.brahmaMuhurta.ifEmpty { "-" },
                    (if (isMalayalam) "🟣 അമൃതകാലം" else "🟣 Amrit Kalam") to day.amritKalam.ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun LiquidGlassInfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    val cardShape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(GlassCardBrush)
            .border(1.dp, GlassBorderBrush, cardShape)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = LiquidGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = TextLiquidMuted,
                        modifier = Modifier.weight(0.42f)
                    )
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextLiquidPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.58f)
                    )
                }
            }
        }
    }
}
