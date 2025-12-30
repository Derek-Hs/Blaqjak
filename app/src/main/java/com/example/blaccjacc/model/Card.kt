package com.example.blaccjacc.model

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank(val value: Int, val displayName: String) {
    ACE(11, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(10, "J"),
    QUEEN(10, "Q"),
    KING(10, "K");

    fun isAce(): Boolean = this == ACE
}

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString(): String = "${rank.displayName}${suit.name[0]}"
}
