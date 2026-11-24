package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.data.lessons.AcademyTrack
import com.pipbeacon.app.data.lessons.LessonItem
import com.pipbeacon.app.data.lessons.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AcademyUiState(
    val activeTrack: AcademyTrack = AcademyTrack.FOREX,
    val selectedLevel: String = "ALL",
    val forexLessons: List<LessonItem> = emptyList(),
    val cryptoLessons: List<LessonItem> = emptyList(),
    val forexCompletedCount: Int = 0,
    val forexTotalCount: Int = 0,
    val cryptoCompletedCount: Int = 0,
    val cryptoTotalCount: Int = 0,
    val totalCompletedCount: Int = 0,
    val totalLessonsCount: Int = 0
)

class AcademyViewModel(
    private val lessonRepository: LessonRepository = LessonRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademyUiState())
    val uiState: StateFlow<AcademyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            lessonRepository.lessonsFlow.collect {
                refreshState()
            }
        }
        refreshState()
    }

    fun selectTrack(track: AcademyTrack) {
        _uiState.update { it.copy(activeTrack = track) }
    }

    fun setLevelFilter(level: String) {
        _uiState.update { it.copy(selectedLevel = level) }
        refreshState()
    }

    fun toggleLessonCompletion(lessonId: String) {
        lessonRepository.toggleLessonCompletion(lessonId)
        refreshState()
    }

    private fun refreshState() {
        val currentLevel = _uiState.value.selectedLevel
        val forexLessons = lessonRepository.getLessons(currentLevel, AcademyTrack.FOREX)
        val cryptoLessons = lessonRepository.getLessons(currentLevel, AcademyTrack.CRYPTO)
        val forexProgress = lessonRepository.getProgress(AcademyTrack.FOREX)
        val cryptoProgress = lessonRepository.getProgress(AcademyTrack.CRYPTO)
        val overallProgress = lessonRepository.getProgress(null)

        _uiState.update {
            it.copy(
                forexLessons = forexLessons,
                cryptoLessons = cryptoLessons,
                forexCompletedCount = forexProgress.first,
                forexTotalCount = forexProgress.second,
                cryptoCompletedCount = cryptoProgress.first,
                cryptoTotalCount = cryptoProgress.second,
                totalCompletedCount = overallProgress.first,
                totalLessonsCount = overallProgress.second
            )
        }
    }
}
