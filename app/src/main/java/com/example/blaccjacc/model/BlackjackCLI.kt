package com.example.blaccjacc.model

import java.util.Scanner

class BlackjackCLI(private val numberOfDecks: Int = 6) {
    private val engine = BlackjackEngine(numberOfDecks)
    private val analyzer = StrategyAnalyzer()
    private val scanner = Scanner(System.`in`)
    private var showHints = true

    fun start() {
        printWelcome()
        askForHints()

        var playAgain = true
        while (playAgain) {
            playHand()
            playAgain = askPlayAgain()
        }

        println("\nThanks for playing!")
    }

    private fun printWelcome() {
        println("╔═══════════════════════════════════════╗")
        println("║      BLACKJACK - CLI Edition          ║")
        println("║   Powered by Basic Strategy Engine    ║")
        println("╚═══════════════════════════════════════╝")
        println()
    }

    private fun askForHints() {
        println("Would you like basic strategy hints? (y/n): ")
        val input = scanner.nextLine().trim().lowercase()
        showHints = input == "y" || input == "yes"
        println()
    }

    private fun playHand() {
        engine.startNewHand()

        println("\n" + "=".repeat(50))
        println("NEW HAND")
        println("=".repeat(50))

        displayHands(showDealerHoleCard = false)

        if (engine.getPlayerHand().isBlackjack()) {
            println("\n🎉 BLACKJACK! 🎉")
            finishHand()
            return
        }

        playerTurn()

        if (engine.getGameState() == GameState.GAME_OVER) {
            if (engine.getPlayerHand().isBusted()) {
                println("\n💥 BUSTED! 💥")
            }
        }

        finishHand()
    }

    private fun displayHands(showDealerHoleCard: Boolean) {
        println("\n┌─ DEALER ─────────────────────────────┐")
        if (showDealerHoleCard) {
            println("│ Hand: ${engine.getDealerHand()}")
            println("│ Value: ${engine.getDealerHand().getValue()}")
        } else {
            println("│ Up Card: ${engine.getDealerUpCard()}")
            println("│ Hole Card: [Hidden]")
        }
        println("└──────────────────────────────────────┘")

        val playerHands = engine.getPlayerHands()
        val activeIndex = engine.getActiveHandIndex()

        if (playerHands.size > 1) {
            println("\n┌─ YOUR HANDS ─────────────────────────┐")
            playerHands.forEach { hand ->
                val indicator = if (hand.handIndex == activeIndex) "→" else " "
                println("│ $indicator Hand ${hand.handIndex + 1} (Bet: $${hand.bet.toInt()})")
                println("│   Cards: ${hand.hand}")
                println("│   Value: ${hand.getValue()}")
                if (hand.isSplitFromAces) {
                    println("│   (Split Aces)")
                }
                if (hand.isBusted()) {
                    println("│   BUST!")
                }
            }
            println("└──────────────────────────────────────┘")
        } else {
            val playerHand = playerHands.firstOrNull()?.hand ?: Hand()
            println("\n┌─ YOUR HAND ──────────────────────────┐")
            println("│ Cards: $playerHand")
            println("│ Value: ${playerHand.getValue()}")
            if (playerHand.isSoft()) {
                println("│ Type: Soft")
            }
            if (playerHand.isPair()) {
                println("│ Type: Pair")
            }
            println("└──────────────────────────────────────┘")
        }
    }

    private fun playerTurn() {
        while (engine.getGameState() == GameState.PLAYER_TURN) {
            println()

            if (showHints) {
                showStrategyHint()
            }

            val availableActions = getAvailableActions()
            println("\nAvailable actions:")
            availableActions.forEach { (key, action) ->
                println("  [$key] $action")
            }

            print("\nYour choice: ")
            val input = scanner.nextLine().trim().lowercase()

            if (!processAction(input, availableActions)) {
                println("Invalid choice. Please try again.")
                continue
            }

            if (engine.getGameState() == GameState.PLAYER_TURN) {
                displayHands(showDealerHoleCard = false)
            }
        }
    }

    private fun getAvailableActions(): Map<String, String> {
        val actions = mutableMapOf<String, String>()

        if (engine.canHit()) {
            actions["h"] = "Hit"
        }
        if (engine.canStand()) {
            actions["s"] = "Stand"
        }
        if (engine.canDouble()) {
            actions["d"] = "Double"
        }
        if (engine.canSplit()) {
            actions["p"] = "Split"
        }

        return actions
    }

    private fun processAction(input: String, availableActions: Map<String, String>): Boolean {
        return when (input) {
            "h", "hit" -> {
                if (!engine.canHit()) return false
                engine.hit()
                if (engine.getGameState() == GameState.PLAYER_TURN) {
                    println("\n→ Drew: ${engine.getPlayerHand().cards.last()}")
                }
                true
            }
            "s", "stand" -> {
                if (!engine.canStand()) return false
                engine.stand()
                println("\n→ Standing...")
                true
            }
            "d", "double" -> {
                if (!engine.canDouble()) return false
                engine.double()
                println("\n→ Doubling down!")
                println("→ Drew: ${engine.getPlayerHand().cards.last()}")
                true
            }
            "p", "split" -> {
                if (!engine.canSplit()) return false
                engine.split()
                println("\n→ Splitting pair...")
                true
            }
            else -> false
        }
    }

    private fun showStrategyHint() {
        val correctAction = BasicStrategy.getCorrectAction(
            playerHand = engine.getPlayerHand(),
            dealerUpCard = engine.getDealerUpCard()!!,
            canDouble = engine.canDouble(),
            canSplit = engine.canSplit()
        )

        println("💡 Basic strategy recommends: $correctAction")
    }

    private fun finishHand() {
        println("\n" + "=".repeat(50))
        println("FINAL HANDS")
        println("=".repeat(50))

        displayHands(showDealerHoleCard = true)

        println("\n" + "─".repeat(50))
        if (engine.hasSplitHands()) {
            val handResults = engine.getHandResults()
            println("RESULTS:")
            handResults.forEach { handResult ->
                val resultText = getResultMessage(handResult.result)
                println("  Hand ${handResult.handIndex + 1}: $resultText (Bet: $${handResult.bet.toInt()}, Payout: $${handResult.payout.toInt()})")
            }
            val totalPayout = handResults.sumOf { it.payout }
            val totalBet = handResults.sumOf { it.bet }
            val netResult = totalPayout - totalBet
            println("  Net: ${if (netResult >= 0) "+" else ""}$${netResult.toInt()}")
        } else {
            val result = engine.getResult()
            println(getResultMessage(result))
        }
        println("─".repeat(50))

        showStrategyAnalysis()
    }

    private fun getResultMessage(result: GameResult): String {
        return when (result) {
            GameResult.PLAYER_BLACKJACK -> "🎉 BLACKJACK! YOU WIN! 🎉"
            GameResult.PLAYER_WIN -> "✅ YOU WIN!"
            GameResult.DEALER_WIN -> "❌ DEALER WINS"
            GameResult.PUSH -> "🤝 PUSH (TIE)"
            GameResult.IN_PROGRESS -> "Game in progress..."
        }
    }

    private fun showStrategyAnalysis() {
        println("\n" + "=".repeat(50))
        println("STRATEGY ANALYSIS")
        println("=".repeat(50))

        val analysis = analyzer.analyzeHand(engine)

        if (analysis.followedBasicStrategy) {
            println("✅ Perfect! You followed basic strategy correctly!")
        } else {
            println("❌ Strategy deviations detected:\n")
            analysis.deviations.forEach { deviation ->
                println("Action #${deviation.actionNumber}:")
                println("  Hand: ${deviation.getHandDescription()} (${deviation.playerCards.joinToString(", ")})")
                println("  Dealer: ${deviation.dealerUpCard}")
                println("  Correct: ${deviation.correctAction}")
                println("  You chose: ${deviation.actionTaken}")
                println()
            }
            println("Total deviations: ${analysis.deviations.size} out of ${analysis.totalActions} actions")
        }
    }

    private fun askPlayAgain(): Boolean {
        println("\n" + "=".repeat(50))
        print("Play another hand? (y/n): ")
        val input = scanner.nextLine().trim().lowercase()
        return input == "y" || input == "yes"
    }
}

fun main() {
    val cli = BlackjackCLI(numberOfDecks = 6)
    cli.start()
}
