package view;

import controller.ControladorJuego;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Ventana "RECARGAR SALDO": seis montos tomados de ControladorJuego.RECARGAS.
public class PanelRecarga extends VentanaModal {

    public PanelRecarga(final ControladorJuego juego, final Runnable alRecargar, Runnable alCerrar) {
        super(600, 380, false);
        setTitulo("RECARGAR SALDO", 66, 54, new Color(0xF6E2B8), new Color(0xD4A862));
        agregarCerrar(alCerrar);

        int[] montos = ControladorJuego.RECARGAS;
        for (int i = 0; i < montos.length; i++) {
            final int monto = montos[i];
            int columna = i % 3;
            int fila = i / 3;
            BotonMetalico b = new BotonMetalico("$" + monto, BotonMetalico.Tipo.MADERA)
                    .tamanoTexto(48).ubicar(25 + columna * 190, 138 + fila * 108, 170, 88);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    juego.recargarSaldo(monto);
                    alRecargar.run();
                }
            });
            add(b);
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = Estilo.suavizar(g0);
        Font f = Estilo.fuente(Font.PLAIN, 25);
        FontMetrics fm = g.getFontMetrics(f);
        String texto = "Seleccione una cantidad para añadir a su saldo.";
        int w = getWidth() - 6;
        Estilo.dibujarTextoSombra(g, texto, f, Estilo.CREMA, (w - fm.stringWidth(texto)) / 2, 114);
        g.dispose();
    }
}