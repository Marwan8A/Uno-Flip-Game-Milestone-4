package UNO;

/**
 * Observer interface for UNO game updates.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 2.0 - Milestone 2
 */
public interface UnoObserver{

    /**
     * Called when the model state changes.
     * Updates the view based on the latest UNO event.
     *
     * @param e the UNO event containing updated game information
     */
    void updateGame(UnoEvent e);
}