package com.example.blaccjacc.model

object BasicStrategy {

    fun getCorrectAction(
        playerHand: Hand,
        dealerUpCard: Card,
        canDouble: Boolean,
        canSplit: Boolean = false
    ): PlayerAction {
        val dealerValue = dealerUpCard.rank.value

        return when {
            playerHand.isPair() && canSplit -> {
                val splitAction = getPairSplitAction(playerHand.cards[0].rank, dealerValue)
                if (splitAction == PlayerAction.SPLIT) {
                    PlayerAction.SPLIT
                } else {
                    getPairAction(playerHand.cards[0].rank, dealerValue, canDouble)
                }
            }
            playerHand.isPair() -> getPairAction(playerHand.cards[0].rank, dealerValue, canDouble)
            playerHand.isSoft() -> getSoftAction(playerHand.getValue(), dealerValue, canDouble)
            else -> getHardAction(playerHand.getValue(), dealerValue, canDouble)
        }
    }

    private fun getPairSplitAction(rank: Rank, dealerValue: Int): PlayerAction {
        return when (rank) {
            Rank.ACE, Rank.EIGHT -> PlayerAction.SPLIT
            Rank.NINE -> when (dealerValue) {
                7, 10, 11 -> PlayerAction.STAND
                else -> PlayerAction.SPLIT
            }
            Rank.SEVEN -> when {
                dealerValue <= 7 -> PlayerAction.SPLIT
                else -> PlayerAction.HIT
            }
            Rank.SIX -> when {
                dealerValue in 2..6 -> PlayerAction.SPLIT
                else -> PlayerAction.HIT
            }
            Rank.FOUR -> when {
                dealerValue in 5..6 -> PlayerAction.SPLIT
                else -> PlayerAction.HIT
            }
            Rank.THREE, Rank.TWO -> when {
                dealerValue in 2..7 -> PlayerAction.SPLIT
                else -> PlayerAction.HIT
            }
            Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> PlayerAction.STAND
            Rank.FIVE -> PlayerAction.DOUBLE
        }
    }

    private fun getPairAction(rank: Rank, dealerValue: Int, canDouble: Boolean): PlayerAction {
        return when (rank) {
            Rank.ACE, Rank.EIGHT -> PlayerAction.HIT
            Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> PlayerAction.STAND
            Rank.NINE -> when (dealerValue) {
                7, 10, 11 -> PlayerAction.STAND
                else -> PlayerAction.HIT
            }
            Rank.SEVEN -> when {
                dealerValue <= 7 -> PlayerAction.HIT
                else -> PlayerAction.HIT
            }
            Rank.SIX -> when {
                dealerValue in 2..6 -> PlayerAction.HIT
                else -> PlayerAction.HIT
            }
            Rank.FIVE -> when {
                dealerValue in 2..9 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            Rank.FOUR -> when {
                dealerValue in 5..6 -> PlayerAction.HIT
                else -> PlayerAction.HIT
            }
            Rank.THREE, Rank.TWO -> when {
                dealerValue in 2..7 -> PlayerAction.HIT
                else -> PlayerAction.HIT
            }
        }
    }

    private fun getSoftAction(playerValue: Int, dealerValue: Int, canDouble: Boolean): PlayerAction {
        return when (playerValue) {
            21, 20 -> PlayerAction.STAND
            19 -> when {
                dealerValue == 6 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.STAND
            }
            18 -> when {
                dealerValue in 2..6 && canDouble -> PlayerAction.DOUBLE
                dealerValue in 2..8 -> PlayerAction.STAND
                else -> PlayerAction.HIT
            }
            17 -> when {
                dealerValue in 3..6 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            16, 15 -> when {
                dealerValue in 4..6 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            14, 13 -> when {
                dealerValue in 5..6 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            else -> PlayerAction.HIT
        }
    }

    private fun getHardAction(playerValue: Int, dealerValue: Int, canDouble: Boolean): PlayerAction {
        return when {
            playerValue >= 17 -> PlayerAction.STAND
            playerValue in 13..16 -> when {
                dealerValue in 2..6 -> PlayerAction.STAND
                else -> PlayerAction.HIT
            }
            playerValue == 12 -> when {
                dealerValue in 4..6 -> PlayerAction.STAND
                else -> PlayerAction.HIT
            }
            playerValue == 11 -> when {
                canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            playerValue == 10 -> when {
                dealerValue in 2..9 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            playerValue == 9 -> when {
                dealerValue in 3..6 && canDouble -> PlayerAction.DOUBLE
                else -> PlayerAction.HIT
            }
            else -> PlayerAction.HIT
        }
    }
}
