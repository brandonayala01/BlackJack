package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;


public final class Texto {

    private Texto() {
    }

    public static List<String> envolver(FontMetrics fm, String texto, int ancho) {
        List<String> lineas = new ArrayList<String>();
        String actual = "";
        for (String palabra : texto.split(" ")) {
            String prueba = actual.isEmpty() ? palabra : actual + " " + palabra;
            if (actual.isEmpty() || fm.stringWidth(prueba) <= ancho) {
                actual = prueba;
            } else {
                lineas.add(actual);
                actual = palabra;
            }
        }
        if (!actual.isEmpty()) {
            lineas.add(actual);
        }
        return lineas;
    }

    public static int alto(Graphics2D g, String texto, Font fuente, int ancho) {
        FontMetrics fm = g.getFontMetrics(fuente);
        return envolver(fm, texto, ancho).size() * fm.getHeight();
    }

    public static int dibujar(Graphics2D g, String texto, Font fuente, Color color,
                            int x, int yArriba, int ancho) {
        FontMetrics fm = g.getFontMetrics(fuente);
        int y = yArriba + fm.getAscent();
        for (String linea : envolver(fm, texto, ancho)) {
            Estilo.dibujarTextoSombra(g, linea, fuente, color, x, y);
            y += fm.getHeight();
        }
        return y - yArriba - fm.getAscent() + fm.getAscent();
    }
}