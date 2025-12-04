package UNO;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Handles the undo and redo system for UNO.
 * Stores past game states and restores them when requested.
 * Used by the model to support reversing and reapplying moves.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 - Milestone 4
 */
public class UnoStack {
    private final Stack<UnoModel> undoStack = new Stack();
    private final Stack<UnoModel> redoStack = new Stack();

    /**
     * Creates a deep copy of the given UnoModel using Java serialization.
     *
     * @param model the model to copy
     * @return a copy of the given model
     * @throws RuntimeException if the copy cannot be created
     */
    private UnoModel deepCopy(UnoModel model) {
        try {

            ArrayList<UnoObserver> savedObservers = new ArrayList<>(model.getObservers());
            model.getObservers().clear();


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(model);


            model.getObservers().addAll(savedObservers);


            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);

            UnoModel copy = (UnoModel) in.readObject();


            return copy;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error deep-copying UnoModel", e);
        }
    }

    /**
     * Saves the current model state onto the undo stack
     * and clears the redo stack.
     *
     * @param current the current game model state
     */
    public void saveState(UnoModel current) {
        undoStack.push(deepCopy(current));
        redoStack.clear();
    }

    /**
     * Restores the most recent state from the undo stack.
     * The current state is pushed onto the redo stack.
     *
     * @param current the current game model state
     * @return the previous state to restore, or null if no undo is available
     */
    public UnoModel undo(UnoModel current) {
        if (undoStack.isEmpty()) return null;

        redoStack.push(deepCopy(current));

        return undoStack.pop();
    }

    /**
     * Restores the most recent state from the redo stack.
     * The current state is pushed onto the undo stack.
     *
     * @param current the current game model state
     * @return the next state to restore, or null if no redo is available
     */
    public UnoModel redo(UnoModel current) {
        if (redoStack.isEmpty()) return null;

        undoStack.push(deepCopy(current));

        return redoStack.pop();
    }

    /**
     * Clears both the undo and redo stacks.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
