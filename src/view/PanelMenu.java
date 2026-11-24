package view;

import controller.ControladorPrincipal;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelMenu extends PanelFieltro implements ActionListener {

    private static final int ANCHO_BOTON = 380;
    private static final int ALTO_BOTON = 78;

    private final VentanaPrincipal ventana;
    private final BotonMetalico jugar;
    private final BotonMetalico historial;
    private final BotonMetalico ajustes;

    public PanelMenu(VentanaPrincipal ventana) {
        super(false);
        this.ventana = ventana;

        int x = (Estilo.ANCHO - ANCHO_BOTON) / 2;
        jugar = agregarBoton("JUGAR", x, 300);
        historial = agregarBoton("HISTORIAL", x, 404);
        ajustes = agregarBoton("AJUSTES", x, 508);
    }

    private BotonMetalico agregarBoton(String texto, int x, int y) {
        BotonMetalico boton = new BotonMetalico(texto).tamanoTexto(46).ubicar(x, y, ANCHO_BOTON, ALTO_BOTON);
        boton.addActionListener(this);
        add(boton);
        return boton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();
        if (fuente == jugar) {
            ventana.elegirDesdeMenu(ControladorPrincipal.DESTINO_JUGAR);
        } else if (fuente == historial) {
            ventana.elegirDesdeMenu(ControladorPrincipal.DESTINO_HISTORIAL);
        } else if (fuente == ajustes) {
            ventana.elegirDesdeMenu(ControladorPrincipal.DESTINO_AJUSTES);
        }
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
