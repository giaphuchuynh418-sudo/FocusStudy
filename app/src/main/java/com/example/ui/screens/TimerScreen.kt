package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.example.ui.theme.ThemeMode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AmbientSoundSelector
import com.example.ui.components.AuthAndIntroDialog
import com.example.ui.components.CircularTimerDisplay
import com.example.ui.components.GreetingIntroCard
import com.example.viewmodel.StudyViewModel
import com.example.viewmodel.TimerMode

@Composable
fun TimerScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    var showAuthAndIntroDialog by remember { mutableStateOf(false) }
    var dialogInitialTab by remember { mutableStateOf(0) } // 0: Intro, 1: Auth

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectGoal by remember { mutableStateOf("60") }

    val scrollState = rememberScrollState()

    if (showAuthAndIntroDialog) {
        AuthAndIntroDialog(
            userAccount = userAccount,
            initialTab = dialogInitialTab,
            onDismiss = { showAuthAndIntroDialog = false },
            onSignInWithGoogle = { name, email ->
                viewModel.signInWithGoogle(name, email)
                showAuthAndIntroDialog = false
            },
            onCreateAccount = { name, email, goal ->
                viewModel.createCustomAccount(name, email, goal)
                showAuthAndIntroDialog = false
            },
            onSignOut = {
                viewModel.signOut()
            },
            onUpdateDailyGoal = { goal ->
                viewModel.updateDailyGoal(goal)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lời Chào & Giới Thiệu / Trạng Thái Tài Khoản Header
        GreetingIntroCard(
            userAccount = userAccount,
            onOpenIntro = {
                dialogInitialTab = 0
                showAuthAndIntroDialog = true
            },
            onOpenAuth = {
                dialogInitialTab = 1
                showAuthAndIntroDialog = true
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Decorative Study Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .testTag("study_hero_banner"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.study_hero),
                    contentDescription = "Study desk artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(topStart = 16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${timerState.completedSessionsToday} phiên hôm nay",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Eye Comfort & Night Study Indicator
        val themeMode by viewModel.themeMode.collectAsState()
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (themeMode == ThemeMode.DARK) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.cycleThemeMode() }
                .testTag("eye_comfort_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = if (themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (themeMode) {
                            ThemeMode.DARK -> "Chế độ đêm: Giảm chói mắt & tông màu ấm dịu"
                            ThemeMode.LIGHT -> "Chế độ ngày: Tương phản rõ nét ban ngày"
                            ThemeMode.SYSTEM -> "Tự động: Thay đổi theo lịch hệ thống"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Đổi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subject Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Môn học:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            subjects.forEach { subj ->
                val isSelected = subj.name == timerState.currentSubject
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedSubject(subj.name) },
                    label = { Text(subj.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("subject_chip_${subj.name}")
                )
            }

            IconButton(
                onClick = { showAddSubjectDialog = true },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .testTag("add_subject_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm môn học",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Mode Switcher (Pill tab row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TimerMode.values().forEach { mode ->
                val isSelected = timerState.mode == mode
                if (isSelected) {
                    FilledTonalButton(
                        onClick = { viewModel.setTimerMode(mode) },
                        modifier = Modifier.testTag("mode_btn_${mode.name}")
                    ) {
                        Text(mode.title)
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.setTimerMode(mode) },
                        modifier = Modifier.testTag("mode_btn_${mode.name}")
                    ) {
                        Text(mode.title)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Circular Canvas Timer Display
        CircularTimerDisplay(
            remainingSeconds = timerState.remainingSeconds,
            totalSeconds = timerState.totalSeconds,
            stopwatchSeconds = timerState.stopwatchElapsedSeconds,
            mode = timerState.mode,
            isRunning = timerState.isRunning,
            subjectName = timerState.currentSubject,
            modifier = Modifier.testTag("circular_timer")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: Play / Pause / Reset
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .testTag("reset_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Đặt lại",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (timerState.isRunning) {
                        viewModel.pauseTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier
                    .height(58.dp)
                    .width(170.dp)
                    .testTag("toggle_timer_button"),
                shape = RoundedCornerShape(29.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerState.isRunning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (timerState.isRunning) "Tạm dừng" else "Bắt đầu",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (timerState.isRunning) "Tạm Dừng" else "Tập Trung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ambient Soundscape Selector
        AmbientSoundSelector(
            selectedSound = timerState.ambientSound,
            onSoundSelected = { viewModel.setAmbientSound(it) },
            isPlaying = timerState.isRunning,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Motivational Focus Quote Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"Sự tập trung là một cơ bắp. Càng loại bỏ được nhiều phiền nhiễu, khả năng tập trung sâu của bạn càng trở nên mạnh mẽ.\"",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Thêm Môn Học Mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSubjectName,
                        onValueChange = { newSubjectName = it },
                        label = { Text("Tên môn học") },
                        placeholder = { Text("Ví dụ: Hóa học hữu cơ") },
                        modifier = Modifier.fillMaxWidth().testTag("subject_name_input")
                    )
                    OutlinedTextField(
                        value = newSubjectGoal,
                        onValueChange = { newSubjectGoal = it },
                        label = { Text("Mục tiêu hàng ngày (Phút)") },
                        modifier = Modifier.fillMaxWidth().testTag("subject_goal_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubjectName.isNotBlank()) {
                            viewModel.addSubject(
                                name = newSubjectName.trim(),
                                colorHex = "#3B82F6",
                                targetDailyMinutes = newSubjectGoal.toIntOrNull() ?: 60
                            )
                            viewModel.setSelectedSubject(newSubjectName.trim())
                            newSubjectName = ""
                            showAddSubjectDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_subject_btn")
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
