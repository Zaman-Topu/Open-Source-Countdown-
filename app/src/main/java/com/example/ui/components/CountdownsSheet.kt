package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountdownItem
import com.example.ui.theme.BanglaFontFamily
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownsSheet(
    isOpen: Boolean,
    countdowns: List<CountdownItem>,
    selectedId: Long?,
    isSequentialMode: Boolean,
    initialEditingItem: CountdownItem? = null,
    onDismiss: () -> Unit,
    onSelectCountdown: (Long) -> Unit,
    onAddCountdown: (title: String, targetEpochMillis: Long, dateDisplay: String, timeDisplay: String) -> Unit,
    onEditCountdown: (CountdownItem) -> Unit,
    onDeleteCountdown: (CountdownItem) -> Unit,
    onMoveCountdown: (fromIndex: Int, toIndex: Int) -> Unit,
    onToggleSequentialMode: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Editing mode state (runs 100% inside sheet, completely crash-proof on Android 10+)
    var editingItem by remember(initialEditingItem) { mutableStateOf(initialEditingItem) }
    var isAddingNew by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CountdownItem?>(null) }

    ModalBottomSheet(
        onDismissRequest = {
            editingItem = null
            isAddingNew = false
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(100.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        AnimatedContent(
            targetState = (isAddingNew || editingItem != null),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "sheet_mode_transition"
        ) { isEditMode ->
            if (isEditMode) {
                // Pure Compose In-Sheet Editor (Zero native dialogs, Zero window token crashes)
                AddEditCountdownSheetView(
                    initialItem = editingItem,
                    onBack = {
                        editingItem = null
                        isAddingNew = false
                    },
                    onSave = { title, epochMillis, dateDisplay, timeDisplay ->
                        if (editingItem != null) {
                            val updated = editingItem!!.copy(
                                title = title,
                                targetEpochMillis = epochMillis,
                                targetDateDisplay = dateDisplay,
                                targetTimeDisplay = timeDisplay
                            )
                            onEditCountdown(updated)
                        } else {
                            onAddCountdown(title, epochMillis, dateDisplay, timeDisplay)
                        }
                        editingItem = null
                        isAddingNew = false
                    }
                )
            } else {
                // Main Countdowns List
                CountdownsListView(
                    countdowns = countdowns,
                    selectedId = selectedId,
                    isSequentialMode = isSequentialMode,
                    onStartAdd = { isAddingNew = true },
                    onStartEdit = { item -> editingItem = item },
                    onDeleteRequest = { item -> itemToDelete = item },
                    onSelectCountdown = onSelectCountdown,
                    onMoveCountdown = onMoveCountdown,
                    onToggleSequentialMode = onToggleSequentialMode
                )
            }
        }
    }

    // Delete Confirmation Dialog (Safe isolated dialog)
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "কাউন্টডাউন মুছে ফেলবেন?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = BanglaFontFamily
                    )
                )
            },
            text = {
                Text(
                    text = "\"${itemToDelete?.title}\" তালিকা থেকে মুছে ফেলতে চান? আপনি যেকোনো সময় পুনরায় নতুন কাউন্টডাউন যোগ করতে পারবেন।",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = BanglaFontFamily)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteCountdown(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("মুছে ফেলুন", style = MaterialTheme.typography.labelLarge.copy(fontFamily = BanglaFontFamily))
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("বাতিল", style = MaterialTheme.typography.labelLarge.copy(fontFamily = BanglaFontFamily))
                }
            }
        )
    }
}

@Composable
private fun CountdownsListView(
    countdowns: List<CountdownItem>,
    selectedId: Long?,
    isSequentialMode: Boolean,
    onStartAdd: () -> Unit,
    onStartEdit: (CountdownItem) -> Unit,
    onDeleteRequest: (CountdownItem) -> Unit,
    onSelectCountdown: (Long) -> Unit,
    onMoveCountdown: (fromIndex: Int, toIndex: Int) -> Unit,
    onToggleSequentialMode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "কাউন্টডাউন তালিকা",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = BanglaFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SSC 27 ও অন্যান্য লক্ষ্যমাত্রা পরিচালনা",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = BanglaFontFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = onStartAdd,
                modifier = Modifier.testTag("btn_add_countdown")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Countdown",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "নতুন যোগ",
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = BanglaFontFamily)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sequential Mode Switch Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "স্বয়ংক্রিয় ধারাবাহিক মোড (Sequential)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = BanglaFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "একটি পরীক্ষা শেষ হলে পরবর্তী পরীক্ষাটি স্বয়ংক্রিয়ভাবে প্রধান কাউন্টডাউনে আসবে।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 16.sp,
                            fontFamily = BanglaFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isSequentialMode,
                    onCheckedChange = { onToggleSequentialMode() },
                    modifier = Modifier.testTag("switch_sequential_mode")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdowns List
        val nowEpoch = remember { System.currentTimeMillis() }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = countdowns,
                key = { _, item -> item.id }
            ) { index, item ->
                val isSelected = (selectedId == item.id) || (selectedId == null && index == 0)
                val isPassed = item.targetEpochMillis <= nowEpoch

                CountdownCard(
                    item = item,
                    index = index,
                    totalItems = countdowns.size,
                    isSelected = isSelected,
                    isPassed = isPassed,
                    onSelect = { onSelectCountdown(item.id) },
                    onEdit = { onStartEdit(item) },
                    onDelete = { onDeleteRequest(item) },
                    onMoveUp = { if (index > 0) onMoveCountdown(index, index - 1) },
                    onMoveDown = { if (index < countdowns.size - 1) onMoveCountdown(index, index + 1) }
                )
            }
        }
    }
}

@Composable
fun CountdownCard(
    item: CountdownItem,
    index: Int,
    totalItems: Int,
    isSelected: Boolean,
    isPassed: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("countdown_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio / Active Indicator
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPassed) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Reached",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.targetDateDisplay} · ${item.targetTimeDisplay}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Actions: Reorder, Edit, Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (totalItems > 1) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = index > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Move Up",
                            tint = if (index > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onMoveDown,
                        enabled = index < totalItems - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Move Down",
                            tint = if (index < totalItems - 1) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp).testTag("edit_countdown_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (totalItems > 1) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_countdown_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 100% Pure Jetpack Compose Countdown Editor
 * Zero Android WindowManager dialogs, zero BadTokenException, zero crashes across Android 10 to Android 15+.
 */
@Composable
fun AddEditCountdownSheetView(
    initialItem: CountdownItem?,
    onBack: () -> Unit,
    onSave: (title: String, targetEpochMillis: Long, dateDisplay: String, timeDisplay: String) -> Unit
) {
    val dhakaZone = remember { ZoneId.of("Asia/Dhaka") }

    val initialZdt = remember(initialItem) {
        if (initialItem != null) {
            Instant.ofEpochMilli(initialItem.targetEpochMillis).atZone(dhakaZone)
        } else {
            ZonedDateTime.now(dhakaZone).plusMonths(3)
        }
    }

    var title by remember(initialItem) { mutableStateOf(initialItem?.title ?: "SSC 27") }

    var selectedYear by remember(initialItem) { mutableIntStateOf(initialZdt.year) }
    var selectedMonth by remember(initialItem) { mutableIntStateOf(initialZdt.monthValue) }
    var selectedDay by remember(initialItem) { mutableIntStateOf(initialZdt.dayOfMonth) }

    val initialHour24 = initialZdt.hour
    val initialHour12 = if (initialHour24 % 12 == 0) 12 else initialHour24 % 12
    var selectedHour by remember(initialItem) { mutableIntStateOf(initialHour12) }
    var selectedMinute by remember(initialItem) { mutableIntStateOf(initialZdt.minute) }
    var isAm by remember(initialItem) { mutableStateOf(initialHour24 < 12) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Ensure selectedDay is valid for current month/year
    val maxDaysInMonth = remember(selectedYear, selectedMonth) {
        YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
    }
    if (selectedDay > maxDaysInMonth) {
        selectedDay = maxDaysInMonth
    }

    // Computed target ZonedDateTime
    val targetHour24 = if (isAm) {
        if (selectedHour == 12) 0 else selectedHour
    } else {
        if (selectedHour == 12) 12 else selectedHour + 12
    }

    val currentZdt = remember(selectedYear, selectedMonth, selectedDay, targetHour24, selectedMinute) {
        try {
            ZonedDateTime.of(
                LocalDate.of(selectedYear, selectedMonth, selectedDay),
                LocalTime.of(targetHour24, selectedMinute, 0),
                dhakaZone
            )
        } catch (_: Exception) {
            ZonedDateTime.now(dhakaZone)
        }
    }

    val currentEpochMillis = currentZdt.toInstant().toEpochMilli()
    val nowMillis = remember { System.currentTimeMillis() }
    val remainingDays = ((currentEpochMillis - nowMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val remainingHours = (((currentEpochMillis - nowMillis) / (1000 * 60 * 60)) % 24).coerceAtLeast(0)

    val monthNamesBangla = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar inside editor
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (initialItem != null) "কাউন্টডাউন এডিট করুন" else "নতুন কাউন্টডাউন যোগ করুন",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = BanglaFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Android 10+ সমর্থিত নির্ভরযোগ্য এডিটর",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = BanglaFontFamily),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Quick Preset Milestones Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "কুইক মাইলস্টোন প্রিসেট (১-ট্যাপে সেট করুন)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = BanglaFontFamily
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {
                        title = "SSC 27"
                        selectedYear = 2027
                        selectedMonth = 1
                        selectedDay = 7
                        selectedHour = 10
                        selectedMinute = 0
                        isAm = true
                    },
                    label = { Text("🎯 SSC 2027 ফাইনাল (৭ জানু ২০২৭)") }
                )
                SuggestionChip(
                    onClick = {
                        title = "SSC Test Examination"
                        selectedYear = 2026
                        selectedMonth = 12
                        selectedDay = 10
                        selectedHour = 10
                        selectedMinute = 0
                        isAm = true
                    },
                    label = { Text("📝 টেস্ট পরীক্ষা (১০ ডিসে ২০২৬)") }
                )
                SuggestionChip(
                    onClick = {
                        title = "SSC Pre-Test Revision"
                        selectedYear = 2026
                        selectedMonth = 11
                        selectedDay = 15
                        selectedHour = 10
                        selectedMinute = 0
                        isAm = true
                    },
                    label = { Text("📚 প্রি-টেস্ট (১৫ নভে ২০২৬)") }
                )
                SuggestionChip(
                    onClick = {
                        title = "Model Test Exam"
                        selectedYear = 2026
                        selectedMonth = 10
                        selectedDay = 1
                        selectedHour = 10
                        selectedMinute = 0
                        isAm = true
                    },
                    label = { Text("⏳ মডেল টেস্ট (১ অক্টো ২০২৬)") }
                )
            }
        }

        // Title Input Field
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            label = { Text("কাউন্টডাউনের নাম (Title)", fontFamily = BanglaFontFamily) },
            placeholder = { Text("যেমন: SSC 27, টেস্ট পরীক্ষা") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_countdown_title")
        )

        // Live Calculated Preview Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentEpochMillis > nowMillis) {
                            "আর মাত্র $remainingDays দিন $remainingHours ঘণ্টা বাকি থাকবে!"
                        } else {
                            "এই তারিখটি ইতোমধ্যে অতিক্রান্ত হয়েছে।"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = BanglaFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "লক্ষ্যমাত্রা: $selectedDay ${monthNamesBangla[selectedMonth - 1]} $selectedYear · $selectedHour:${String.format(Locale.ENGLISH, "%02d", selectedMinute)} ${if (isAm) "AM" else "PM"} (BST)",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = BanglaFontFamily),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
        }

        // Section 1: Target Date Selector
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "তারিখ নির্বাচন করুন (Target Date)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = BanglaFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Year Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "সাল:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(2026, 2027, 2028).forEach { yr ->
                        FilterChip(
                            selected = selectedYear == yr,
                            onClick = { selectedYear = yr },
                            label = { Text("$yr") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Month Selector (Horizontal scrollable)
                Text(
                    text = "মাস:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    monthNamesBangla.forEachIndexed { idx, mName ->
                        val mNum = idx + 1
                        FilterChip(
                            selected = selectedMonth == mNum,
                            onClick = { selectedMonth = mNum },
                            label = { Text("$mNum. $mName", style = MaterialTheme.typography.bodySmall.copy(fontFamily = BanglaFontFamily)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Day Selector Stepper + Quick day chips
                Text(
                    text = "দিন (১ থেকে $maxDaysInMonth):",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { if (selectedDay > 1) selectedDay-- },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Day", modifier = Modifier.size(18.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "$selectedDay",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = { if (selectedDay < maxDaysInMonth) selectedDay++ },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Day", modifier = Modifier.size(18.dp))
                    }

                    // Common Quick Day Chips
                    listOf(1, 7, 10, 15, 20, 25).forEach { d ->
                        if (d <= maxDaysInMonth) {
                            SuggestionChip(
                                onClick = { selectedDay = d },
                                label = { Text("$d") },
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Target Time Selector
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সময় নির্বাচন করুন (Target Time · BST)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = BanglaFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Hour Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ঘণ্টা:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    IconButton(
                        onClick = { selectedHour = if (selectedHour > 1) selectedHour - 1 else 12 },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Hour", modifier = Modifier.size(16.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = String.format(Locale.ENGLISH, "%02d", selectedHour),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = { selectedHour = if (selectedHour < 12) selectedHour + 1 else 1 },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Hour", modifier = Modifier.size(16.dp))
                    }

                    // AM / PM Segmented Selection
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = isAm,
                            onClick = { isAm = true },
                            label = { Text("AM (সকাল)", fontFamily = BanglaFontFamily) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = !isAm,
                            onClick = { isAm = false },
                            label = { Text("PM (বিকাল/রাত)", fontFamily = BanglaFontFamily) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Minute Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "মিনিট:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(0, 15, 30, 45).forEach { minVal ->
                        FilterChip(
                            selected = selectedMinute == minVal,
                            onClick = { selectedMinute = minVal },
                            label = { Text(String.format(Locale.ENGLISH, "%02d", minVal)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = BanglaFontFamily)
            )
        }

        // Action Buttons: Save & Cancel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("বাতিল", style = MaterialTheme.typography.titleMedium.copy(fontFamily = BanglaFontFamily))
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "দয়া করে কাউন্টডাউনের একটি নাম লিখুন।"
                        return@Button
                    }
                    val dateDisplay = currentZdt.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
                    val timeDisplay = currentZdt.format(DateTimeFormatter.ofPattern("hh:mm a (BST)", Locale.ENGLISH))

                    onSave(title.trim(), currentEpochMillis, dateDisplay, timeDisplay)
                },
                modifier = Modifier.weight(1.5f).height(50.dp).testTag("btn_save_countdown")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("সংরক্ষণ করুন", style = MaterialTheme.typography.titleMedium.copy(fontFamily = BanglaFontFamily))
            }
        }
    }
}
