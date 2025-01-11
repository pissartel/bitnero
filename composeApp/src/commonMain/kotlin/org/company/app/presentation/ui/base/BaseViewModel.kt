package org.company.app.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * inspired by : https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 */

interface UiState

interface UiEffect


abstract class BaseViewModel<State : UiState, Effect : UiEffect> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    /**
     * Get current State
     */
    val currentState: State
        get() = state.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state = _uiState.asStateFlow()

    private val _effects: Channel<Effect> = Channel()
    val effects = _effects

    /**
     * Set new Ui State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }

    /**
     * Send new Effect
     */
    protected fun sendEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effects.send(effectValue) }
    }
}
