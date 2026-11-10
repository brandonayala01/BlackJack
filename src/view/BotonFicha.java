package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

// Ficha de casino redonda. Al hacer clic suma su valor a la apuesta.
public class BotonFicha extends JButton {

    private final int valor;

    public BotonFicha(int valor) {
        this.valor = valor;
        setToolTipText("Apostar " + valor + " fichas");
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public int getValor() {
        return valor;
    }

    // Color de cada ficha según su valor.
    public static Color colorDe(int valor) {
        if (valor >= 100) return new Color(0x2A2A2A);
        if (valor >= 50) return new Color(0xD9822B);
        if (valor >= 25) return new Color(0x23894A);
        if (valor >= 10) return new Color(0x2557A8);
        if (valor >= 5) return new Color(0xB8282E);
        return new Color(0x8C8C8C);
    }

    // Dibuja una ficha. También la usa la mesa para la pila de fichas apostadas.
    public static void dibujar(Graphics2D g, int x, int y, int tam, int valor, boolean activa) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = activa ? colorDe(valor) : new Color(0x4F5552);

        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(x + 2, y + 3, tam, tam);

        g.setColor(base);
        g.fillOval(x, y, tam, tam);

        g.setColor(activa ? Color.WHITE : new Color(0x9A9A9A));
        g.setStroke(new BasicStroke(Math.max(2f, tam / 9f)));
        for (int i = 0; i < 6; i++) {
            g.drawArc(x + tam / 14, y + tam / 14, tam - tam / 7, tam - tam / 7, i * 60, 22);
        }

        int interior = (int) (tam * 0.62);
        int margen = (tam - interior) / 2;
        g.setColor(base.darker());
        g.fillOval(x + margen, y + margen, interior, interior);
        g.setStroke(new BasicStroke(1.2f));
        g.setColor(new Color(255, 255, 255, 170));
        g.drawOval(x + margen, y + margen, interior, interior);

        String texto = String.valueOf(valor);
        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, tam / (texto.length() > 2 ? 4 : 3))));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(activa ? Color.WHITE : new Color(0xB0B0B0));
        g.drawString(texto, x + (tam - fm.stringWidth(texto)) / 2,
                y + (tam + fm.getAscent() - fm.getDescent()) / 2);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        int tam = Math.min(getWidth(), getHeight()) - 6;
        int subir = getModel().isRollover() && isEnabled() ? 0 : 3;
        dibujar(g, (getWidth() - tam) / 2 - 1, subir, tam, valor, isEnabled());
        if (getModel().isPressed()) {
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval((getWidth() - tam) / 2 - 1, subir, tam, tam);
        }
        g.dispose();
    }
}
