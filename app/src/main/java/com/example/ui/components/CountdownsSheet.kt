package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
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
    var showAddEditDialog by remember { mutableStateOf<CountdownItem?>(null) }
    var isNewCountdownDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CountdownItem?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                        text = "Countdowns",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Manage targets and milestones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        isNewCountdownDialog = true
                        showAddEditDialog = null
                    },
                    modifier = Modifier.testTag("btn_add_countdown")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sequential Mode Banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Sequential Mode",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "When Countdown 1 finishes, Countdown 2 automatically becomes active.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
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

            // List of Countdowns
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                itemsIndexed(countdowns, key = { _, item -> item.id }) { index, item ->
                    val isSelected = selectedId == item.id || (selectedId == null && item.isDefault)
                    val isPassed = item.targetEpochMillis <= System.currentTimeMillis()

                    CountdownCard(
                        item = item,
                        index = index,
                        totalItems = countdowns.size,
                        isSelected = isSelected,
                        isPassed = isPassed,
                        onSelect = { onSelectCountdown(item.id) },
                        onEdit = {
                            isNewCountdownDialog = false
                            showAddEditDialog = item
                        },
                        onDelete = { itemToDelete = item },
                        onMoveUp = { if (index > 0) onMoveCountdown(index, index - 1) },
                        onMoveDown = { if (index < countdowns.size - 1) onMoveCountdown(index, index + 1) }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "Delete Countdown?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text("Are you sure you want to delete \"${itemToDelete?.title}\"? This action cannot be undone.")
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
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add or Edit Dialog
    if (isNewCountdownDialog || showAddEditDialog != null) {
        AddEditCountdownDialog(
            initialItem = showAddEditDialog,
            onDismiss = {
                isNewCountdownDialog = false
                showAddEditDialog = null
            },
            onSave = { title, epochMillis, dateDisplay, timeDisplay ->
                if (showAddEditDialog != null) {
                    val updated = showAddEditDialog!!.copy(
                        title = title,
                        targetEpochMillis = epochMillis,
                        targetDateDisplay = dateDisplay,
                        targetTimeDisplay = timeDisplay
                    )
                    onEditCountdown(updated)
                } else {
                    onAddCountdown(title, epochMillis, dateDisplay, timeDisplay)
                }
                isNewCountdownDialog = false
                showAddEditDialog = null
            }
        )
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
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (totalItems > 1 && !item.isDefault) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
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

@Composable
fun AddEditCountdownDialog(
    initialItem: CountdownItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, targetEpochMillis: Long, dateDisplay: String, timeDisplay: String) -> Unit
) {
    val initialZdt = remember(initialItem) {
        if (initialItem != null) {
            Instant.ofEpochMilli(initialItem.targetEpochMillis).atZone(ZoneId.of("Asia/Dhaka"))
        } else {
            null
        }
    }

    var title by remember(initialItem) { mutableStateOf(initialItem?.title ?: "") }

    // Date inputs
    var day by remember(initialItem) {
        mutableStateOf(initialZdt?.dayOfMonth?.toString() ?: "7")
    }
    var month by remember(initialItem) {
        mutableStateOf(initialZdt?.monthValue?.toString() ?: "1")
    }
    var year by remember(initialItem) {
        mutableStateOf(initialZdt?.year?.toString() ?: "2027")
    }

    // Time inputs
    var hour by remember(initialItem) {
        val h = initialZdt?.hour ?: 10
        val h12 = if (h % 12 == 0) 12 else h % 12
        mutableStateOf(h12.toString())
    }
    var minute by remember(initialItem) {
        val m = initialZdt?.minute ?: 0
        mutableStateOf(String.format(Locale.ENGLISH, "%02d", m))
    }
    var isAm by remember(initialItem) {
        val h = initialZdt?.hour ?: 10
        mutableStateOf(h < 12)
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialItem != null) "Edit Countdown" else "New Countdown",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. SSC 27)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_countdown_title")
                )

                // Quick Presets
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                title = "SSC 27"
                                day = "7"; month = "1"; year = "2027"
                                hour = "10"; minute = "00"; isAm = true
                            },
                            label = { Text("SSC Final (7 Jan 2027)") }
                        )
                        SuggestionChip(
                            onClick = {
                                title = "SSC Test Exam"
                                day = "10"; month = "12"; year = "2026"
                                hour = "10"; minute = "00"; isAm = true
                            },
                            label = { Text("Test Exam (Dec 2026)") }
                        )
                        SuggestionChip(
                            onClick = {
                                title = "SSC Pre-Test Exam"
                                day = "15"; month = "11"; year = "2026"
                                hour = "10"; minute = "00"; isAm = true
                            },
                            label = { Text("Pre-Test (Nov 2026)") }
                        )
                        SuggestionChip(
                            onClick = {
                                title = "Model Test Exam"
                                day = "1"; month = "10"; year = "2026"
                                hour = "10"; minute = "00"; isAm = true
                            },
                            label = { Text("Model Test (Oct 2026)") }
                        )
                    }
                }

                Text(
                    text = "Target Date (Day / Month / Year)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { if (it.length <= 2) day = it },
                        label = { Text("Day") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { if (it.length <= 2) month = it },
                        label = { Text("Month") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { if (it.length <= 4) year = it },
                        label = { Text("Year") },
                        singleLine = true,
                        modifier = Modifier.weight(1.4f)
                    )
                }

                Text(
                    text = "Target Time (Asia/Dhaka BST)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = { if (it.length <= 2) hour = it },
                        label = { Text("Hour") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { if (it.length <= 2) minute = it },
                        label = { Text("Minute") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    FilledTonalButton(
                        onClick = { isAm = !isAm },
                        modifier = Modifier.height(54.dp)
                    ) {
                        Text(if (isAm) "AM" else "PM")
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter a title."
                        return@Button
                    }
                    val d = day.toIntOrNull() ?: 1
                    val m = month.toIntOrNull() ?: 1
                    val y = year.toIntOrNull() ?: 2027
                    val h = hour.toIntOrNull() ?: 10
                    val min = minute.toIntOrNull() ?: 0

                    try {
                        val hour24 = if (isAm) {
                            if (h == 12) 0 else h
                        } else {
                            if (h == 12) 12 else h + 12
                        }
                        val dhakaZone = ZoneId.of("Asia/Dhaka")
                        val zonedDateTime = ZonedDateTime.of(
                            LocalDate.of(y, m, d),
                            LocalTime.of(hour24, min, 0),
                            dhakaZone
                        )
                        val epochMillis = zonedDateTime.toInstant().toEpochMilli()
                        val dateDisplay = zonedDateTime.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
                        val timeDisplay = zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm a (BST)", Locale.ENGLISH))

                        onSave(title, epochMillis, dateDisplay, timeDisplay)
                    } catch (e: Exception) {
                        errorMessage = "Invalid date or time."
                    }
                },
                modifier = Modifier.testTag("btn_save_countdown")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
