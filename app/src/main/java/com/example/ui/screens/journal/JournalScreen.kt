package com.example.ui.screens.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.data.model.Mood
import com.example.ui.components.MoodSelectorRow
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.QuitUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JournalScreen(
    state: QuitUiState,
    onAddEntry: (title: String, content: String, mood: Mood, tags: List<String>) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isComposing by remember { mutableStateOf(false) }
    var entryTitle by remember { mutableStateOf("") }
    var entryContent by remember { mutableStateOf("") }
    var entryMood by remember { mutableStateOf(Mood.GOOD) }
    val selectedTags = remember { mutableStateListOf<String>() }
    var filterTag by remember { mutableStateOf<String?>(null) }

    val allTags = listOf(
        "Craving",
        "Stress",
        "Success",
        "Trigger",
        "Motivation",
        "Difficult day",
        "Milestone",
        "Gratitude"
    )

    val displayedEntries = state.journalEntries.filter { entry ->
        if (filterTag == null) true
        else entry.getTagsList().contains(filterTag)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Journal",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "A private space to process triggers and wins.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = { isComposing = !isComposing },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .testTag("open_journal_composer")
                ) {
                    Icon(
                        imageVector = if (isComposing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "New Entry",
                        tint = Color.White
                    )
                }
            }
        }

        // Inline Journal Composer Card
        item {
            AnimatedVisibility(visible = isComposing) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "How was today?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        MoodSelectorRow(
                            selectedMood = entryMood,
                            onMoodSelected = { entryMood = it }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = entryTitle,
                            onValueChange = { entryTitle = it },
                            placeholder = { Text("Title (e.g., Evening walk reflection)") },
                            modifier = Modifier.fillMaxWidth().testTag("journal_title_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceSubtle,
                                unfocusedContainerColor = SurfaceSubtle,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = entryContent,
                            onValueChange = { entryContent = it },
                            placeholder = { Text("Write your thoughts, triggers, or gratitude...") },
                            modifier = Modifier.fillMaxWidth().testTag("journal_content_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceSubtle,
                                unfocusedContainerColor = SurfaceSubtle,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            minLines = 3,
                            maxLines = 6
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Select Tags",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) TealPrimary else SurfaceSubtle)
                                        .clickable {
                                            if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (entryContent.isNotBlank()) {
                                    onAddEntry(
                                        entryTitle.ifBlank { "Daily Reflection" },
                                        entryContent,
                                        entryMood,
                                        selectedTags.toList()
                                    )
                                    entryTitle = ""
                                    entryContent = ""
                                    selectedTags.clear()
                                    isComposing = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_journal_entry_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(
                                text = "Save Entry",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Tag Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = filterTag == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAll) TealPrimary else SurfaceWhite)
                            .border(1.dp, if (isAll) TealPrimary else SurfaceBorder, RoundedCornerShape(12.dp))
                            .clickable { filterTag = null }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "All Entries (${state.journalEntries.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isAll) Color.White else TextSecondary
                        )
                    }
                }
                items(allTags) { tag ->
                    val isSel = filterTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) TealPrimary else SurfaceWhite)
                            .border(1.dp, if (isSel) TealPrimary else SurfaceBorder, RoundedCornerShape(12.dp))
                            .clickable { filterTag = if (isSel) null else tag }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSel) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // Journal Entries Feed
        if (displayedEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📝", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No journal entries found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button to record how you're feeling.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(displayedEntries) { entry ->
                JournalEntryCard(
                    entry = entry,
                    onDelete = { onDeleteEntry(entry.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = entry.mood.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = entry.title.ifBlank { "Journal Reflection" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Text(
                            text = entry.dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            val tags = entry.getTagsList()
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceSubtle
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = TealPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
