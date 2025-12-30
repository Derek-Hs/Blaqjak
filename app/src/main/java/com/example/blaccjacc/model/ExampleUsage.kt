package com.example.blaccjacc.model

fun main() {
    exampleGameWithStrategyAnalysis()
}

fun exampleGameWithStrategyAnalysis() {
    println("=== Blackjack Engine Example ===\n")

    val engine = BlackjackEngine(numberOfDecks = 6)
    val analyzer = StrategyAnalyzer()

    engine.startNewHand()

    println("Initial Deal:")
    println("Player Hand: ${engine.getPlayerHand()} = ${engine.getPlayerHand().getValue()}")
    println("Dealer Up Card: ${engine.getDealerUpCard()}")
    println()

    if (engine.getPlayerHand().isBlackjack()) {
        println("BLACKJACK!")
    } else {
        playHand(engine)
    }

    println("\n=== Game Result ===")
    println("Player Final Hand: ${engine.getPlayerHand()} = ${engine.getPlayerHand().getValue()}")
    println("Dealer Final Hand: ${engine.getDealerHand()} = ${engine.getDealerHand().getValue()}")
    println("Result: ${engine.getResult()}")

    println("\n=== Strategy Analysis ===")
    val analysis = analyzer.analyzeHand(engine)
    println(analysis.getReport())
}

fun playHand(engine: BlackjackEngine) {
    var actionCount = 0

    while (engine.getGameState() == GameState.PLAYER_TURN) {
        actionCount++
        val playerHand = engine.getPlayerHand()
        val dealerUpCard = engine.getDealerUpCard()!!

        val correctAction = BasicStrategy.getCorrectAction(
            playerHand = playerHand,
            dealerUpCard = dealerUpCard,
            canDouble = engine.canDouble()
        )

        println("Action #$actionCount:")
        println("Current Hand: $playerHand = ${playerHand.getValue()} " +
                "(${if (playerHand.isSoft()) "Soft" else "Hard"})")
        println("Dealer Up Card: $dealerUpCard")
        println("Basic Strategy recommends: $correctAction")

        val actionTaken = when {
            actionCount == 1 && correctAction == PlayerAction.DOUBLE -> {
                println("Taking action: DOUBLE")
                engine.double()
                PlayerAction.DOUBLE
            }
            actionCount == 1 && correctAction == PlayerAction.STAND -> {
                println("Taking action: STAND")
                engine.stand()
                PlayerAction.STAND
            }
            correctAction == PlayerAction.HIT -> {
                println("Taking action: HIT")
                engine.hit()
                PlayerAction.HIT
            }
            else -> {
                println("Taking action: STAND")
                engine.stand()
                PlayerAction.STAND
            }
        }

        if (actionTaken == PlayerAction.HIT) {
            println("New card: ${playerHand.cards.last()}")
            if (playerHand.isBusted()) {
                println("BUSTED!")
            }
        }
        println()
    }
}

fun exampleWithDeviations() {
    println("=== Example with Strategy Deviations ===\n")

    val engine = BlackjackEngine()
    val analyzer = StrategyAnalyzer()

    engine.startNewHand()

    println("Player Hand: ${engine.getPlayerHand()} = ${engine.getPlayerHand().getValue()}")
    println("Dealer Up Card: ${engine.getDealerUpCard()}")
    println()

    println("Intentionally making wrong moves...")
    if (engine.canHit()) {
        engine.hit()
        println("Hit (may or may not be correct)")
    }

    if (engine.canStand()) {
        engine.stand()
        println("Stand")
    }

    val analysis = analyzer.analyzeHand(engine)
    println("\n" + analysis.getReport())
}
