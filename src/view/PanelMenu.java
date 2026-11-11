package view;

import controller.ControladorPrincipal;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelMenu extends PanelFieltro {

    private static final int ANCHO_BOTON = 380;
    private static final int ALTO_BOTON = 78;

    public PanelMenu(final VentanaPrincipal ventana) {
        super(false);  

        int x = (Estilo.ANCHO - ANCHO_BOTON) / 2;
        agregarBoton("JUGAR", x, 300, ventana, ControladorPrincipal.DESTINO_JUGAR);
        agregarBoton("HISTORIAL", x, 404, ventana, ControladorPrincipal.DESTINO_HISTORIAL);
        agregarBoton("AJUSTES", x, 508, ventana, ControladorPrincipal.DESTINO_AJUSTES);
    }

    private void agregarBoton(String texto, int x, int y, final VentanaPrincipal ventana,
                              final String destino) {
        BotonMetalico boton = new BotonMetalico(texto).tamanoTexto(46).ubicar(x, y, ANCHO_BOTON, ALTO_BOTON);
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.elegirDesdeMenu(destino);
            }
        });
        add(boton);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);  
        Graphics2D g = Estilo.suavizar(g0);
        float tamano = 120;
        while (tamano > 60 && g.getFontMetrics(Estilo.fuente(Font.BOLD, tamano))
                .stringWidth("BLACKJACK") > 620) {
            tamano -= 4;
        }
        Estilo.dibujarTituloDorado(g, "BLACKJACK", Estilo.ANCHO / 2, 215, tamano);
        g.dispose();
    }
}
