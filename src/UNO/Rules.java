package UNO;

/**
 * Contains methods for validating and interpreting UNO Flip card plays.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 3.0 - Milestone 3
 */
public final class Rules {

    /**
     * Constructs a Rules utility class.
     *
     */
    private Rules() { }

    /**
     * Checks if a played card is legal according to UNO Flip rules.
     *
     * @param player the player attempting to play the card
     * @param played the card being played
     * @param top the top card on the discard pile
     * @param activeColor the current active color to be matched
     * @return true if the play is legal, false otherwise
     */
    public static boolean isLegal(Player player, Card played, Card top, CardColor activeColor) {
        if (played.getType() == CardType.WILD || played.getType() == CardType.WILD_DRAW_TWO) {
            return true;
        }

        CardColor colorToMatch = (activeColor != null) ? activeColor : top.getColor();

        if(played.getType() == CardType.WILD_DRAW_COLOR) {
            for(Card c: player.getHand().getCards()) {
                if(c.getColor() == colorToMatch) {
                    return false;
                }
            }
            return true;

        }

        if (played.getColor() == colorToMatch) {
            return true;
        }

        if (played.getType() == CardType.NUMBER && top.getType() == CardType.NUMBER) {
            return played.getValue() == top.getValue();
        }

        return played.getType() == top.getType();
    }
}
