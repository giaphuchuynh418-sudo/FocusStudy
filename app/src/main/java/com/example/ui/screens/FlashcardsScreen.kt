package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardDeck
import com.example.ui.components.FlashcardView
import com.example.viewmodel.StudyViewModel

@Composable
fun FlashcardsScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val decks by viewModel.decks.collectAsState()
    val studyState by viewModel.flashcardStudyState.collectAsState()

    var showAddDeckDialog by remember { mutableStateOf(false) }
    var newDeckTitle by remember { mutableStateOf("") }
    var newDeckDesc by remember { mutableStateOf("") }
    var newDeckSubject by remember { mutableStateOf("Computer Science") }

    var showAddCardDeckId by remember { mutableStateOf<Long?>(null) }
    var newCardQuestion by remember { mutableStateOf("") }
    var newCardAnswer by remember { mutableStateOf("") }
    var newCardHint by remember { mutableStateOf("") }

    // Active Study Mode
    if (studyState.activeDeck != null) {
        BackHandler {
            viewModel.exitDeckStudy()
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Study Header with Back Button and Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.exitDeckStudy() },
                    modifier = Modifier.testTag("exit_study_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Thoát ôn tập"
                    )
                }

                Text(
                    text = studyState.activeDeck?.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Text(
                    text = if (studyState.cards.isNotEmpty()) {
                        "${(studyState.currentIndex + 1).coerceAtMost(studyState.cards.size)}/${studyState.cards.size}"
                    } else "0/0",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val currentFraction = if (studyState.cards.isNotEmpty()) {
                studyState.currentIndex.toFloat() / studyState.cards.size.toFloat()
            } else 1f

            LinearProgressIndicator(
                progress = { currentFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (studyState.isFinished) {
                // Completed Session Result
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = "Chúc mừng",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hoàn Thành Bộ Thẻ!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn đã ôn tập xong tất cả ${studyState.cards.size} thẻ ghi nhớ. Thuật toán lặp lại ngắt quãng đã cập nhật độ thành thạo và chu kỳ ôn tập tiếp theo.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.exitDeckStudy() },
                            modifier = Modifier.fillMaxWidth().testTag("finish_review_btn")
                        ) {
                            Text("Quay Lại Danh Sách")
                        }
                    }
                }
            } else if (studyState.currentIndex < studyState.cards.size) {
                val currentCard = studyState.cards[studyState.currentIndex]
                FlashcardView(
                    card = currentCard,
                    isFlipped = studyState.isFlipped,
                    onFlip = { viewModel.flipFlashcard() },
                    onRate = { viewModel.rateCard(it) }
                )
            } else {
                Text(
                    text = "Không có thẻ ghi nhớ nào trong bộ này.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // Deck List Mode
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bộ Thẻ Ghi Nhớ",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Học sâu với kỹ thuật lặp lại ngắt quãng",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(decks) { deck ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deck_item_${deck.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = deck.subjectName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { showAddCardDeckId = deck.id },
                                        modifier = Modifier.size(32.dp).testTag("add_card_to_deck_${deck.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Thêm thẻ",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteDeck(deck) },
                                        modifier = Modifier.size(32.dp).testTag("delete_deck_${deck.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Xóa bộ thẻ",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = deck.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (deck.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = deck.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.startDeckStudy(deck) },
                                modifier = Modifier.fillMaxWidth().testTag("study_deck_btn_${deck.id}"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Học Bộ Thẻ Này")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Floating Action Button to Add Deck
            FloatingActionButton(
                onClick = { showAddDeckDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("fab_add_deck"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tạo bộ thẻ"
                )
            }
        }
    }

    // Add Deck Dialog
    if (showAddDeckDialog) {
        AlertDialog(
            onDismissRequest = { showAddDeckDialog = false },
            title = { Text("Tạo Bộ Thẻ Mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newDeckTitle,
                        onValueChange = { newDeckTitle = it },
                        label = { Text("Tiêu đề bộ thẻ") },
                        placeholder = { Text("Ví dụ: Hóa học hữu cơ") },
                        modifier = Modifier.fillMaxWidth().testTag("deck_title_input")
                    )
                    OutlinedTextField(
                        value = newDeckDesc,
                        onValueChange = { newDeckDesc = it },
                        label = { Text("Mô tả") },
                        placeholder = { Text("Ví dụ: Cơ chế phản ứng SN1, SN2") },
                        modifier = Modifier.fillMaxWidth().testTag("deck_desc_input")
                    )
                    OutlinedTextField(
                        value = newDeckSubject,
                        onValueChange = { newDeckSubject = it },
                        label = { Text("Môn học") },
                        modifier = Modifier.fillMaxWidth().testTag("deck_subject_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeckTitle.isNotBlank()) {
                            viewModel.addDeck(
                                title = newDeckTitle.trim(),
                                description = newDeckDesc.trim(),
                                subjectName = newDeckSubject.trim()
                            )
                            newDeckTitle = ""
                            newDeckDesc = ""
                            showAddDeckDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_deck_btn")
                ) {
                    Text("Tạo Bộ Thẻ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDeckDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Add Card to Deck Dialog
    if (showAddCardDeckId != null) {
        AlertDialog(
            onDismissRequest = { showAddCardDeckId = null },
            title = { Text("Thêm Thẻ Ghi Nhớ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCardQuestion,
                        onValueChange = { newCardQuestion = it },
                        label = { Text("Câu hỏi / Thuật ngữ") },
                        modifier = Modifier.fillMaxWidth().testTag("card_question_input")
                    )
                    OutlinedTextField(
                        value = newCardAnswer,
                        onValueChange = { newCardAnswer = it },
                        label = { Text("Đáp án / Định nghĩa") },
                        modifier = Modifier.fillMaxWidth().testTag("card_answer_input")
                    )
                    OutlinedTextField(
                        value = newCardHint,
                        onValueChange = { newCardHint = it },
                        label = { Text("Gợi ý / Ghi chú (Tùy chọn)") },
                        modifier = Modifier.fillMaxWidth().testTag("card_hint_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dId = showAddCardDeckId
                        if (dId != null && newCardQuestion.isNotBlank() && newCardAnswer.isNotBlank()) {
                            viewModel.addFlashcard(
                                deckId = dId,
                                question = newCardQuestion.trim(),
                                answer = newCardAnswer.trim(),
                                hint = newCardHint.trim()
                            )
                            newCardQuestion = ""
                            newCardAnswer = ""
                            newCardHint = ""
                            showAddCardDeckId = null
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_card_btn")
                ) {
                    Text("Thêm Thẻ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDeckId = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}
