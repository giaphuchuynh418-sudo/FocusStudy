package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconName: String = "menu_book",
    val dailyGoalMinutes: Int = 60
)

@Entity(tableName = "flashcard_decks")
data class FlashcardDeck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val subjectName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val hintOrExplanation: String = "",
    val intervalDays: Int = 1,
    val repetitionCount: Int = 0,
    val easeFactor: Float = 2.5f,
    val lastReviewedTime: Long = 0,
    val nextReviewTime: Long = System.currentTimeMillis(),
    val masteryLevel: Int = 0 // 0 to 5
)

enum class SessionType {
    POMODORO,
    SHORT_BREAK,
    LONG_BREAK,
    STOPWATCH
}

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val durationMinutes: Int,
    val sessionType: SessionType = SessionType.POMODORO,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectName: String,
    val dueDateEpoch: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val estimatedPomodoros: Int = 2,
    val completedPomodoros: Int = 0,
    val priority: Priority = Priority.MEDIUM
)

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val subjectName: String,
    val category: String = "Concept", // "Formula", "Definition", "Summary", "Quote"
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
