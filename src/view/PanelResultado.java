package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Locale;
import model.ResultadoMano;
import model.Ronda;
import model.Usuario;

// Ventana "¡GANASTE LA RONDA!" con el nuevo saldo y los botones SIGUIENTE RONDA / VOLVER AL MENÚ PRINCIPAL.
public class PanelResultado extends VentanaModal {

    private final String detalle;
    private final String saldoTexto;
    private final String detalleManos;

    public PanelResultado(Ronda ronda, Usuario usuario, final Runnable siguiente, final Runnable menu) {
        super(610, 390, true);

        int neta = ronda.getGananciaNeta();
        Color arriba;
        Color abajo;
        if (neta > 0) {
            arriba = new Color(0xF6E19A);
            abajo = new Color(0xC9962F);
        } else if (neta < 0) {
            arriba = new Color(0xFAD3CB);
            abajo = new Color(0xD9786C);
        } else {
            arriba = new Color(0xF3E6CB);
            abajo = new Color(0xC8B48C);
        }
        setTitulo(ronda.getTituloResultado(), 108, 56, arriba, abajo);

        detalle = ronda.getDetalleResultado();
        saldoTexto = String.format(Locale.US, "%,d FICHAS", usuario.getSaldo());

        // Con split se listan los resultados de cada mano.
        ArrayList<ResultadoMano> resultados = ronda.getResultados();
        if (resultados.size() > 1) {
            StringBuilder sb = new StringBuilder("Manos: ");
            for (int i = 0; i < resultados.size(); i++) {
                if (i > 0) {
                    sb.append("  \u00B7  ");
                }
                sb.append(resultados.get(i).getTipo());
            }
            detalleManos = sb.toString();
        } else {
            detalleManos = "";
        }

        BotonMetalico bSiguiente = new BotonMetalico("SIGUIENTE RONDA", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(24).ubicar(22, 306, 272, 58);
        bSiguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                siguiente.run();
            }
        });
        add(bSiguiente);

        BotonMetalico bMenu = new BotonMetalico("VOLVER AL MENÚ PRINCIPAL", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(24).ubicar(316, 306, 272, 58);
        bMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu.run();
            }
        });
        add(bMenu);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);   // marco + título
        Graphics2D g = Estilo.suavizar(g0);
        int w = getWidth() - 6;

        // Caja del nuevo saldo
        RoundRectangle2D caja = new RoundRectangle2D.Float(45, 142, w - 90, 70, 16, 16);
        g.setColor(new Color(58, 28, 16, 235));
        g.fill(caja);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Estilo.DORADO_OSCURO);
        g.draw(caja);

        String parte1 = "NUEVO SALDO: ";
        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 40, w - 90 - 36, parte1 + saldoTexto);
        Font f1 = Estilo.fuente(Font.PLAIN, tam * 0.95f);
        Font f2 = Estilo.fuente(Font.BOLD, tam);
        FontMetrics m1 = g.getFontMetrics(f1);
        FontMetrics m2 = g.getFontMetrics(f2);
        int total = m1.stringWidth(parte1) + m2.stringWidth(saldoTexto);
        int x = (w - total) / 2;
        int base = 142 + (70 - m2.getHeight()) / 2 + m2.getAscent();
        Estilo.dibujarTextoSombra(g, parte1, f1, Estilo.CREMA, x, base);
        Estilo.dibujarTextoDegradado(g, saldoTexto, f2, x + m1.stringWidth(parte1), base,
                new Color(0xF6E19A), new Color(0xD1A040), 2);

        // Detalle
        Font f3 = Estilo.fuente(Font.PLAIN, 26);
        FontMetrics m3 = g.getFontMetrics(f3);
        Estilo.dibujarTextoSombra(g, detalle, f3, Estilo.CREMA, (w - m3.stringWidth(detalle)) / 2, 252);

        if (!detalleManos.isEmpty()) {
            Font f4 = Estilo.fuente(Font.PLAIN, 21);
            FontMetrics m4 = g.getFontMetrics(f4);
            Estilo.dibujarTextoSombra(g, detalleManos, f4, new Color(0xD9C9AA),
                    (w - m4.stringWidth(detalleManos)) / 2, 284);
        }
        g.dispose();
    }
}