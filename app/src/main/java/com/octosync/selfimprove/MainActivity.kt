package com.octosync.selfimprove

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.*
import com.octosync.selfimprove.ui.NavDestination
import com.octosync.selfimprove.ui.history.HistoryScreen
import com.octosync.selfimprove.ui.theme.SelfImproveTheme
import com.octosync.selfimprove.worker.ReminderWorker
import com.octosync.selfimprove.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HabitViewModel = hiltViewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            
            SelfImproveTheme(darkTheme = isDarkMode) {
                val backStack = rememberNavBackStack(NavDestination.Home)
                
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeAt(backStack.lastIndex) },
                    transitionSpec = {
                        (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)))
                            .togetherWith(slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300)))
                    },
                    popTransitionSpec = {
                        (slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300)))
                            .togetherWith(slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)))
                    },
                    entryProvider = { key ->
                        when (key) {
                            is NavDestination.Home -> NavEntry(key) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToHistory = { backStack.add(NavDestination.History) }
                                )
                            }
                            is NavDestination.History -> NavEntry(key) {
                                HistoryScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                            else -> error("Unknown destination: $key")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val habits by viewModel.todayHabits.collectAsStateWithLifecycle()
    val streaks by viewModel.habitStreaks.collectAsStateWithLifecycle()
    val globalStreak by viewModel.globalStreak.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(value = false) }

    // Request notification permission for Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(12, TimeUnit.HOURS)
            .build()
            
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "habit_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
            
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "habit_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode(!isDarkMode) }) {
                        Icon(
                            painter = painterResource(id = if (isDarkMode) android.R.drawable.ic_menu_day else android.R.drawable.ic_menu_compass),
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                            contentDescription = "History"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colorResource(id = R.color.primary),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_habit))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorResource(id = R.color.background))
        ) {
            HomeHeader(globalStreak)
            
            HabitListContent(
                modifier = Modifier.weight(1f),
                habits = habits,
                streaks = streaks,
                onUpdateHabit = viewModel::update,
                onDeleteHabit = viewModel::delete,
                onAddHabit = { name ->
                    viewModel.insert(Habit(name = name))
                }
            )
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, reminderTime ->
                viewModel.insert(Habit(name = name, category = category, reminderTime = reminderTime))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HomeHeader(streakCount: Int) {
    Column(modifier = Modifier.padding(16.dp)) {
        HeaderSection(streakCount)
        SectionTitle(stringResource(id = R.string.daily_habits))
    }
}

@Composable
fun HabitListContent(
    modifier: Modifier = Modifier,
    habits: List<Habit>,
    streaks: Map<String, Int>,
    onUpdateHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onAddHabit: (String) -> Unit
) {
    var quickHabitName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            val streak = remember(streaks, habit.name) { streaks[habit.name] ?: 0 }
            HabitItem(
                habit = habit,
                streak = streak,
                onStatusChange = onUpdateHabit,
                onDelete = { onDeleteHabit(habit) }
            )
        }

        item {
            OutlinedTextField(
                value = quickHabitName,
                onValueChange = { quickHabitName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Quick add habit...") },
                trailingIcon = {
                    if (quickHabitName.isNotBlank()) {
                        IconButton(onClick = {
                            onAddHabit(quickHabitName)
                            quickHabitName = ""
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item { SectionTitle(stringResource(id = R.string.daily_evaluation)) }
        item { EvaluationSection() }
        item { MotivationSection() }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun HeaderSection(streakCount: Int) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
    val dateString = remember { dateFormat.format(Date()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.primary))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateString,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = colorResource(id = R.color.streak_orange),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.streak_format, streakCount),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun HabitItem(habit: Habit, streak: Int, onStatusChange: (Habit) -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded },
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habit.category.name,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        if (streak > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = colorResource(id = R.color.streak_orange).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🔥 $streak",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.streak_orange),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = habit.status)
                    if (expanded) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_delete),
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val successSelected = habit.status == Habit.Status.SUCCESS
                    val failedSelected = habit.status == Habit.Status.FAILED

                    StatusButton(
                        text = stringResource(id = R.string.habit_success),
                        iconId = R.drawable.ic_check,
                        isSelected = successSelected,
                        color = colorResource(id = R.color.success_green),
                        onClick = {
                            val newStatus = if (successSelected) Habit.Status.PENDING else Habit.Status.SUCCESS
                            onStatusChange(habit.copy(status = newStatus))
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    StatusButton(
                        text = stringResource(id = R.string.habit_failed),
                        iconId = R.drawable.ic_close,
                        isSelected = failedSelected,
                        color = colorResource(id = R.color.failure_red),
                        onClick = {
                            val newStatus = if (failedSelected) Habit.Status.PENDING else Habit.Status.FAILED
                            onStatusChange(habit.copy(status = newStatus))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: Habit.Status) {
    val pair: Pair<String, Color> = when (status) {
        Habit.Status.SUCCESS -> "Done" to colorResource(id = R.color.success_green)
        Habit.Status.FAILED -> "Failed" to colorResource(id = R.color.failure_red)
        Habit.Status.PENDING -> "Pending" to Color.Gray
    }
    val text = pair.first
    val color = pair.second

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusButton(text: String, iconId: Int, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    val containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) color else Color.Gray
    val borderColor = if (isSelected) color else Color.LightGray

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

@Composable
fun EvaluationSection() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.day_status),
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            var selectedStatus by remember { mutableIntStateOf(1) } // 0: Great, 1: Ok, 2: Bad
            Row(modifier = Modifier.padding(top = 8.dp)) {
                listOf(R.string.status_great, R.string.status_ok, R.string.status_bad).forEachIndexed { index, resId ->
                    OutlinedButton(
                        onClick = { selectedStatus = index },
                        modifier = Modifier.weight(1f),
                        shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                            2 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                            else -> RoundedCornerShape(0.dp)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedStatus == index) colorResource(id = R.color.primary).copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (selectedStatus == index) colorResource(id = R.color.primary) else Color.Gray
                        )
                    ) {
                        Text(stringResource(id = resId), fontSize = 13.sp)
                    }
                }
            }

            var notes by remember { mutableStateOf("") }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text(stringResource(id = R.string.regrets_notes)) },
                minLines = 3
            )
        }
    }
}

@Composable
fun MotivationSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.secondaryContainer).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(id = R.string.daily_motivation),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.onSecondaryContainer)
            )
            Text(
                text = stringResource(id = R.string.default_quote),
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = colorResource(id = R.color.onSecondaryContainer)
            )
            Text(
                text = stringResource(id = R.string.default_author),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp),
                fontSize = 14.sp,
                color = colorResource(id = R.color.onSecondaryContainer)
            )
        }
    }
}

@Composable
fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (String, Habit.Category, Long?) -> Unit) {
    var habitName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Habit.Category.OTHER) }
    var selectedTime by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.new_goal)) },
        text = {
            Column {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text(stringResource(id = R.string.habit_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category", style = MaterialTheme.typography.labelMedium)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Habit.Category.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase(Locale.getDefault()) }) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                selectedTime = (hour * 60 + minute) * 60 * 1000L
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val timeText = if (selectedTime != null) {
                        val hours = (selectedTime!! / (60 * 60 * 1000))
                        val minutes = (selectedTime!! % (60 * 60 * 1000)) / (60 * 1000)
                        String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
                    } else {
                        "Set Reminder Time"
                    }
                    Text(timeText)
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (habitName.isNotBlank()) onAdd(habitName, selectedCategory, selectedTime) }) {
                Text(stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
