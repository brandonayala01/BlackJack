package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Carta;

// Dibuja las cartas con Java2D: no necesita imágenes y los símbolos
// (corazón, rombo, trébol, pica) se ven igual en cualquier computador.
public final class DibujoCarta {

    private DibujoCarta() {
    }

    // Tamaño normal de una carta (como en los bocetos).
    public static final int ANCHO = 94;
    public static final int ALTO = 133;

    private static final Color TINTA_ROJA = new Color(0xC62828);
    private static final Color TINTA_NEGRA = new Color(0x1C1C1C);

    private static final int CORAZONES = 0;
    private static final int DIAMANTES = 1;
    private static final int TREBOLES = 2;
    private static final int PICAS = 3;

    // Posición de cada símbolo en la carta: {columna (0,1,2), fila (0 arriba ... 1 abajo)}.
    // Índice 0 = carta de valor 2, ..., índice 8 = carta de valor 10.
    private static final double[][][] PIPS = {
        {{1, 0}, {1, 1}},
        {{1, 0}, {1, .5}, {1, 1}},
        {{0, 0}, {2, 0}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {1, .5}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {0, .5}, {2, .5}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {1, .25}, {0, .5}, {2, .5}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {1, .25}, {0, .5}, {2, .5}, {1, .75}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {0, 1 / 3.0}, {2, 1 / 3.0}, {1, .5}, {0, 2 / 3.0}, {2, 2 / 3.0}, {0, 1}, {2, 1}},
        {{0, 0}, {2, 0}, {1, 1 / 6.0}, {0, 1 / 3.0}, {2, 1 / 3.0}, {0, 2 / 3.0}, {2, 2 / 3.0}, {1, 5 / 6.0}, {0, 1}, {2, 1}}
    };

    // ---------------------------------------------------------------
    // SÍMBOLOS DE LOS PALOS (formas en una caja de 1 x 1)
    // ---------------------------------------------------------------
    private static final Map<Integer, Shape> FORMAS = new HashMap<Integer, Shape>();

    private static Path2D.Double corazon() {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(0.5, 0.95);
        p.curveTo(0.08, 0.62, 0.0, 0.30, 0.22, 0.12);
        p.curveTo(0.38, 0.0, 0.5, 0.12, 0.5, 0.26);
        p.curveTo(0.5, 0.12, 0.62, 0.0, 0.78, 0.12);
        p.curveTo(1.0, 0.30, 0.92, 0.62, 0.5, 0.95);
        p.closePath();
        return p;
    }

    private static Shape crearForma(int palo) {
        if (palo == CORAZONES) {
            return corazon();
        }
        if (palo == DIAMANTES) {
            Path2D.Double p = new Path2D.Double();
            p.moveTo(0.5, 0.02);
            p.lineTo(0.86, 0.5);
            p.lineTo(0.5, 0.98);
            p.lineTo(0.14, 0.5);
            p.closePath();
            return p;
        }
        if (palo == TREBOLES) {
            Area a = new Area(new Ellipse2D.Double(0.5 - 0.235, 0.27 - 0.235, 0.47, 0.47));
            a.add(new Area(new Ellipse2D.Double(0.27 - 0.235, 0.58 - 0.235, 0.47, 0.47)));
            a.add(new Area(new Ellipse2D.Double(0.73 - 0.235, 0.58 - 0.235, 0.47, 0.47)));
            a.add(new Area(new Ellipse2D.Double(0.5 - 0.17, 0.5 - 0.17, 0.34, 0.34)));
            Path2D.Double tallo = new Path2D.Double();
            tallo.moveTo(0.5, 0.52);
            tallo.lineTo(0.36, 0.98);
            tallo.lineTo(0.64, 0.98);
            tallo.closePath();
            a.add(new Area(tallo));
            return a;
        }
        // Pica: un corazón al revés con un tallo.
        AffineTransform voltear = new AffineTransform();
        voltear.translate(0, 0.85);
        voltear.scale(1, -0.85);
        Area a = new Area(voltear.createTransformedShape(corazon()));
        Path2D.Double tallo = new Path2D.Double();
        tallo.moveTo(0.5, 0.62);
        tallo.lineTo(0.33, 0.98);
        tallo.lineTo(0.67, 0.98);
        tallo.closePath();
        a.add(new Area(tallo));
        return a;
    }

    private static Shape forma(int palo) {
        Shape f = FORMAS.get(palo);
        if (f == null) {
            f = crearForma(palo);
            FORMAS.put(palo, f);
        }
        return f;
    }

    private static void dibujarPalo(Graphics2D g, int palo, double cx, double cy, double tam,
                                    boolean invertido) {
        AffineTransform at = new AffineTransform();
        at.translate(cx, cy);
        if (invertido) {
            at.rotate(Math.PI);
        }
        at.scale(tam, tam);
        at.translate(-0.5, -0.5);
        g.fill(at.createTransformedShape(forma(palo)));
    }

    private static int indicePalo(Carta carta) {
        String palo = carta.getPalo();
        if (palo.equals("Corazones")) {
            return CORAZONES;
        }
        if (palo.equals("Diamantes")) {
            return DIAMANTES;
        }
        if (palo.equals("Treboles")) {
            return TREBOLES;
        }
        return PICAS;
    }

    // ---------------------------------------------------------------
    // CARTA BOCA ARRIBA
    // ---------------------------------------------------------------
    public static void dibujarCarta(Graphics2D g0, Carta carta, int x, int y, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        int arco = Math.max(8, w / 7);

        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 4, w, h, arco, arco));

        RoundRectangle2D cuerpo = new RoundRectangle2D.Float(x, y, w, h, arco, arco);
        g.setColor(Color.WHITE);
        g.fill(cuerpo);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0xB5B5B5));
        g.draw(cuerpo);

        Color tinta = carta.esRoja() ? TINTA_ROJA : TINTA_NEGRA;
        int palo = indicePalo(carta);
        String valor = carta.getValor();
        g.setColor(tinta);

        // Esquinas: arriba a la izquierda y (girada 180°) abajo a la derecha.
        esquina(g, valor, palo, x, y, w, h);
        Graphics2D girado = (Graphics2D) g.create();
        girado.rotate(Math.PI, x + w / 2.0, y + h / 2.0);
        esquina(girado, valor, palo, x, y, w, h);
        girado.dispose();

        // Centro de la carta.
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;
        if (valor.equals("A")) {
            dibujarPalo(g, palo, cx, cy, w * 0.52, false);
        } else if (valor.equals("J") || valor.equals("Q") || valor.equals("K")) {
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(Estilo.conAlfa(tinta, 110));
            g.draw(new RoundRectangle2D.Float(x + w * 0.2f, y + h * 0.13f, w * 0.6f, h * 0.74f, 8, 8));
            g.setColor(tinta);
            dibujarPalo(g, palo, cx, y + h * 0.27, w * 0.2, false);
            dibujarPalo(g, palo, cx, y + h * 0.73, w * 0.2, true);
            Font f = new Font(Font.SERIF, Font.BOLD, Math.max(12, (int) (h * 0.34)));
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(valor, (float) (cx - fm.stringWidth(valor) / 2.0),
                    (float) (cy + fm.getAscent() * 0.36));
        } else {
            int numero = Integer.parseInt(valor);
            double[][] posiciones = PIPS[numero - 2];
            double[] columnas = {x + w * 0.31, cx, x + w * 0.69};
            double tam = w * 0.22;
            for (double[] pos : posiciones) {
                double px = columnas[(int) pos[0]];
                // Deja aire bajo los índices de las esquinas, sobre todo en 10.
                double py = y + h * (0.29 + 0.53 * pos[1]);
                dibujarPalo(g, palo, px, py, tam, pos[1] > 0.5);
            }
        }
        g.dispose();
    }

    // Valor + símbolo pequeño en la esquina superior izquierda.
    private static void esquina(Graphics2D g, String valor, int palo, int x, int y, int w, int h) {
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, (int) (w * 0.24)));
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int ancho = fm.stringWidth(valor);
        float bx = x + w * 0.08f;
        // Mantén el índice y el palo en dos zonas independientes. En valores de
        // dos caracteres (10) el símbolo ya no invade la línea del número.
        float base = y + h * 0.055f + fm.getAscent() * 0.78f;
        g.drawString(valor, bx, base);
        double tamPalo = Math.max(7, Math.min(w * 0.16, h * 0.075));
        dibujarPalo(g, palo, bx + ancho / 2.0, base + tamPalo * 0.92, tamPalo, false);
    }

    // ---------------------------------------------------------------
    // CARTA BOCA ABAJO Y HUECO VACÍO
    // ---------------------------------------------------------------
    public static void dibujarReverso(Graphics2D g0, int x, int y, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        int arco = Math.max(8, w / 7);

        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 4, w, h, arco, arco));
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, arco, arco));

        int m = Math.max(3, (int) (w * 0.055));
        RoundRectangle2D interior = new RoundRectangle2D.Float(x + m, y + m, w - 2 * m, h - 2 * m,
                arco - 3, arco - 3);
        g.setPaint(new GradientPaint(0, y, new Color(0x7B4A31), 0, y + h, new Color(0x58301F)));
        g.fill(interior);

        // Trama de rombos
        Graphics2D t = (Graphics2D) g.create();
        t.clip(interior);
        t.setColor(new Color(255, 215, 170, 34));
        t.setStroke(new BasicStroke(1f));
        for (int d = -h; d < w + h; d += 9) {
            t.drawLine(x + d, y, x + d + h, y + h);
            t.drawLine(x + d + h, y, x + d, y + h);
        }
        t.dispose();

        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 225, 190, 70));
        g.draw(interior);
        g.dispose();
    }

    // Contorno punteado que marca dónde irá una carta.
    public static void dibujarHueco(Graphics2D g0, int x, int y, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                new float[]{6f, 6f}, 0f));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, Math.max(8, w / 7), Math.max(8, w / 7)));
        g.dispose();
    }

    // ---------------------------------------------------------------
    // UNA MANO (varias cartas en fila, solapadas si no caben)
    // ---------------------------------------------------------------
    // cuantas: cuántas cartas mostrar (sirve para la animación del reparto).
    // indiceOculto: carta que va boca abajo (-1 si ninguna).
    // pasoMax: separación normal entre cartas; anchoMax: ancho máximo de todo el grupo.
    private static int paso(int n, int w, int pasoMax, int anchoMax) {
        if (n <= 1) {
            return 0;
        }
        return Math.max(Math.min(pasoMax, (anchoMax - w) / (n - 1)), w / 5);
    }

    // Ancho total que ocupa un grupo de n cartas (para saber dónde poner el puntaje).
    public static int anchoGrupo(int n, int w, int pasoMax, int anchoMax) {
        if (n <= 0) {
            return 0;
        }
        return w + (n - 1) * paso(n, w, pasoMax, anchoMax);
    }

    public static void dibujarFila(Graphics2D g, List<Carta> cartas, int cuantas, int indiceOculto,
                                int centroX, int y, int w, int h, int pasoMax, int anchoMax) {
        int n = Math.min(cuantas, cartas.size());
        if (n <= 0) {
            return;
        }
        int paso = paso(n, w, pasoMax, anchoMax);
        int total = w + (n - 1) * paso;
        int x0 = centroX - total / 2;
        for (int i = 0; i < n; i++) {
            if (i == indiceOculto) {
                dibujarReverso(g, x0 + i * paso, y, w, h);
            } else {
                dibujarCarta(g, cartas.get(i), x0 + i * paso, y, w, h);
            }
        }
    }
}
