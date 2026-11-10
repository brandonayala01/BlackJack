package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;
import model.Carta;

// Ventana "GUÍA DE JUEGO": reglas básicas (con desplazamiento) y los 4 controles.
// Un único botón cierra esta ventana modal y devuelve al jugador a la mesa.
public class PanelGuia extends VentanaModal {

    private static final String[] REGLAS = {
        "El objetivo es obtener una mano con un valor más cercano a 21 que la del Dealer, sin pasarse.",
        "El As vale 1 u 11, las figuras valen 10, y el resto su valor nominal.",
        "“Blackjack” (As + Figura/10) paga 3:2.",
        "El Dealer pide carta hasta llegar a 17 o más, y entonces se planta.",
        "Si te pasas de 21 pierdes la mano. Si empatas con el Dealer recuperas tu apuesta.",
        "Puedes dividir un par hasta en 4 manos, cada una con su propia apuesta."
    };
    private static final int REGLAS_ANTES_DEL_EJEMPLO = 3;
    private static final int ALTO_EJEMPLO = 150;

    private static final String[][] CONTROLES = {
        {"PEDIR (HIT):", "Recibe otra carta."},
        {"PLANTARSE (STAND):", "Mantén tu mano actual."},
        {"DOBLAR (DOUBLE):", "Duplica tu apuesta y recibe solo una carta más."},
        {"DIVIDIR (SPLIT):", "Si tienes dos cartas iguales, divídelas en dos manos independientes (apuesta adicional requerida)."}
    };

    public PanelGuia(final Runnable alCerrar) {
        super(1066, 508, true);
        setTitulo("GUÍA DE JUEGO", 36, 32, new Color(0xF8EEDA), new Color(0xD8C39C));

        TextoReglas reglas = new TextoReglas();
        reglas.setBounds(24, 98, 398, 318);
        add(reglas);

        BotonMetalico cerrar = new BotonMetalico("CERRAR VENTANA", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(21).ubicar(443, 438, 180, 46);
        cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                alCerrar.run();
            }
        });
        add(cerrar);
    }

    // ---------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);   // marco + título
        Graphics2D g = Estilo.suavizar(g0);
        int w = getWidth() - 6;

        // Subtítulo y cabecera izquierda
        Font fSub = Estilo.fuente(Font.PLAIN, 27);
        String sub = "CÓMO JUGAR AL BLACKJACK";
        FontMetrics fm = g.getFontMetrics(fSub);
        Estilo.dibujarTextoSombra(g, sub, fSub, Estilo.CREMA, (w - fm.stringWidth(sub)) / 2, 74);

        // Paneles
        panelOscuro(g, 19, 92, 408, 332);
        panelOscuro(g, 439, 92, 601, 332);

        Font fCab = Estilo.fuente(Font.BOLD, 25);
        String t1 = "REGLAS BÁSICAS";
        Estilo.dibujarTextoSombra(g, t1, fCab, new Color(0xE9D9B8), 19 + (408 - g.getFontMetrics(fCab).stringWidth(t1)) / 2, 88);
        String t2 = "CONTROLES Y ACCIONES";
        Estilo.dibujarTextoSombra(g, t2, fCab, new Color(0xE9D9B8), 439 + (601 - g.getFontMetrics(fCab).stringWidth(t2)) / 2, 128);

        // Las 4 tarjetas de controles (2 x 2), todas con el mismo tamaño de letra
        float tam = tamanoControles(g, 274 - 106 - 8, 130 - 14);
        for (int i = 0; i < 4; i++) {
            int columna = i % 2;
            int fila = i / 2;
            int x = 453 + columna * 288;
            int y = 140 + fila * 140;
            tarjetaControl(g, i, x, y, 274, 130, tam);
        }
        g.dispose();
    }

    private void panelOscuro(Graphics2D g, int x, int y, int w, int h) {
        RoundRectangle2D forma = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g.setColor(new Color(52, 25, 14, 200));
        g.fill(forma);
        g.setStroke(new BasicStroke(1.6f));
        g.setColor(new Color(0xB08A5C));
        g.draw(forma);
    }

    // Mayor tamaño de letra con el que las 4 tarjetas caben en su espacio.
    private float tamanoControles(Graphics2D g, int ancho, int altoMax) {
        for (float tam = 25; tam > 12; tam -= 1) {
            boolean caben = true;
            for (int i = 0; i < 4; i++) {
                if (altoControl(g, i, tam, ancho) > altoMax) {
                    caben = false;
                }
            }
            if (caben) {
                return tam;
            }
        }
        return 12;
    }

    private int altoControl(Graphics2D g, int indice, float tam, int ancho) {
        return Texto.alto(g, CONTROLES[indice][0], Estilo.fuente(Font.BOLD, tam), ancho)
                + Texto.alto(g, CONTROLES[indice][1], Estilo.fuente(Font.PLAIN, tam - 1), ancho);
    }

    private void tarjetaControl(Graphics2D g, int indice, int x, int y, int w, int h, float tam) {
        RoundRectangle2D forma = new RoundRectangle2D.Float(x, y, w, h, 14, 14);
        g.setColor(new Color(40, 19, 10, 225));
        g.fill(forma);

        dibujarIcono(g, indice, x + 10, y + (h - 84) / 2, 84);

        int xt = x + 106;
        int ancho = w - 106 - 8;
        Font ft = Estilo.fuente(Font.BOLD, tam);
        Font fd = Estilo.fuente(Font.PLAIN, tam - 1);
        int altoTitulo = Texto.alto(g, CONTROLES[indice][0], ft, ancho);
        int alto = altoTitulo + Texto.alto(g, CONTROLES[indice][1], fd, ancho);
        int yy = y + (h - alto) / 2;
        Texto.dibujar(g, CONTROLES[indice][0], ft, new Color(0xE8B96A), xt, yy, ancho);
        Texto.dibujar(g, CONTROLES[indice][1], fd, Estilo.CREMA, xt, yy + altoTitulo, ancho);
    }

    // ---------------------------------------------------------------
    // ICONOS DE LOS CONTROLES
    // ---------------------------------------------------------------
    private static void dibujarIcono(Graphics2D g0, int tipo, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        RoundRectangle2D baldosa = new RoundRectangle2D.Float(x, y, s, s, 20, 20);
        g.setPaint(new GradientPaint(0, y, new Color(0x8E5536), 0, y + s, new Color(0x573020)));
        g.fill(baldosa);
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(0xD3B084));
        g.draw(baldosa);

        double cx = x + s / 2.0;
        double cy = y + s / 2.0;
        if (tipo == 0) {          // PEDIR: dos cartas
            carta(g, new Carta("8", "Corazones"), cx - 9, cy + 2, -0.25, 38, 54);
            carta(g, new Carta("A", "Picas"), cx + 7, cy, 0.14, 38, 54);
        } else if (tipo == 1) {   // PLANTARSE: mano abierta
            mano(g, cx, cy);
        } else if (tipo == 2) {   // DOBLAR: ficha
            ficha(g, cx, cy, 31);
        } else {                  // DIVIDIR: dos cartas que se separan
            carta(g, new Carta("A", "Picas"), cx - 14, cy + 2, -0.3, 36, 51);
            carta(g, new Carta("A", "Corazones"), cx + 14, cy + 2, 0.3, 36, 51);
        }
        g.dispose();
    }

    // Carta centrada en (cx,cy), girada "ang" radianes.
    private static void carta(Graphics2D g0, Carta c, double cx, double cy, double ang, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        g.rotate(ang, cx, cy);
        DibujoCarta.dibujarCarta(g, c, (int) Math.round(cx - w / 2.0), (int) Math.round(cy - h / 2.0), w, h);
        g.dispose();
    }

    private static void mano(Graphics2D g0, double cx, double cy) {
        Graphics2D g = (Graphics2D) g0.create();
        Color piel = new Color(0xF1DDBF);
        Color borde = new Color(0x7A4E32);
        g.setStroke(new BasicStroke(1.6f));
        // pulgar
        Graphics2D t = (Graphics2D) g.create();
        t.rotate(-0.75, cx - 15, cy + 8);
        figura(t, new RoundRectangle2D.Double(cx - 21, cy - 2, 10, 24, 10, 10), piel, borde);
        t.dispose();
        // dedos
        double[] altos = {24, 30, 28, 21};
        for (int i = 0; i < 4; i++) {
            figura(g, new RoundRectangle2D.Double(cx - 17 + i * 9, cy + 6 - altos[i], 8.5, altos[i] + 8, 8, 8), piel, borde);
        }
        // palma
        figura(g, new RoundRectangle2D.Double(cx - 18, cy + 2, 36, 28, 14, 14), piel, borde);
        g.dispose();
    }

    private static void figura(Graphics2D g, java.awt.Shape s, Color relleno, Color borde) {
        g.setColor(relleno);
        g.fill(s);
        g.setColor(borde);
        g.draw(s);
    }

    private static void ficha(Graphics2D g0, double cx, double cy, double r) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setColor(new Color(0xE9C97F));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        // marcas del borde
        g.setColor(new Color(0x6B3A1E));
        for (int i = 0; i < 8; i++) {
            Graphics2D m = (Graphics2D) g.create();
            m.rotate(Math.toRadians(i * 45), cx, cy);
            m.fill(new RoundRectangle2D.Double(cx - 4.5, cy - r - 1, 9, 11, 3, 3));
            m.dispose();
        }
        g.setColor(new Color(0xA8702F));
        g.fill(new Ellipse2D.Double(cx - r * 0.66, cy - r * 0.66, r * 1.32, r * 1.32));
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(0xF3E2B3));
        g.draw(new Ellipse2D.Double(cx - r * 0.5, cy - r * 0.5, r, r));
        g.setColor(new Color(0xE9C97F));
        g.fill(new Ellipse2D.Double(cx - r * 0.26, cy - r * 0.26, r * 0.52, r * 0.52));
        g.dispose();
    }

    // ---------------------------------------------------------------
    // REGLAS (zona con scroll) + EJEMPLO DE MANO
    // ---------------------------------------------------------------
    private static class TextoReglas extends VistaDesplazable {

        private static final int SANGRIA = 34;

        @Override
        protected int medir(Graphics2D g, int ancho) {
            return recorrer(g, ancho, false);
        }

        @Override
        protected void pintar(Graphics2D g, int ancho, int altoVisible) {
            recorrer(g, ancho, true);
        }

        // Mide (dibujar=false) o dibuja (true) todo el contenido; devuelve su alto.
        private int recorrer(Graphics2D g, int ancho, boolean dibujar) {
            Font fTexto = Estilo.fuente(Font.PLAIN, 24);
            Font fNum = Estilo.fuente(Font.BOLD, 24);
            int y = 8;
            for (int i = 0; i < REGLAS.length; i++) {
                if (i == REGLAS_ANTES_DEL_EJEMPLO) {
                    if (dibujar) {
                        ejemplo(g, 6, y, ancho - 10, ALTO_EJEMPLO);
                    }
                    y += ALTO_EJEMPLO + 12;
                }
                if (dibujar) {
                    Estilo.dibujarTextoSombra(g, (i + 1) + ".", fNum, Estilo.DORADO, 8,
                            y + g.getFontMetrics(fNum).getAscent());
                }
                int alto = dibujar
                        ? Texto.dibujar(g, REGLAS[i], fTexto, Estilo.CREMA, SANGRIA, y, ancho - SANGRIA - 6)
                        : 0;
                y += Texto.alto(g, REGLAS[i], fTexto, ancho - SANGRIA - 6) + 10;
            }
            return y + 4;
        }

        private void ejemplo(Graphics2D g, int x, int y, int w, int h) {
            RoundRectangle2D caja = new RoundRectangle2D.Float(x, y, w, h, 14, 14);
            g.setColor(new Color(0x4A2616));
            g.fill(caja);
            g.setStroke(new BasicStroke(1.6f));
            g.setColor(new Color(0xB08A5C));
            g.draw(caja);

            int zona = h - 34;
            dibujarManoEjemplo(g, x + w / 4, y + 8, Arrays.asList(new Carta("A", "Diamantes"), new Carta("5", "Picas")), "16");
            dibujarManoEjemplo(g, x + 3 * w / 4, y + 8, Arrays.asList(new Carta("K", "Corazones"), new Carta("9", "Treboles")), "19");
            ficha(g, x + w / 2.0, y + 8 + zona / 2.0 + 6, 17);

            // Franja con el pie de foto
            Graphics2D c = (Graphics2D) g.create();
            c.clip(caja);
            c.setColor(new Color(0x2E160C));
            c.fillRect(x, y + h - 32, w, 32);
            c.dispose();
            Font f = Estilo.fuente(Font.PLAIN, 21);
            String pie = "Ejemplo de mano";
            FontMetrics fm = g.getFontMetrics(f);
            Estilo.dibujarTextoSombra(g, pie, f, Estilo.CREMA, x + (w - fm.stringWidth(pie)) / 2,
                    y + h - 10);
        }

        private void dibujarManoEjemplo(Graphics2D g, int centroX, int y, List<Carta> cartas, String puntos) {
            // etiqueta con el puntaje
            Font f = Estilo.fuente(Font.BOLD, 21);
            FontMetrics fm = g.getFontMetrics(f);
            int ancho = Math.max(40, fm.stringWidth(puntos) + 18);
            RoundRectangle2D pastilla = new RoundRectangle2D.Float(centroX - ancho / 2f, y, ancho, 26, 10, 10);
            g.setColor(new Color(0x2E160C));
            g.fill(pastilla);
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(new Color(0xB08A5C));
            g.draw(pastilla);
            Estilo.dibujarTextoSombra(g, puntos, f, Estilo.CREMA, centroX - fm.stringWidth(puntos) / 2,
                    y + (26 - fm.getHeight()) / 2 + fm.getAscent());
            DibujoCarta.dibujarFila(g, cartas, 2, -1, centroX, y + 32, 48, 68, 30, 100);
        }
    }
}
