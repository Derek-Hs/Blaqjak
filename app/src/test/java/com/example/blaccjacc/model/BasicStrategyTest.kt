package com.example.blaccjacc.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BasicStrategyTest {

    // Helper function to create a hand
    private fun createHand(vararg cards: Card): Hand {
        val hand = Hand()
        cards.forEach { hand.addCard(it) }
        return hand
    }

    @Test
    fun `test hard 17 or higher should stand`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SEVEN, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test hard 13-16 vs dealer 2-6 should stand`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FOUR, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test hard 13-16 vs dealer 7+ should hit`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.EIGHT, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.HIT, action)
    }

    @Test
    fun `test hard 12 vs dealer 4-6 should stand`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.TWO, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test hard 12 vs dealer 2-3 or 7+ should hit`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.TWO, Suit.SPADES)
        )
        val dealerCard = Card(Rank.THREE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.HIT, action)
    }

    @Test
    fun `test hard 11 should double when possible`() {
        val hand = createHand(
            Card(Rank.FIVE, Suit.HEARTS),
            Card(Rank.SIX, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test hard 11 should hit when cannot double`() {
        val hand = createHand(
            Card(Rank.FIVE, Suit.HEARTS),
            Card(Rank.SIX, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.HIT, action)
    }

    @Test
    fun `test hard 10 vs dealer 2-9 should double when possible`() {
        val hand = createHand(
            Card(Rank.FIVE, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test hard 9 vs dealer 3-6 should double when possible`() {
        val hand = createHand(
            Card(Rank.FOUR, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test soft 19 should stand`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.EIGHT, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test soft 19 vs dealer 6 should double when possible`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.EIGHT, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SIX, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test soft 18 vs dealer 2-8 should stand when cannot double`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.SEVEN, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test soft 18 vs dealer 9+ should hit`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.SEVEN, Suit.SPADES)
        )
        val dealerCard = Card(Rank.NINE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.HIT, action)
    }

    @Test
    fun `test soft 17 vs dealer 3-6 should double when possible`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.SIX, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test soft 13 vs dealer 5-6 should double when possible`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.TWO, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test pair of aces should split`() {
        val hand = createHand(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.ACE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.SPLIT, action)
    }

    @Test
    fun `test pair of eights should split`() {
        val hand = createHand(
            Card(Rank.EIGHT, Suit.HEARTS),
            Card(Rank.EIGHT, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.SPLIT, action)
    }

    @Test
    fun `test pair of tens should stand`() {
        val hand = createHand(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.TEN, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test pair of nines vs dealer 7 should stand`() {
        val hand = createHand(
            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.NINE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.STAND, action)
    }

    @Test
    fun `test pair of nines vs dealer 5 should split`() {
        val hand = createHand(
            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.NINE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.SPLIT, action)
    }

    @Test
    fun `test pair of sixes vs dealer 2-6 should split`() {
        val hand = createHand(
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.SIX, Suit.SPADES)
        )
        val dealerCard = Card(Rank.FIVE, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.SPLIT, action)
    }

    @Test
    fun `test pair of sixes vs dealer 7+ should hit`() {
        val hand = createHand(
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.SIX, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.HIT, action)
    }

    @Test
    fun `test pair of fives should never split - should double vs dealer 2-9`() {
        val hand = createHand(
            Card(Rank.FIVE, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = true, canSplit = true)
        assertEquals(PlayerAction.DOUBLE, action)
    }

    @Test
    fun `test low hard totals should hit`() {
        val hand = createHand(
            Card(Rank.TWO, Suit.HEARTS),
            Card(Rank.FIVE, Suit.SPADES)
        )
        val dealerCard = Card(Rank.SEVEN, Suit.CLUBS)

        val action = BasicStrategy.getCorrectAction(hand, dealerCard, canDouble = false)
        assertEquals(PlayerAction.HIT, action)
    }
}
