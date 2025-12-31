package com.example.blaccjacc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blaccjacc.`interface`.BlackjackHandController
import com.example.blaccjacc.`interface`.BlackjackUiState
import com.example.blaccjacc.model.Card
import com.example.blaccjacc.model.GameResult
import com.example.blaccjacc.model.GameState
import com.example.blaccjacc.model.PlayerAction
import com.example.blaccjacc.model.Suit
import com.example.blaccjacc.model.StrategyDeviation

@Composable
fun BlackjackGameView(
    controller: BlackjackHandController,
    modifier: Modifier = Modifier
) {
    val uiState by controller.uiState.collectAsState()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val verticalSpacing = if (isLandscape) 8.dp else 16.dp
    val padding = if (isLandscape) 8.dp else 16.dp

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0D5A2C)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dealer Section
            DealerSection(uiState)

            Spacer(modifier = Modifier.height(verticalSpacing))

            // Game Result
            if (uiState.gameState == GameState.GAME_OVER) {
                if (uiState.hasSplit) {
                    MultiHandResultDisplay(uiState.handResults)
                } else {
                    val firstResult = uiState.gameResults.firstOrNull()?.second ?: GameResult.IN_PROGRESS
                    GameResultDisplay(firstResult)
                }
            }

            // Flexible spacer to anchor player section at consistent position
            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 32.dp))

            // Player Section - anchored position
            PlayerSection(uiState)

            Spacer(modifier = Modifier.height(verticalSpacing))

            // Action Buttons
            ActionButtons(
                uiState = uiState,
                onHit = { controller.hit() },
                onStand = { controller.stand() },
                onDouble = { controller.double() },
                onSplit = { controller.split() },
                onNewHand = { controller.startNewHand() }
            )

            Spacer(modifier = Modifier.height(verticalSpacing))
        }
    }
}

@Composable
fun DealerSection(uiState: BlackjackUiState) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val titleSize = if (isLandscape) 18.sp else 24.sp
    val valueSize = if (isLandscape) 14.sp else 18.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dealer",
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))

        if (uiState.showDealerHoleCard) {
            Text(
                text = "Value: ${uiState.dealerHandValue}",
                fontSize = valueSize,
                color = Color.White
            )
        } else {
            Text(
                text = "Value: ?",
                fontSize = valueSize,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CardRow(
            cards = if (uiState.showDealerHoleCard) {
                uiState.dealerCards
            } else {
                listOfNotNull(uiState.dealerUpCard)
            },
            showHoleCard = uiState.showDealerHoleCard,
            totalCards = uiState.dealerCards.size
        )

        if (uiState.dealerIsBusted) {
            Text(
                text = "BUST!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }
    }
}

@Composable
fun PlayerSection(uiState: BlackjackUiState) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val titleSize = if (isLandscape) 18.sp else 24.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Player",
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))

        if (uiState.hasSplit) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
            ) {
                uiState.playerHands.forEach { hand ->
                    PlayerHandDisplay(
                        hand = hand,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            val hand = uiState.playerHands.firstOrNull()
            if (hand != null) {
                PlayerHandDisplay(
                    hand = hand,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PlayerHandDisplay(
    hand: com.example.blaccjacc.`interface`.PlayerHandUiState,
    modifier: Modifier = Modifier
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val handLabelSize = if (isLandscape) 12.sp else 14.sp
    val valueSize = if (isLandscape) 13.sp else 16.sp
    val statusSize = if (isLandscape) 13.sp else 16.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(4.dp)
            .background(
                if (hand.isActive) Color(0x44FFFFFF) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(if (isLandscape) 4.dp else 8.dp)
    ) {
        if (hand.handIndex > 0 || hand.isSplitFromAces) {
            Text(
                text = "Hand ${hand.handIndex + 1}",
                fontSize = handLabelSize,
                color = if (hand.isActive) Color.Yellow else Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
            text = "Value: ${hand.handValue}${if (hand.isSoft) " (Soft)" else ""}",
            fontSize = valueSize,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        CardRow(
            cards = hand.cards,
            showHoleCard = true,
            totalCards = hand.cards.size
        )

        when {
            hand.isBusted -> Text(
                text = "BUST!",
                fontSize = statusSize,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            hand.hasBlackjack -> Text(
                text = "BJ!",
                fontSize = statusSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            hand.isSplitFromAces -> Text(
                text = "(Aces)",
                fontSize = if (isLandscape) 10.sp else 12.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun CardRow(
    cards: List<Card>,
    showHoleCard: Boolean,
    totalCards: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        cards.forEach { card ->
            CardDisplay(card)
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (!showHoleCard && totalCards > cards.size) {
            HoleCardDisplay()
        }
    }
}

@Composable
fun CardDisplay(card: Card) {
    val suitEmoji = when (card.suit) {
        Suit.HEARTS -> "♥️"
        Suit.DIAMONDS -> "♦️"
        Suit.CLUBS -> "♣️"
        Suit.SPADES -> "♠️"
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val cardWidth = if (isLandscape) 40.dp else 60.dp
    val cardHeight = if (isLandscape) 60.dp else 90.dp
    val fontSize = if (isLandscape) 14.sp else 20.sp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${card.rank.displayName}$suitEmoji",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = if (card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS) {
                    Color.Red
                } else {
                    Color.Black
                }
            )
        }
    }
}

@Composable
fun HoleCardDisplay() {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val cardWidth = if (isLandscape) 40.dp else 60.dp
    val cardHeight = if (isLandscape) 60.dp else 90.dp
    val fontSize = if (isLandscape) 16.sp else 24.sp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun GameResultDisplay(result: GameResult) {
    val (text, color) = when (result) {
        GameResult.PLAYER_WIN -> "You Win!" to Color(0xFF4CAF50)
        GameResult.DEALER_WIN -> "Dealer Wins" to Color.Red
        GameResult.PUSH -> "Push" to Color.Yellow
        GameResult.PLAYER_BLACKJACK -> "Blackjack!" to Color(0xFFFFD700)
        GameResult.IN_PROGRESS -> "" to Color.Transparent
    }

    if (text.isNotEmpty()) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Text(
                text = text,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MultiHandResultDisplay(handResults: List<com.example.blaccjacc.model.HandResult>) {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Results",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            handResults.forEach { handResult ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val (resultText, color) = when (handResult.result) {
                        GameResult.PLAYER_WIN -> "Win" to Color(0xFF4CAF50)
                        GameResult.DEALER_WIN -> "Loss" to Color.Red
                        GameResult.PUSH -> "Push" to Color.Yellow
                        GameResult.PLAYER_BLACKJACK -> "Blackjack!" to Color(0xFFFFD700)
                        GameResult.IN_PROGRESS -> "?" to Color.Gray
                    }

                    Text(
                        text = "Hand ${handResult.handIndex + 1}:",
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Text(
                        text = resultText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ActionButtons(
    uiState: BlackjackUiState,
    onHit: () -> Unit,
    onStand: () -> Unit,
    onDouble: () -> Unit,
    onSplit: () -> Unit,
    onNewHand: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val buttonHeight = if (isLandscape) 44.dp else 56.dp
    val actionFontSize = if (isLandscape) 13.sp else 13.sp
    val newHandFontSize = if (isLandscape) 14.sp else 18.sp

    // Fixed height container to prevent repositioning
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.gameState == GameState.GAME_OVER) {
            Button(
                onClick = onNewHand,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(
                    text = "New Hand",
                    fontSize = newHandFontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val correctAction = uiState.correctAction
            val attemptedIncorrect = uiState.attemptedIncorrectActions

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hit Button - always visible
                Button(
                    onClick = onHit,
                    enabled = uiState.canHit && !attemptedIncorrect.contains(PlayerAction.HIT),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "Hit",
                        fontSize = actionFontSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (attemptedIncorrect.contains(PlayerAction.HIT))
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        else null,
                        color = if (attemptedIncorrect.contains(PlayerAction.HIT))
                            Color.Red
                        else Color.White
                    )
                }

                // Stand Button - always visible
                Button(
                    onClick = onStand,
                    enabled = uiState.canStand && !attemptedIncorrect.contains(PlayerAction.STAND),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text(
                        text = "Stand",
                        fontSize = actionFontSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (attemptedIncorrect.contains(PlayerAction.STAND))
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        else null,
                        color = if (attemptedIncorrect.contains(PlayerAction.STAND))
                            Color.Red
                        else Color.White
                    )
                }

                // Double Button - always visible
                Button(
                    onClick = onDouble,
                    enabled = uiState.canDouble && !attemptedIncorrect.contains(PlayerAction.DOUBLE),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text(
                        text = "Double",
                        fontSize = actionFontSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (attemptedIncorrect.contains(PlayerAction.DOUBLE))
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        else null,
                        color = if (attemptedIncorrect.contains(PlayerAction.DOUBLE))
                            Color.Red
                        else Color.White
                    )
                }

                // Split Button - always visible
                Button(
                    onClick = onSplit,
                    enabled = uiState.canSplit && !attemptedIncorrect.contains(PlayerAction.SPLIT),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text(
                        text = "Split",
                        fontSize = actionFontSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (attemptedIncorrect.contains(PlayerAction.SPLIT))
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        else null,
                        color = if (attemptedIncorrect.contains(PlayerAction.SPLIT))
                            Color.Red
                        else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StrategyAnalysisDisplay(uiState: BlackjackUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.followedBasicStrategy) {
                Color(0xFF4CAF50).copy(alpha = 0.9f)
            } else {
                Color(0xFFFFA726).copy(alpha = 0.9f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = if (uiState.followedBasicStrategy) {
                    "✓ Perfect Basic Strategy!"
                } else {
                    "⚠ Strategy Deviation"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (uiState.strategyDeviations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                // Only show the first deviation
                StrategyDeviationItem(uiState.strategyDeviations.first())
            }
        }
    }
}

@Composable
fun StrategyDeviationItem(deviation: StrategyDeviation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deviation.getHandDescription(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "vs ${deviation.dealerUpCard}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "You: ${getActionEmoji(deviation.actionTaken)} ${deviation.actionTaken}",
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Correct: ${getActionEmoji(deviation.correctAction)} ${deviation.correctAction}",
                        fontSize = 12.sp,
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun getActionEmoji(action: PlayerAction): String {
    return when (action) {
        PlayerAction.HIT -> "👆"
        PlayerAction.STAND -> "✋"
        PlayerAction.DOUBLE -> "2️⃣"
        PlayerAction.SPLIT -> "✂️"
    }
}

@Composable
fun InlineStrategyDeviation(
    deviation: StrategyDeviation,
    onUndo: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFA726).copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "⚠ Strategy Deviation",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deviation.getHandDescription(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "vs ${deviation.dealerUpCard}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "You: ${getActionEmoji(deviation.actionTaken)} ${deviation.actionTaken}",
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Correct: ${getActionEmoji(deviation.correctAction)} ${deviation.correctAction}",
                        fontSize = 12.sp,
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUndo,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(
                    text = "↶ Undo & Retry",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
