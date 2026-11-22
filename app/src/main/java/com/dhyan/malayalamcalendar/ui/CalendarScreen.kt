package com.dhyan.malayalamcalendar.ui

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

    // Determine calendar grid offset (Day of week for 1st of this month)
    val startDayOfWeek = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth - 1, 1)
        cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
    }

    val totalCells = (startDayOfWeek - 1) + days.size
    val trailingEmptyCells = if (totalCells % 7 == 0) 0 else 7 - (totalCells % 7)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isMalayalam) "മലയാളം കലണ്ടർ" else "Malayalam Calendar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = if (isMalayalam) "പഞ്ചാംഗവും വിശേഷങ്ങളും" else "Panchangam & Festivals",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                },
                actions = {
                    // Language Switch Button
                    TextButton(
                        onClick = { isMalayalam = !isMalayalam },
                        colors = ButtonDefaults.textButtonColors(contentColor = TempleMaroon)
                    ) {
                        Text(
                            text = if (isMalayalam) "EN" else "മല",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Year toggle chips
                    FilterChip(
                        selected = selectedYear == 2026,
                        onClick = { selectedYear = 2026 },
                        label = { Text("2026") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FilterChip(
                        selected = selectedYear == 2027,
                        onClick = { selectedYear = 2027 },
                        label = { Text("2027") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Month Navigation Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (selectedMonth > 1) {
                                selectedMonth--
                            } else if (selectedYear > 2026) {
                                selectedYear--
                                selectedMonth = 12
                            }
                        }
                    ) {
                        Text("◀", fontSize = 18.sp, color = TempleMaroon)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val currentMonthName = if (isMalayalam) monthNamesMl[selectedMonth - 1] else monthNamesEn[selectedMonth - 1]
                        val malayalamMonthName = CalendarRepository.getMonthMalayalamName(selectedMonth)

                        Text(
                            text = "$currentMonthName $selectedYear",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TempleMaroon
                            )
                        )
                        if (malayalamMonthName.isNotEmpty()) {
                            Text(
                                text = malayalamMonthName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TempleGold
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (selectedMonth < 12) {
                                selectedMonth++
                            } else if (selectedYear < 2027) {
                                selectedYear++
                                selectedMonth = 1
                            }
                        }
                    ) {
                        Text("▶", fontSize = 18.sp, color = TempleMaroon)
                    }
                }
            }

            // Day of Week Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
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
                        color = if (index == 0) SundayRed else TextDark
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = BorderLight
            )

            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Leading empty days
                items(startDayOfWeek - 1) {
                    Box(modifier = Modifier.aspectRatio(0.85f))
                }

                // Days of the month
                items(days) { day ->
                    val isSunday = (startDayOfWeek - 1 + day.gregorianDay - 1) % 7 == 0

                    CalendarDayCell(
                        day = day,
                        isSunday = isSunday,
                        isSelected = selectedDay?.gregorianDay == day.gregorianDay,
                        onClick = { selectedDay = day }
                    )
                }

                // Trailing empty days
                items(trailingEmptyCells) {
                    Box(modifier = Modifier.aspectRatio(0.85f))
                }
            }
        }

        // Details Bottom Sheet when a date is clicked
        selectedDay?.let { day ->
            DateDetailsBottomSheet(
                day = day,
                isMalayalam = isMalayalam,
                onDismiss = { selectedDay = null }
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    day: DayPanchang,
    isSunday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(0.85f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFE4E6) else CardBackground
        ),
        border = if (isSelected) borderStroke(TempleMaroon) else borderStroke(BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Malayalam date (left) and festival indicator (right)
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
                        color = TempleMaroon
                    )
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (day.festivals.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SundayRed)
                    )
                }
            }

            // Center: Gregorian Date number
            Text(
                text = "${day.gregorianDay}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSunday) SundayRed else TextDark
            )

            // Bottom: Nakshathram short name
            val nakshatraShort = day.nakshathram.firstOrNull()?.split(" ")?.firstOrNull() ?: ""
            Text(
                text = nakshatraShort,
                fontSize = 8.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDetailsBottomSheet(
    day: DayPanchang,
    isMalayalam: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Gregorian and Malayalam Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = day.fullDateString.ifEmpty { "${day.gregorianDay}/${day.gregorianMonth}/${day.gregorianYear}" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TempleMaroon
                        )
                    )
                    if (day.localDate.isNotEmpty() && day.malayalamMonth.isNotEmpty()) {
                        Text(
                            text = "കൊല്ലവർഷം: ${day.localDate} ${day.malayalamMonth}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TempleGold
                            )
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Festivals / Holidays Banner
            if (day.festivals.isNotEmpty()) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(12.dp),
                    border = borderStroke(Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isMalayalam) "🎉 വിശേഷദിവസങ്ങൾ / വ്രതങ്ങൾ" else "🎉 Festivals & Observances",
                            fontWeight = FontWeight.Bold,
                            color = SundayRed,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        day.festivals.forEach { fest ->
                            Text(
                                text = "• $fest",
                                fontSize = 13.sp,
                                color = TextDark,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Section: Panchangam Essentials (Nakshatram, Tithi)
            PanchangInfoCard(
                title = if (isMalayalam) "നക്ഷത്രവും തിഥിയും" else "Nakshathram & Tithi",
                items = listOf(
                    (if (isMalayalam) "നക്ഷത്രം" else "Nakshathram") to day.nakshathram.joinToString(", ").ifEmpty { "-" },
                    (if (isMalayalam) "തിഥി" else "Tithi") to day.tithi.joinToString(", ").ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Section: Sun & Moon Timings
            PanchangInfoCard(
                title = if (isMalayalam) "സൂര്യ-ചന്ദ്ര സമയങ്ങൾ" else "Sun & Moon Timings",
                items = listOf(
                    (if (isMalayalam) "സൂര്യോദയം" else "Sunrise") to day.sunrise.ifEmpty { "-" },
                    (if (isMalayalam) "സൂര്യാസ്തമയം" else "Sunset") to day.sunset.ifEmpty { "-" },
                    (if (isMalayalam) "ചന്ദ്രോദയം" else "Moonrise") to day.moonrise.ifEmpty { "-" },
                    (if (isMalayalam) "ചന്ദ്രാസ്തമയം" else "Moonset") to day.moonset.ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Section: Auspicious & Inauspicious Times (Rahu Kalam, Yamaganda, Gulikai)
            PanchangInfoCard(
                title = if (isMalayalam) "ശുഭാശുഭ സമയങ്ങൾ" else "Auspicious & Inauspicious Times",
                items = listOf(
                    (if (isMalayalam) "രാഹുകാലം" else "Rahu Kalam") to day.rahuKalam.ifEmpty { "-" },
                    (if (isMalayalam) "ഗുളികകാലം" else "Gulikai Kalam") to day.gulikaiKalam.ifEmpty { "-" },
                    (if (isMalayalam) "യമഗണ്ഡം" else "Yamaganda") to day.yamaganda.ifEmpty { "-" },
                    (if (isMalayalam) "അഭിജിത്" else "Abhijit") to day.abhijit.ifEmpty { "-" },
                    (if (isMalayalam) "ബ്രഹ്മ മുഹൂർത്തം" else "Brahma Muhurta") to day.brahmaMuhurta.ifEmpty { "-" },
                    (if (isMalayalam) "അമൃതകാലം" else "Amrit Kalam") to day.amritKalam.ifEmpty { "-" }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PanchangInfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(12.dp),
        border = borderStroke(BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TempleMaroon
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)
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
                        color = TextMuted,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
        }
    }
}
