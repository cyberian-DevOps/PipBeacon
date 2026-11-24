package com.pipbeacon.app

import com.pipbeacon.app.data.lessons.AcademyTrack
import com.pipbeacon.app.data.lessons.LessonRepository
import com.pipbeacon.app.viewmodel.AcademyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AcademyAndMilestoneTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun lessonRepositoryContainsDistinctOfflineForexAndCryptoTracks() {
        val repository = LessonRepository()

        val forexLessons = repository.getLessons("ALL", AcademyTrack.FOREX)
        val cryptoLessons = repository.getLessons("ALL", AcademyTrack.CRYPTO)

        assertTrue("Forex Academy should have at least 6 offline modules", forexLessons.size >= 6)
        assertTrue("Crypto Academy should have at least 6 offline modules", cryptoLessons.size >= 6)
        assertTrue("All Forex Academy modules must belong to FOREX track", forexLessons.all { it.track == AcademyTrack.FOREX })
        assertTrue("All Crypto Academy modules must belong to CRYPTO track", cryptoLessons.all { it.track == AcademyTrack.CRYPTO })
        assertTrue("Every lesson must contain non-empty educational content", (forexLessons + cryptoLessons).all { it.fullContent.isNotBlank() })
    }

    @Test
    fun academyViewModelTracksCompletionAndFilteringReactively() {
        val repository = LessonRepository()
        val viewModel = AcademyViewModel(repository)

        val initialState = viewModel.uiState.value
        assertEquals(0, initialState.totalCompletedCount)
        assertEquals(12, initialState.totalLessonsCount)

        val firstForexId = initialState.forexLessons.first().id
        val firstCryptoId = initialState.cryptoLessons.first().id

        viewModel.toggleLessonCompletion(firstForexId)
        viewModel.toggleLessonCompletion(firstCryptoId)

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.forexCompletedCount)
        assertEquals(1, updatedState.cryptoCompletedCount)
        assertEquals(2, updatedState.totalCompletedCount)
        assertTrue(updatedState.forexLessons.first { it.id == firstForexId }.isCompleted)
        assertTrue(updatedState.cryptoLessons.first { it.id == firstCryptoId }.isCompleted)

        viewModel.setLevelFilter("BEGINNER")
        val beginnerState = viewModel.uiState.value
        assertTrue(beginnerState.forexLessons.isNotEmpty())
        assertTrue(beginnerState.forexLessons.all { it.level.equals("Beginner", ignoreCase = true) })
        assertTrue(beginnerState.cryptoLessons.all { it.level.equals("Beginner", ignoreCase = true) })

        // Unmark one lesson and verify state updates
        viewModel.toggleLessonCompletion(firstForexId)
        val finalState = viewModel.uiState.value
        assertEquals(0, finalState.forexCompletedCount)
        assertFalse(finalState.forexLessons.first { it.id == firstForexId }.isCompleted)
    }
}
