package UNO;

import java.io.Serializable;
import java.util.*;

/**
 * UNO deck with all 112 cards.
 * Creates, shuffles, and draws cards.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class Deck implements Serializable {

    private final List<Card> drawPile;

    /** Makes a full UNO deck and shuffles it. */
    public Deck() {
        drawPile = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Creates all UNO Flip cards and populates the draw pile.
     * Builds light-side and dark-side pairs for each card, including
     * number cards, action cards, and wild cards, based on official
     * UNO Flip color mappings.
     */
    private void initializeDeck() {
        HashMap<CardColor, CardColor> colourMap =  new HashMap<>(4);
        colourMap.put(CardColor.BLUE, CardColor.PINK);
        colourMap.put(CardColor.YELLOW, CardColor.PURPLE);
        colourMap.put(CardColor.GREEN, CardColor.TEAL);
        colourMap.put(CardColor.RED, CardColor.ORANGE);

        CardColor[] lightColors = {
                CardColor.RED, CardColor.YELLOW, CardColor.GREEN, CardColor.BLUE
        };

        for (CardColor light : lightColors) {
            CardColor dark = colourMap.get(light);

            for (int i = 1; i <= 9; i++) {
                drawPile.add(new Card(light, CardType.NUMBER, dark, CardType.NUMBER, i, i));
                drawPile.add(new Card(light, CardType.NUMBER, dark, CardType.NUMBER, i, i));
            }
        }

        for (CardColor light : lightColors) {
            CardColor dark = colourMap.get(light);

            for (int i = 0; i < 2; i++) {
                drawPile.add(new Card(light, CardType.SKIP, dark, CardType.SKIP_EVERYONE, -1, -1));
                drawPile.add(new Card(light, CardType.REVERSE, dark, CardType.REVERSE, -1, -1));
                drawPile.add(new Card(light, CardType.DRAW_ONE, dark, CardType.DRAW_FIVE, -1, -1));
                drawPile.add(new Card(light, CardType.FLIP, dark, CardType.FLIP, -1, -1));
            }

        }

        for (int i = 0; i < 4; i++) {
            drawPile.add(new Card(CardColor.WILD, CardType.WILD,
                    CardColor.WILD, CardType.WILD, -1, -1));

            drawPile.add(new Card(CardColor.WILD, CardType.WILD_DRAW_TWO,
                    CardColor.WILD, CardType.WILD_DRAW_COLOR, -1, -1));
        }
    }

    /** Shuffles the deck. */
    public void shuffle() {
        Collections.shuffle(drawPile);
    }

    /**
     * Draws one card.
     *
     * @return the drawn card, or null if deck is empty
     */
    public Card drawCard() {
        if (drawPile.isEmpty()) return null;
        return drawPile.remove(drawPile.size() - 1);
    }
    
    /**
     * Reshuffles cards from the discard pile back into the draw pile.
     * Keeps the top card on the discard pile.
     *
     * @param discardPile the discard pile to take cards from
     */
    public void reshuffleFromDiscard(Stack<Card> discardPile) {
        if (discardPile == null || discardPile.size() <= 1) {
            return; 
        }
        
        Card topCard = discardPile.pop();
        
        while (!discardPile.isEmpty()) {
            drawPile.add(discardPile.pop());
        }
        
        shuffle();

        discardPile.push(topCard);
    }

    /**
     * Returns the draw pile.
     *
     * @return the list of cards in the draw pile
     */
    public List<Card> getDrawPile() {

        return drawPile;
    }

    /**
     * Returns number of cards left.
     *
     * @return the current deck size
     */
    public int size() {

        return drawPile.size();
    }

    /**
     * Flips all cards in the draw pile to their opposite side.
     */
    public void flipDeck() {
        for(Card card : drawPile) {
            card.flipCard();
        }
    }

    /**
     * Converts the current draw pile to an XML representation.
     *
     * @param indent the indentation to apply to each XML line
     * @return a string containing the draw pile encoded as XML
     */
    public String toXML(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("<drawPile>\n");

        for (Card c : drawPile) {
            sb.append(c.toXML(indent + "  "));
        }

        sb.append(indent).append("</drawPile>\n");

        return sb.toString();
    }
}
