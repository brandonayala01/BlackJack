package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;


public class VentanaModal extends JComponent {

    private final boolean translucida;

    private String titulo = "";
    private int tituloBase = 60;
    private float tituloTam = 50;
    private Color tituloArriba = Estilo.DORADO_CLARO;
    private Color tituloAbajo = Estilo.DORADO_OSCURO;

    public VentanaModal(int ancho, int alto, boolean translucida) {
        super();
        this.translucida = translucida;
        setLayout(null);
        setOpaque(false);
        setSize(ancho, alto);
        setFocusable(true);
    }

    protected void setTitulo(String texto, int baseline, float tamano, Color arriba, Color abajo) {
        this.titulo = texto;
        this.tituloBase = baseline;
        this.tituloTam = tamano;
        this.tituloArriba = arriba;
        this.tituloAbajo = abajo;
        repaint();
    }


    protected void agregarCerrar(final Runnable alCerrar) {
        JButton x = new JButton() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = Estilo.suavizar(g0);
                g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(getModel().isRollover() ? Color.WHITE : Estilo.CREMA);
                int m = 12;
                g.drawLine(m, m, getWidth() - m, getHeight() - m);
                g.drawLine(getWidth() - m, m, m, getHeight() - m);
                g.dispose();
            }
        };
        x.setBounds(getWidth() - 62, 14, 40, 40);
        x.setContentAreaFilled(false);
        x.setBorderPainted(false);
        x.setFocusPainted(false);
        x.setOpaque(false);
        x.setRolloverEnabled(true);
        x.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar.run();
            }
        });
        add(x);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cerrarVentana");
        getActionMap().put("cerrarVentana", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        int w = getWidth() - 6;   
        int h = getHeight() - 8;

        if (translucida) {
            Estilo.atenuar(g, 0.9f);
        }
        Estilo.dibujarPanelMadera(g, 0, 0, w, h, 26);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Estilo.conAlfa(Estilo.DORADO_OSCURO, 190));
        g.draw(new RoundRectangle2D.Float(6, 6, w - 12, h - 12, 20, 20));

        if (!titulo.isEmpty()) {
            float tam = tituloTam;
            Font f = Estilo.fuente(Font.BOLD, tam);
            while (tam > 18 && g.getFontMetrics(f).stringWidth(titulo) > w - 140) {
                tam -= 2;
                f = Estilo.fuente(Font.BOLD, tam);
            }
            FontMetrics fm = g.getFontMetrics(f);
            Estilo.dibujarTextoDegradado(g, titulo, f, (w - fm.stringWidth(titulo)) / 2, tituloBase,
                    tituloArriba, tituloAbajo, 3);
        }
        g.dispose();
    }
}