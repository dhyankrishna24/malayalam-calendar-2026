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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    val initialYear = remember {
        val y = todayCal.get(Calendar.YEAR)
        if (y in 2026..2027) y else 2026
    }
    val initialMonth = remember {
        val y = todayCal.get(Calendar.YEAR)
        if (y in 2026..2027) todayCal.get(Calendar.MONTH) + 1 else 1
    }

    var isLightMode by remember { mutableStateOf(true) }
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) } // 1-12
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

    // Dynamic Liquid Glass Brushes & Colors
    val bgGradient = if (isLightMode) {
        listOf(LightLiquidBgTop, LightLiquidBgMid, LightLiquidBgBottom)
    } else {
        listOf(LiquidBgTop, LiquidBgMid, LiquidBgBottom)
    }
    val glassCardBrush = if (isLightMode) LightGlassCardBrush else GlassCardBrush
    val glassBorderBrush = if (isLightMode) LightGlassBorderBrush else GlassBorderBrush
    val glassFillSubtle = if (isLightMode) LightGlassFillSubtle else GlassFillSubtle
    val textSecondary = if (isLightMode) LightTextSecondary else TextLiquidSecondary
    val textGold = if (isLightMode) LightTextGold else TextLiquidGold
    val titleMaroon = if (isLightMode) LightTextMaroon else TextLiquidPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        // Ambient Liquid Light Glow Orbs
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 180.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(if (isLightMode) LightLiquidOrbGold else LiquidOrbGold, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = 240.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(if (isLightMode) LightLiquidOrbRose else LiquidOrbRose, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 160.dp, y = 560.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(if (isLightMode) LightLiquidOrbCyan else LiquidOrbCyan, Color.Transparent)))
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
                            .background(glassCardBrush)
                            .border(1.dp, glassBorderBrush, RoundedCornerShape(24.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isMalayalam) "മലയാളം കലണ്ടർ" else "Malayalam Calendar",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = titleMaroon
                                )
                                Text(
                                    text = "2026 - 2027 • പഞ്ചാംഗം",
                                    fontSize = 11.sp,
                                    color = textGold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Jump to Today Button
                                val isAtToday = selectedYear == initialYear && selectedMonth == initialMonth
                                GlassPill(
                                    text = if (isMalayalam) "ഇന്ന്" else "Today",
                                    isActive = isAtToday,
                                    isLightMode = isLightMode,
                                    onClick = {
                                        selectedYear = initialYear
                                        selectedMonth = initialMonth
                                    }
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Theme Toggle Pill (☀️ Light / 🌙 Dark)
                                GlassPill(
                                    text = if (isLightMode) "☀️" else "🌙",
                                    isActive = true,
                                    isLightMode = isLightMode,
                                    onClick = { isLightMode = !isLightMode }
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Language Switch Pill
                                GlassPill(
                                    text = if (isMalayalam) "EN" else "മല",
                                    isActive = false,
                                    isLightMode = isLightMode,
                                    onClick = { isMalayalam = !isMalayalam }
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Year Switch Pills
                                GlassPill(
                                    text = "2026",
                                    isActive = selectedYear == 2026,
                                    isLightMode = isLightMode,
                                    onClick = { selectedYear = 2026 }
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                GlassPill(
                                    text = "2027",
                                    isActive = selectedYear == 2027,
                                    isLightMode = isLightMode,
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
                        .background(glassCardBrush)
                        .border(1.dp, glassBorderBrush, RoundedCornerShape(22.dp))
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
                            isLightMode = isLightMode,
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
                                color = titleMaroon
                            )
                            if (malayalamMonthName.isNotEmpty()) {
                                Text(
                                    text = malayalamMonthName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textGold
                                )
                            }
                        }

                        // Next Month Button
                        GlassCircleButton(
                            symbol = "❯",
                            isLightMode = isLightMode,
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
                        .background(SolidColor(glassFillSubtle))
                        .border(1.dp, if (isLightMode) LightGlassBorderSubtle else GlassBorderSubtle, RoundedCornerShape(16.dp))
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
                                color = if (index == 0) SundayRed else textSecondary
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
                            isLightMode = isLightMode,
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
                isLightMode = isLightMode,
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
    isLightMode: Boolean,
    onClick: () -> Unit
) {
    val cellShape = RoundedCornerShape(14.dp)

    // Vibrant background brush when selected, glowing warm amber glass when isToday, or frosted glass
    val dayCellBrush = if (isSelected) {
        LiquidSelectedBrush
    } else if (isToday) {
        if (isLightMode) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFEF3C7),
                    Color(0xFFFDE68A).copy(alpha = 0.85f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0x55F59E0B),
                    Color(0x30FBBF24)
                )
            )
        }
    } else if (isLightMode) {
        LightGlassGradientBrush
    } else {
        GlassGradientBrush
    }

    // Prominent glowing border: 2dp gold when today, 2dp white-gold when selected
    val dayCellBorderBrush = if (isSelected) {
        Brush.linearGradient(listOf(Color.White, LiquidGold))
    } else if (isToday) {
        Brush.linearGradient(
            listOf(
                Color(0xFFF59E0B),
                Color(0xFFFDE68A),
                Color(0xFFF59E0B)
            )
        )
    } else if (isLightMode) {
        LightGlassBorderBrush
    } else {
        GlassBorderBrush
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.82f)
            .padding(3.dp)
            .clip(cellShape)
            .background(dayCellBrush)
            .border(
                width = if (isToday || isSelected) 2.dp else 1.dp,
                brush = dayCellBorderBrush,
                shape = cellShape
            )
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        // Glowing halo behind day number when isToday
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isLightMode) Color(0x3DF59E0B) else Color(0x40FBBF24))
                    .align(Alignment.Center)
            )
        }

        // Top Row: Malayalam Date (Left) and Festival / TODAY Badge (Right)
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
                    color = if (isSelected) Color.White
                            else if (isToday) Color(0xFFB45309)
                            else if (isLightMode) LightTextMaroon
                            else TextLiquidGold
                )
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // "TODAY" micro-badge
                if (isToday && !isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF59E0B))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "TODAY",
                            fontSize = 6.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                }

                if (day.festivals.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else SundayRed)
                    )
                }
            }
        }

        // Center: Gregorian Day Number
        Text(
            text = "${day.gregorianDay}",
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) Color.White
                    else if (isToday) (if (isLightMode) Color(0xFF92400E) else LiquidGold)
                    else if (isSunday) SundayRed
                    else if (isLightMode) LightTextPrimary
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
            color = if (isSelected) Color.White.copy(alpha = 0.9f)
                    else if (isToday) (if (isLightMode) Color(0xFF92400E) else TextLiquidGold)
                    else if (isLightMode) LightTextMuted
                    else TextLiquidMuted,
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
    isLightMode: Boolean,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(16.dp)
    val bgBrush = if (isActive) LiquidSelectedBrush
    else SolidColor(if (isLightMode) LightGlassFillSubtle else GlassFillSubtle)

    val borderStroke = if (isActive) {
        if (isLightMode) LightGlassBorderHighlight else GlassBorderHighlight
    } else {
        if (isLightMode) LightGlassBorderSubtle else GlassBorderSubtle
    }

    Box(
        modifier = Modifier
            .clip(pillShape)
            .background(bgBrush)
            .border(1.dp, borderStroke, pillShape)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White
                    else if (isLightMode) LightTextSecondary
                    else TextLiquidSecondary
        )
    }
}

@Composable
fun GlassCircleButton(
    symbol: String,
    isLightMode: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (isLightMode) LightGlassFill else GlassFill)
            .border(1.dp, if (isLightMode) LightGlassBorderBrush else GlassBorderBrush, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLightMode) LightTextMaroon else TextLiquidPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassDetailsBottomSheet(
    day: DayPanchang,
    isMalayalam: Boolean,
    isLightMode: Boolean,
    onDismiss: () -> Unit
) {
    val sheetBg = if (isLightMode) Color(0xFDF8FAFC) else Color(0xF5180B14)
    val cardBrush = if (isLightMode) LightGlassCardBrush else GlassCardBrush
    val borderBrush = if (isLightMode) LightGlassBorderBrush else GlassBorderBrush
    val titleColor = if (isLightMode) LightTextMaroon else TextLiquidPrimary
    val goldColor = if (isLightMode) LightTextGold else LiquidGold

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isLightMode) Color(0x40000000) else Color(0x4DFFFFFF))
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
                    .background(cardBrush)
                    .border(1.dp, borderBrush, RoundedCornerShape(20.dp))
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
                            color = titleColor
                        )
                        if (day.localDate.isNotEmpty() && day.malayalamMonth.isNotEmpty()) {
                            Text(
                                text = "കൊല്ലവർഷം: ${day.localDate} ${day.malayalamMonth} 1201",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = goldColor
                            )
                        }
                    }

                    GlassCircleButton(
                        symbol = "✕",
                        isLightMode = isLightMode,
                        onClick = onDismiss
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Festivals / Holidays Glowing Glass Banner
            if (day.festivals.isNotEmpty()) {
                val festBg = if (isLightMode) {
                    Brush.linearGradient(listOf(Color(0xFFFFF1F2), Color(0xFFFEF3C7)))
                } else {
                    Brush.linearGradient(listOf(Color(0x35E11D48), Color(0x20F59E0B)))
                }
                val festBorder = if (isLightMode) {
                    Color(0xFFFDA4AF)
                } else {
                    LiquidRose.copy(0.6f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(festBg)
                        .border(1.dp, festBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = if (isMalayalam) "🎉 വിശേഷദിവസങ്ങൾ / വ്രതങ്ങൾ" else "🎉 Festivals & Observances",
                            fontWeight = FontWeight.Bold,
                            color = if (isLightMode) LightTextMaroon else LiquidGold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        day.festivals.forEach { fest ->
                            Text(
                                text = "✨ $fest",
                                fontSize = 13.5.sp,
                                color = if (isLightMode) LightTextPrimary else TextLiquidPrimary,
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
                ),
                isLightMode = isLightMode
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
                ),
                isLightMode = isLightMode
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
                ),
                isLightMode = isLightMode
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun LiquidGlassInfoCard(
    title: String,
    items: List<Pair<String, String>>,
    isLightMode: Boolean
) {
    val cardShape = RoundedCornerShape(18.dp)
    val cardBrush = if (isLightMode) LightGlassCardBrush else GlassCardBrush
    val borderBrush = if (isLightMode) LightGlassBorderBrush else GlassBorderBrush
    val titleColor = if (isLightMode) LightTextMaroon else LiquidGold
    val textPrimary = if (isLightMode) LightTextPrimary else TextLiquidPrimary
    val textMuted = if (isLightMode) LightTextMuted else TextLiquidMuted

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardBrush)
            .border(1.dp, borderBrush, cardShape)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = if (isLightMode) Color(0x1A000000) else Color(0x1AFFFFFF)
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
                        color = textMuted,
                        modifier = Modifier.weight(0.42f)
                    )
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.58f)
                    )
                }
            }
        }
    }
}
