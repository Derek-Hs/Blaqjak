package com.example.blaccjacc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blaccjacc.`interface`.BlackjackHandController
import com.example.blaccjacc.`interface`.BlackjackUiState
import com.example.blaccjacc.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlackjackViewModel(numberOfDecks: Int = 1) : ViewModel(), BlackjackHandController {
    private val engine = BlackjackEngine(numberOfDecks)
    private val strategyAnalyzer = StrategyAnalyzer()

    private val _uiState = MutableStateFlow(BlackjackUiState())
    override val uiState: StateFlow<BlackjackUiState> = _uiState.asStateFlow()

    private val attemptedIncorrectActions = mutableSetOf<PlayerAction>()
    private var comboCount = 0
    private var previousGameState = GameState.INITIAL
    private var hadIncorrectActionThisHand = false

    init {
        startNewHand()
    }

    override fun startNewHand() {
        viewModelScope.launch {
            attemptedIncorrectActions.clear()
            hadIncorrectActionThisHand = false
            engine.startNewHand()
            updateUiState()
        }
    }

    private fun tryAction(action: PlayerAction, execute: () -> Boolean) {
        val correctAction = getCurrentCorrectAction()

        if (correctAction != null && action != correctAction) {
            // Incorrect action - mark as attempted but don't execute
            attemptedIncorrectActions.add(action)
            hadIncorrectActionThisHand = true
            comboCount = 0  // Reset combo on incorrect action
            updateUiState(showToast = true, isCorrect = false)
        } else {
            // Correct action or no strategy requirement - execute
            if (execute()) {
                attemptedIncorrectActions.clear()
                updateUiState()
            }
        }
    }

    override fun hit() {
        viewModelScope.launch {
            tryAction(PlayerAction.HIT) { engine.hit() }
        }
    }

    override fun stand() {
        viewModelScope.launch {
            tryAction(PlayerAction.STAND) { engine.stand() }
        }
    }

    override fun double() {
        viewModelScope.launch {
            tryAction(PlayerAction.DOUBLE) { engine.double() }
        }
    }

    override fun split() {
        viewModelScope.launch {
            tryAction(PlayerAction.SPLIT) { engine.split() }
        }
    }

    override fun undoLastAction() {
        viewModelScope.launch {
            if (engine.undoLastAction()) {
                updateUiState()
            }
        }
    }



    override fun getActionHistory(): List<BlackjackEngine.ActionRecord> {
        return engine.getActionHistory()
    }

    private fun getCurrentCorrectAction(): PlayerAction? {
        val gameState = engine.getGameState()
        if (gameState != GameState.PLAYER_TURN) return null

        val dealerUpCard = engine.getDealerUpCard() ?: return null
        val currentHand = engine.getPlayerHand()
        val actionHistory = engine.getActionHistory()
        val currentHandActionCount = actionHistory.count { it.handIndex == engine.getActiveHandIndex() }

        val canDouble = currentHand.canDouble() && currentHandActionCount == 0
        val canSplit = currentHand.isPair() && currentHandActionCount == 0 && engine.getActiveHandIndex() == 0 && !engine.hasSplitHands()

        return BasicStrategy.getCorrectAction(
            playerHand = currentHand,
            dealerUpCard = dealerUpCard,
            canDouble = canDouble,
            canSplit = canSplit
        )
    }

    private fun updateUiState(showToast: Boolean = false, isCorrect: Boolean = false) {
        val dealerHand = engine.getDealerHand()
        val gameState = engine.getGameState()
        val gameOver = gameState == GameState.GAME_OVER

        val showDealerHoleCard = gameOver || gameState == GameState.DEALER_TURN

        // Convert engine hands to UI hands
        val playerHandsUi = engine.getPlayerHands().map { playerHand ->
            com.example.blaccjacc.`interface`.PlayerHandUiState(
                handIndex = playerHand.handIndex,
                cards = playerHand.hand.cards.toList(),
                handValue = playerHand.getValue(),
                isBusted = playerHand.isBusted(),
                hasBlackjack = playerHand.isBlackjack(),
                isSoft = playerHand.hand.isSoft(),
                isActive = playerHand.handIndex == engine.getActiveHandIndex(),
                isCompleted = playerHand.isCompleted,
                isSplitFromAces = playerHand.isSplitFromAces
            )
        }

        // Analyze strategy deviations
        val analysisResult = strategyAnalyzer.analyzeHand(engine)

        // Update combo counter when game ends
        if (gameOver && previousGameState != GameState.GAME_OVER) {
            // Only increment combo if no incorrect actions were attempted during the entire hand
            if (analysisResult.followedBasicStrategy && !hadIncorrectActionThisHand) {
                comboCount++
            } else if (hadIncorrectActionThisHand) {
                // Don't reset to 0 here since we already did it when the wrong action was tapped
                // Just don't increment
            } else {
                comboCount = 0
            }
        }
        previousGameState = gameState

        // Calculate correct action for current state
        val correctAction = getCurrentCorrectAction()

        // Create toast message if needed
        val toastMessage = if (showToast) {
            com.example.blaccjacc.`interface`.ToastMessage(
                message = if (isCorrect) "Correct" else "Try again",
                backgroundColor = if (isCorrect) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336)
            )
        } else {
            null
        }

        _uiState.value = BlackjackUiState(
            playerHands = playerHandsUi,
            activeHandIndex = engine.getActiveHandIndex(),
            hasSplit = engine.hasSplitHands(),
            dealerCards = dealerHand.cards.toList(),
            dealerHandValue = dealerHand.getValue(),
            dealerUpCard = engine.getDealerUpCard(),
            dealerIsBusted = dealerHand.isBusted(),
            gameState = gameState,
            gameResults = if (gameOver) engine.getAllResults() else emptyList(),
            canHit = engine.canHit(),
            canStand = engine.canStand(),
            canDouble = engine.canDouble(),
            canSplit = engine.canSplit(),
            showDealerHoleCard = showDealerHoleCard,
            strategyDeviations = analysisResult.deviations,
            followedBasicStrategy = analysisResult.followedBasicStrategy,
            handResults = if (gameOver) engine.getHandResults() else emptyList(),
            correctAction = correctAction,
            attemptedIncorrectActions = attemptedIncorrectActions.toSet(),
            toastMessage = toastMessage,
            comboCount = comboCount
        )
    }
}
