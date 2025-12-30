package com.example.blaccjacc.model

data class PlayerHand(
    val hand: Hand,
    val bet: Double,
    val handIndex: Int,
    val isSplitFromAces: Boolean = false,
    var isCompleted: Boolean = false
) {
    fun getValue() = hand.getValue()
    fun isBusted() = hand.isBusted()
    fun isBlackjack() = hand.isBlackjack() && !isSplitFromAces
    fun canReceiveCard() = !isCompleted && !isBusted() && (!isSplitFromAces || hand.cards.size < 2)
}

data class HandResult(
    val handIndex: Int,
    val result: GameResult,
    val bet: Double,
    val payout: Double
)
