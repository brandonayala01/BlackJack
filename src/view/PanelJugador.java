package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import model.Carta;
import model.Mano;
import model.Ronda;

// Cuadro de madera del jugador. Con una mano muestra "Score: N" a la derecha (como el boceto);
// tras un split muestra varias manos en fila, resaltando la que se está jugando.
public class PanelJugador extends JComponent {

    public static final int ANCHO_PANEL = 524;
    public static final int ALTO_PANEL = 167;

    private Ronda ronda;
    private int limite = -1;   // cuántas cartas mostrar de la primera mano (-1 = todas)

    public PanelJugador() {
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO_PANEL + 8, ALTO_PANEL + 10));
    }

    public void setDatos(Ronda ronda, int limite) {
        this.ronda = ronda;
        this.limite = limite;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        Estilo.dibujarPanelMadera(g, 0, 0, ANCHO_PANEL, ALTO_PANEL, 22);

        ArrayList<Mano> manos = ronda == null ? new ArrayList<Mano>() : ronda.getManos();
        if (manos.isEmpty() || manos.get(0).getCantidadCartas() == 0) {
            int y = (ALTO_PANEL - DibujoCarta.ALTO) / 2;
            DibujoCarta.dibujarHueco(g, ANCHO_PANEL / 2 - 103, y, DibujoCarta.ANCHO, DibujoCarta.ALTO);
            DibujoCarta.dibujarHueco(g, ANCHO_PANEL / 2 + 9, y, DibujoCarta.ANCHO, DibujoCarta.ALTO);
        } else if (manos.size() == 1) {
            dibujarUnaMano(g, manos.get(0));
        } else {
            dibujarVariasManos(g, manos);
        }
        g.dispose();
    }

    // ---------------------------------------------------------------
    private void dibujarUnaMano(Graphics2D g, Mano mano) {
        List<Carta> cartas = mano.getCartas();
        int cuantas = limite < 0 ? cartas.size() : Math.min(limite, cartas.size());
        int y = (ALTO_PANEL - DibujoCarta.ALTO) / 2;
        DibujoCarta.dibujarFila(g, cartas, cuantas, -1, ANCHO_PANEL / 2, y,
                DibujoCarta.ANCHO, DibujoCarta.ALTO, 112, 260);

        int puntos = puntaje(cartas, cuantas);
        String valor = String.valueOf(puntos);
        // Espacio libre entre el borde derecho de las cartas y el borde del cuadro.
        int bordeCartas = ANCHO_PANEL / 2
                + DibujoCarta.anchoGrupo(cuantas, DibujoCarta.ANCHO, 112, 260) / 2;
        int libre = Math.max(70, ANCHO_PANEL - 22 - (bordeCartas + 14));
        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 40, libre, "Score: " + valor);
        Font fEtiqueta = Estilo.fuente(Font.PLAIN, tam * 0.85f);
        Font fValor = Estilo.fuente(Font.BOLD, tam);
        FontMetrics fe = g.getFontMetrics(fEtiqueta);
        FontMetrics fv = g.getFontMetrics(fValor);
        int total = fe.stringWidth("Score: ") + fv.stringWidth(valor);
        int x = ANCHO_PANEL - 22 - total;
        int base = (ALTO_PANEL - fv.getHeight()) / 2 + fv.getAscent();
        Estilo.dibujarTextoSombra(g, "Score: ", fEtiqueta, new Color(0xE6D5B8), x, base);
        Color c = puntos > 21 ? Estilo.iluminar(Estilo.ROJO, 25) : Estilo.DORADO;
        Estilo.dibujarTextoSombra(g, valor, fValor, c, x + fe.stringWidth("Score: "), base);
    }

    // ---------------------------------------------------------------
    private void dibujarVariasManos(Graphics2D g, ArrayList<Mano> manos) {
        int n = manos.size();
        int ancho = n == 2 ? 64 : 52;
        int alto = (int) Math.round(ancho * DibujoCarta.ALTO / (double) DibujoCarta.ANCHO);
        int ranura = (ANCHO_PANEL - 32) / n;
        int paso = n == 2 ? 46 : 30;
        int yCartas = n == 2 ? 12 : 14;

        boolean jugando = ronda.getFase().equals(Ronda.FASE_JUGADOR);
        for (int i = 0; i < n; i++) {
            Mano mano = manos.get(i);
            int x0 = 16 + i * ranura;
            int centro = x0 + ranura / 2;

            // Resalte de la mano activa
            if (jugando && i == ronda.getIndiceManoActual()) {
                RoundRectangle2D marco = new RoundRectangle2D.Float(x0 + 3, 8, ranura - 6, ALTO_PANEL - 16, 16, 16);
                g.setColor(new Color(244, 217, 138, 38));
                g.fill(marco);
                g.setStroke(new BasicStroke(2.4f));
                g.setColor(Estilo.DORADO);
                g.draw(marco);
            }

            DibujoCarta.dibujarFila(g, mano.getCartas(), Integer.MAX_VALUE, -1, centro, yCartas,
                    ancho, alto, paso, ranura - 14);

            // Puntaje y apuesta debajo de las cartas
            int puntos = mano.getPuntaje();
            String texto = String.valueOf(puntos);
            Font fp = Estilo.fuente(Font.BOLD, 28);
            FontMetrics fm = g.getFontMetrics(fp);
            int by = yCartas + alto + 6 + fm.getAscent();
            Color c = puntos > 21 ? Estilo.iluminar(Estilo.ROJO, 25) : Estilo.DORADO;
            Estilo.dibujarTextoSombra(g, texto, fp, c, centro - fm.stringWidth(texto) / 2, by);

            Font fa = Estilo.fuente(Font.PLAIN, 17);
            String apuesta = (mano.isDoblada() ? "x2 · " : "") + mano.getApuesta() + " fichas";
            FontMetrics fa2 = g.getFontMetrics(fa);
            Estilo.dibujarTextoSombra(g, apuesta, fa, new Color(0xE6D5B8),
                    centro - fa2.stringWidth(apuesta) / 2, by + fa2.getAscent() + 1);
        }
    }

    private static int puntaje(List<Carta> cartas, int cuantas) {
        Mano m = new Mano();
        for (int i = 0; i < cuantas && i < cartas.size(); i++) {
            m.agregarCarta(cartas.get(i));
        }
        return m.getPuntaje();
    }
}