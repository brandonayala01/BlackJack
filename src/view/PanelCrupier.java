package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import model.Crupier;

// Cuadro de madera del crupier: "Dealer:", sus cartas y su puntaje ("-" mientras hay una oculta).
public class PanelCrupier extends JComponent {

    public static final int ANCHO_PANEL = 524;
    public static final int ALTO_PANEL = 167;

    private Crupier crupier;
    private int limite = -1;   // cuántas cartas mostrar (-1 = todas); sirve para la animación del reparto

    public PanelCrupier() {
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO_PANEL + 8, ALTO_PANEL + 10));
    }

    public void setDatos(Crupier crupier, int limite) {
        this.crupier = crupier;
        this.limite = limite;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        Estilo.dibujarPanelMadera(g, 0, 0, ANCHO_PANEL, ALTO_PANEL, 22);

        // "Dealer:"
        Font fEtiqueta = Estilo.fuente(Font.PLAIN, 34);
        FontMetrics fm = g.getFontMetrics(fEtiqueta);
        int base = (ALTO_PANEL - fm.getHeight()) / 2 + fm.getAscent();
        Estilo.dibujarTextoSombra(g, "Dealer:", fEtiqueta, new Color(0xE6D5B8), 36, base);

        int y = (ALTO_PANEL - DibujoCarta.ALTO) / 2;
        boolean hayCartas = crupier != null && crupier.getMano().getCantidadCartas() > 0;
        if (!hayCartas) {
            DibujoCarta.dibujarHueco(g, ANCHO_PANEL / 2 - 103, y, DibujoCarta.ANCHO, DibujoCarta.ALTO);
            DibujoCarta.dibujarHueco(g, ANCHO_PANEL / 2 + 9, y, DibujoCarta.ANCHO, DibujoCarta.ALTO);
        } else {
            int cuantas = limite < 0 ? Integer.MAX_VALUE : limite;
            int oculta = crupier.isSegundaOculta() ? 1 : -1;
            DibujoCarta.dibujarFila(g, crupier.getMano().getCartas(), cuantas, oculta,
                    ANCHO_PANEL / 2, y, DibujoCarta.ANCHO, DibujoCarta.ALTO, 112, 260);
        }

        // Puntaje a la derecha ("-" mientras la segunda carta está oculta o se está repartiendo)
        String puntaje = "-";
        if (hayCartas && limite < 0) {
            puntaje = crupier.getTextoPuntaje();
        }
        Font fPuntaje = Estilo.fuente(Font.BOLD, 38);
        FontMetrics fp = g.getFontMetrics(fPuntaje);
        int xp = ANCHO_PANEL - 74 - fp.stringWidth(puntaje) / 2;
        int bp = (ALTO_PANEL - fp.getHeight()) / 2 + fp.getAscent();
        Estilo.dibujarTextoSombra(g, puntaje, fPuntaje, Estilo.DORADO, xp, bp);
        g.dispose();
    }
}