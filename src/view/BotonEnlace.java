package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JButton;

// Texto que se comporta como botón (enlace). Ej: "INICIAR SESIÓN" subrayado dorado.
public class BotonEnlace extends JButton {

    private final float tamano;
    private final Color color;
    private final boolean subrayado;

    public BotonEnlace(String texto, float tamano, Color color, boolean subrayado) {
        super(texto);
        this.tamano = tamano;
        this.color = color;
        this.subrayado = subrayado;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private Font fuente() {
        return Estilo.fuente(Font.BOLD, tamano);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(fuente());
        return new Dimension(fm.stringWidth(getText()) + 12, fm.getHeight() + 8);
    }

    // Coloca el enlace centrado horizontalmente en "centroX".
    public BotonEnlace centrarEn(int centroX, int y) {
        Dimension d = getPreferredSize();
        setBounds(centroX - d.width / 2, y, d.width, d.height);
        return this;
    }

    // Coloca el enlace pegado a un borde derecho.
    public BotonEnlace alinearDerecha(int xDerecha, int y) {
        Dimension d = getPreferredSize();
        setBounds(xDerecha - d.width, y, d.width, d.height);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        Font f = fuente();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int ancho = fm.stringWidth(getText());
        int x = (getWidth() - ancho) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        Color c = getModel().isRollover() ? Estilo.iluminar(color, 25) : color;
        Estilo.dibujarTextoSombra(g, getText(), f, c, x, y);
        if (subrayado) {
            g.setColor(c);
            g.fillRect(x, y + 4, ancho, 2);
        }
        g.dispose();
    }
}