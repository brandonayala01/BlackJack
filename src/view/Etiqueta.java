package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

// Texto con la fuente condensada del juego y una sombra suave.
// Respeta setHorizontalAlignment (LEFT, CENTER o RIGHT).
public class Etiqueta extends JLabel {

    public Etiqueta(String texto, float tamano, Color color, boolean negrita) {
        super(texto);
        setFont(Estilo.fuente(negrita ? Font.BOLD : Font.PLAIN, tamano));
        setForeground(color);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        String texto = getText();
        if (texto == null || texto.isEmpty()) {
            return;
        }
        Graphics2D g = Estilo.suavizar(g0);

        // Si el texto no cabe en el ancho de la etiqueta, se reduce la letra hasta que quepa
        // (así los mensajes largos de error no se cortan).
        Font fuente = getFont();
        FontMetrics fm = g.getFontMetrics(fuente);
        float tam = fuente.getSize2D();
        while (fm.stringWidth(texto) > getWidth() && tam > 11) {
            tam -= 1;
            fuente = fuente.deriveFont(tam);
            fm = g.getFontMetrics(fuente);
        }
        g.setFont(fuente);
        int ancho = fm.stringWidth(texto);

        int x = 0;
        if (getHorizontalAlignment() == SwingConstants.CENTER) {
            x = (getWidth() - ancho) / 2;
        } else if (getHorizontalAlignment() == SwingConstants.RIGHT) {
            x = getWidth() - ancho;
        }
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        Estilo.dibujarTextoSombra(g, texto, fuente, getForeground(), x, y);
        g.dispose();
    }
}