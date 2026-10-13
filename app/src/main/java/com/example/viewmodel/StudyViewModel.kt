package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundManager
import com.example.audio.AmbientSoundType
import com.example.data.local.AppDatabase
import com.example.data.model.Flashcard
import com.example.data.model.FlashcardDeck
import com.example.data.model.Priority
import com.example.data.model.SessionType
import com.example.data.model.StudyNote
import com.example.data.model.StudySession
import com.example.data.model.StudyTask
import com.example.data.model.Subject
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.ui.theme.ThemeMode
import java.util.Calendar

enum class TimerMode(val defaultMinutes: Int, val title: String, val sessionType: SessionType) {
    POMODORO(25, "Tập Trung Sâu", SessionType.POMODORO),
    LONG_FOCUS(50, "Cường Độ Cao", SessionType.POMODORO),
    SHORT_BREAK(5, "Nghỉ Ngắn", SessionType.SHORT_BREAK),
    LONG_BREAK(15, "Nghỉ Dài", SessionType.LONG_BREAK),
    STOPWATCH(0, "Bấm Giờ Tự Do", SessionType.STOPWATCH)
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.POMODORO,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val stopwatchElapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val completedSessionsToday: Int = 0,
    val currentSubject: String = "Khoa Học Máy Tính",
    val ambientSound: AmbientSoundType = AmbientSoundType.OFF
)

data class UserAccount(
    val name: String = "Học Viên",
    val email: String = "khach@focusstudy.local",
    val isGoogleUser: Boolean = false,
    val isLoggedIn: Boolean = false,
    val dailyGoalMinutes: Int = 60,
    val joinedAt: String = "Hôm nay"
)

data class FlashcardStudyState(
    val activeDeck: FlashcardDeck? = null,
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isFinished: Boolean = false,
    val correctCount: Int = 0
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository
    private val ambientManager = AmbientSoundManager()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudyRepository(db.studyDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val subjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val decks: StateFlow<List<FlashcardDeck>> = repository.allDecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<StudyTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme Mode (System, Day Light, Late-Night Dark)
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun cycleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }
    }

    // Account State
    private val _userAccount = MutableStateFlow(
        UserAccount(
            name = "Học Viên",
            email = "khach@focusstudy.local",
            isGoogleUser = false,
            isLoggedIn = false,
            dailyGoalMinutes = 60
        )
    )
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _showIntroDialog = MutableStateFlow(false)
    val showIntroDialog: StateFlow<Boolean> = _showIntroDialog.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    fun setIntroDialogVisible(visible: Boolean) {
        _showIntroDialog.value = visible
    }

    fun setAuthDialogVisible(visible: Boolean) {
        _showAuthDialog.value = visible
    }

    fun signInWithGoogle(name: String = "Gia Phúc", email: String = "giaphuchuynh418@gmail.com") {
        _userAccount.value = _userAccount.value.copy(
            name = name,
            email = email,
            isGoogleUser = true,
            isLoggedIn = true
        )
        _showAuthDialog.value = false
    }

    fun createCustomAccount(name: String, email: String, dailyGoalMinutes: Int) {
        _userAccount.value = _userAccount.value.copy(
            name = if (name.isNotBlank()) name.trim() else "Học Viên",
            email = if (email.isNotBlank()) email.trim() else "user@focusstudy.app",
            dailyGoalMinutes = dailyGoalMinutes.coerceAtLeast(15),
            isGoogleUser = false,
            isLoggedIn = true
        )
        _showAuthDialog.value = false
    }

    fun signOut() {
        _userAccount.value = UserAccount(
            name = "Học Viên",
            email = "khach@focusstudy.local",
            isGoogleUser = false,
            isLoggedIn = false,
            dailyGoalMinutes = 60
        )
    }

    fun updateDailyGoal(minutes: Int) {
        _userAccount.value = _userAccount.value.copy(dailyGoalMinutes = minutes.coerceAtLeast(15))
    }

    // Timer State
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState: StateFlow<TimerUiState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    // Flashcard Study State
    private val _flashcardStudyState = MutableStateFlow(FlashcardStudyState())
    val flashcardStudyState: StateFlow<FlashcardStudyState> = _flashcardStudyState.asStateFlow()

    // Task Filter State
    private val _taskFilterSubject = MutableStateFlow<String?>(null)
    val taskFilterSubject: StateFlow<String?> = _taskFilterSubject.asStateFlow()

    // Notes Filter & Search
    private val _notesSearchQuery = MutableStateFlow("")
    val notesSearchQuery: StateFlow<String> = _notesSearchQuery.asStateFlow()

    private val _notesCategoryFilter = MutableStateFlow<String?>(null)
    val notesCategoryFilter: StateFlow<String?> = _notesCategoryFilter.asStateFlow()

    val filteredNotes: StateFlow<List<StudyNote>> = combine(
        notes,
        _notesSearchQuery,
        _notesCategoryFilter
    ) { list, query, category ->
        list.filter { note ->
            val matchQuery = query.isBlank() ||
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true) ||
                note.subjectName.contains(query, ignoreCase = true)
            val matchCategory = category == null || note.category.equals(category, ignoreCase = true)
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Timer Controls ---

    fun setTimerMode(mode: TimerMode) {
        pauseTimer()
        val totalSecs = mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            mode = mode,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs,
            stopwatchElapsedSeconds = 0,
            isRunning = false
        )
    }

    fun setTimerDurationMinutes(minutes: Int) {
        pauseTimer()
        val totalSecs = minutes * 60
        _timerState.value = _timerState.value.copy(
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs,
            isRunning = false
        )
    }

    fun setSelectedSubject(subjectName: String) {
        _timerState.value = _timerState.value.copy(currentSubject = subjectName)
    }

    fun setAmbientSound(sound: AmbientSoundType) {
        _timerState.value = _timerState.value.copy(ambientSound = sound)
        if (_timerState.value.isRunning && sound != AmbientSoundType.OFF) {
            ambientManager.playSound(sound)
        } else {
            ambientManager.stopSound()
        }
    }

    fun startTimer() {
        if (_timerState.value.isRunning) return
        _timerState.value = _timerState.value.copy(isRunning = true)

        if (_timerState.value.ambientSound != AmbientSoundType.OFF) {
            ambientManager.playSound(_timerState.value.ambientSound)
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                delay(1000L)
                val current = _timerState.value
                if (current.mode == TimerMode.STOPWATCH) {
                    _timerState.value = current.copy(
                        stopwatchElapsedSeconds = current.stopwatchElapsedSeconds + 1
                    )
                } else {
                    if (current.remainingSeconds > 1) {
                        _timerState.value = current.copy(
                            remainingSeconds = current.remainingSeconds - 1
                        )
                    } else {
                        // Finished
                        onTimerFinished()
                        break
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        ambientManager.stopSound()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        pauseTimer()
        val mode = _timerState.value.mode
        val total = mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            totalSeconds = total,
            remainingSeconds = total,
            stopwatchElapsedSeconds = 0,
            isRunning = false
        )
    }

    private fun onTimerFinished() {
        val current = _timerState.value
        _timerState.value = current.copy(
            remainingSeconds = 0,
            isRunning = false,
            completedSessionsToday = current.completedSessionsToday + 1
        )
        ambientManager.stopSound()
        ambientManager.playCompletionChime()

        val durationMinutes = if (current.mode == TimerMode.STOPWATCH) {
            (current.stopwatchElapsedSeconds / 60).coerceAtLeast(1)
        } else {
            (current.totalSeconds / 60).coerceAtLeast(1)
        }

        viewModelScope.launch {
            repository.logSession(
                subjectName = current.currentSubject,
                durationMinutes = durationMinutes,
                type = current.mode.sessionType,
                note = "Completed ${current.mode.title}"
            )
        }
    }

    // --- Flashcard Operations ---

    fun startDeckStudy(deck: FlashcardDeck) {
        viewModelScope.launch {
            val cards = repository.getCardsForDeckSync(deck.id)
            _flashcardStudyState.value = FlashcardStudyState(
                activeDeck = deck,
                cards = cards,
                currentIndex = 0,
                isFlipped = false,
                isFinished = cards.isEmpty(),
                correctCount = 0
            )
        }
    }

    fun flipFlashcard() {
        val state = _flashcardStudyState.value
        _flashcardStudyState.value = state.copy(isFlipped = !state.isFlipped)
    }

    fun rateCard(rating: Int) {
        val state = _flashcardStudyState.value
        if (state.currentIndex >= state.cards.size) return
        val currentCard = state.cards[state.currentIndex]

        viewModelScope.launch {
            repository.reviewFlashcard(currentCard, rating)
        }

        val nextIndex = state.currentIndex + 1
        val newCorrect = if (rating >= 2) state.correctCount + 1 else state.correctCount
        if (nextIndex >= state.cards.size) {
            _flashcardStudyState.value = state.copy(
                currentIndex = nextIndex,
                isFinished = true,
                isFlipped = false,
                correctCount = newCorrect
            )
        } else {
            _flashcardStudyState.value = state.copy(
                currentIndex = nextIndex,
                isFlipped = false,
                correctCount = newCorrect
            )
        }
    }

    fun exitDeckStudy() {
        _flashcardStudyState.value = FlashcardStudyState()
    }

    fun addDeck(title: String, description: String, subjectName: String) {
        viewModelScope.launch {
            repository.insertDeck(
                FlashcardDeck(
                    title = title,
                    description = description,
                    subjectName = subjectName
                )
            )
        }
    }

    fun deleteDeck(deck: FlashcardDeck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }

    fun addFlashcard(deckId: Long, question: String, answer: String, hint: String) {
        viewModelScope.launch {
            repository.insertCard(
                Flashcard(
                    deckId = deckId,
                    question = question,
                    answer = answer,
                    hintOrExplanation = hint
                )
            )
        }
    }

    fun deleteFlashcard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    // --- Task Operations ---

    fun setTaskSubjectFilter(subject: String?) {
        _taskFilterSubject.value = subject
    }

    fun addTask(title: String, subjectName: String, estimatedPomodoros: Int, priority: Priority) {
        viewModelScope.launch {
            repository.insertTask(
                StudyTask(
                    title = title,
                    subjectName = subjectName,
                    estimatedPomodoros = estimatedPomodoros,
                    priority = priority
                )
            )
        }
    }

    fun toggleTaskCompleted(task: StudyTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun incrementTaskPomodoro(task: StudyTask) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    completedPomodoros = task.completedPomodoros + 1,
                    isCompleted = task.completedPomodoros + 1 >= task.estimatedPomodoros
                )
            )
        }
    }

    fun deleteTask(task: StudyTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Subject Operations ---

    fun addSubject(name: String, colorHex: String, targetDailyMinutes: Int) {
        viewModelScope.launch {
            repository.insertSubject(
                Subject(
                    name = name,
                    colorHex = colorHex,
                    dailyGoalMinutes = targetDailyMinutes
                )
            )
        }
    }

    // --- Notes Operations ---

    fun setNotesSearchQuery(query: String) {
        _notesSearchQuery.value = query
    }

    fun setNotesCategoryFilter(category: String?) {
        _notesCategoryFilter.value = category
    }

    fun addNote(title: String, content: String, subjectName: String, category: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.insertNote(
                StudyNote(
                    title = title,
                    content = content,
                    subjectName = subjectName,
                    category = category,
                    isPinned = isPinned
                )
            )
        }
    }

    fun toggleNotePinned(note: StudyNote) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteNote(note: StudyNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ambientManager.stopSound()
    }
}
