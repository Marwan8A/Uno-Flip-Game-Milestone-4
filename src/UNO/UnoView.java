package UNO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Implements the GUI of the Uno Game.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class UnoView extends JFrame implements UnoObserver {

    private JPanel topPanel = new JPanel();
    private JPanel topCardPanel = new JPanel();
    private JPanel messagePanel = new JPanel();
    private JPanel centrePanel = new JPanel();
    private JPanel scoreBoardPanel = new JPanel();
    private JPanel handPanel = new JPanel();

    private JLabel topCardLabel = new JLabel("Card Placeholder");
    private JLabel currentPlayerLabel = new JLabel("Current Player: ");
    private JLabel statusMessageLabel = new JLabel();
    private JLabel wildColorLabel = new JLabel();
    private JLabel[] playersScoreLabel;

    private JButton drawCardButton = new JButton("Draw Card");
    private JButton nextPlayerButton = new JButton("Next Player");

    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Menu");
    private JMenuItem undoMenuItem = new JMenuItem("Undo");
    private JMenuItem redoMenuItem = new JMenuItem("Redo");
    private JMenuItem saveMenuItem = new JMenuItem("Save");
    private JMenuItem loadMenuItem = new JMenuItem("Load");

    private UnoModel model;
    private UnoController controller;
    private int numPlayers;

    private int numHumanPlayers = 0;
    private int numAIPlayers = 0;

    /**
     * Constructor for UnoView.
     */
    public UnoView() {
        super("Uno Flip Game");

        this.model = new  UnoModel();
        this.controller = new UnoController(model,this);
        this.model.addObserver(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1000);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(false);
        selectNumberOfHumanPlayers();

        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Top Card"));
        messagePanel.setLayout(new GridLayout(3, 1));
        topPanel.add(messagePanel, BorderLayout.SOUTH);
        topPanel.add(topCardPanel, BorderLayout.CENTER);

        menuBar.add(menu);
        menu.add(undoMenuItem);
        menu.add(redoMenuItem);
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        setJMenuBar(menuBar);

        topCardPanel.add(topCardLabel);
        messagePanel.add(currentPlayerLabel);
        messagePanel.add(statusMessageLabel);
        messagePanel.add(wildColorLabel);

        centrePanel.setLayout(new BorderLayout());
        handPanel.setLayout(new GridLayout(0, 7));
        centrePanel.add(drawCardButton, "East");
        centrePanel.add(nextPlayerButton, "West");
        centrePanel.add(handPanel, "Center");
        nextPlayerButton.setEnabled(false);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centrePanel, BorderLayout.CENTER);
        this.add(scoreBoardPanel, BorderLayout.SOUTH);
    }

    /**
     * Asks the players to select number of human players.
     */
    private void selectNumberOfHumanPlayers() {
        String[] options = {"1", "2", "3", "4"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setPreferredSize(new Dimension(220, 30));
        comboBox.setSelectedIndex(0);

        int result = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                "Select the total number of human players:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            numHumanPlayers = Integer.parseInt((String) comboBox.getSelectedItem());
            selectNumberOfAIPlayers(numHumanPlayers);
        } else {
            System.exit(0);
        }
    }

    /**
     * Asks the players to select number of AI players.
     */
    private void selectNumberOfAIPlayers(int humanCount) {

        int maxPlayers = 4;
        int minTotal = 2;

        int minAI = Math.max(0, minTotal - humanCount);
        int maxAI = maxPlayers - humanCount;

        String[] options = new String[maxAI - minAI + 1];
        int idx = 0;
        for (int i = minAI; i <= maxAI; i++) {
            options[idx++] = Integer.toString(i);
        }

        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedIndex(0);

        int result = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                "Select the total number of AI players:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            numAIPlayers = Integer.parseInt((String) comboBox.getSelectedItem());
            numPlayers =  numHumanPlayers + numAIPlayers;
            initScoreboard();
            model.startGame(numHumanPlayers, numAIPlayers);
            this.setVisible(true);

        } else {
            System.exit(0);
        }
    }

    /**
     * Creates the scoreboard area and labels for each player.
     */
    private void initScoreboard() {
        scoreBoardPanel.removeAll();
        scoreBoardPanel.setLayout(new GridLayout(numPlayers, 1));
        scoreBoardPanel.setBorder(BorderFactory.createTitledBorder("Scoreboard: "));

        playersScoreLabel = new JLabel[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            JLabel playerLabel = new JLabel("Player " + (i + 1) + ": 0");
            playersScoreLabel[i] = playerLabel;
            scoreBoardPanel.add(playerLabel);
        }
    }

    /**
     * Displays active color chosen after WILD card played.
     *
     * @return the selected color.
     */
    private CardColor wildColourSelection() {
        String[] options = {"RED", "YELLOW", "GREEN", "BLUE"};
        if(model.getTopCard().getSide() == CardSide.DARK){
            options = new String[]{"PINK", "TEAL", "ORANGE", "PURPLE"};
        }

        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedIndex(0);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    comboBox,
                    "Choose a colour for the WILD CARD:",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) comboBox.getSelectedItem();

                switch (selected) {
                    case "RED":
                        return CardColor.RED;
                    case "YELLOW":
                        return CardColor.YELLOW;
                    case "GREEN":
                        return CardColor.GREEN;
                    case "BLUE":
                        return CardColor.BLUE;
                    case "PINK":
                        return CardColor.PINK;
                    case "TEAL":
                        return CardColor.TEAL;
                    case "ORANGE":
                        return CardColor.ORANGE;
                    case "PURPLE":
                        return CardColor.PURPLE;
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "You must choose a colour to continue.", "WILD Colour Selection", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Updates UI after every action.
     * Updates things like top card, hands, scoreboard.
     *
     * @param e the UNO event with the latest state.
     */
    @Override
    public void updateGame(UnoEvent e) {
        handPanel.removeAll();

        Player current = e.getCurrentPlayer();
        java.util.List<Card> cards = current.getHand().getCards();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            JButton cardButton = new JButton(card.toString(), card.getImageIcon());
            cardButton.setHorizontalTextPosition(SwingConstants.CENTER);
            cardButton.setVerticalTextPosition(SwingConstants.TOP);
            cardButton.setActionCommand(String.valueOf(i));
            cardButton.addActionListener(controller);
            handPanel.add(cardButton);
        }

        topCardLabel.setIcon(e.getTopCard().getImageIcon());
        currentPlayerLabel.setText("Current Player: " + e.getCurrentPlayer().getName());
        statusMessageLabel.setText(e.getMessage());

        if (e.isNextPlayerTurn()) {
            for (Component c : handPanel.getComponents()) {
                if (c instanceof JButton) c.setEnabled(false);
            }
            drawCardButton.setEnabled(false);
            nextPlayerButton.setEnabled(true);
        } else {
            for (Component c : handPanel.getComponents()) {
                if (c instanceof JButton) c.setEnabled(true);
            }
            drawCardButton.setEnabled(true);
            nextPlayerButton.setEnabled(false);
        }

        if (e.isNeedsColorSelection() && current.isHuman()) {

            handPanel.revalidate();
            handPanel.repaint();

            CardColor chosen = wildColourSelection();
            model.setChosenColor(chosen);
        }

        if(e.isNeedsDrawColorSelection() && current.isHuman()){

            handPanel.revalidate();
            handPanel.repaint();
            CardColor chosen = wildColourSelection();
            model.setWildDrawColor(chosen);
        }

        List<Player> players = model.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            playersScoreLabel[i].setText(p.getName() + ": " + p.getScore());
        }

        Card topCard = e.getTopCard();

        if ((topCard.getType() == CardType.WILD || topCard.getType() == CardType.WILD_DRAW_TWO)
                && model.getActiveColor() != null) {

            CardColor color = model.getActiveColor();
            wildColorLabel.setText("Active Wild Color: " + color);

        } else {
            wildColorLabel.setText("");
        }

        if (e.getRoundWinner() != null) {
            Player winner = e.getRoundWinner();

            if (e.isGameOver()) {

                int choice = JOptionPane.showConfirmDialog(this,
                        winner.getName() + " reached 500 points and wins the game!\n\n" +
                                "Would you like to replay the entire game?", "Game Over",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {

                    this.setVisible(false);
                    this.dispose();
                    new UnoView();
                } else {
                    JOptionPane.showMessageDialog(this, "Thanks for playing UNO Flip!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }

                return;
            }

            else {
                int choice = JOptionPane.showConfirmDialog(this,
                        winner.getName() + " wins the round!\nWould you like to start the next round?",
                        "Round Winner", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    model.startGame(numHumanPlayers, numAIPlayers);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Thanks for playing UNO Flip!",
                            "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }

        handPanel.revalidate();
        handPanel.repaint();
    }

    /**
     * Provide the draw button to the controller.
     *
     * @return the draw card button.
     */
    public JButton getDrawCardButton() {
        return drawCardButton;
    }

    /**
     * Provide the next player button to the controller.
     *
     * @return the next player button.
     */
    public JButton getNextPlayerButton() {
        return nextPlayerButton;
    }

    /**
     * Returns the Undo menu item so the controller can attach listeners.
     *
     * @return the Undo menu item
     */
    public JMenuItem getUndoMenuItem() {

        return undoMenuItem;
    }

    /**
     * Returns the Redo menu item so the controller can attach listeners.
     *
     * @return the Redo menu item
     */
    public JMenuItem getRedoMenuItem() {

        return redoMenuItem;
    }

    /**
     * Returns the Save menu item so the controller can attach listeners.
     *
     * @return the Save menu item
     */
    public JMenuItem getSaveMenuItem(){

        return saveMenuItem;
    }

    /**
     * Returns the Load menu item so the controller can attach listeners.
     *
     * @return the Load menu item
     */
    public JMenuItem getLoadMenuItem(){

        return loadMenuItem;
    }

    /**
     * Prompts the user for a file location to save the game to.
     *
     * @return the selected file, or null if the user cancels
     */
    public File promptSaveFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save UNO Game");
        chooser.setSelectedFile(new File("uno_save.xml"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Prompts the user to choose a file to load a saved game from.
     *
     * @return the selected file, or null if the user cancels
     */
    public File promptLoadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load UNO Game");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Rebuilds the scoreboard panel for the given number of players.
     * Resets and re-adds all player score labels.
     *
     * @param playerCount the number of players to show in the scoreboard
     */
    public void rebuildScoreboard(int playerCount) {
        scoreBoardPanel.removeAll();
        scoreBoardPanel.setLayout(new GridLayout(playerCount, 1));
        scoreBoardPanel.setBorder(BorderFactory.createTitledBorder("Scoreboard: "));

        playersScoreLabel = new JLabel[playerCount];
        for (int i = 0; i < playerCount; i++) {
            JLabel label = new JLabel("Player " + (i + 1) + ": 0");
            playersScoreLabel[i] = label;
            scoreBoardPanel.add(label);
        }

        scoreBoardPanel.revalidate();
        scoreBoardPanel.repaint();
    }

    public static void main(String[] args) {

        UnoView view = new UnoView();

    }
}
