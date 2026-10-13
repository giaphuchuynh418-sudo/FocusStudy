package com.example.data.repository

import com.example.data.local.StudyDao
import com.example.data.model.Flashcard
import com.example.data.model.FlashcardDeck
import com.example.data.model.Priority
import com.example.data.model.SessionType
import com.example.data.model.StudyNote
import com.example.data.model.StudySession
import com.example.data.model.StudyTask
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.math.max
import kotlin.math.roundToInt

class StudyRepository(private val dao: StudyDao) {

    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects()
    val allDecks: Flow<List<FlashcardDeck>> = dao.getAllDecks()
    val allSessions: Flow<List<StudySession>> = dao.getAllSessions()
    val allTasks: Flow<List<StudyTask>> = dao.getAllTasks()
    val allNotes: Flow<List<StudyNote>> = dao.getAllNotes()

    fun getCardsForDeck(deckId: Long): Flow<List<Flashcard>> = dao.getCardsByDeck(deckId)
    suspend fun getCardsForDeckSync(deckId: Long): List<Flashcard> = dao.getCardsByDeckSync(deckId)

    suspend fun insertSubject(subject: Subject): Long = dao.insertSubject(subject)
    suspend fun deleteSubject(subject: Subject) = dao.deleteSubject(subject)

    suspend fun insertDeck(deck: FlashcardDeck): Long = dao.insertDeck(deck)
    suspend fun deleteDeck(deck: FlashcardDeck) = dao.deleteDeck(deck)

    suspend fun insertCard(card: Flashcard): Long = dao.insertCard(card)
    suspend fun updateCard(card: Flashcard) = dao.updateCard(card)
    suspend fun deleteCard(card: Flashcard) = dao.deleteCard(card)

    suspend fun logSession(
        subjectName: String,
        durationMinutes: Int,
        type: SessionType = SessionType.POMODORO,
        note: String = ""
    ) {
        dao.insertSession(
            StudySession(
                subjectName = subjectName,
                durationMinutes = durationMinutes,
                sessionType = type,
                timestamp = System.currentTimeMillis(),
                note = note
            )
        )
    }

    suspend fun insertTask(task: StudyTask): Long = dao.insertTask(task)
    suspend fun updateTask(task: StudyTask) = dao.updateTask(task)
    suspend fun deleteTask(task: StudyTask) = dao.deleteTask(task)

    suspend fun insertNote(note: StudyNote): Long = dao.insertNote(note)
    suspend fun updateNote(note: StudyNote) = dao.updateNote(note)
    suspend fun deleteNote(note: StudyNote) = dao.deleteNote(note)

    /**
     * Process Spaced Repetition (SuperMemo SM-2 inspired)
     * rating: 0 = Again, 1 = Hard, 2 = Good, 3 = Easy
     */
    suspend fun reviewFlashcard(card: Flashcard, rating: Int) {
        val now = System.currentTimeMillis()
        var rep = card.repetitionCount
        var interval = card.intervalDays
        var ease = card.easeFactor
        var mastery = card.masteryLevel

        when (rating) {
            0 -> { // Again
                rep = 0
                interval = 1
                ease = max(1.3f, ease - 0.2f)
                mastery = max(0, mastery - 1)
            }
            1 -> { // Hard
                rep += 1
                interval = max(1, (interval * 1.2f).roundToInt())
                ease = max(1.3f, ease - 0.15f)
                mastery = minOf(5, mastery + 1)
            }
            2 -> { // Good
                rep += 1
                interval = if (rep == 1) 1 else if (rep == 2) 3 else (interval * ease).roundToInt()
                mastery = minOf(5, mastery + 1)
            }
            3 -> { // Easy
                rep += 1
                interval = if (rep == 1) 2 else if (rep == 2) 5 else (interval * ease * 1.3f).roundToInt()
                ease += 0.15f
                mastery = minOf(5, mastery + 2)
            }
        }

        val nextReview = now + (interval.toLong() * 24L * 60L * 60L * 1000L)
        val updated = card.copy(
            repetitionCount = rep,
            intervalDays = interval,
            easeFactor = ease,
            lastReviewedTime = now,
            nextReviewTime = nextReview,
            masteryLevel = mastery
        )
        dao.updateCard(updated)
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingSubjects = dao.getAllSubjects().first()
        if (existingSubjects.isNotEmpty()) return

        // 1. Seed Subjects
        val sCompSci = Subject(name = "Khoa Học Máy Tính", colorHex = "#3B82F6", iconName = "terminal", dailyGoalMinutes = 90)
        val sNeuro = Subject(name = "Khoa Học Não Bộ", colorHex = "#8B5CF6", iconName = "psychology", dailyGoalMinutes = 60)
        val sHistory = Subject(name = "Lịch Sử Thế Giới", colorHex = "#D97706", iconName = "history_edu", dailyGoalMinutes = 45)
        val sMath = Subject(name = "Toán Học Cao Cấp", colorHex = "#10B981", iconName = "calculate", dailyGoalMinutes = 60)
        val sLanguage = Subject(name = "Ngoại Ngữ", colorHex = "#F43F5E", iconName = "translate", dailyGoalMinutes = 30)

        dao.insertSubject(sCompSci)
        dao.insertSubject(sNeuro)
        dao.insertSubject(sHistory)
        dao.insertSubject(sMath)
        dao.insertSubject(sLanguage)

        // 2. Seed Flashcard Decks & Cards
        val deck1Id = dao.insertDeck(
            FlashcardDeck(
                title = "Giải Thuật & Cấu Trúc Dữ Liệu",
                description = "Độ phức tạp tính toán, đồ thị và các dạng quy hoạch động cốt lõi",
                subjectName = "Khoa Học Máy Tính"
            )
        )
        dao.insertCards(
            listOf(
                Flashcard(
                    deckId = deck1Id,
                    question = "Độ phức tạp thời gian trường hợp xấu nhất của QuickSort là gì?",
                    answer = "O(n²)",
                    hintOrExplanation = "Xảy ra khi phần tử chốt (pivot) liên tục là phần tử nhỏ nhất hoặc lớn nhất (ví dụ mảng đã sắp xếp với cách chọn pivot ngây thơ)."
                ),
                Flashcard(
                    deckId = deck1Id,
                    question = "Thuật toán Dijkstra sử dụng cấu trúc dữ liệu nào để lấy đỉnh khoảng cách ngắn nhất?",
                    answer = "Hàng đợi ưu tiên Min (Min-Priority Queue / Binary Heap)",
                    hintOrExplanation = "Đạt độ phức tạp O((V + E) log V) khi dùng binary min-heap."
                ),
                Flashcard(
                    deckId = deck1Id,
                    question = "Điểm khác biệt căn bản giữa duyệt BFS và DFS là gì?",
                    answer = "BFS duyệt theo tầng (Queue - Hàng đợi), trong khi DFS đi sâu nhất có thể (Stack - Ngăn xếp / Đệ quy).",
                    hintOrExplanation = "BFS đảm bảo tìm đường đi ngắn nhất trên đồ thị không trọng số."
                ),
                Flashcard(
                    deckId = deck1Id,
                    question = "Hai điều kiện then chốt để áp dụng Quy hoạch động là gì?",
                    answer = "Cấu trúc con tối ưu (Optimal Substructure) và Bài toán con gối nhau (Overlapping Subproblems)",
                    hintOrExplanation = "Lưu kết quả bài toán con vào bảng nhớ để tránh tính toán lặp lại."
                )
            )
        )

        val deck2Id = dao.insertDeck(
            FlashcardDeck(
                title = "Cấu Trúc Não Bộ & Trí Nhớ",
                description = "Tính mềm dẻo của synap, các thùy não và chất dẫn truyền thần kinh",
                subjectName = "Khoa Học Não Bộ"
            )
        )
        dao.insertCards(
            listOf(
                Flashcard(
                    deckId = deck2Id,
                    question = "Vùng não nào đóng vai trò chính trong củng cố trí nhớ ngắn hạn thành dài hạn?",
                    answer = "Hồi hải mã (Hippocampus)",
                    hintOrExplanation = "Nằm ở thùy thái dương trong, trung tâm của trí nhớ khai báo (declarative memory)."
                ),
                Flashcard(
                    deckId = deck2Id,
                    question = "Hiện tượng Tăng cường Dài hạn (LTP) trong synap là gì?",
                    answer = "Sự củng cố liên kết synap kéo dài nhờ các chuỗi kích thích lặp lại thường xuyên.",
                    hintOrExplanation = "Nguyên lý Hebb: 'Các tế bào thần kinh cùng phát xung sẽ cùng kết nối' - cơ sở sinh học của việc học."
                ),
                Flashcard(
                    deckId = deck2Id,
                    question = "Chất dẫn truyền thần kinh nào gắn liền nhất với động lực, phần thưởng và sự tập trung?",
                    answer = "Dopamine",
                    hintOrExplanation = "Được tổng hợp tại liềm đen (substantia nigra) và vùng trần bụng (VTA)."
                )
            )
        )

        val deck3Id = dao.insertDeck(
            FlashcardDeck(
                title = "Giải Tích & Đại Số Tuyến Tính",
                description = "Định lý cơ bản, đạo hàm, tích phân và vector riêng",
                subjectName = "Toán Học Cao Cấp"
            )
        )
        dao.insertCards(
            listOf(
                Flashcard(
                    deckId = deck3Id,
                    question = "Định lý Cơ bản của Giải tích (Phần 1) phát biểu gì?",
                    answer = "Nếu f liên tục trên [a, b], thì F(x) = ∫_a^x f(t) dt có đạo hàm F'(x) = f(x).",
                    hintOrExplanation = "Chứng minh phép lấy vi phân và tích phân là hai phép toán ngược nhau."
                ),
                Flashcard(
                    deckId = deck3Id,
                    question = "Vector riêng (Eigenvector) của ma trận vuông A là gì?",
                    answer = "Vector khác không v thỏa mãn A·v = λ·v, với λ là giá trị riêng (scalar).",
                    hintOrExplanation = "Phương của vector không bị thay đổi khi chịu tác động của phép biến đổi tuyến tính A."
                )
            )
        )

        // 3. Seed Starter Planner Tasks
        dao.insertTask(
            StudyTask(
                title = "Luyện tập dạng bài duyệt đồ thị BFS/DFS",
                subjectName = "Khoa Học Máy Tính",
                dueDateEpoch = System.currentTimeMillis() + 86400000L,
                estimatedPomodoros = 3,
                completedPomodoros = 1,
                priority = Priority.HIGH
            )
        )
        dao.insertTask(
            StudyTask(
                title = "Đọc chương 4: Tính mềm dẻo của synap và LTP",
                subjectName = "Khoa Học Não Bộ",
                dueDateEpoch = System.currentTimeMillis() + 172800000L,
                estimatedPomodoros = 2,
                completedPomodoros = 0,
                priority = Priority.MEDIUM
            )
        )
        dao.insertTask(
            StudyTask(
                title = "Giải bài tập ma trận và tìm vector riêng bài 5",
                subjectName = "Toán Học Cao Cấp",
                dueDateEpoch = System.currentTimeMillis() + 259200000L,
                estimatedPomodoros = 4,
                completedPomodoros = 0,
                priority = Priority.MEDIUM
            )
        )

        // 4. Seed Cheat Sheet Notes
        dao.insertNote(
            StudyNote(
                title = "Định Lý Tổng Quát Cho Thuật Toán Chia Để Trị (Master Theorem)",
                content = "T(n) = aT(n/b) + f(n)\n• Trường hợp 1: Nếu f(n) = O(n^(log_b(a) - ε)), thì T(n) = Θ(n^(log_b(a)))\n• Trường hợp 2: Nếu f(n) = Θ(n^(log_b(a))), thì T(n) = Θ(n^(log_b(a)) * log n)\n• Trường hợp 3: Nếu f(n) = Ω(n^(log_b(a) + ε)) và thỏa điều kiện đều, thì T(n) = Θ(f(n))",
                subjectName = "Khoa Học Máy Tính",
                category = "Công thức",
                isPinned = true
            )
        )
        dao.insertNote(
            StudyNote(
                title = "Kỹ Thuật Feynman Để Học Sâu Mọi Kiến Thức",
                content = "1. Chọn một khái niệm cần nắm vững.\n2. Giảng giải lại cho một đứa trẻ 10 tuổi bằng ngôn từ đơn giản nhất.\n3. Nhận diện các 'lỗ hổng' khi gặp khó khăn trong việc diễn đạt trực quan.\n4. Đọc lại tài liệu gốc để tinh chỉnh phép ẩn dụ và giản lược hóa.",
                subjectName = "Khoa Học Não Bộ",
                category = "Tóm tắt",
                isPinned = true
            )
        )
        dao.insertNote(
            StudyNote(
                title = "Khai Triển Chuỗi Taylor & Maclaurin",
                content = "f(x) = ∑ [f^(n)(a) / n!] * (x - a)^n\nChuỗi Maclaurin khi a = 0:\ne^x = 1 + x + x²/2! + x³/3! + ...\nsin(x) = x - x³/3! + x⁵/5! - ...\ncos(x) = 1 - x²/2! + x⁴/4! - ...",
                subjectName = "Toán Học Cao Cấp",
                category = "Công thức",
                isPinned = false
            )
        )

        // 5. Seed initial study sessions
        val now = System.currentTimeMillis()
        val day = 86400000L
        dao.insertSession(StudySession(subjectName = "Khoa Học Máy Tính", durationMinutes = 50, sessionType = SessionType.POMODORO, timestamp = now - (day * 3), note = "Giải thuật đồ thị"))
        dao.insertSession(StudySession(subjectName = "Khoa Học Não Bộ", durationMinutes = 25, sessionType = SessionType.POMODORO, timestamp = now - (day * 2), note = "Hồi hải mã và LTP"))
        dao.insertSession(StudySession(subjectName = "Toán Học Cao Cấp", durationMinutes = 45, sessionType = SessionType.POMODORO, timestamp = now - (day * 1), note = "Đại số tuyến tính"))
        dao.insertSession(StudySession(subjectName = "Khoa Học Máy Tính", durationMinutes = 25, sessionType = SessionType.POMODORO, timestamp = now - 3600000L, note = "Khởi động thẻ flashcard"))
    }
}
