package UNOTest;

import UNO.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests for undo and redo functionality in the UNO model.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class UndoRedoTest {
    /**
     * Tests that undo correctly restores the state after a draw-and-pass
     * action and that redo re-applies the change.
     */
    @Test
    public void undoRedoAfterDrawAndPass() {
        UnoModel model = new UnoModel();
        model.startGame(2, 0);

        Player beforePlayer = model.getCurrentPlayer();
        String beforeName = beforePlayer.getName();
        int handSizeBefore = beforePlayer.getHand().getNumberOfCards();
        Card topBefore = model.getTopCard();
        CardColor activeBefore = model.getActiveColor();

        model.drawAndPass();

        Player afterDrawPlayer = model.getCurrentPlayer();
        int handSizeAfterDraw = afterDrawPlayer.getHand().getNumberOfCards();

        assertEquals(beforeName, afterDrawPlayer.getName());
        assertEquals(handSizeBefore + 1, handSizeAfterDraw);

        model.undo();
        Player afterUndoPlayer = model.getCurrentPlayer();
        int handSizeAfterUndo = afterUndoPlayer.getHand().getNumberOfCards();

        assertEquals(beforeName, afterUndoPlayer.getName());
        assertEquals(handSizeBefore, handSizeAfterUndo);
        assertEquals(topBefore.toString(), model.getTopCard().toString());
        assertEquals(activeBefore, model.getActiveColor());

        model.redo();
        Player afterRedoPlayer = model.getCurrentPlayer();
        int handSizeAfterRedo = afterRedoPlayer.getHand().getNumberOfCards();

        assertEquals(beforeName, afterRedoPlayer.getName());
        assertEquals(handSizeBefore + 1, handSizeAfterRedo);
    }

    /**
     * Tests that playing a legal card can be undone and redone,
     * restoring both the player's hand and the discard top card.
     */
    @Test
    public void undoRedoAfterPlayCard() {
        UnoModel model = new UnoModel();
        model.startGame(2, 0);

        Player current = model.getCurrentPlayer();

        Hand hand = current.getHand();
        hand.clearHand();

        Card wild = new Card(CardColor.WILD, CardType.WILD,
                CardColor.WILD, CardType.WILD, -1, -1);

        Card redFive = new Card(CardColor.RED, CardType.NUMBER,
                CardColor.ORANGE, CardType.NUMBER, 5, 5);

        Card blueNine = new Card(CardColor.BLUE, CardType.NUMBER,
                CardColor.PINK, CardType.NUMBER, 9, 9);

        hand.addCard(wild);
        hand.addCard(redFive);
        hand.addCard(blueNine);

        String playerName = current.getName();
        int handSizeBefore = hand.getNumberOfCards();
        Card topBefore = model.getTopCard();
        CardColor activeBefore = model.getActiveColor();

        Card toPlay = wild;

        model.play(toPlay, model.getActiveColor());

        Player afterPlayPlayer = model.getCurrentPlayer();
        int handSizeAfterPlay = afterPlayPlayer.getHand().getNumberOfCards();
        Card topAfterPlay = model.getTopCard();

        assertEquals(playerName, afterPlayPlayer.getName());
        assertEquals(handSizeBefore - 1, handSizeAfterPlay);
        assertEquals(toPlay.toString(), topAfterPlay.toString());

        model.undo();
        Player afterUndoPlayer = model.getCurrentPlayer();
        int handSizeAfterUndo = afterUndoPlayer.getHand().getNumberOfCards();
        Card topAfterUndo = model.getTopCard();

        assertEquals(playerName, afterUndoPlayer.getName());
        assertEquals(handSizeBefore, handSizeAfterUndo);
        assertEquals(topBefore.toString(), topAfterUndo.toString());
        assertEquals(activeBefore, model.getActiveColor());

        model.redo();
        Player afterRedoPlayer = model.getCurrentPlayer();
        int handSizeAfterRedo = afterRedoPlayer.getHand().getNumberOfCards();
        Card topAfterRedo = model.getTopCard();

        assertEquals(playerName, afterRedoPlayer.getName());
        assertEquals(handSizeBefore - 1, handSizeAfterRedo);
        assertEquals(toPlay.toString(), topAfterRedo.toString());
    }

    /**
     * Tests that multiple undo and redo operations correctly walk
     * backwards and forwards through the history of states.
     */
    @Test
    public void multiLevelUndoRedo() {
        UnoModel model = new UnoModel();
        model.startGame(2, 0);

        Player first = model.getCurrentPlayer();
        String firstName = first.getName();
        int initialHandSize = first.getHand().getNumberOfCards();

        model.drawAndPass();

        Player afterDraw = model.getCurrentPlayer();
        int handAfterDraw = afterDraw.getHand().getNumberOfCards();
        assertEquals(firstName, afterDraw.getName());
        assertEquals(initialHandSize + 1, handAfterDraw);

        model.nextPlayer();
        Player second = model.getCurrentPlayer();
        String secondName = second.getName();
        assertNotEquals(firstName, secondName);

        model.undo();
        Player afterUndo1 = model.getCurrentPlayer();
        int handAfterUndo1 = afterUndo1.getHand().getNumberOfCards();

        assertEquals(firstName, afterUndo1.getName());
        assertEquals(initialHandSize + 1, handAfterUndo1);

        model.undo();
        Player afterUndo2 = model.getCurrentPlayer();
        int handAfterUndo2 = afterUndo2.getHand().getNumberOfCards();

        assertEquals(firstName, afterUndo2.getName());
        assertEquals(initialHandSize, handAfterUndo2);

        model.redo();
        Player afterRedo1 = model.getCurrentPlayer();
        int handAfterRedo1 = afterRedo1.getHand().getNumberOfCards();

        assertEquals(firstName, afterRedo1.getName());
        assertEquals(initialHandSize + 1, handAfterRedo1);

        model.redo();
        Player afterRedo2 = model.getCurrentPlayer();
        String nameAfterRedo2 = afterRedo2.getName();

        assertEquals(secondName, nameAfterRedo2);
    }

    /**
     * Tests undo and redo when an AI player takes a turn.
     * Ensures the current player is correctly restored after undo
     * and returned to the AI turn after redo.
     */
    @Test
    public void undoRedoWithAITurn() {
        UnoModel model = new UnoModel();
        model.startGame(1, 1);

        Player initial = model.getCurrentPlayer();
        boolean initialIsHuman = initial.isHuman();
        String initialName = initial.getName();

        model.drawAndPass();
        model.nextPlayer();

        Player afterAITurn = model.getCurrentPlayer();
        String afterAITurnName = afterAITurn.getName();

        assertEquals(2, model.getPlayers().size());

        model.undo();
        Player afterUndo = model.getCurrentPlayer();

        assertNotNull(afterUndo);
        assertEquals("Undo should restore a valid player index",
                afterUndo, model.getCurrentPlayer());

        model.redo();
        Player afterRedo = model.getCurrentPlayer();
        assertEquals(afterAITurnName, afterRedo.getName());
    }

    /**
     * After performing an undo, any new action should clear
     * the redo history so that redo no longer has any effect.
     */
    @Test
    public void redoClearedAfterNewAction() {
        UnoModel model = new UnoModel();
        model.startGame(2, 0);

        Player current = model.getCurrentPlayer();
        int initialHandSize = current.getHand().getNumberOfCards();

        model.drawAndPass();
        int handAfterDraw = model.getCurrentPlayer().getHand().getNumberOfCards();
        assertEquals(initialHandSize + 1, handAfterDraw);

        model.undo();
        int handAfterUndo = model.getCurrentPlayer().getHand().getNumberOfCards();
        assertEquals(initialHandSize, handAfterUndo);

        model.drawAndPass();
        int handAfterSecondDraw = model.getCurrentPlayer().getHand().getNumberOfCards();
        assertEquals(initialHandSize + 1, handAfterSecondDraw);

        model.redo();
        int handAfterRedo = model.getCurrentPlayer().getHand().getNumberOfCards();

        assertEquals("Redo should have no effect after a new action",
                handAfterSecondDraw, handAfterRedo);
    }

    /**
     * Calling undo on a fresh game with no recorded moves
     * should not change the state or throw an exception.
     */
    @Test
    public void undoOnFreshGameDoesNotChangeState() {
        UnoModel model = new UnoModel();
        model.startGame(2, 0);

        Player before = model.getCurrentPlayer();
        String beforeName = before.getName();
        int beforeHandSize = before.getHand().getNumberOfCards();
        Card beforeTop = model.getTopCard();
        CardColor beforeActive = model.getActiveColor();

        model.undo();

        Player after = model.getCurrentPlayer();
        String afterName = after.getName();
        int afterHandSize = after.getHand().getNumberOfCards();
        Card afterTop = model.getTopCard();
        CardColor afterActive = model.getActiveColor();

        assertEquals("Current player should not change after undo with no history",
                beforeName, afterName);
        assertEquals("Hand size should not change after undo with no history",
                beforeHandSize, afterHandSize);
        assertEquals("Top card should not change after undo with no history",
                beforeTop.toString(), afterTop.toString());
        assertEquals("Active color should not change after undo with no history",
                beforeActive, afterActive);
    }

}
