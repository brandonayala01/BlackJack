import controller.ControladorPrincipal;
import javax.swing.SwingUtilities;
import view.VentanaPrincipal;

// Punto de entrada del juego. Ejecuta esta clase.
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ControladorPrincipal controlador = new ControladorPrincipal();
                new VentanaPrincipal(controlador).setVisible(true);
            }
        });
    }
}
