package com.example.blaccjacc.model

data class Hand(val cards: MutableList<Card> = mutableListOf()) {

    fun addCard(card: Card) {
        cards.add(card)
    }

    fun getValue(): Int {
        var total = 0
        var aces = 0

        for (card in cards) {
            total += card.rank.value
            if (card.rank.isAce()) {
                aces++
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10
            aces--
        }

        return total
    }

    fun isBusted(): Boolean = getValue() > 21

    fun isBlackjack(): Boolean = cards.size == 2 && getValue() == 21

    fun isSoft(): Boolean {
        var total = 0
        var hasUsableAce = false

        for (card in cards) {
            total += card.rank.value
            if (card.rank.isAce()) {
                hasUsableAce = true
            }
        }

        if (!hasUsableAce) return false

        var tempTotal = total
        var aces = cards.count { it.rank.isAce() }

        while (tempTotal > 21 && aces > 0) {
            tempTotal -= 10
            aces--
        }

        return aces > 0 && tempTotal <= 21
    }

    fun isPair(): Boolean = cards.size == 2 && cards[0].rank == cards[1].rank

    fun getUpCard(): Card? = cards.firstOrNull()

    fun canDouble(): Boolean = cards.size == 2

    override fun toString(): String = cards.joinToString(", ") { it.toString() }
}
