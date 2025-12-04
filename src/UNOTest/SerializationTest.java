package UNOTest;

import UNO.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

/**
 * JUnit tests for XML serialization and deserialization of the UNO model.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class SerializationTest {

    /**
     * Ensures that converting a game state to XML and back again
     * preserves core game data such as players, hands, top card
     * and active colour.
     */
    @Test
    public void serializeAndDeserializeGameState() throws IOException {
        UnoModel original = new UnoModel();
        original.startGame(2, 1);

        original.drawAndPass();
        original.nextPlayer();
        original.drawAndPass();

        String xml = original.toXML();
        assertNotNull(xml);
        assertTrue(xml.contains("<unoGame>"));

        UnoModel loaded = new UnoModel();
        loaded.parseXML(xml);

        List<Player> originalPlayers = original.getPlayers();
        List<Player> loadedPlayers = loaded.getPlayers();

        assertEquals(originalPlayers.size(), loadedPlayers.size());

        for (int i = 0; i < originalPlayers.size(); i++) {
            Player p1 = originalPlayers.get(i);
            Player p2 = loadedPlayers.get(i);

            assertEquals(p1.getName(), p2.getName());
            assertEquals(p1.getScore(), p2.getScore());
            assertEquals(p1.isHuman(), p2.isHuman());

            int handSize1 = p1.getHand().getNumberOfCards();
            int handSize2 = p2.getHand().getNumberOfCards();
            assertEquals(handSize1, handSize2);

            for (int j = 0; j < handSize1; j++) {
                Card c1 = p1.getHand().getCards().get(j);
                Card c2 = p2.getHand().getCards().get(j);

                assertEquals(c1.getColor(), c2.getColor());
                assertEquals(c1.getType(), c2.getType());
                assertEquals(c1.getValue(), c2.getValue());
                assertEquals(c1.getSide(), c2.getSide());
            }
        }

        assertEquals(original.getCurrentPlayer().getName(), loaded.getCurrentPlayer().getName());

        assertEquals(original.getActiveColor(), loaded.getActiveColor());

        Card originalTop = original.getTopCard();
        Card loadedTop = loaded.getTopCard();

        assertEquals(originalTop.getColor(), loadedTop.getColor());
        assertEquals(originalTop.getType(), loadedTop.getType());
        assertEquals(originalTop.getValue(), loadedTop.getValue());
        assertEquals(originalTop.getSide(), loadedTop.getSide());
    }

    /**
     * Ensures that attempting to parse an empty XML string
     * results in an IOException.
     */
    @Test(expected = IOException.class)
    public void parseEmptyXMLThrowsIOException() throws IOException {
        UnoModel model = new UnoModel();
        model.parseXML("");
    }

    /**
     * Ensures that invalid or corrupted XML produces an IOException.
     */
    @Test(expected = IOException.class)
    public void parseInvalidXMLThrowsIOException() throws IOException {
        String badXML = "<?xml version=\"1.0\"?><unoGame>" +
                "<currentPlayer>NOT_A_NUMBER</currentPlayer>" + "</unoGame>";

        UnoModel model = new UnoModel();
        model.parseXML(badXML);
    }

    /**
     * Ensures that the side (LIGHT or DARK) of cards in players' hands
     * is preserved when saving to XML and loading back.
     */
    @Test
    public void cardSidePreservedAcrossSerialization() throws IOException {
        UnoModel original = new UnoModel();
        original.startGame(1, 0);

        Player player = original.getCurrentPlayer();
        List<Card> cards = player.getHand().getCards();
        assertFalse("Player should have at least one card in hand", cards.isEmpty());

        Card firstCard = cards.getFirst();
        CardSide originalSide = firstCard.getSide();

        firstCard.flipCard();
        CardSide flippedSide = firstCard.getSide();
        assertNotEquals("Card side should change after flip", originalSide, flippedSide);

        String xml = original.toXML();
        assertNotNull(xml);
        assertTrue(xml.contains("<unoGame>"));

        UnoModel loaded = new UnoModel();
        loaded.parseXML(xml);

        Player loadedPlayer = loaded.getCurrentPlayer();
        List<Card> loadedCards = loadedPlayer.getHand().getCards();
        assertFalse("Loaded player should have at least one card in hand", loadedCards.isEmpty());

        Card loadedFirst = loadedCards.getFirst();

        assertEquals("Card side should be preserved after serialization/deserialization",
                flippedSide, loadedFirst.getSide());
    }
}
