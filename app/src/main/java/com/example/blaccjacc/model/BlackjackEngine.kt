package com.example.blaccjacc.model

enum class GameState {
    INITIAL,
    PLAYER_TURN,
    DEALER_TURN,
    GAME_OVER
}

enum class GameResult {
    PLAYER_WIN,
    DEALER_WIN,
    PUSH,
    PLAYER_BLACKJACK,
    IN_PROGRESS
}

enum class PlayerAction {
    HIT,
    STAND,
    DOUBLE,
    SPLIT
}

class BlackjackEngine(numberOfDecks: Int = 1) {
    private val deck = Deck(numberOfDecks)
    private var playerHands = mutableListOf<PlayerHand>()
    private var activeHandIndex = 0
    private var dealerHand = Hand()
    private var gameState = GameState.INITIAL
    private var currentBet: Double = 10.0
    private var hasSplit = false
    private val actionHistory = mutableListOf<ActionRecord>()
    private var snapshot: GameSnapshot? = null

    private data class GameSnapshot(
        val playerHands: List<PlayerHand>,
        val activeHandIndex: Int,
        val dealerHand: Hand,
        val gameState: GameState,
        val hasSplit: Boolean,
        val actionHistorySize: Int
    )

    data class ActionRecord(
        val action: PlayerAction,
        val playerHandValue: Int,
        val playerCards: List<Card>,
        val dealerUpCard: Card,
        val isSoft: Boolean,
        val isPair: Boolean,
        val handIndex: Int = 0
    )

    fun setBet(amount: Double) {
        require(amount > 0) { "Bet must be positive" }
        currentBet = amount
    }

    fun getCurrentBet(): Double = currentBet

    fun getHandResults(): List<HandResult> {
        if (gameState != GameState.GAME_OVER) return emptyList()

        return playerHands.map { playerHand ->
            val result = calculateHandResult(playerHand)
            val payout = calculatePayout(playerHand, result)
            HandResult(playerHand.handIndex, result, playerHand.bet, payout)
        }
    }

    private fun calculateHandResult(playerHand: PlayerHand): GameResult {
        val playerValue = playerHand.getValue()
        val dealerValue = dealerHand.getValue()

        return when {
            playerHand.isBlackjack() && !dealerHand.isBlackjack() -> GameResult.PLAYER_BLACKJACK
            playerHand.isBusted() -> GameResult.DEALER_WIN
            dealerHand.isBusted() -> GameResult.PLAYER_WIN
            playerValue > dealerValue -> GameResult.PLAYER_WIN
            playerValue < dealerValue -> GameResult.DEALER_WIN
            else -> GameResult.PUSH
        }
    }

    private fun calculatePayout(playerHand: PlayerHand, result: GameResult): Double {
        return when (result) {
            GameResult.PLAYER_BLACKJACK -> playerHand.bet * 2.5
            GameResult.PLAYER_WIN -> playerHand.bet * 2.0
            GameResult.PUSH -> playerHand.bet
            GameResult.DEALER_WIN -> 0.0
            GameResult.IN_PROGRESS -> 0.0
        }
    }

    fun startNewHand() {
        playerHands.clear()
        dealerHand = Hand()
        actionHistory.clear()
        gameState = GameState.INITIAL
        activeHandIndex = 0
        hasSplit = false

        val initialHand = Hand()
        initialHand.addCard(deck.deal())
        dealerHand.addCard(deck.deal())
        initialHand.addCard(deck.deal())
        dealerHand.addCard(deck.deal())

        playerHands.add(PlayerHand(
            hand = initialHand,
            bet = currentBet,
            handIndex = 0,
            isSplitFromAces = false
        ))

        if (initialHand.isBlackjack()) {
            gameState = GameState.DEALER_TURN
            finishDealerHand()
        } else {
            gameState = GameState.PLAYER_TURN
        }
    }

    private fun getCurrentPlayerHand(): PlayerHand {
        return playerHands[activeHandIndex]
    }

    fun getPlayerHand(): Hand = playerHands.getOrNull(activeHandIndex)?.hand ?: Hand()

    fun getDealerHand(): Hand = dealerHand

    fun getDealerUpCard(): Card? = dealerHand.getUpCard()

    fun getGameState(): GameState = gameState

    fun getPlayerHands(): List<PlayerHand> = playerHands.toList()

    fun getActiveHandIndex(): Int = activeHandIndex

    fun hasSplitHands(): Boolean = playerHands.size > 1

    private fun saveSnapshot() {
        snapshot = GameSnapshot(
            playerHands = playerHands.map { it.copy(hand = Hand(it.hand.cards.toMutableList())) },
            activeHandIndex = activeHandIndex,
            dealerHand = Hand(dealerHand.cards.toMutableList()),
            gameState = gameState,
            hasSplit = hasSplit,
            actionHistorySize = actionHistory.size
        )
    }

    fun undoLastAction(): Boolean {
        val savedSnapshot = snapshot ?: return false

        playerHands.clear()
        playerHands.addAll(savedSnapshot.playerHands.map {
            it.copy(hand = Hand(it.hand.cards.toMutableList()))
        })
        activeHandIndex = savedSnapshot.activeHandIndex
        dealerHand = Hand(savedSnapshot.dealerHand.cards.toMutableList())
        gameState = savedSnapshot.gameState
        hasSplit = savedSnapshot.hasSplit

        while (actionHistory.size > savedSnapshot.actionHistorySize) {
            actionHistory.removeAt(actionHistory.size - 1)
        }

        snapshot = null
        return true
    }

    private fun advanceToNextHand() {
        playerHands[activeHandIndex].isCompleted = true

        activeHandIndex++

        if (activeHandIndex >= playerHands.size) {
            gameState = GameState.DEALER_TURN
            finishDealerHand()
        } else if (playerHands[activeHandIndex].isBusted()) {
            advanceToNextHand()
        }
    }

    fun canSplit(): Boolean {
        if (gameState != GameState.PLAYER_TURN) return false
        if (hasSplit) return false
        if (playerHands.isEmpty()) return false
        return playerHands[0].hand.isPair()
    }

    fun split(): Boolean {
        if (!canSplit()) return false

        saveSnapshot()
        val originalHand = playerHands[0]
        val originalCards = originalHand.hand.cards

        recordAction(PlayerAction.SPLIT)

        val isSplittingAces = originalCards[0].rank.isAce()

        val hand1 = Hand(mutableListOf(originalCards[0]))
        val hand2 = Hand(mutableListOf(originalCards[1]))

        hand1.addCard(deck.deal())
        hand2.addCard(deck.deal())

        playerHands.clear()
        playerHands.add(PlayerHand(
            hand = hand1,
            bet = currentBet,
            handIndex = 0,
            isSplitFromAces = isSplittingAces,
            isCompleted = isSplittingAces
        ))
        playerHands.add(PlayerHand(
            hand = hand2,
            bet = currentBet,
            handIndex = 1,
            isSplitFromAces = isSplittingAces,
            isCompleted = isSplittingAces
        ))

        hasSplit = true
        activeHandIndex = 0

        if (isSplittingAces) {
            gameState = GameState.DEALER_TURN
            finishDealerHand()
        } else {
            if (playerHands[0].isBusted()) {
                advanceToNextHand()
            }
        }

        return true
    }

    fun canHit(): Boolean {
        if (gameState != GameState.PLAYER_TURN) return false
        val currentHand = getCurrentPlayerHand()
        return currentHand.canReceiveCard() && !currentHand.isCompleted && currentHand.getValue() < 21
    }

    fun canStand(): Boolean = gameState == GameState.PLAYER_TURN

    fun canDouble(): Boolean {
        if (gameState != GameState.PLAYER_TURN) return false
        val currentHand = getCurrentPlayerHand()
        return currentHand.hand.canDouble() && !currentHand.isCompleted
    }

    fun hit(): Boolean {
        if (!canHit()) return false

        saveSnapshot()
        recordAction(PlayerAction.HIT)
        val currentHand = getCurrentPlayerHand()
        currentHand.hand.addCard(deck.deal())

        if (currentHand.isBusted()) {
            advanceToNextHand()
        }

        return true
    }

    fun stand(): Boolean {
        if (!canStand()) return false

        saveSnapshot()
        recordAction(PlayerAction.STAND)
        advanceToNextHand()

        return true
    }

    fun double(): Boolean {
        if (!canDouble()) return false

        saveSnapshot()
        recordAction(PlayerAction.DOUBLE)
        val currentHand = getCurrentPlayerHand()

        playerHands[activeHandIndex] = currentHand.copy(bet = currentHand.bet * 2)

        currentHand.hand.addCard(deck.deal())

        advanceToNextHand()

        return true
    }

    private fun finishDealerHand() {
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.deal())
        }
        gameState = GameState.GAME_OVER
    }

    fun getResult(): GameResult {
        if (gameState != GameState.GAME_OVER) {
            return GameResult.IN_PROGRESS
        }

        return if (playerHands.isNotEmpty()) {
            calculateHandResult(playerHands[0])
        } else {
            GameResult.IN_PROGRESS
        }
    }

    fun getAllResults(): List<Pair<Int, GameResult>> {
        if (gameState != GameState.GAME_OVER) {
            return emptyList()
        }

        return playerHands.map { playerHand ->
            playerHand.handIndex to calculateHandResult(playerHand)
        }
    }

    private fun recordAction(action: PlayerAction) {
        val dealerUpCard = getDealerUpCard() ?: return
        val currentHand = getCurrentPlayerHand()

        actionHistory.add(
            ActionRecord(
                action = action,
                playerHandValue = currentHand.getValue(),
                playerCards = currentHand.hand.cards.toList(),
                dealerUpCard = dealerUpCard,
                isSoft = currentHand.hand.isSoft(),
                isPair = currentHand.hand.isPair(),
                handIndex = activeHandIndex
            )
        )
    }

    fun getActionHistory(): List<ActionRecord> = actionHistory.toList()
}
