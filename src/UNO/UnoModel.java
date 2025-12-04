package UNO;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Core game model for UNO. Manages players, piles, turn order,
 * active color, action effects, scoring, and observer updates.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class UnoModel implements Serializable {
    private final ArrayList<UnoObserver> observers = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final Stack<Card> discard = new Stack<>();
    private Deck drawPile;
    private int currentPlayer = 0;
    private int direction = 1;
    private CardColor activeColor = null;
    private boolean nextPlayerTurn;
    private String statusMessage = "Status Message: ";
    private int steps;
    private boolean needsColourSelection;
    private Player roundWinner = null;
    private boolean gameOver = false;
    private boolean keepStatusMessage = false;
    private CardColor wildDrawColor = null;
    private boolean needsDrawColorSelection;
    private transient UnoStack unoStack = new  UnoStack();
    private int savedHumanPlayers;
    private int savedAIPlayers;


    /**
     * Starts a new game with the given number of human and AI players.
     * Initializes decks, deals cards, and sets the starter card.
     *
     * @param numHumanPlayers number of human players
     * @param numAIPlayers    number of AI-controlled players
     */
    public void startGame(int numHumanPlayers, int numAIPlayers) {
        unoStack.clear();
        this.savedHumanPlayers = numHumanPlayers;
        this.savedAIPlayers = numAIPlayers;

        drawPile = new Deck();
        discard.clear();
        steps = 0;
        int numPlayers = numHumanPlayers + numAIPlayers;

        if(players.isEmpty()) {
            for (int i = 1; i <= numHumanPlayers; i++) {
                players.add(new Player("Player" + i, new Hand(), true));
            }

            for (int i = numHumanPlayers + 1; i <= numPlayers; i++) {
                players.add(new Player("Player" + i, new Hand(), false));
            }
        }
        for (Player p : players) {
            p.getHand().clearHand();
            p.drawFrom(drawPile, 7);
        }

        Card starter = drawPile.drawCard();

        while (starter.getType() == CardType.WILD || starter.getType() == CardType.WILD_DRAW_TWO) {
            drawPile.getDrawPile().addFirst(starter);
            starter = drawPile.drawCard();
        }

        activeColor = starter.getColor();
        discard.push(starter);
        nextPlayerTurn = false;
        needsColourSelection = false;
        needsDrawColorSelection = false;
        roundWinner = null;
        gameOver = false;
        currentPlayer = 0;
        direction = 1;

        notifyObservers();
    }

    /**
     * Plays the given card for the current player.
     * Validates the move, updates piles, handles actions,
     * and triggers observer updates. If a wild is played,
     * sets the color-selection flag and waits for choice.
     *
     * @param card the card to play.
     * @param chosenColor color to set if the card is a wild.
     */
    public void play(Card card, CardColor chosenColor) {
        unoStack.saveState(this);

        Player p = players.get(currentPlayer);
        Card top = getTopCard();

        if (!Rules.isLegal(players.get(currentPlayer), card, top, activeColor)) {
            statusMessage = "Placing that card is not a valid move. Try again.";
            notifyObservers();
            return;
        }

        p.getHand().removeCard(card);
        discard.push(card);

        if (p.getHand().getNumberOfCards() == 0) {
            updateScore(p);
            roundWinner = p;
            statusMessage = p.getName() + " wins the round!";

            if (p.getScore() >= 20) {
                statusMessage += "  " + p.getName() + " reached 500 points and wins the game!";
                gameOver = true;
            } else {
                gameOver = false;
            }
        } else {
            roundWinner = null;
            gameOver = false;
        }

        if (card.getType() == CardType.WILD || card.getType() == CardType.WILD_DRAW_TWO) {
            needsColourSelection = true;
            statusMessage = p.getName() + " played a wild card, choose a color:";
        }
        else if(card.getType() == CardType.WILD_DRAW_COLOR) {
            activeColor = top.getColor();
            needsDrawColorSelection = true;
            statusMessage = p.getName() + " played a wild card, choose a color:";
        }
        else {
            statusMessage = p.getName() + " played a card: " + card;
            activeColor = card.getColor();
        }

        handleActionCard(card, p);
        nextPlayerTurn = true;

        notifyObservers();
    }

    /**
     * Applies special card effects.
     *
     * @param card the action card played.
     * @param p the player who played the card.
     */
    private void handleActionCard(Card card, Player p) {
        switch (card.getType()) {
            case SKIP -> {
                steps = 2;
                statusMessage += "\n Next player skipped!";
            }
            case REVERSE -> {
                if (players.size() == 2) {
                    steps = 2;
                } else {
                    direction *= -1;
                    steps = 1;
                }
                statusMessage += "\n Play direction reversed.";
            }
            case DRAW_ONE -> {
                if (drawPile.size() == 0) {
                    drawPile.reshuffleFromDiscard(discard);
                }
                int next = peekNextPlayer();
                players.get(next).drawFrom(drawPile, 1);
                statusMessage += " " + players.get(next).getName() + " draws 1 card. ";
                steps = 2;
            }
            case WILD_DRAW_TWO -> {
                if (drawPile.size() < 2) {
                    drawPile.reshuffleFromDiscard(discard);
                }
                int next = peekNextPlayer();
                players.get(next).drawFrom(drawPile, 2);
                statusMessage += "\n " + players.get(next).getName() + " draws 2 cards. ";
                steps = 2;
            }
            case SKIP_EVERYONE -> {
                steps = players.size();
                statusMessage += "\n All players skipped! " + p.getName() + " plays again.";
            }
            case DRAW_FIVE -> {
                if (drawPile.size() < 5) drawPile.reshuffleFromDiscard(discard);
                int next = peekNextPlayer();
                players.get(next).drawFrom(drawPile, 5);
                statusMessage += "\n " + players.get(next).getName() + " draws 5 cards.";
                steps = 2;
            }
            case FLIP -> {
                flipGame();
                statusMessage += "\n Flip card played! All cards flipped sides.";
                steps = 1;
            }
            case WILD_DRAW_COLOR -> {
                int next = peekNextPlayer();
                CardColor target = wildDrawColor;
                int count = 0;

                while (true) {
                    if (drawPile.size() == 0) drawPile.reshuffleFromDiscard(discard);
                    Card drawn = drawPile.drawCard();
                    players.get(next).getHand().addCard(drawn);
                    count++;

                    if (drawn.getColor() == target) break;
                }

                statusMessage += "\n" + players.get(next).getName()
                        + " draws until they get " + target
                        + " (" + count + " cards drawn)";
                steps = 2;
            }

            default -> steps = 1;
        }
    }

    /**
     * Advances the current player.
     *
     * @param steps number of positions to move.
     */
    private void advanceTurn(int steps) {
        currentPlayer += steps * direction;

        if (currentPlayer < 0) {
            currentPlayer += players.size();
        } else if (currentPlayer >= players.size()) {
            currentPlayer %= players.size();
        }
    }

    /**
     * Peeks at the index of the next player without changing current player.
     *
     * @return index of the next player
     */
    private int peekNextPlayer() {
        int next = currentPlayer + direction;
        if (next < 0) next += players.size();
        else if (next >= players.size()) next %= players.size();
        return next;
    }

    /**
     * Current player draws one card and passes the turn.
     * Reshuffles from discard if the draw pile is empty.
     */
    public void drawAndPass() {
        unoStack.saveState(this);

        Player p = players.get(currentPlayer);

        if (drawPile.size() == 0) {
            drawPile.reshuffleFromDiscard(discard);
        }

        Card drawn = drawPile.drawCard();
        if (drawn != null) p.getHand().addCard(drawn);

        statusMessage = p.getName() + " drew a card: " + drawn;
        nextPlayerTurn = true;
        steps = 1;
        notifyObservers();
    }

    /**
     * Moves to the next player's turn and notifies observers.
     */
    public void nextPlayer() {
        unoStack.saveState(this);

        advanceTurn(steps);

        Player p = getCurrentPlayer();
        statusMessage = "Now it's " + p.getName() + "'s turn.";
        nextPlayerTurn = false;
        notifyObservers();

        if (!p.isHuman()) {
            aiTakeTurn(p);
        }
    }

    /**
     * Updates the winner's score using the sum of all other hands.
     *
     * @param winner the player who won the round
     */
    public void updateScore(Player winner) {
        int total = 0;
        for (Player p : players) {
            if (p != winner) {
                total += p.getHand().getHandScore();
            }
        }
        winner.setScore(winner.getScore() + total);
    }

    /**
     * Gets the current player.
     *
     * @return current player
     */
    public Player getCurrentPlayer() { return players.get(currentPlayer); }

    /**
     * Registers an observer to receive game updates.
     *
     * @param observer observer to add
     */
    void addObserver(UnoObserver observer) { observers.add(observer); }

    /**
     * Notifies all observers with a new UnoEvent.
     * Resets roundWinner and gameOver flags afterward.
     */
    void notifyObservers() {
        UnoEvent event = new UnoEvent(
                this,
                statusMessage,
                getTopCard(),
                getCurrentPlayer(),
                nextPlayerTurn,
                needsColourSelection,
                roundWinner,
                gameOver,
                needsDrawColorSelection
        );

        for (UnoObserver observer : observers) {
            observer.updateGame(event);
        }

        roundWinner = null;
        gameOver = false;
    }

    /**
     * Gets the top card on the discard pile.
     *
     * @return top discard card
     */
    public Card getTopCard() { return discard.peek(); }

    /**
     * Gets the current active color.
     *
     * @return active color
     */
    public CardColor getActiveColor() { return activeColor; }


    /**
     * Applies the chosen color after a wild.
     *
     * @param colour color selected for the wild
     */
    public void setChosenColor(CardColor colour) {
        activeColor = colour;
        needsColourSelection = false;
        statusMessage = "Wild color chosen: " + colour + " ";
        keepStatusMessage = true;
        handleActionCard(getTopCard(), players.get(currentPlayer));
        nextPlayerTurn = true;
        notifyObservers();
    }

    /**
     * Returns the list of players in turn order.
     *
     * @return the list of players in the game.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Flips the entire game state from light side to dark side or vice versa.
     * Updates the active color, flips the draw pile, the top card, and all player hands.
     */
    private void flipGame(){
        switch (activeColor) {
            case BLUE   -> activeColor = CardColor.PINK;
            case YELLOW -> activeColor = CardColor.PURPLE;
            case GREEN  -> activeColor = CardColor.TEAL;
            case RED    -> activeColor = CardColor.ORANGE;

            case PINK   -> activeColor = CardColor.BLUE;
            case PURPLE -> activeColor = CardColor.YELLOW;
            case TEAL   -> activeColor = CardColor.GREEN;
            case ORANGE -> activeColor = CardColor.RED;

            default -> {}
        }

        drawPile.flipDeck();
        getTopCard().flipCard();

        for(Player p : players){
            p.flipPlayerHand();
        }

    }

    /**
     * Checks if a draw color selection is needed for the current wild draw color card.
     *
     * @return true if a draw color must be selected, false otherwise
     */
    public boolean isNeedsDrawColorSelection() {
        return needsDrawColorSelection;
    }

    /**
     * Sets the color that the next player must draw to for a wild draw color card.
     *
     * @param wildDrawColor the color the next player must draw to
     */
    public void setWildDrawColor(CardColor wildDrawColor) {
        this.wildDrawColor = wildDrawColor;

        needsDrawColorSelection = false;
        statusMessage = "Status Message: Current Active Color is " + activeColor + ". ";
        handleActionCard(getTopCard(), players.get(currentPlayer));
        nextPlayerTurn = true;
        notifyObservers();
    }

    /**
     * Replaces the current list of players with the given list.
     *
     * @param playerList the new list of players in turn order
     */
    public void setPlayers(List<Player> playerList) {
        players.clear();
        players.addAll(playerList);
    }

    /**
     * Lets the AI choose a card to play by returning the first legal card
     * in its hand based on the current top card and active color.
     *
     * @param ai the AI player whose hand is being checked
     * @return the first legal card to play, or null if none are legal
     */
    private Card chooseAICard(Player ai) {
        Card top = getTopCard();

        for (Card c : ai.getHand().getCards()) {
            if (Rules.isLegal(ai, c, top, activeColor)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Chooses a color for the AI when playing a wild card.
     * The AI counts the colors in its hand (on the current side)
     * and selects one of the colors it has the most cards of.
     *
     * @return the color chosen by the AI
     */
    private CardColor chooseAIColor() {
        HashMap<CardColor, Integer> colorMap = new HashMap<>();

        if (getTopCard().getSide() == CardSide.LIGHT) {
            colorMap.put(CardColor.RED, 0);
            colorMap.put(CardColor.YELLOW, 0);
            colorMap.put(CardColor.GREEN, 0);
            colorMap.put(CardColor.BLUE, 0);
        } else {
            colorMap.put(CardColor.PINK, 0);
            colorMap.put(CardColor.TEAL, 0);
            colorMap.put(CardColor.ORANGE, 0);
            colorMap.put(CardColor.PURPLE, 0);
        }

        ArrayList<Card> playerCards = players.get(currentPlayer).getHand().getCards();

        for (Card c : playerCards) {
            CardColor color = c.getColor();

            if (colorMap.containsKey(color)) {
                colorMap.put(color, colorMap.get(color) + 1);
            }
        }

        int maxCount = -1;
        ArrayList<CardColor> bestColors = new ArrayList<>();

        for (CardColor color : colorMap.keySet()) {
            int count = colorMap.get(color);

            if (count > maxCount) {
                maxCount = count;
                bestColors.clear();
                bestColors.add(color);
            } else if (count == maxCount) {
                bestColors.add(color);
            }
        }

        return bestColors.get((int)(Math.random() * bestColors.size()));
    }

    /**
     * Executes the full AI turn using simple first-valid-card logic.
     *
     * @param ai the AI player taking their turn
     */
    private void aiTakeTurn(Player ai) {
        Card chosen = chooseAICard(ai);

        if (chosen == null) {
            drawAndPass();
            return;
        }

        play(chosen, null);

        switch (chosen.getType()) {
            case WILD, WILD_DRAW_TWO -> setChosenColor(chooseAIColor());
            case WILD_DRAW_COLOR -> setWildDrawColor(chooseAIColor());
        }
    }

    /**
     * Reverts the game state to the previous saved state, if available.
     * Notifies observers after a successful undo.
     */
    public void undo() {
        UnoModel prev = unoStack.undo(this);
        if (prev != null) {
            restoreState(prev);
            notifyObservers();
        }
    }

    /**
     * Restores the game state to the state undone most recently, if available.
     * Notifies observers after a successful redo.
     */
    public void redo() {
        UnoModel next = unoStack.redo(this);
        if (next != null) {
            restoreState(next);
            notifyObservers();
        }
    }

    /**
     * Restarts the game using the same number of human and AI players.
     * Clears undo and redo history and starts a fresh game.
     */
    public void replay() {
        unoStack.clear();
        startGame(countHumanPlayers(),countAIPlayers());
    }

    /**
     * Copies all fields from the given model into this model.
     * Used internally for undo/redo state restoration.
     *
     * @param m the model whose state should be copied
     */
    private void restoreState(UnoModel m) {
        this.players.clear();
        this.players.addAll(m.players);

        this.discard.clear();
        this.discard.addAll(m.discard);

        this.drawPile = m.drawPile;
        this.currentPlayer = m.currentPlayer;
        this.direction = m.direction;
        this.activeColor = m.activeColor;
        this.nextPlayerTurn = m.nextPlayerTurn;
        this.statusMessage = m.statusMessage;
        this.steps = m.steps;
        this.needsColourSelection = m.needsColourSelection;
        this.roundWinner = m.roundWinner;
        this.gameOver = m.gameOver;
        this.keepStatusMessage = m.keepStatusMessage;
        this.wildDrawColor = m.wildDrawColor;
        this.needsDrawColorSelection = m.needsDrawColorSelection;
        this.savedHumanPlayers = m.savedHumanPlayers;
        this.savedAIPlayers = m.savedAIPlayers;
    }

    /**
     * Returns the number of human players saved for replay.
     *
     * @return the number of human players
     */
    private int countHumanPlayers() { return savedHumanPlayers; }

    /**
     * Returns the number of AI players saved for replay.
     *
     * @return the number of AI players
     */
    private int countAIPlayers() { return savedAIPlayers; }

    /**
     * Returns the list of observers currently registered with this model.
     *
     * @return an ArrayList of UnoObservers
     */
    public ArrayList<UnoObserver> getObservers() {
        return observers;
    }

    /**
     * Converts the discard pile to an XML representation.
     *
     * @param indent the indentation to apply to each XML line
     * @return a string containing the discard pile encoded as XML
     */
    private String discardPileToXML(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("<discardPile>\n");

        for (Card c : discard) {
            sb.append(c.toXML(indent + "  "));
        }

        sb.append(indent).append("</discardPile>\n");

        return sb.toString();
    }

    /**
     * Converts the complete game state to an XML representation.
     * Includes players, draw pile, discard pile, and core game variables.
     *
     * @return a string containing the current game encoded as XML
     */
    public String toXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<unoGame>\n");


        sb.append("  <currentPlayer>").append(currentPlayer).append("</currentPlayer>\n");
        sb.append("  <direction>").append(direction).append("</direction>\n");
        sb.append("  <activeColor>").append(activeColor == null ? "NONE" : activeColor).append("</activeColor>\n");
        sb.append("  <nextPlayerTurn>").append(nextPlayerTurn).append("</nextPlayerTurn>\n");
        sb.append("  <steps>").append(steps).append("</steps>\n");
        sb.append("  <needsColourSelection>").append(needsColourSelection).append("</needsColourSelection>\n");
        sb.append("  <needsDrawColorSelection>").append(needsDrawColorSelection).append("</needsDrawColorSelection>\n");
        sb.append("  <wildDrawColor>").append(wildDrawColor == null ? "NONE" : wildDrawColor).append("</wildDrawColor>\n");
        sb.append("  <savedHumanPlayers>").append(savedHumanPlayers).append("</savedHumanPlayers>\n");
        sb.append("  <savedAIPlayers>").append(savedAIPlayers).append("</savedAIPlayers>\n");

        sb.append("  <players>\n");
        for (Player p : players) {
            sb.append(p.toXML("    "));
        }
        sb.append("  </players>\n");

        sb.append(drawPile.toXML("  "));

        sb.append(discardPileToXML("  "));

        sb.append("</unoGame>\n");

        return sb.toString();
    }

    /**
     * Restores the game state from an XML representation.
     * Rebuilds players, draw pile, discard pile, and core game variables.
     *
     * @param xml the XML string to parse
     * @throws IOException if the XML is missing required data or is invalid
     */
    public void parseXML(String xml) throws IOException {

        if (xml == null || xml.isEmpty()) {
            throw new IOException("The file is empty or unreadable.");
        }

        try {

            this.currentPlayer = Integer.parseInt(requireTag(xml, "currentPlayer"));
            this.direction = Integer.parseInt(requireTag(xml, "direction"));

            String activeCol = requireTag(xml, "activeColor");
            this.activeColor = activeCol.equals("NONE") ? null : CardColor.valueOf(activeCol);

            this.nextPlayerTurn = Boolean.parseBoolean(requireTag(xml, "nextPlayerTurn"));
            this.steps = Integer.parseInt(requireTag(xml, "steps"));
            this.needsColourSelection = Boolean.parseBoolean(requireTag(xml, "needsColourSelection"));
            this.needsDrawColorSelection = Boolean.parseBoolean(requireTag(xml, "needsDrawColorSelection"));

            String wildCol = requireTag(xml, "wildDrawColor");
            this.wildDrawColor = wildCol.equals("NONE") ? null : CardColor.valueOf(wildCol);

            this.savedHumanPlayers = Integer.parseInt(requireTag(xml, "savedHumanPlayers"));
            this.savedAIPlayers = Integer.parseInt(requireTag(xml, "savedAIPlayers"));


            String playersSection = getSection(xml, "players");
            if (playersSection == null || playersSection.isEmpty()) {
                throw new IOException("Missing <players> section.");
            }

            this.players.clear();
            java.util.List<String> playerBlocks = getRepeatedSections(playersSection, "player");
            for (String block : playerBlocks) {
                this.players.add(parsePlayerXML(block));
            }

            String drawSection = getSection(xml, "drawPile");
            if (drawSection == null || drawSection.isEmpty()) {
                throw new IOException("Missing <drawPile> section.");
            }
            java.util.List<Card> drawCards = parseCardListXML(drawSection);
            this.drawPile = new Deck();
            this.drawPile.getDrawPile().clear();
            this.drawPile.getDrawPile().addAll(drawCards);

            String discardSection = getSection(xml, "discardPile");
            if (discardSection == null || discardSection.isEmpty()) {
                throw new IOException("Missing <discardPile> section.");
            }
            java.util.List<Card> discardCards = parseCardListXML(discardSection);
            this.discard.clear();
            for (Card c : discardCards) {
                this.discard.push(c);
            }

            this.roundWinner = null;
            this.gameOver = false;
        }
        catch (IOException io) {
            throw io;
        }
        catch (Exception e) {
            throw new IOException("Corrupted or invalid UNO file.", e);
        }
    }

    /**
     * Parses a single player from its XML representation.
     *
     * @param xml the XML block for one player
     * @return the Player parsed from the given XML
     */
    private Player parsePlayerXML(String xml) {
        String name = getTagValue(xml, "name");
        int score = Integer.parseInt(getTagValue(xml, "score"));
        boolean isHuman = Boolean.parseBoolean(getTagValue(xml, "isHuman"));

        String handSection = getSection(xml, "hand");
        java.util.List<Card> cards = parseCardListXML(handSection);

        Hand h = new Hand();
        for (Card c : cards) {
            h.addCard(c);
        }

        Player p = new Player(name, h, isHuman);
        p.setScore(score);

        return p;
    }

    /**
     * Returns the text content of the first occurrence of the given tag.
     *
     * @param xml the XML string to search
     * @param tag the tag name without angle brackets
     * @return the text inside the tag, or null if not found
     */
    private String getTagValue(String xml, String tag) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";
        int s = xml.indexOf(start);
        int e = xml.indexOf(end);

        if (s == -1 || e == -1) return null;

        return xml.substring(s + start.length(), e).trim();
    }

    /**
     * Returns the text content of the given tag, or throws an exception
     * if the tag is missing.
     *
     * @param xml the XML string to search
     * @param tag the tag name without angle brackets
     * @return the text inside the tag
     * @throws IOException if the tag is not found in the XML
     */
    private String requireTag(String xml, String tag) throws IOException {
        String value = getTagValue(xml, tag);
        if (value == null) {
            throw new IOException("Missing required tag <" + tag + "> in the file.");
        }
        return value;
    }

    /**
     * Returns the contents of the first matching tag block, excluding the tags.
     *
     * @param xml the XML string to search
     * @param tag the tag name without angle brackets
     * @return the inner contents of the tag, or an empty string if not found
     */
    private String getSection(String xml, String tag) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";

        int s = xml.indexOf(start);
        int e = xml.indexOf(end);

        if (s == -1 || e == -1) return "";

        return xml.substring(s + start.length(), e).trim();
    }

    /**
     * Returns all non-overlapping blocks that match the given tag.
     *
     * @param xml the XML string to search
     * @param tag the tag name without angle brackets
     * @return a list of inner contents for each occurrence of the tag
     */
    private List<String> getRepeatedSections(String xml, String tag) {
        List<String> list = new ArrayList<>();

        String start = "<" + tag + ">";
        String end = "</" + tag + ">";

        int s = 0;
        while (true) {
            int pos1 = xml.indexOf(start, s);
            int pos2 = xml.indexOf(end, pos1);

            if (pos1 == -1 || pos2 == -1) break;

            String block = xml.substring(pos1 + start.length(), pos2);
            list.add(block.trim());

            s = pos2 + end.length();
        }

        return list;
    }
    /**
     * Parses a list of cards from a section containing one or more &lt;card&gt; blocks.
     *
     * @param xml the XML string containing card entries
     * @return a list of Card objects parsed from the XML
     */
    private List<Card> parseCardListXML(String xml) {
        List<Card> list = new ArrayList<>();
        List<String> cards = getRepeatedSections(xml, "card");

        for (String block : cards) {
            list.add(parseCardXML(block));
        }

        return list;
    }

    /**
     * Parses a single Card from its XML representation.
     *
     * @param xml the XML block for one card
     * @return the Card parsed from the given XML
     */
    private Card parseCardXML(String xml) {

        CardColor lightColor = CardColor.valueOf(getTagValue(xml, "lightColor"));
        CardType lightType = CardType.valueOf(getTagValue(xml, "lightType"));
        int lightValue = Integer.parseInt(getTagValue(xml, "lightValue"));

        CardColor darkColor = CardColor.valueOf(getTagValue(xml, "darkColor"));
        CardType darkType = CardType.valueOf(getTagValue(xml, "darkType"));
        int darkValue = Integer.parseInt(getTagValue(xml, "darkValue"));

        CardSide side = CardSide.valueOf(getTagValue(xml, "side"));

        Card card = new Card(lightColor, lightType, darkColor, darkType, lightValue, darkValue);
        if (side == CardSide.DARK) card.flipCard();

        return card;
    }
}
