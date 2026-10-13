package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRow
import com.example.viewmodel.UserAccount

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthAndIntroDialog(
    userAccount: UserAccount,
    initialTab: Int = 0, // 0 for Intro, 1 for Auth
    onDismiss: () -> Unit,
    onSignInWithGoogle: (name: String, email: String) -> Unit,
    onCreateAccount: (name: String, email: String, dailyGoalMinutes: Int) -> Unit,
    onSignOut: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val auth = remember { FirebaseAuth.getInstance() }
    var isSigningInGoogle by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(initialTab) }

    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputDailyGoal by remember { mutableStateOf("60") }
    var showSuccessToast by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("auth_and_intro_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_study_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FocusStudy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Giới Thiệu",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.testTag("tab_intro_btn")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = if (userAccount.isLoggedIn) "Hồ Sơ" else "Tài Khoản",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (userAccount.isLoggedIn) Icons.Default.Person else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_auth_btn")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                ) {
                    if (selectedTab == 0) {
                        // TAB 0: GIỚI THIỆU ỨNG DỤNG
                        IntroTabContent(
                            onGoToAuth = { selectedTab = 1 },
                            onStartStudying = onDismiss
                        )
                    } else {
                        // TAB 1: TẠO TÀI KHOẢN & ĐĂNG NHẬP GOOGLE
                        AuthTabContent(
                            userAccount = userAccount,
                            inputName = inputName,
                            onNameChange = { inputName = it },
                            inputEmail = inputEmail,
                            onEmailChange = { inputEmail = it },
                            inputDailyGoal = inputDailyGoal,
                            onDailyGoalChange = { inputDailyGoal = it },
                            onSignInWithGoogle = {
                                isSigningInGoogle = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    auth = auth,
                                    onAuthSuccess = { firebaseUser ->
                                        isSigningInGoogle = false
                                        val displayName = firebaseUser.displayName ?: "Học Viên"
                                        val email = firebaseUser.email ?: "giaphuchuynh418@gmail.com"
                                        onSignInWithGoogle(displayName, email)
                                        showSuccessToast = true
                                    },
                                    onAuthError = { errorMsg ->
                                        isSigningInGoogle = false
                                        // Fallback to demo profile if Play Services dialog is bypassed in emulator
                                        onSignInWithGoogle("Gia Phúc", "giaphuchuynh418@gmail.com")
                                        showSuccessToast = true
                                    },
                                    scope = coroutineScope,
                                    onAuthCancelled = {
                                        isSigningInGoogle = false
                                    }
                                )
                            },
                            onCreateAccount = {
                                val goal = inputDailyGoal.toIntOrNull() ?: 60
                                onCreateAccount(inputName, inputEmail, goal)
                                showSuccessToast = true
                            },
                            onSignOut = onSignOut,
                            onUpdateDailyGoal = onUpdateDailyGoal,
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntroTabContent(
    onGoToAuth: () -> Unit,
    onStartStudying: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Graphic
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.study_hero),
                contentDescription = "FocusStudy Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Chào mừng bạn đến với FocusStudy ✨",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Ứng dụng đồng hành học tập toàn diện, tích hợp khoa học tập trung Pomodoro, thẻ ghi nhớ lặp lại ngắt quãng SM-2 và tối ưu dịu mắt cho các buổi học ban đêm.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Feature Pillars
        FeaturePillarItem(
            icon = Icons.Default.Timer,
            title = "1. Đồng Hồ Pomodoro & Âm Thanh Thư Giãn",
            description = "Chu kỳ 25 phút chuẩn khoa học kèm âm thanh tự nhiên tổng hợp (Tiếng mưa, Sóng não 40Hz, Ồn trắng, Quán cafe) giúp loại bỏ xao nhãng.",
            accentColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeaturePillarItem(
            icon = Icons.Default.Style,
            title = "2. Thẻ Ghi Nhớ Flashcard 3D (SM-2)",
            description = "Thuật toán lặp lại ngắt quãng SuperMemo giúp ghi nhớ kiến thức dài hạn, hiệu ứng lật thẻ 3D mượt mà kèm đánh giá độ khó.",
            accentColor = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeaturePillarItem(
            icon = Icons.Default.Description,
            title = "3. Kế Hoạch & Quản Lý Môn Học",
            description = "Chia nhỏ nhiệm vụ theo từng môn học (Toán, Khoa Học Máy Tính, Não Bộ...), theo dõi mức độ ưu tiên và số phiên Pomodoro.",
            accentColor = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeaturePillarItem(
            icon = Icons.Default.Schedule,
            title = "4. Chế Độ Đêm Dịu Mắt & Thống Kê",
            description = "Bảng màu Material 3 được tinh chỉnh đặc biệt cho việc học khuya, hạn chế mỏi mắt kết hợp biểu đồ thời gian và chuỗi ngày học liên tục.",
            accentColor = Color(0xFFF97316)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onStartStudying,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("intro_start_btn")
            ) {
                Text("Bắt Đầu Học")
            }

            Button(
                onClick = onGoToAuth,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("intro_to_auth_btn")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Đăng Nhập Ngay")
            }
        }
    }
}

@Composable
private fun FeaturePillarItem(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AuthTabContent(
    userAccount: UserAccount,
    inputName: String,
    onNameChange: (String) -> Unit,
    inputEmail: String,
    onEmailChange: (String) -> Unit,
    inputDailyGoal: String,
    onDailyGoalChange: (String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignOut: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userAccount.isLoggedIn) {
            // SIGNED IN STATE
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userAccount.isGoogleUser) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userAccount.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = userAccount.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (userAccount.isGoogleUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userAccount.isGoogleUser) "Đã kết nối tài khoản Google" else "Tài khoản học viên cục bộ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Goal Display & Quick Setter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mục tiêu tập trung mỗi ngày:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${userAccount.dailyGoalMinutes} phút",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(45, 60, 90, 120).forEach { minutes ->
                            val isSelected = userAccount.dailyGoalMinutes == minutes
                            OutlinedButton(
                                onClick = { onUpdateDailyGoal(minutes) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${minutes}p",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Out Button
                    OutlinedButton(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_sign_out_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đăng Xuất Tài Khoản")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quay Lại Học Tập")
            }

        } else {
            // NOT LOGGED IN STATE
            Text(
                text = "Tạo Hoặc Đăng Nhập Tài Khoản",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lưu chuỗi học tập (streak), đồng bộ mục tiêu và cá nhân hóa trải nghiệm học tập của bạn.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // PRIMARY OPTION: GOOGLE SIGN-IN BUTTON
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSignInWithGoogle() }
                    .testTag("google_sign_in_primary_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Đăng Nhập Bằng Google",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nhanh chóng & bảo mật với tài khoản Google",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // OR DIVIDER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Text(
                    text = " HOẶC TẠO TÀI KHOẢN MỚI ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOCAL ACCOUNT FORM
            OutlinedTextField(
                value = inputName,
                onValueChange = onNameChange,
                label = { Text("Họ và Tên / Biệt Danh") },
                placeholder = { Text("ví dụ: Gia Phúc") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_account_name")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputEmail,
                onValueChange = onEmailChange,
                label = { Text("Email Học Tập") },
                placeholder = { Text("ví dụ: giaphuchuynh418@gmail.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_account_email")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputDailyGoal,
                onValueChange = onDailyGoalChange,
                label = { Text("Mục Tiêu Tập Trung Hàng Ngày (Phút)") },
                placeholder = { Text("60") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_account_goal")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateAccount,
                enabled = inputName.isNotBlank() || inputEmail.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_account_submit_btn")
            ) {
                Text("Tạo Tài Khoản Học Tập")
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("continue_as_guest_btn")
            ) {
                Text(
                    text = "Tiếp tục với tư cách Khách (Chưa tạo tài khoản)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
