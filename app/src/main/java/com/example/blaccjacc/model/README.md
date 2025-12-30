# Blackjack Engine

A headless blackjack engine with basic strategy verification.

## Features

- Complete blackjack game logic (hit, stand, double)
- Multi-deck support
- Basic strategy implementation
- Strategy deviation detection and reporting
- Clean, type-safe Kotlin API

## Quick Start

```kotlin
val engine = BlackjackEngine(numberOfDecks = 6)
val analyzer = StrategyAnalyzer()

// Start a new hand
engine.startNewHand()

// Check the deal
println("Player: ${engine.getPlayerHand()} = ${engine.getPlayerHand().getValue()}")
println("Dealer: ${engine.getDealerUpCard()}")

// Make decisions
if (engine.canDouble()) {
    engine.double()
} else if (engine.canHit()) {
    engine.hit()
} else {
    engine.stand()
}

// Analyze strategy compliance
val analysis = analyzer.analyzeHand(engine)
println(analysis.getReport())
```

## API Reference

### BlackjackEngine

Main game engine class.

**Constructor:**
```kotlin
BlackjackEngine(numberOfDecks: Int = 1)
```

**Core Methods:**

- `startNewHand()` - Deals a new hand
- `hit(): Boolean` - Player takes another card
- `stand(): Boolean` - Player stands with current hand
- `double(): Boolean` - Player doubles down (one card + double bet)

**Query Methods:**

- `getPlayerHand(): Hand` - Returns player's current hand
- `getDealerHand(): Hand` - Returns dealer's hand
- `getDealerUpCard(): Card?` - Returns dealer's visible card
- `getGameState(): GameState` - Returns current game state
- `getResult(): GameResult` - Returns final result (when game is over)

**Validation Methods:**

- `canHit(): Boolean` - Check if player can hit
- `canStand(): Boolean` - Check if player can stand
- `canDouble(): Boolean` - Check if player can double

**Strategy Analysis:**

- `getActionHistory(): List<ActionRecord>` - Returns all actions taken

### Hand

Represents a blackjack hand.

**Methods:**

- `getValue(): Int` - Returns hand value (accounts for aces)
- `isBusted(): Boolean` - Returns true if over 21
- `isBlackjack(): Boolean` - Returns true if natural blackjack
- `isSoft(): Boolean` - Returns true if hand contains usable ace
- `isPair(): Boolean` - Returns true if initial hand is a pair
- `canDouble(): Boolean` - Returns true if hand has exactly 2 cards

### BasicStrategy

Determines correct basic strategy action.

**Method:**
```kotlin
BasicStrategy.getCorrectAction(
    playerHand: Hand,
    dealerUpCard: Card,
    canDouble: Boolean
): PlayerAction
```

Returns the correct action according to basic strategy:
- `PlayerAction.HIT`
- `PlayerAction.STAND`
- `PlayerAction.DOUBLE`

### StrategyAnalyzer

Analyzes player actions for strategy compliance.

**Method:**
```kotlin
fun analyzeHand(engine: BlackjackEngine): StrategyAnalysisResult
```

**StrategyAnalysisResult:**

- `followedBasicStrategy: Boolean` - True if all actions were correct
- `deviations: List<StrategyDeviation>` - List of incorrect actions
- `totalActions: Int` - Total number of actions taken
- `getReport(): String` - Human-readable analysis report

**StrategyDeviation:**

Contains details about a strategy violation:
- `actionTaken: PlayerAction` - What the player did
- `correctAction: PlayerAction` - What basic strategy recommends
- `playerHandValue: Int` - Hand value at time of action
- `playerCards: List<Card>` - Cards in hand
- `dealerUpCard: Card` - Dealer's up card
- `isSoft: Boolean` - Whether hand was soft
- `isPair: Boolean` - Whether hand was a pair
- `actionNumber: Int` - Sequence number of action

## Game States

- `INITIAL` - Game just created
- `PLAYER_TURN` - Player is making decisions
- `DEALER_TURN` - Dealer is playing
- `GAME_OVER` - Hand is complete

## Game Results

- `PLAYER_WIN` - Player wins
- `DEALER_WIN` - Dealer wins
- `PUSH` - Tie
- `PLAYER_BLACKJACK` - Player has blackjack
- `IN_PROGRESS` - Game still in progress

## Example: Complete Game Flow

```kotlin
fun playCompleteGame() {
    val engine = BlackjackEngine(numberOfDecks = 6)
    val analyzer = StrategyAnalyzer()

    engine.startNewHand()

    // Play according to basic strategy
    while (engine.getGameState() == GameState.PLAYER_TURN) {
        val correctAction = BasicStrategy.getCorrectAction(
            playerHand = engine.getPlayerHand(),
            dealerUpCard = engine.getDealerUpCard()!!,
            canDouble = engine.canDouble()
        )

        when (correctAction) {
            PlayerAction.HIT -> engine.hit()
            PlayerAction.STAND -> engine.stand()
            PlayerAction.DOUBLE -> {
                if (engine.canDouble()) {
                    engine.double()
                } else {
                    engine.hit()
                }
            }
        }
    }

    // Check result
    val result = engine.getResult()
    println("Result: $result")

    // Verify we followed basic strategy
    val analysis = analyzer.analyzeHand(engine)
    println(analysis.getReport())
}
```

## Basic Strategy Rules

The engine implements standard basic strategy for:
- Hard hands (no ace or ace counted as 1)
- Soft hands (ace counted as 11)
- Pairs

Strategy assumes:
- Dealer stands on soft 17
- Double allowed on any two cards
- No surrender option
- No resplitting

## Implementation Notes

- Aces are automatically adjusted to prevent busting
- Deck automatically reshuffles when depleted
- All actions are recorded for strategy analysis
- Thread-safe for single game instance
