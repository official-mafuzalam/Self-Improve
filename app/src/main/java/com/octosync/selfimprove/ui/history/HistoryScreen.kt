package com.octosync.selfimprove.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.octosync.selfimprove.Habit
import com.octosync.selfimprove.HabitViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HistoryScreen(
    viewModel: HabitViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    val consistencyData by viewModel.consistencyData.collectAsStateWithLifecycle()
    val groupedHabits = remember(allHabits) {
        allHabits.groupBy { it.date }
    }
    val sortedDates = remember(groupedHabits) {
        groupedHabits.keys.sortedDescending()
    }
    
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit History") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navigator.canNavigateBack()) {
                            scope.launch { navigator.navigateBack() }
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (allHabits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No habit history yet.")
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                // Analytics Section
                Text(
                    text = "Weekly Consistency",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                ConsistencyChart(consistencyData)
                
                Spacer(modifier = Modifier.height(16.dp))

                NavigableListDetailPaneScaffold(
                    modifier = Modifier.weight(1f),
                    navigator = navigator,
                    listPane = {
                        AnimatedPane {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(sortedDates) { date ->
                                    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
                                    val dateString = remember(date) { dateFormat.format(Date(date)) }
                                    
                                    val isSelected = navigator.currentDestination?.contentKey == date

                                    Card(
                                        onClick = { 
                                            scope.launch { 
                                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, date) 
                                            } 
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Text(
                                            text = dateString,
                                            modifier = Modifier.padding(16.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            val selectedDate = navigator.currentDestination?.contentKey
                            if (selectedDate != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
                                    Text(
                                        text = dateFormat.format(Date(selectedDate)),
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    groupedHabits[selectedDate]?.forEach { habit ->
                                        HistoryHabitItem(habit = habit)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Select a date to see details")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ConsistencyChart(last7Days: List<Float>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            last7Days.forEach { successRate ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .fillMaxHeight(successRate.coerceAtLeast(0.1f))
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryHabitItem(habit: Habit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = habit.name, fontSize = 14.sp)
            StatusBadge(status = habit.status)
        }
    }
}

@Composable
fun StatusBadge(status: Habit.Status) {
    val (text, color) = when (status) {
        Habit.Status.SUCCESS -> "Success" to Color(0xFF4CAF50)
        Habit.Status.FAILED -> "Failed" to Color(0xFFF44336)
        Habit.Status.PENDING -> "Pending" to Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = androidx.compose.foundation.shape.CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
