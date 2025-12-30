package com.example.blaccjacc.model

data class StrategyDeviation(
    val actionTaken: PlayerAction,
    val correctAction: PlayerAction,
    val playerHandValue: Int,
    val playerCards: List<Card>,
    val dealerUpCard: Card,
    val isSoft: Boolean,
    val isPair: Boolean,
    val actionNumber: Int
) {
    fun getHandDescription(): String {
        return when {
            isPair -> "Pair of ${playerCards[0].rank.displayName}s"
            isSoft -> "Soft $playerHandValue"
            else -> "Hard $playerHandValue"
        }
    }

    override fun toString(): String {
        return """
            Action #$actionNumber - Strategy Deviation Detected:
            Hand: ${getHandDescription()} (${playerCards.joinToString(", ")})
            Dealer Up Card: $dealerUpCard
            Correct Action: $correctAction
            Action Taken: $actionTaken
        """.trimIndent()
    }
}

class StrategyAnalyzer {

    fun analyzeHand(engine: BlackjackEngine): StrategyAnalysisResult {
        val actionHistory = engine.getActionHistory()
        val deviations = mutableListOf<StrategyDeviation>()

        val actionsByHand = actionHistory.groupBy { it.handIndex }

        actionsByHand.forEach { (handIndex, actions) ->
            actions.forEachIndexed { actionIndexInHand, actionRecord ->
                val hand = Hand(actionRecord.playerCards.toMutableList())

                val canDouble = hand.canDouble() && actionIndexInHand == 0
                val canSplit = hand.isPair() && actionIndexInHand == 0 && handIndex == 0

                val correctAction = BasicStrategy.getCorrectAction(
                    playerHand = hand,
                    dealerUpCard = actionRecord.dealerUpCard,
                    canDouble = canDouble,
                    canSplit = canSplit
                )

                if (actionRecord.action != correctAction) {
                    deviations.add(
                        StrategyDeviation(
                            actionTaken = actionRecord.action,
                            correctAction = correctAction,
                            playerHandValue = actionRecord.playerHandValue,
                            playerCards = actionRecord.playerCards,
                            dealerUpCard = actionRecord.dealerUpCard,
                            isSoft = actionRecord.isSoft,
                            isPair = actionRecord.isPair,
                            actionNumber = actionHistory.indexOf(actionRecord) + 1
                        )
                    )
                }
            }
        }

        return StrategyAnalysisResult(
            followedBasicStrategy = deviations.isEmpty(),
            deviations = deviations,
            totalActions = actionHistory.size
        )
    }
}

data class StrategyAnalysisResult(
    val followedBasicStrategy: Boolean,
    val deviations: List<StrategyDeviation>,
    val totalActions: Int
) {
    fun getReport(): String {
        return if (followedBasicStrategy) {
            "Perfect! You followed basic strategy correctly for all $totalActions action(s)."
        } else {
            buildString {
                appendLine("Basic strategy violations detected: ${deviations.size} out of $totalActions action(s)")
                appendLine()
                deviations.forEach { deviation ->
                    appendLine(deviation.toString())
                    appendLine()
                }
            }
        }
    }
}
