package ru.alimov.repeatenglish.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Model view of main page.
 */
class MainActivityViewModel : ViewModel() {
    val uiState: MutableLiveData<MainActivityUiState> =
        MutableLiveData<MainActivityUiState>(MainActivityUiState(null, null))
}