package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.FocusAdapter;
import java.io.File;
import java.awt.event.FocusEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

// Colores, fuentes y funciones de dibujo que comparte TODA la interfaz.
// Aquí se define el "look" del juego: fieltro verde, madera, dorado y metal.
public final class Estilo {

    private Estilo() {
    }

    // Tamaño fijo de la ventana (misma proporción 16:9 de los bocetos).
    public static final int ANCHO = 1280;
    public static final int ALTO = 760;
    public static final int ALTO_BARRA = 92;

    // ---------------------------------------------------------------
    // COLORES
    // ---------------------------------------------------------------
    public static final Color VERDE_CENTRO = new Color(0x34805A);
    public static final Color VERDE_BORDE = new Color(0x1B5236);

    public static final Color MADERA_CLARA = new Color(0x7C4029);
    public static final Color MADERA = new Color(0x6A3523);

    public static final Color DORADO_CLARO = new Color(0xF4D98A);
    public static final Color DORADO = new Color(0xE2B65A);
    public static final Color DORADO_OSCURO = new Color(0xA8782C);

    public static final Color CREMA = new Color(0xF3E6CB);
    public static final Color TEXTO = new Color(0xD9D6CC);
    public static final Color TEXTO_SUAVE = new Color(0xB9BFB2);

    public static final Color ROJO = new Color(0xE0584F);
    public static final Color VERDE_GANA = new Color(0x7FCB7A);

    // ---------------------------------------------------------------
    // FUENTES
    // ---------------------------------------------------------------
    // Se usa la primera fuente "condensada" que tenga el computador.
    // Si quieres el look exacto de los bocetos, instala "Oswald" o
    // "Roboto Condensed" (gratis en Google Fonts).
    private static final String[] CONDENSADAS = {
        "Bahnschrift SemiBold Condensed", "Bahnschrift Condensed", "Oswald",
        "Roboto Condensed", "Arial Narrow", "DejaVu Sans Condensed", "Dialog"
    };
    private static final String[] SCRIPT = {
        "Segoe Script", "Brush Script MT", "Monotype Corsiva",
        "Lucida Calligraphy", "URW Chancery L", "Serif"
    };

    // Si pones archivos .ttf u .otf en una carpeta "fuentes" junto al proyecto
    // (por ejemplo Oswald o Roboto Condensed de Google Fonts), el juego los usa solo.
    private static final String FAMILIA_INCLUIDA = cargarFuentesIncluidas();
    private static final String FAMILIA = FAMILIA_INCLUIDA != null
            ? FAMILIA_INCLUIDA : elegirFamilia(CONDENSADAS);
    private static final String FAMILIA_SCRIPT = elegirFamilia(SCRIPT);

    private static String cargarFuentesIncluidas() {
        File[] archivos = new File("fuentes").listFiles();
        if (archivos == null) {
            return null;
        }
        Arrays.sort(archivos);
        String primera = null;
        for (File archivo : archivos) {
            String nombre = archivo.getName().toLowerCase();
            if (nombre.endsWith(".ttf") || nombre.endsWith(".otf")) {
                try {
                    Font f = Font.createFont(Font.TRUETYPE_FONT, archivo);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                    if (primera == null) {
                        primera = f.getFamily();
                    }
                } catch (Exception e) {
                    // archivo de fuente dañado: se ignora
                }
            }
        }
        return primera;
    }

    private static String elegirFamilia(String[] candidatas) {
        Set<String> disponibles = new HashSet<String>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String nombre : candidatas) {
            if (disponibles.contains(nombre)) {
                return nombre;
            }
        }
        return candidatas[candidatas.length - 1];
    }

    // estilo: Font.PLAIN o Font.BOLD
    public static Font fuente(int estilo, float tamano) {
        return new Font(FAMILIA, estilo, 12).deriveFont(tamano);
    }

    // Mayor tamaño (hasta tamMax) con el que "texto" cabe en anchoMax píxeles.
    // Sirve para que los textos no se salgan de sus cuadros con fuentes anchas.
    public static float tamanoQueCabe(Graphics2D g, int estilo, float tamMax, int anchoMax, String texto) {
        float tam = tamMax;
        while (tam > 12 && g.getFontMetrics(fuente(estilo, tam)).stringWidth(texto) > anchoMax) {
            tam -= 1;
        }
        return tam;
    }

    public static Font fuenteScript(float tamano) {
        int estilo = FAMILIA_SCRIPT.equals("Serif") ? Font.ITALIC : Font.PLAIN;
        return new Font(FAMILIA_SCRIPT, estilo, 12).deriveFont(tamano);
    }

    // ---------------------------------------------------------------
    // UTILIDADES DE DIBUJO
    // ---------------------------------------------------------------

    // Activa el suavizado de bordes y texto en un Graphics2D.
    public static void suavizado(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    // Copia del Graphics con suavizado (acuérdate de llamar dispose()).
    public static Graphics2D suavizar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        suavizado(g2);
        return g2;
    }

    public static Color conAlfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alfa);
    }

    public static Color iluminar(Color c, int cantidad) {
        return new Color(Math.min(255, c.getRed() + cantidad),
                Math.min(255, c.getGreen() + cantidad),
                Math.min(255, c.getBlue() + cantidad),
                c.getAlpha());
    }

    // Texto liso con una sombra suave debajo.
    public static void dibujarTextoSombra(Graphics2D g, String texto, Font fuente, Color color, int x, int y) {
        g.setFont(fuente);
        g.setColor(new Color(0, 0, 0, 130));
        g.drawString(texto, x + 1, y + 2);
        g.setColor(color);
        g.drawString(texto, x, y);
    }

    // Texto con degradado vertical (dorado, madera clara...), sombra y contorno fino.
    public static void dibujarTextoDegradado(Graphics2D g, String texto, Font fuente, int x, int y,
                                             Color arriba, Color abajo, int sombra) {
        GlyphVector gv = fuente.createGlyphVector(g.getFontRenderContext(), texto);
        Shape forma = gv.getOutline(x, y);
        FontMetrics fm = g.getFontMetrics(fuente);

        if (sombra > 0) {
            AffineTransform mover = AffineTransform.getTranslateInstance(sombra * 0.6, sombra);
            g.setColor(new Color(0, 0, 0, 150));
            g.fill(mover.createTransformedShape(forma));
        }
        g.setPaint(new GradientPaint(0, y - fm.getAscent() * 0.8f, arriba, 0, y, abajo));
        g.fill(forma);
        g.setStroke(new BasicStroke(1.2f));
        g.setColor(new Color(0, 0, 0, 90));
        g.draw(forma);
    }

    // Título grande dorado centrado en centroX (menú principal).
    public static void dibujarTituloDorado(Graphics2D g, String texto, int centroX, int baseline, float tamano) {
        Font f = fuente(Font.BOLD, tamano);
        int ancho = g.getFontMetrics(f).stringWidth(texto);
        dibujarTextoDegradado(g, texto, f, centroX - ancho / 2, baseline,
                new Color(0xF6E19A), new Color(0xB4822E), 4);
    }

    // Logo "BLACKJACK Royale" de la barra de madera.
    public static void dibujarLogo(Graphics2D g, int x, int baseline) {
        Font f1 = fuente(Font.BOLD, 50);
        dibujarTextoDegradado(g, "BLACKJACK", f1, x, baseline,
                new Color(0xE9C79D), new Color(0xA67650), 2);
        int ancho = g.getFontMetrics(f1).stringWidth("BLACKJACK");
        Font f2 = fuenteScript(64);
        dibujarTextoDegradado(g, "Royale", f2, x + ancho + 14, baseline + 4,
                new Color(0xEFD2A8), new Color(0xB98A5C), 2);
    }

    // ---------------------------------------------------------------
    // FIELTRO
    // ---------------------------------------------------------------
    private static BufferedImage texturaFieltro;

    private static BufferedImage getTexturaFieltro() {
        if (texturaFieltro == null) {
            int n = 256;
            BufferedImage img = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
            Random r = new Random(7);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    int alfa = r.nextInt(30);
                    int gris = r.nextBoolean() ? 0 : 255;
                    if (gris == 255) {
                        alfa = alfa * 2 / 3;
                    }
                    img.setRGB(x, y, (alfa << 24) | (gris << 16) | (gris << 8) | gris);
                }
            }
            texturaFieltro = img;
        }
        return texturaFieltro;
    }

    public static void dibujarFieltro(Graphics2D g, int w, int h) {
        g.setPaint(new RadialGradientPaint(new Point2D.Float(w / 2f, h / 2f), Math.max(w, h) * 0.72f,
                new float[]{0f, 1f}, new Color[]{VERDE_CENTRO, VERDE_BORDE}));
        g.fillRect(0, 0, w, h);
        g.setPaint(new TexturePaint(getTexturaFieltro(), new Rectangle(0, 0, 256, 256)));
        g.fillRect(0, 0, w, h);
    }

    // ---------------------------------------------------------------
    // MADERA
    // ---------------------------------------------------------------

    // Rellena cualquier forma (rectángulo, rectángulo redondeado...) con madera.
    public static void dibujarMadera(Graphics2D g0, Shape forma) {
        Graphics2D g = (Graphics2D) g0.create();
        suavizado(g);
        Rectangle b = forma.getBounds();
        g.clip(forma);

        g.setPaint(new GradientPaint(0, b.y, MADERA_CLARA, 0, b.y + b.height, MADERA));
        g.fill(b);

        // Vetas: líneas casi horizontales y ligeramente onduladas.
        Random r = new Random(11 + b.height);
        int lineas = Math.max(12, b.height / 3);
        for (int i = 0; i < lineas; i++) {
            float y0 = b.y + r.nextFloat() * b.height;
            Path2D.Float veta = new Path2D.Float();
            veta.moveTo(b.x, y0);
            float x = b.x;
            while (x < b.x + b.width) {
                x += 30 + r.nextInt(70);
                veta.lineTo(x, y0 + (r.nextFloat() - 0.5f) * 5f);
            }
            if (r.nextBoolean()) {
                g.setColor(new Color(0, 0, 0, 18 + r.nextInt(30)));
            } else {
                g.setColor(new Color(255, 205, 165, 8 + r.nextInt(16)));
            }
            g.setStroke(new BasicStroke(0.8f + r.nextFloat() * 0.9f));
            g.draw(veta);
        }
        g.dispose();
    }

    // Barra de madera superior con el logo.
    public static void dibujarBarra(Graphics2D g, int w) {
        dibujarMadera(g, new Rectangle(0, 0, w, ALTO_BARRA));
        g.setColor(new Color(30, 12, 6, 200));
        g.fillRect(0, ALTO_BARRA - 3, w, 3);
        g.setPaint(new GradientPaint(0, ALTO_BARRA, new Color(0, 0, 0, 120),
                0, ALTO_BARRA + 16, new Color(0, 0, 0, 0)));
        g.fillRect(0, ALTO_BARRA, w, 16);
        dibujarLogo(g, 34, 68);
    }

    // Panel de madera con esquinas redondeadas y borde (cuadros de la mesa y ventanas).
    public static void dibujarPanelMadera(Graphics2D g, int x, int y, int w, int h, int arco) {
        RoundRectangle2D forma = new RoundRectangle2D.Float(x, y, w, h, arco, arco);
        g.setColor(new Color(0, 0, 0, 90));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 5, w, h, arco, arco));
        dibujarMadera(g, forma);
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(0x2E160D));
        g.draw(forma);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 220, 180, 40));
        g.draw(new RoundRectangle2D.Float(x + 2, y + 2, w - 4, h - 4, arco - 2, arco - 2));
    }

    // ---------------------------------------------------------------
    // CAMPOS DE TEXTO
    // ---------------------------------------------------------------
    public static void configurarCampo(final JTextField campo) {
        campo.setOpaque(false);
        campo.setFont(fuente(Font.PLAIN, 24));
        campo.setForeground(CREMA);
        campo.setCaretColor(DORADO_CLARO);
        campo.setSelectionColor(DORADO_OSCURO);
        campo.setSelectedTextColor(Color.WHITE);
        campo.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.repaint();
            }
        });
    }

    public static void pintarFondoCampo(Graphics g0, JTextField campo) {
        Graphics2D g = suavizar(g0);
        int w = campo.getWidth();
        int h = campo.getHeight();
        RoundRectangle2D forma = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, 14, 14);
        g.setPaint(new GradientPaint(0, 0, new Color(28, 32, 28, 235), 0, h, new Color(54, 60, 54, 235)));
        g.fill(forma);
        g.setStroke(new BasicStroke(2f));
        g.setColor(campo.hasFocus() ? DORADO_OSCURO : new Color(0x62543F));
        g.draw(forma);
        g.dispose();
    }

    // Atenúa lo que se dibuje después (botones deshabilitados).
    // Ficha de casino redonda: color según el valor, rayas blancas y el número en el centro.
    public static void dibujarFicha(Graphics2D g, int x, int y, int tam, int valor) {
        Color base;
        if (valor >= 100) {
            base = new Color(0x2A2A2A);
        } else if (valor >= 50) {
            base = new Color(0xD9822B);
        } else if (valor >= 25) {
            base = new Color(0x23894A);
        } else if (valor >= 10) {
            base = new Color(0x2557A8);
        } else if (valor >= 5) {
            base = new Color(0xB8282E);
        } else {
            base = new Color(0x8C8C8C);
        }

        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(x + 2, y + 3, tam, tam);
        g.setColor(base);
        g.fillOval(x, y, tam, tam);

        g.setColor(Color.WHITE);
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
        Font f = fuente(Font.BOLD, Math.max(9, tam / (texto.length() > 2 ? 4 : 3)));
        FontMetrics fm = g.getFontMetrics(f);
        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString(texto, x + (tam - fm.stringWidth(texto)) / 2,
                y + (tam + fm.getAscent() - fm.getDescent()) / 2);
    }

    public static void atenuar(Graphics2D g, float opacidad) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
    }
}
