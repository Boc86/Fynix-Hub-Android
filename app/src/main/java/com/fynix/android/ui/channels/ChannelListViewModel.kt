package com.fynix.android.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fynix.android.data.NetworkRepository
import com.fynix.android.network.models.MergedChannel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChannelListViewModel(
    private val networkRepo: NetworkRepository
) : ViewModel() {
    private val _channels = MutableStateFlow<List<MergedChannel>>(emptyList())
    val channels: StateFlow<List<MergedChannel>> = _channels

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(FlowPreview::class)
    fun loadChannels(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val settings = networkRepo.settings.first()
                networkRepo.getChannels(
                    settings.host,
                    settings.port,
                    settings.username,
                    settings.password,
                    limit = 100,
                    search = query
                ).collect { result ->
                    result.onSuccess { channels ->
                        _channels.value = channels
                    }.onFailure { e ->
                        _error.value = e.message
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadChannels(query)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

class ChannelListViewModelFactory(
    private val networkRepo: NetworkRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelListViewModel::class.java)) {
            return ChannelListViewModel(networkRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}