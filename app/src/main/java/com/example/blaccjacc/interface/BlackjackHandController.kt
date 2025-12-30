package com.example.blaccjacc.`interface`

import com.example.blaccjacc.model.BlackjackEngine
import kotlinx.coroutines.flow.StateFlow

interface BlackjackHandController {
    val uiState: StateFlow<BlackjackUiState>

    fun startNewHand()
    fun hit()
    fun stand()
    fun double()
    fun split()
    fun setBet(amount: Double)
    fun undoLastAction()
    fun getActionHistory(): List<BlackjackEngine.ActionRecord>
}
