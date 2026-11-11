package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

public abstract class VistaDesplazable extends JComponent {

    private static final int ANCHO_BARRA = 18;

    private int desplazamiento = 0;
    private int altoContenido = 0;
    private int altoVisible = 0;

    private int pistaY;
    private int pistaAlto;
    private int pulgarY;
    private int pulgarAlto;
    private int arrastreDesde = -1;
    private int desplazamientoInicial;

    public VistaDesplazable() {
        setOpaque(false);
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                mover(e.getUnitsToScroll() * 20);
            }
        });
        MouseAdapter arrastre = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() >= getWidth() - ANCHO_BARRA && altoContenido > altoVisible) {
                    if (e.getY() >= pulgarY && e.getY() <= pulgarY + pulgarAlto) {
                        arrastreDesde = e.getY();
                        desplazamientoInicial = desplazamiento;
                    } else {
                        mover(e.getY() < pulgarY ? -altoVisible + 20 : altoVisible - 20);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastreDesde >= 0) {
                    int recorrido = Math.max(1, pistaAlto - pulgarAlto);
                    int maximo = altoContenido - altoVisible;
                    desplazamiento = desplazamientoInicial + (e.getY() - arrastreDesde) * maximo / recorrido;
                    ajustar();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                arrastreDesde = -1;
            }
        };
        addMouseListener(arrastre);
        addMouseMotionListener(arrastre);
    }

    protected int altoCabecera() {
        return 0;
    }

    protected void pintarCabecera(Graphics2D g, int ancho) {
    }

    protected abstract int medir(Graphics2D g, int ancho);


    protected abstract void pintar(Graphics2D g, int ancho, int altoVisible);

    private void mover(int delta) {
        desplazamiento += delta;
        ajustar();
        repaint();
    }

    private void ajustar() {
        int maximo = Math.max(0, altoContenido - altoVisible);
        desplazamiento = Math.max(0, Math.min(maximo, desplazamiento));
    }

    public void volverAlInicio() {
        desplazamiento = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Estilo.suavizar(g0);
        int ancho = getWidth() - ANCHO_BARRA;
        int cabecera = altoCabecera();
        altoVisible = getHeight() - cabecera;
        altoContenido = medir(g, ancho);
        ajustar();

        Graphics2D gc = (Graphics2D) g.create(0, 0, ancho, cabecera + 1);
        pintarCabecera(gc, ancho);
        gc.dispose();

        Graphics2D gv = (Graphics2D) g.create(0, cabecera, ancho, altoVisible);
        gv.translate(0, -desplazamiento);
        pintar(gv, ancho, altoVisible);
        gv.dispose();

        if (altoContenido > altoVisible) {
            pistaY = cabecera + 4;
            pistaAlto = altoVisible - 8;
            pulgarAlto = Math.max(34, pistaAlto * altoVisible / altoContenido);
            int recorrido = pistaAlto - pulgarAlto;
            int maximo = altoContenido - altoVisible;
            pulgarY = pistaY + (maximo == 0 ? 0 : recorrido * desplazamiento / maximo);
            int x = getWidth() - 12;
            g.setColor(new Color(0, 0, 0, 90));
            g.fill(new RoundRectangle2D.Float(x, pistaY, 8, pistaAlto, 8, 8));
            g.setColor(new Color(0xC9A877));
            g.fill(new RoundRectangle2D.Float(x, pulgarY, 8, pulgarAlto, 8, 8));
        }
        g.dispose();
    }
}