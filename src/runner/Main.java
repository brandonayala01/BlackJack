package runner;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import presenter.Presenter;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            Presenter presenter = new Presenter();
            presenter.run();
        });
    }
}
