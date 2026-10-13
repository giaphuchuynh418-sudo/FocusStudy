package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CheatsheetScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.FocusStudyTheme
import com.example.viewmodel.StudyViewModel

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.IconButton
import com.example.ui.components.AuthAndIntroDialog
import com.example.ui.theme.ThemeMode

enum class StudyTab(val title: String, val icon: ImageVector) {
    TIMER("Đồng Hồ", Icons.Default.Timer),
    FLASHCARDS("Thẻ Nhớ", Icons.Default.Style),
    PLANNER("Kế Hoạch", Icons.Default.Checklist),
    STATS("Thống Kê", Icons.Default.BarChart),
    CHEATSHEET("Sổ Tay", Icons.Default.Description)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StudyViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            FocusStudyTheme(themeMode = themeMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: StudyViewModel) {
    var selectedTab by remember { mutableStateOf(StudyTab.TIMER) }
    val timerState by viewModel.timerState.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    var showTopBarAuthDialog by remember { mutableStateOf(false) }
    var topBarDialogInitialTab by remember { mutableStateOf(1) }

    val completedToday = timerState.completedSessionsToday

    if (showTopBarAuthDialog) {
        AuthAndIntroDialog(
            userAccount = userAccount,
            initialTab = topBarDialogInitialTab,
            onDismiss = { showTopBarAuthDialog = false },
            onSignInWithGoogle = { name, email ->
                viewModel.signInWithGoogle(name, email)
                showTopBarAuthDialog = false
            },
            onCreateAccount = { name, email, goal ->
                viewModel.createCustomAccount(name, email, goal)
                showTopBarAuthDialog = false
            },
            onSignOut = {
                viewModel.signOut()
            },
            onUpdateDailyGoal = { goal ->
                viewModel.updateDailyGoal(goal)
            }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_study_logo),
                                contentDescription = "FocusStudy Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "FocusStudy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // User Profile / Google Sign-In Button
                        IconButton(
                            onClick = {
                                topBarDialogInitialTab = 1
                                showTopBarAuthDialog = true
                            },
                            modifier = Modifier.testTag("top_bar_account_btn")
                        ) {
                            if (userAccount.isGoogleUser) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Tài khoản Google",
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Tài khoản người dùng",
                                    tint = if (userAccount.isLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Night Study / Day Focus / System Theme Toggle
                        IconButton(
                            onClick = { viewModel.cycleThemeMode() },
                            modifier = Modifier.testTag("theme_mode_toggle_btn")
                        ) {
                            val (themeIcon, themeDesc) = when (themeMode) {
                                ThemeMode.DARK -> Pair(Icons.Default.DarkMode, "Chế Độ Đêm (Bảo Vệ Mắt)")
                                ThemeMode.LIGHT -> Pair(Icons.Default.LightMode, "Chế Độ Ngày (Tập Trung)")
                                ThemeMode.SYSTEM -> Pair(Icons.Default.BrightnessAuto, "Tự Động Theo Hệ Thống")
                            }
                            Icon(
                                imageVector = themeIcon,
                                contentDescription = themeDesc,
                                tint = if (themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Chuỗi ngày",
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$completedToday phiên",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        StudyTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight().testTag("side_navigation_rail")
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        StudyTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = { Text(tab.title) },
                                modifier = Modifier.testTag("rail_tab_${tab.name}")
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Content Pane
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        TabContent(selectedTab = selectedTab, viewModel = viewModel)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabContent(selectedTab = selectedTab, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TabContent(
    selectedTab: StudyTab,
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    when (selectedTab) {
        StudyTab.TIMER -> TimerScreen(viewModel = viewModel, modifier = modifier)
        StudyTab.FLASHCARDS -> FlashcardsScreen(viewModel = viewModel, modifier = modifier)
        StudyTab.PLANNER -> PlannerScreen(viewModel = viewModel, modifier = modifier)
        StudyTab.STATS -> StatsScreen(viewModel = viewModel, modifier = modifier)
        StudyTab.CHEATSHEET -> CheatsheetScreen(viewModel = viewModel, modifier = modifier)
    }
}
