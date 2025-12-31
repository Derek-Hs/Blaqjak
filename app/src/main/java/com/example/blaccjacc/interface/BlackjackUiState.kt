package com.example.blaccjacc.`interface`

import com.example.blaccjacc.model.Card
import com.example.blaccjacc.model.GameResult
import com.example.blaccjacc.model.GameState
import com.example.blaccjacc.model.HandResult
import com.example.blaccjacc.model.StrategyDeviation

data class PlayerHandUiState(
    val handIndex: Int,
    val cards: List<Card>,
    val handValue: Int,
    val isBusted: Boolean,
    val hasBlackjack: Boolean,
    val isSoft: Boolean,
    val isActive: Boolean,
    val isCompleted: Boolean,
    val isSplitFromAces: Boolean
)

data class BlackjackUiState(
    val playerHands: List<PlayerHandUiState> = emptyList(),
    val activeHandIndex: Int = 0,
    val hasSplit: Boolean = false,
    val dealerCards: List<Card> = emptyList(),
    val dealerHandValue: Int = 0,
    val dealerUpCard: Card? = null,
    val dealerIsBusted: Boolean = false,
    val gameState: GameState = GameState.INITIAL,
    val gameResults: List<Pair<Int, GameResult>> = emptyList(),
    val canHit: Boolean = false,
    val canStand: Boolean = false,
    val canDouble: Boolean = false,
    val canSplit: Boolean = false,
    val showDealerHoleCard: Boolean = false,
    val strategyDeviations: List<StrategyDeviation> = emptyList(),
    val followedBasicStrategy: Boolean = true,
    val handResults: List<HandResult> = emptyList(),
    val correctAction: com.example.blaccjacc.model.PlayerAction? = null,
    val attemptedIncorrectActions: Set<com.example.blaccjacc.model.PlayerAction> = emptySet()
)
