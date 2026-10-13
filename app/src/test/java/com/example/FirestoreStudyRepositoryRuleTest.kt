package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.remote.FirestoreStudyRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test

class FirestoreStudyRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticatedUser_canSaveAndRetrieveProfile() = runBlocking {
        val uid = signInTestUser("student1@example.com")
        val repo = FirestoreStudyRepository(firestore)

        repo.saveUserProfile(
            userId = uid,
            name = "Gia Phúc",
            email = "student1@example.com",
            dailyGoalMinutes = 90
        )

        val profile = repo.getUserProfile(uid)
        assertNotNull(profile)
        assertEquals("Gia Phúc", profile?.name)
        assertEquals(90, profile?.dailyGoalMinutes)
    }

    @Test
    fun authenticatedUser_canLogAndReadSessions() = runBlocking {
        val testEmail = "student_${System.currentTimeMillis()}@example.com"
        val uid = signInTestUser(testEmail)
        val repo = FirestoreStudyRepository(firestore)

        val sessionId = repo.logSession(
            userId = uid,
            subjectName = "Lập Trình Android",
            durationMinutes = 25,
            sessionType = "POMODORO",
            note = "Học Jetpack Compose"
        )
        assertNotNull(sessionId)

        val sessions = repo.getSessions(uid)
        assertNotNull(sessions.find { it.sessionId == sessionId })
        assertEquals("Lập Trình Android", sessions.find { it.sessionId == sessionId }?.subjectName)
    }

    @Test
    fun unauthenticatedUser_cannotAccessSessions() = runBlocking {
        val repo = FirestoreStudyRepository(firestore)
        auth.signOut()

        try {
            repo.getSessions("some_user_id")
            fail("Should fail with PERMISSION_DENIED when unauthenticated")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }
}
