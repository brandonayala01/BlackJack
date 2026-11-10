package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;


public final class DibujoAvatar {

    private DibujoAvatar() {
    }

    private static final int CORTO = 0;
    private static final int LARGO = 1;
    private static final int BARBA = 2;
    private static final int RIZADO = 3;
    private static final int MONO = 4;
    private static final int GORRA = 5;


    private static final int[][] DATOS = {
        {0x7C6CE0, 0x2E2A78, 0xE8B48A, 0x2B2018, 0x23314F, CORTO, 0},
        {0xE08B6C, 0x7A2F3A, 0xF2CBA6, 0x7A3B1E, 0xB43E5C, LARGO, 0},
        {0x4FB3A5, 0x145A58, 0x8D5A3B, 0x141414, 0xE6E0D0, BARBA, 0},
        {0xE0B24F, 0x7A4A12, 0x5E3A26, 0x151515, 0x3B7A4F, RIZADO, 0},
        {0x6CB4E0, 0x1F4C7A, 0xF0C8A0, 0xD8B45A, 0x6A4C93, MONO, 0},
        {0x8FB36C, 0x2F5A2A, 0xD9A27A, 0x3A2A1A, 0xC0562E, GORRA, 0x2B5FAE}
    };

    public static void dibujar(Graphics2D g0, int indice, double cx, double cy, double r) {
        if (AvatarImagenes.dibujar(g0, indice, cx, cy, r)) {
            return;
        }
        int[] d = DATOS[((indice % DATOS.length) + DATOS.length) % DATOS.length];
        Color piel = new Color(d[2]);
        Color pielSombra = piel.darker();
        Color pelo = new Color(d[3]);
        Color camisa = new Color(d[4]);
        int estilo = d[5];

        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        g.clip(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));

        
        g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy - r * 0.3), (float) (r * 1.5),
                new float[]{0f, 1f}, new Color[]{new Color(d[0]), new Color(d[1])}));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));

        
        if (estilo == LARGO) {
            g.setColor(pelo);
            g.fill(new RoundRectangle2D.Double(cx - 0.38 * r, cy - 0.5 * r, 0.76 * r, 1.05 * r, 0.7 * r, 0.7 * r));
        }

        
        g.setColor(camisa);
        g.fill(new Ellipse2D.Double(cx - 0.88 * r, cy + 0.42 * r, 1.76 * r, 1.2 * r));
        

        g.setColor(pielSombra);
        g.fill(new RoundRectangle2D.Double(cx - 0.14 * r, cy + 0.2 * r, 0.28 * r, 0.34 * r, 0.1 * r, 0.1 * r));
        Path2D.Double v = new Path2D.Double();
        v.moveTo(cx - 0.2 * r, cy + 0.47 * r);
        v.lineTo(cx, cy + 0.78 * r);
        v.lineTo(cx + 0.2 * r, cy + 0.47 * r);
        v.closePath();
        g.setColor(piel);
        g.fill(v);
        if (estilo == CORTO) {   // corbata
            g.setColor(new Color(0xB03A3A));
            Path2D.Double corbata = new Path2D.Double();
            corbata.moveTo(cx - 0.04 * r, cy + 0.55 * r);
            corbata.lineTo(cx + 0.04 * r, cy + 0.55 * r);
            corbata.lineTo(cx + 0.07 * r, cy + 0.95 * r);
            corbata.lineTo(cx, cy + 1.02 * r);
            corbata.lineTo(cx - 0.07 * r, cy + 0.95 * r);
            corbata.closePath();
            g.fill(corbata);
        }

        // Orejas y cabeza
        g.setColor(pielSombra);
        g.fill(new Ellipse2D.Double(cx - 0.35 * r, cy - 0.17 * r, 0.1 * r, 0.2 * r));
        g.fill(new Ellipse2D.Double(cx + 0.25 * r, cy - 0.17 * r, 0.1 * r, 0.2 * r));
        g.setColor(piel);
        g.fill(new Ellipse2D.Double(cx - 0.31 * r, cy - 0.5 * r, 0.62 * r, 0.76 * r));

        // Pelo / accesorios por estilo
        g.setColor(pelo);
        if (estilo == RIZADO) {
            for (int i = 0; i < 9; i++) {
                double ang = Math.toRadians(190 + i * 20);
                g.fill(new Ellipse2D.Double(cx + Math.cos(ang) * 0.36 * r - 0.14 * r,
                        cy - 0.14 * r + Math.sin(ang) * 0.4 * r - 0.14 * r, 0.28 * r, 0.28 * r));
            }
        }
        if (estilo == MONO) {
            g.fill(new Ellipse2D.Double(cx - 0.15 * r, cy - 0.72 * r, 0.3 * r, 0.3 * r));
        }
        if (estilo != GORRA) {
            
            Path2D.Double copete = new Path2D.Double();
            copete.moveTo(cx - 0.31 * r, cy - 0.08 * r);
            copete.curveTo(cx - 0.36 * r, cy - 0.48 * r, cx - 0.16 * r, cy - 0.57 * r,
                    cx + 0.02 * r, cy - 0.49 * r);
            copete.curveTo(cx + 0.24 * r, cy - 0.57 * r, cx + 0.36 * r, cy - 0.38 * r,
                    cx + 0.31 * r, cy - 0.08 * r);
            copete.curveTo(cx + 0.20 * r, cy - 0.20 * r, cx + 0.06 * r, cy - 0.16 * r,
                    cx - 0.02 * r, cy - 0.21 * r);
            copete.curveTo(cx - 0.11 * r, cy - 0.12 * r, cx - 0.22 * r, cy - 0.12 * r,
                    cx - 0.31 * r, cy - 0.08 * r);
            copete.closePath();
            g.fill(copete);
        }
        if (estilo == BARBA) {
            g.fill(new Arc2D.Double(cx - 0.32 * r, cy - 0.5 * r, 0.64 * r, 0.8 * r, 190, 160, Arc2D.CHORD));
            g.setColor(piel);   // boca despejada
            g.fill(new Ellipse2D.Double(cx - 0.1 * r, cy + 0.02 * r, 0.2 * r, 0.09 * r));
        }
        if (estilo == GORRA) {
            g.setColor(new Color(d[6]));
            g.fill(new Arc2D.Double(cx - 0.35 * r, cy - 0.76 * r, 0.7 * r, 0.5 * r, 0, 180, Arc2D.CHORD));
            g.fill(new RoundRectangle2D.Double(cx - 0.22 * r, cy - 0.33 * r, 0.62 * r, 0.07 * r, 0.07 * r, 0.07 * r));
            g.setColor(pelo);
            g.fill(new RoundRectangle2D.Double(cx - 0.34 * r, cy - 0.35 * r, 0.1 * r, 0.22 * r, 0.1 * r, 0.1 * r));
        }

        // Cara
        g.setColor(new Color(0x1F1A17));
        g.fill(new Ellipse2D.Double(cx - 0.15 * r, cy - 0.19 * r, 0.065 * r, 0.075 * r));
        g.fill(new Ellipse2D.Double(cx + 0.085 * r, cy - 0.19 * r, 0.065 * r, 0.075 * r));
        g.setStroke(new BasicStroke((float) Math.max(1.5, r * 0.028), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(estilo == BARBA || estilo == CORTO ? pelo : pelo.darker());
        g.draw(new Arc2D.Double(cx - 0.17 * r, cy - 0.27 * r, 0.12 * r, 0.06 * r, 20, 140, Arc2D.OPEN));
        g.draw(new Arc2D.Double(cx + 0.05 * r, cy - 0.27 * r, 0.12 * r, 0.06 * r, 20, 140, Arc2D.OPEN));
        g.setColor(pielSombra);
        g.draw(new Arc2D.Double(cx - 0.04 * r, cy - 0.1 * r, 0.08 * r, 0.1 * r, 200, 140, Arc2D.OPEN));
        g.setColor(estilo == BARBA ? new Color(0x8E3B35) : new Color(0xA34A42));
        g.draw(new Arc2D.Double(cx - 0.1 * r, cy - 0.02 * r, 0.2 * r, 0.12 * r, 205, 130, Arc2D.OPEN));
        g.dispose();
    }
}
