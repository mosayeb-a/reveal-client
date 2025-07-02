package app.ma.reveal.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ma.reveal.common.formatDate
import app.ma.reveal.domain.Presentation
import app.ma.reveal.domain.usecase.GetPresentationById
import app.ma.reveal.domain.usecase.GetPresentations
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PresentationListState(
    val isLoading: Boolean = true,
    val slides: List<Presentation> = emptyList()
)

class PresentationListViewModel(
    private val getPresentations: GetPresentations,
    private val getPresentationById: GetPresentationById,
    presentationId: String?,
    private val fromAssets: Boolean,
    private val fromFiles: Boolean
) : ViewModel() {
    private val _state = MutableStateFlow(PresentationListState())
    val state = _state.asStateFlow()

    init {
        loadPresentations()
        if (presentationId != null) {
            viewModelScope.launch {
                loadPresentationById(presentationId)
            }
        }
    }

    fun loadPresentations() {
        viewModelScope.launch {
            getPresentations(files = fromFiles, asset = fromAssets).fold(
                onSuccess = { newPresentations ->
                    _state.update { currentState ->
                        val currentPresentations = currentState.slides.associateBy { it.id }
                        val newPresentationsMap = newPresentations.associateBy { it.id }
                        val mergedPresentations = (currentPresentations + newPresentationsMap)
                            .values
                            .toList()
                            .sortedByDescending { it.addedDate }
                        currentState.copy(
                            isLoading = false,
                            slides = mergedPresentations
                        )
                    }
                },
                onFailure = { e ->
                    Napier.e("failed to load presentations: ${e.message}", e)
                    _state.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    private suspend fun loadPresentationById(id: String) {
        _state.update { it.copy(isLoading = true) }
        getPresentationById(id, fromAssets, fromFiles).fold(
            onSuccess = { presentation ->
                _state.update { currentState ->
                    val currentPresentations = currentState.slides.associateBy { it.id }
                    val updatedPresentations =
                        currentPresentations + (presentation.id to presentation)
                    currentState.copy(
                        isLoading = false,
                        slides = updatedPresentations.values.toList()
                            .sortedByDescending { it.addedDate }
                    )
                }
            },
            onFailure = { e ->
                Napier.e("failed to load presentation with id $id: ${e.message}", e)
                _state.update { it.copy(isLoading = false) }
            }
        )
    }
}