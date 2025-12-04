package UNO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a player's hand in UNO.
 * Keeps track of the cards and the total hand score.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class Hand implements Serializable {

    private ArrayList<Card> hand;
    private int handScore;

    /**
     * Creates an empty hand.
     */
    public Hand() {
        this.hand = new ArrayList<Card>();
        this.handScore = 0;
    }

    /**
     * Adds a card to the hand and updates the score.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        hand.add(card);
        updateHandScore();
    }

    /**
     * Removes a card from the hand and updates the score.
     *
     * @param card the card to remove
     */
    public void removeCard(Card card) {
        hand.remove(card);
        updateHandScore();
    }

    /**
     * Recalculates the total hand score based on all cards.
     */
    private void updateHandScore() {
        handScore = 0;
        for (Card card : hand) {
            handScore += card.getScore();
        }
    }

    /**
     * Gets the total score of the hand.
     *
     * @return the hand's total score
     */
    public int getHandScore() {
        return handScore;
    }

    /**
     * Gets the number of cards in the hand.
     *
     * @return the number of cards
     */
    public int getNumberOfCards() {
        return hand.size();
    }

    /**
     * Returns a numbered string representation of all cards in the hand.
     *
     * @return a formatted string listing each card in the hand
     */
    @Override
    public String toString(){

        int i = 1;
        String handString = "";
        for (Card card : hand){
            handString += i + ". " + card.toString() + "\n";
            i += 1;
        }
        return handString;
    }

    /**
     * Returns the list of cards in the hand.
     *
     * @return the player's current hand of cards
     */
    public ArrayList<Card> getCards() {
        return hand;
    }

    /**
     * Clears all cards from the player's hand.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Flips all cards in the hand to their opposite side.
     */
    public void flipHand() {
        for (Card card : hand) {
            card.flipCard();
        }

        updateHandScore();
    }

    /**
     * Converts this hand and all of its cards to an XML representation.
     *
     * @param indent the indentation to apply to each XML line
     * @return a string containing this hand encoded as XML
     */
    public String toXML(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("<hand>\n");

        for (Card c : hand) {
            sb.append(c.toXML(indent + "  "));
        }

        sb.append(indent).append("</hand>\n");

        return sb.toString();
    }
}