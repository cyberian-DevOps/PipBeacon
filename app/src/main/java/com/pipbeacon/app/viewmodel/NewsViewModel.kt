package com.pipbeacon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipbeacon.app.data.news.NewsCategory
import com.pipbeacon.app.data.news.NewsImpact
import com.pipbeacon.app.data.news.NewsItem
import com.pipbeacon.app.data.news.NewsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NewsUiState(
    val searchQuery: String = "",
    val selectedCategory: NewsCategory = NewsCategory.ALL,
    val selectedImpact: NewsImpact? = null,
    val selectedArticle: NewsItem? = null,
    val isRefreshing: Boolean = false
)

class NewsViewModel(
    private val newsRepository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    val newsList: StateFlow<List<NewsItem>> = combine(
        newsRepository.newsFlow,
        _uiState
    ) { allNews, state ->
        allNews.filter { item ->
            val matchesCategory = state.selectedCategory == NewsCategory.ALL || item.category == state.selectedCategory
            val matchesImpact = state.selectedImpact == null || item.impact == state.selectedImpact
            val matchesSearch = state.searchQuery.isBlank() ||
                    item.title.contains(state.searchQuery, ignoreCase = true) ||
                    item.summary.contains(state.searchQuery, ignoreCase = true) ||
                    item.affectedSymbols.any { it.contains(state.searchQuery, ignoreCase = true) }
            matchesCategory && matchesImpact && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onCategorySelected(category: NewsCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onImpactFilterToggled(impact: NewsImpact?) {
        val next = if (_uiState.value.selectedImpact == impact) null else impact
        _uiState.value = _uiState.value.copy(selectedImpact = next)
    }

    fun selectArticle(article: NewsItem?) {
        _uiState.value = _uiState.value.copy(selectedArticle = article)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            delay(600) // Simulated network reload animation
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }
}
