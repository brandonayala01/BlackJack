package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

// Botón dibujado a mano (sin imágenes) con el aspecto de los bocetos.
//  - Tipo.METAL  : gris metálico con texto gris claro (JUGAR, INGRESAR, HIT...)
//  - Tipo.DORADO : igual pero con texto dorado (REGISTRARSE)
//  - Tipo.MADERA : madera con texto crema (ventanas: SIGUIENTE RONDA, CERRAR...)
// Para texto en dos líneas (HIT / (PEDIR)) separa con "\n":  new BotonMetalico("HIT\n(PEDIR)")
public class BotonMetalico extends JButton {

    public enum Tipo {
        METAL, DORADO, MADERA
    }

    private static final int ARCO = 16;
    private static final int GROSOR_BASE = 5;

    private final Tipo tipo;
    private float tamanoTexto = 28f;

    public BotonMetalico(String texto) {
        this(texto, Tipo.METAL);
    }

    public BotonMetalico(String texto, Tipo tipo) {
        super(texto);
        this.tipo = tipo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Métodos "encadenables" para crear botones en una sola línea.
    public BotonMetalico tamanoTexto(float tamano) {
        this.tamanoTexto = tamano;
        repaint();
        return this;
    }

    public BotonMetalico ubicar(int x, int y, int ancho, int alto) {
        setBounds(x, y, ancho, alto);
        return this;
    }

    private String[] lineas() {
        String t = getText();
        if (t == null) {
            return new String[]{""};
        }
        return t.split("\n");
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        String[] ls = lineas();
        FontMetrics fm = getFontMetrics(Estilo.fuente(Font.BOLD, tamanoTexto));
        int ancho = 0;
        for (String l : ls) {
            ancho = Math.max(ancho, fm.stringWidth(l));
        }
        return new Dimension(ancho + 70, ls.length > 1 ? 66 : 54);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        int w = getWidth();
        int h = getHeight();
        boolean activo = isEnabled();
        boolean sobre = activo && getModel().isRollover();
        boolean presionado = activo && getModel().isArmed() && getModel().isPressed();
        int dy = presionado ? 2 : 0;

        if (!activo) {
            Estilo.atenuar(g, 0.45f);
        }

        // 1) Sombra proyectada
        g.setColor(new Color(0, 0, 0, 80));
        g.fill(new RoundRectangle2D.Float(2, 6, w - 4, h - 7, ARCO, ARCO));

        // 2) Base plateada (se ve solo la franja inferior, como en los bocetos)
        g.setPaint(new GradientPaint(0, h / 2f, new Color(0x6A6A6A), 0, h - 2, new Color(0xE6E6E6)));
        g.fill(new RoundRectangle2D.Float(0, 4, w, h - 6, ARCO, ARCO));

        // 3) Cuerpo del botón
        int altoCuerpo = h - GROSOR_BASE - 3;
        Shape cuerpo = new RoundRectangle2D.Float(1, 1 + dy, w - 2, altoCuerpo, ARCO, ARCO);

        Color arriba;
        Color abajo;
        Color borde;
        if (tipo == Tipo.MADERA) {
            arriba = new Color(0x9A5C3A);
            abajo = new Color(0x5E3120);
            borde = new Color(0xD7B68C);
        } else {
            arriba = new Color(0x6C6C6C);
            abajo = new Color(0x303030);
            borde = new Color(0xB8B8B8);
        }
        if (sobre) {
            arriba = Estilo.iluminar(arriba, 20);
            abajo = Estilo.iluminar(abajo, 14);
        }
        g.setPaint(new GradientPaint(0, 1 + dy, arriba, 0, 1 + dy + altoCuerpo, abajo));
        g.fill(cuerpo);

        // Brillo en la mitad superior
        Shape clipAnterior = g.getClip();
        g.clip(cuerpo);
        g.setPaint(new GradientPaint(0, 1 + dy, new Color(255, 255, 255, 60),
                0, 1 + dy + altoCuerpo / 2f, new Color(255, 255, 255, 0)));
        g.fillRect(0, 1 + dy, w, altoCuerpo / 2);
        g.setClip(clipAnterior);

        g.setStroke(new BasicStroke(2f));
        g.setColor(borde);
        g.draw(cuerpo);

        // 4) Texto
        dibujarTexto(g, w, 1 + dy, altoCuerpo, sobre);
        g.dispose();
    }

    private void dibujarTexto(Graphics2D g, int w, int y0, int altoCuerpo, boolean sobre) {
        String[] ls = lineas();
        int maxAncho = w - 22;

        float tam1 = tamanoTexto;
        Font f1 = Estilo.fuente(Font.BOLD, tam1);
        while (tam1 > 9 && g.getFontMetrics(f1).stringWidth(ls[0]) > maxAncho) {
            tam1 -= 1;
            f1 = Estilo.fuente(Font.BOLD, tam1);
        }
        FontMetrics fm1 = g.getFontMetrics(f1);

        Font f2 = null;
        FontMetrics fm2 = null;
        if (ls.length > 1) {
            float tam2 = tam1 * 0.78f;
            f2 = Estilo.fuente(Font.PLAIN, tam2);
            while (tam2 > 8 && g.getFontMetrics(f2).stringWidth(ls[1]) > maxAncho) {
                tam2 -= 1;
                f2 = Estilo.fuente(Font.PLAIN, tam2);
            }
            fm2 = g.getFontMetrics(f2);
        }

        int bloque = fm1.getAscent() + fm1.getDescent();
        if (fm2 != null) {
            bloque += (int) (fm2.getHeight() * 0.85);
        }
        int base1 = y0 + (altoCuerpo - bloque) / 2 + fm1.getAscent();
        int x1 = (w - fm1.stringWidth(ls[0])) / 2;

        if (tipo == Tipo.DORADO) {
            Estilo.dibujarTextoDegradado(g, ls[0], f1, x1, base1,
                    new Color(0xF3D58E), new Color(0xC08F3A), 2);
        } else {
            Color c = (tipo == Tipo.MADERA) ? Estilo.CREMA : new Color(0xD8D8D2);
            if (sobre) {
                c = Estilo.iluminar(c, 18);
            }
            Estilo.dibujarTextoSombra(g, ls[0], f1, c, x1, base1);
        }

        if (fm2 != null) {
            int base2 = base1 + (int) (fm2.getHeight() * 0.85);
            int x2 = (w - fm2.stringWidth(ls[1])) / 2;
            Color c2 = (tipo == Tipo.MADERA) ? Estilo.CREMA : new Color(0xC9C9C2);
            Estilo.dibujarTextoSombra(g, ls[1], f2, c2, x2, base2);
        }
    }
}