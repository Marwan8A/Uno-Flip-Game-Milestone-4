package UNO;

/**
 * Enum representing the various types of UNO cards.
 * Each type has an associated base score used for scoring.
 * NUMBER cards use their face value as the score (1–9).
 * Other types have fixed point values.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 3.0 - Milestone 3
 */
public enum CardType {

    NUMBER(0),        // Score handled by the card’s value field
    SKIP(20),         // Skip next player's turn
    REVERSE(20),      // Reverse play direction
    DRAW_ONE(10),     // Next player draws one card
    WILD(40),         // Choose the next color
    WILD_DRAW_TWO(50), // Next player draws two cards

    SKIP_EVERYONE(30), // All players skipped, goes back to current player
    DRAW_FIVE(20), // Next player draws five cards.
    FLIP(20), // All cards flip from light side to dark side, or vice versa.
    WILD_DRAW_COLOR(60); // Next player must draw until they get a color of your choosing

    // Base score value for this card type.
    private final int score;

    /**
     * Creates a card type with the given score.
     * @param score the point value for this type
     */
    CardType(int score) {
        this.score = score;
    }

    /**
     * Gets the base score for this card type.
     * @return the score value
     */
    public int getScore() {
        return score;
    }
}