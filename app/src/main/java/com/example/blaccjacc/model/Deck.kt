package com.example.blaccjacc.model

class Deck(private val numberOfDecks: Int = 1) {
    private val cards = mutableListOf<Card>()

    init {
        shuffle()
    }

    fun shuffle() {
        cards.clear()
        repeat(numberOfDecks) {
            for (suit in Suit.values()) {
                for (rank in Rank.values()) {
                    cards.add(Card(rank, suit))
                }
            }
        }
        cards.shuffle()
    }

    fun deal(): Card {
        if (cards.isEmpty()) {
            shuffle()
        }
        return cards.removeAt(0)
    }

    fun remainingCards(): Int = cards.size
}
