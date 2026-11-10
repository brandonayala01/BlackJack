package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

// Fondo de todas las pantallas: fieltro verde y, opcionalmente, la barra
// de madera con el logo "BLACKJACK Royale". Usa posicionamiento absoluto
// (layout null) porque la ventana tiene tamaño fijo, como en los bocetos.
public class PanelFieltro extends JPanel {

    private final boolean conBarra;
    private BufferedImage fondo;   // se dibuja una sola vez y se reutiliza

    public PanelFieltro(boolean conBarra) {
        super(null);
        this.conBarra = conBarra;
        setPreferredSize(new Dimension(Estilo.ANCHO, Estilo.ALTO));
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (fondo == null || fondo.getWidth() != w || fondo.getHeight() != h) {
            fondo = crearFondo(w, h);
        }
        g.drawImage(fondo, 0, 0, null);
    }

    private BufferedImage crearFondo(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        Estilo.suavizado(g);
        Estilo.dibujarFieltro(g, w, h);
        if (conBarra) {
            Estilo.dibujarBarra(g, w);
        }
        g.dispose();
        return img;
    }
}