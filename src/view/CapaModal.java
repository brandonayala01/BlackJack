package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;


public class CapaModal extends JComponent {

    public CapaModal() {
        setLayout(null);
        setOpaque(false);
        setVisible(false);
        MouseAdapter bloqueo = new MouseAdapter() {
        };
        addMouseListener(bloqueo);
        addMouseMotionListener(bloqueo);
        addMouseWheelListener(bloqueo);
    }

    public void mostrar(VentanaModal ventana) {
        removeAll();
        ventana.setLocation((Estilo.ANCHO - ventana.getWidth()) / 2,
                (Estilo.ALTO - ventana.getHeight()) / 2);
        add(ventana);
        setVisible(true);
        revalidate();
        repaint();
        ventana.requestFocusInWindow();
    }

    public void cerrar() {
        removeAll();
        setVisible(false);
        repaint();
    }

    public boolean estaAbierta() {
        return isVisible();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 105));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}