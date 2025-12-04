package UNO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Implements methods to handle player actions.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class UnoController implements ActionListener {

    private UnoModel model;
    private UnoView view;

    /**
     * Constructs a controller class with given model and view.
     *
     * @param model the UnoModel.
     * @param view the UnoView.
     */
    public UnoController(UnoModel model, UnoView view) {
        this.view = view;
        this.model = model;

        view.getDrawCardButton().addActionListener(this);
        view.getNextPlayerButton().addActionListener(this);
        view.getUndoMenuItem().addActionListener(this);
        view.getRedoMenuItem().addActionListener(this);
        view.getSaveMenuItem().addActionListener(this);
        view.getLoadMenuItem().addActionListener(this);
    }

    /**
     * Handles actions performed by players like draw, next and play.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Draw Card":{
                this.model.drawAndPass();
                break;
            }
            case "Next Player":{
                this.model.nextPlayer();
                break;
            }

            case "Undo":
                model.undo();
                return;

            case "Redo":
                model.redo();
                return;

            case "Save": {
                File file = view.promptSaveFile();
                if (file != null) {
                    try {
                        String xml = model.toXML();
                        java.nio.file.Files.writeString(file.toPath(), xml);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            }
            case "Load": {
                File file = view.promptLoadFile();
                if (file != null) {
                    try {
                        String xml = Files.readString(file.toPath());
                        model.parseXML(xml);
                        view.rebuildScoreboard(model.getPlayers().size());
                        model.notifyObservers();
                        JOptionPane.showMessageDialog(view, "Game loaded successfully!");
                    }
                    catch (IOException io) {
                        JOptionPane.showMessageDialog(view,
                                "Error loading file:\n" + io.getMessage(),
                                "Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(view,
                                "Unexpected error loading game.",
                                "Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return;
            }

            case "Replay":
                model.replay();
                return;

            default: {
                int index = Integer.parseInt(command);
                Card selectedCard = model.getCurrentPlayer().getHand().getCards().get(index);
                this.model.play(selectedCard, model.getActiveColor());
            }
        }
    }
}
