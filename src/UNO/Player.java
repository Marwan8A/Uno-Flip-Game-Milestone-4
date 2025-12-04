package UNO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the UNO Flip game.
 * Stores the player's name and hand of cards.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class Player implements Serializable {

    private final String name;
    private Hand hand;
    private int score;
    private boolean isHuman;

    /**
     * Constructs a player with the given name and hand.
     *
     * @param name    the player's name
     * @param hand    the player's initial hand
     * @param isHuman true if this player is human, false if AI-controlled
     */
    public Player(String name, Hand hand, boolean isHuman) {
        this.name = name;
        this.hand = hand;
        this.score = 0;
        this.isHuman = isHuman;
    }

    /**
     * Gets the player's name.
     *
     * @return String the player's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Adds the new score to the player's score.
     *
     * @param  score the player's score
     */
    public void setScore(int score) {

        this.score += score;
    }

    /**
     * Gets the player's score.
     *
     * @return the player's score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Gets the player's hand.
     *
     * @return the player's hand
     */
    public Hand getHand() {
        return this.hand;
    }

    /**
     * Draws the specified number of cards from the deck
     * and adds them to the player's hand.
     *
     * @param deck the deck to draw cards from
     * @param count the number of cards to draw
     */
    public void drawFrom(Deck deck, int count) {
        for (int i = 0; i < count; i++) {
            Card c = deck.drawCard();
            if (c != null) {
                hand.addCard(c);
            }
        }
    }

    /**
     * Flips all cards in this player's hand to their opposite side.
     */
    public void flipPlayerHand() {
        hand.flipHand();
    }

    /**
     * Checks if this player is human.
     *
     * @return true if the player is human, false if AI-controlled
     */
    public boolean isHuman() {return isHuman;}

    /**
     * Converts this player, including name, score, type, and hand, to XML.
     *
     * @param indent the indentation to apply to each XML line
     * @return a string containing this player encoded as XML
     */
    public String toXML(String indent) {

        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("<player>\n");
        sb.append(indent).append("  <name>").append(name).append("</name>\n");
        sb.append(indent).append("  <score>").append(score).append("</score>\n");
        sb.append(indent).append("  <isHuman>").append(isHuman).append("</isHuman>\n");
        sb.append(hand.toXML(indent + "  "));
        sb.append(indent).append("</player>\n");

        return sb.toString();
    }
}
