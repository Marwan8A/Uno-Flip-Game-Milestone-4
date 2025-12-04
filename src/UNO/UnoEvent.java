package UNO;

import java.util.EventObject;

/**
 * Represents an event in the UNO game that holds details
 * about the current game state and actions taken.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 3.0 - Milestone 3
 */
public class UnoEvent extends EventObject {

    private final String message;
    private final Card topCard;
    private final Player currentPlayer;
    private final boolean nextPlayerTurn;
    private final boolean needsColorSelection;
    private final boolean needsDrawColorSelection;
    private final Player roundWinner;
    private final boolean gameOver;

    /**
     * Creates a new UNO event with all necessary game details.
     *
     * @param source the object where the event originated
     * @param message the message describing the event
     * @param topCard the current top card on the discard pile
     * @param currentPlayer the player currently taking a turn
     * @param nextPlayerTurn true if the next player's turn should begin
     * @param needsColorSelection true if a color must be selected after a wild card
     * @param roundWinner the player who won the round, if any
     * @param gameOver true if the game has ended
     * @param needsDrawColorSelection true if a color must be selected for a Wild Draw Color card
     */
    public UnoEvent(Object source, String message, Card topCard,
                    Player currentPlayer, boolean nextPlayerTurn, boolean needsColorSelection,
                    Player roundWinner, boolean gameOver, boolean needsDrawColorSelection) {
        super(source);
        this.message = message;
        this.topCard = topCard;
        this.currentPlayer = currentPlayer;
        this.nextPlayerTurn = nextPlayerTurn;
        this.needsColorSelection = needsColorSelection;
        this.roundWinner = roundWinner;
        this.gameOver = gameOver;
        this.needsDrawColorSelection = needsDrawColorSelection;
    }

    /**
     * Gets the message to display
     *
     * @return message based on th event that happens.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the top card
     *
     * @return the top card on the discard pile.
     */
    public Card getTopCard() {
        return topCard;
    }

    /**
     * Gets the current player in the round.
     *
     * @return the player currently playing
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Checks if it is the next players turn.
     *
     * @return a boolean based on if it is the next players turn.
     */
    public boolean isNextPlayerTurn() {
        return nextPlayerTurn;
    }

    /**
     * Checks if the card played needs to select a color.
     *
     * @return a boolean based on if the card played needs to select a color.
     */
    public boolean isNeedsColorSelection() {
        return needsColorSelection;
    }

    /**
     * Gets round winner.
     *
     * @return the player that won the round.
     */
    public Player getRoundWinner() { return roundWinner; }

    /**
     * Checks if the game is over.
     *
     * @return a boolean based on if the game is over.
     */
    public boolean isGameOver() { return gameOver; }

    /**
     * Checks if a draw color selection is required for a wild draw color card.
     *
     * @return true if a draw color must be selected, false otherwise
     */
    public boolean isNeedsDrawColorSelection() { return  needsDrawColorSelection; }
}
