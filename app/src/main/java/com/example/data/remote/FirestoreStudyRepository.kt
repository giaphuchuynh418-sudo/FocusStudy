package com.example.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class RemoteUserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val dailyGoalMinutes: Int = 60,
    val updatedAt: Timestamp? = null
)

data class RemoteStudySession(
    val sessionId: String = "",
    val userId: String = "",
    val subjectName: String = "",
    val durationMinutes: Int = 25,
    val sessionType: String = "POMODORO",
    val note: String = "",
    val createdAt: Timestamp? = null
)

data class RemoteStudyTask(
    val taskId: String = "",
    val userId: String = "",
    val title: String = "",
    val subjectName: String = "",
    val dueDateEpoch: Long = 0L,
    val isCompleted: Boolean = false,
    val estimatedPomodoros: Int = 2,
    val completedPomodoros: Int = 0,
    val priority: String = "MEDIUM",
    val createdAt: Timestamp? = null
)

class FirestoreStudyRepository(private val db: FirebaseFirestore) {

    suspend fun saveUserProfile(userId: String, name: String, email: String, dailyGoalMinutes: Int) {
        val data = mapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "dailyGoalMinutes" to dailyGoalMinutes,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(userId).set(data, SetOptions.merge()).await()
    }

    suspend fun getUserProfile(userId: String): RemoteUserProfile? {
        val doc = db.collection("users").document(userId).get().await()
        return if (doc.exists()) {
            RemoteUserProfile(
                userId = doc.getString("userId") ?: userId,
                name = doc.getString("name") ?: "Học Viên",
                email = doc.getString("email") ?: "",
                dailyGoalMinutes = doc.getLong("dailyGoalMinutes")?.toInt() ?: 60,
                updatedAt = doc.getTimestamp("updatedAt")
            )
        } else {
            null
        }
    }

    suspend fun logSession(
        userId: String,
        subjectName: String,
        durationMinutes: Int,
        sessionType: String = "POMODORO",
        note: String = ""
    ): String {
        val sessionId = UUID.randomUUID().toString()
        val data = mapOf(
            "sessionId" to sessionId,
            "userId" to userId,
            "subjectName" to subjectName,
            "durationMinutes" to durationMinutes,
            "sessionType" to sessionType,
            "note" to note,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(userId).collection("sessions").document(sessionId).set(data).await()
        return sessionId
    }

    suspend fun getSessions(userId: String): List<RemoteStudySession> {
        val snapshot = db.collection("users").document(userId).collection("sessions").get().await()
        return snapshot.documents.mapNotNull { doc ->
            RemoteStudySession(
                sessionId = doc.getString("sessionId") ?: doc.id,
                userId = doc.getString("userId") ?: userId,
                subjectName = doc.getString("subjectName") ?: "",
                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                sessionType = doc.getString("sessionType") ?: "POMODORO",
                note = doc.getString("note") ?: "",
                createdAt = doc.getTimestamp("createdAt")
            )
        }
    }

    suspend fun saveTask(
        userId: String,
        title: String,
        subjectName: String,
        dueDateEpoch: Long,
        estimatedPomodoros: Int = 2,
        priority: String = "MEDIUM"
    ): String {
        val taskId = UUID.randomUUID().toString()
        val data = mapOf(
            "taskId" to taskId,
            "userId" to userId,
            "title" to title,
            "subjectName" to subjectName,
            "dueDateEpoch" to dueDateEpoch,
            "isCompleted" to false,
            "estimatedPomodoros" to estimatedPomodoros,
            "completedPomodoros" to 0,
            "priority" to priority,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(userId).collection("tasks").document(taskId).set(data).await()
        return taskId
    }

    suspend fun updateTaskStatus(userId: String, taskId: String, isCompleted: Boolean) {
        db.collection("users").document(userId).collection("tasks").document(taskId)
            .update("isCompleted", isCompleted)
            .await()
    }
}
