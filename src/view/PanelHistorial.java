package view;

import controller.ControladorHistorial;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Locale;
import model.Estadisticas;
import model.RegistroPartida;
import model.Usuario;

// Ventana "HISTORIAL Y ESTADÍSTICAS": tabla de rondas a la izquierda y tres tarjetas
// (% de victorias, racha actual y saldo) a la derecha.
// Un único botón cierra esta ventana modal y devuelve al jugador a la mesa.
public class PanelHistorial extends VentanaModal {

    private static final int TABLA_X = 25;
    private static final int PANEL_Y = 100;
    private static final int TABLA_ANCHO = 610;
    private static final int PANEL_ALTO = 352;
    private static final int METRICAS_X = 657;
    private static final int METRICAS_ANCHO = 520;

    private final Estadisticas estadisticas;
    private final int saldo;

    public PanelHistorial(Usuario usuario, ControladorHistorial controlador,
                          final Runnable alCerrar) {
        super(1202, 560, true);
        setTitulo("HISTORIAL Y ESTADÍSTICAS", 44, 34, new Color(0xF8EEDA), new Color(0xD8C39C));

        estadisticas = controlador.getEstadisticas(usuario);
        saldo = usuario.getSaldo();

        TablaHistorial tabla = new TablaHistorial(controlador.getRegistros(usuario));
        tabla.setBounds(TABLA_X + 6, PANEL_Y + 6, TABLA_ANCHO - 12, PANEL_ALTO - 12);
        add(tabla);

        BotonMetalico cerrar = new BotonMetalico("CERRAR VENTANA", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(22).ubicar(510, 492, 182, 46);
        cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                alCerrar.run();
            }
        });
        add(cerrar);
    }

    // ---------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);   // marco + título
        Graphics2D g = Estilo.suavizar(g0);

        // Cabeceras
        encabezado(g, "REGISTRO DETALLADO DE PARTIDAS", TABLA_X, TABLA_ANCHO, 26);
        encabezado(g, "PERSISTENCIA VISUAL & MÉTRICAS ACUMULADAS", METRICAS_X, METRICAS_ANCHO, 26);

        // Fondos de los dos cuadros
        cuadro(g, TABLA_X, PANEL_Y, TABLA_ANCHO, PANEL_ALTO);
        cuadro(g, METRICAS_X, PANEL_Y, METRICAS_ANCHO, PANEL_ALTO);

        // Métricas
        Font fTitulo = Estilo.fuente(Font.BOLD, 28);
        String titulo = "MÉTRICAS CLAVE";
        Estilo.dibujarTextoSombra(g, titulo, fTitulo, new Color(0xE9C79D),
                METRICAS_X + (METRICAS_ANCHO - g.getFontMetrics(fTitulo).stringWidth(titulo)) / 2, PANEL_Y + 44);

        int ancho = 160;
        int hueco = (METRICAS_ANCHO - 3 * ancho) / 4;
        int y = PANEL_Y + 68;
        int alto = 214;
        int x0 = METRICAS_X + hueco;

        tarjetaVictorias(g, x0, y, ancho, alto);
        tarjetaNumero(g, x0 + ancho + hueco, y, ancho, alto, "RACHA ACTUAL",
                String.valueOf(estadisticas.getRachaActual()), "GANADAS", 82);
        tarjetaNumero(g, x0 + 2 * (ancho + hueco), y, ancho, alto, "TU SALDO ACTUAL",
                String.format(Locale.US, "%,d", saldo), "FICHAS", 54);
        g.dispose();
    }

    private void encabezado(Graphics2D g, String texto, int x, int ancho, float tamMax) {
        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, tamMax, ancho - 10, texto);
        Font f = Estilo.fuente(Font.BOLD, tam);
        Estilo.dibujarTextoSombra(g, texto, f, new Color(0xE9D9B8),
                x + (ancho - g.getFontMetrics(f).stringWidth(texto)) / 2, 88);
    }

    private void cuadro(Graphics2D g, int x, int y, int w, int h) {
        RoundRectangle2D forma = new RoundRectangle2D.Float(x, y, w, h, 14, 14);
        g.setColor(new Color(46, 22, 12, 238));
        g.fill(forma);
        g.setStroke(new BasicStroke(1.6f));
        g.setColor(new Color(0xB08A5C));
        g.draw(forma);
    }

    // ---------------------------------------------------------------
    // TARJETAS CLARAS
    // ---------------------------------------------------------------
    private static final Color TINTA = new Color(0x3B2214);

    private void fondoTarjeta(Graphics2D g, int x, int y, int w, int h, String etiqueta) {
        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 4, w, h, 16, 16));
        g.setColor(new Color(0xF4EBD6));
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 16, 16));
        g.setStroke(new BasicStroke(1.4f));
        g.setColor(new Color(0xCDBFA0));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, 16, 16));

        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 24, w - 16, etiqueta);
        Font f = Estilo.fuente(Font.BOLD, tam);
        g.setFont(f);
        g.setColor(TINTA);
        g.drawString(etiqueta, x + (w - g.getFontMetrics().stringWidth(etiqueta)) / 2f, y + 38);
    }

    private void tarjetaVictorias(Graphics2D g, int x, int y, int w, int h) {
        fondoTarjeta(g, x, y, w, h, "% DE VICTORIAS");
        int pct = estadisticas.getPorcentajeVictorias();
        double cx = x + w / 2.0;
        double cy = y + 128;
        double r = 58;

        g.setStroke(new BasicStroke(16f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(0xD3CFC4));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        if (pct > 0) {
            g.setColor(new Color(0x6E7F77));
            g.draw(new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, 90, -360.0 * pct / 100.0, Arc2D.OPEN));
        }

        String texto = pct + "%";
        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 42, (int) (r * 1.45), texto);
        Font f = Estilo.fuente(Font.BOLD, tam);
        FontMetrics fm = g.getFontMetrics(f);
        g.setFont(f);
        g.setColor(TINTA);
        g.drawString(texto, (float) (cx - fm.stringWidth(texto) / 2.0),
                (float) (cy - fm.getHeight() / 2.0 + fm.getAscent()));
    }

    private void tarjetaNumero(Graphics2D g, int x, int y, int w, int h, String etiqueta,
                               String valor, String unidad, float tamMax) {
        fondoTarjeta(g, x, y, w, h, etiqueta);

        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, tamMax, w - 20, valor);
        Font f = Estilo.fuente(Font.BOLD, tam);
        FontMetrics fm = g.getFontMetrics(f);
        g.setFont(f);
        g.setColor(TINTA);
        g.drawString(valor, x + (w - fm.stringWidth(valor)) / 2f, y + 128 - fm.getHeight() / 2f + fm.getAscent());

        float tu = Estilo.tamanoQueCabe(g, Font.PLAIN, 30, w - 20, unidad);
        Font fu = Estilo.fuente(Font.PLAIN, tu);
        g.setFont(fu);
        g.drawString(unidad, x + (w - g.getFontMetrics().stringWidth(unidad)) / 2f, y + 190);
    }

    // ---------------------------------------------------------------
    // TABLA DE RONDAS
    // ---------------------------------------------------------------
    private static class TablaHistorial extends VistaDesplazable {

        private static final String[][] TITULOS = {
            {"ID DE RONDA", ""}, {"HORA/FECHA", ""}, {"APUESTA", "(Fichas)"},
            {"MANO", "JUGADOR/DEALER"}, {"RESULTADO", "(+/- Fichas)"}
        };
        // El viewport reserva 18 px para el scroll: las columnas llenan
        // exactamente los 580 px útiles y ya no se corta el resultado.
        private static final int[] COLUMNAS = {90, 154, 76, 154, 106};
        private static final int ALTO_FILA = 38;
        private static final int ALTO_CABECERA = 54;

        private final ArrayList<RegistroPartida> registros;

        TablaHistorial(ArrayList<RegistroPartida> registros) {
            this.registros = registros;
        }

        @Override
        protected int altoCabecera() {
            return ALTO_CABECERA;
        }

        @Override
        protected void pintarCabecera(Graphics2D g, int ancho) {
            g.setColor(new Color(74, 37, 21, 235));
            g.fillRect(0, 0, ancho, ALTO_CABECERA);
            g.setColor(new Color(0xB08A5C));
            g.fillRect(0, ALTO_CABECERA - 2, ancho, 2);

            int x = 0;
            for (int i = 0; i < COLUMNAS.length; i++) {
                String[] t = TITULOS[i];
                boolean dosLineas = !t[1].isEmpty();
                float tam = dosLineas ? 17 : 18;
                for (String linea : t) {
                    if (!linea.isEmpty()) {
                        tam = Math.min(tam, Estilo.tamanoQueCabe(g, Font.PLAIN, tam, COLUMNAS[i] - 6, linea));
                    }
                }
                Font f = Estilo.fuente(Font.PLAIN, tam);
                FontMetrics fm = g.getFontMetrics(f);
                Color c = new Color(0xE2B88A);
                if (dosLineas) {
                    int y1 = 22;
                    Estilo.dibujarTextoSombra(g, t[0], f, c, x + (COLUMNAS[i] - fm.stringWidth(t[0])) / 2, y1);
                    Estilo.dibujarTextoSombra(g, t[1], f, c, x + (COLUMNAS[i] - fm.stringWidth(t[1])) / 2, y1 + fm.getHeight() - 2);
                } else {
                    Estilo.dibujarTextoSombra(g, t[0], f, c, x + (COLUMNAS[i] - fm.stringWidth(t[0])) / 2,
                            (ALTO_CABECERA - fm.getHeight()) / 2 + fm.getAscent());
                }
                x += COLUMNAS[i];
            }
        }

        @Override
        protected int medir(Graphics2D g, int ancho) {
            return Math.max(registros.size(), 1) * ALTO_FILA;
        }

        @Override
        protected void pintar(Graphics2D g, int ancho, int altoVisible) {
            if (registros.isEmpty()) {
                Font f = Estilo.fuente(Font.PLAIN, 24);
                String msg = "Aún no has jugado ninguna ronda.";
                float tam = Estilo.tamanoQueCabe(g, Font.PLAIN, 24, ancho - 20, msg);
                f = Estilo.fuente(Font.PLAIN, tam);
                Estilo.dibujarTextoSombra(g, msg, f, new Color(0xD9C9AA),
                        (ancho - g.getFontMetrics(f).stringWidth(msg)) / 2, 90);
                return;
            }
            for (int i = 0; i < registros.size(); i++) {
                RegistroPartida r = registros.get(i);
                int y = i * ALTO_FILA;
                g.setColor(i % 2 == 0 ? new Color(58, 28, 16, 235) : new Color(40, 19, 10, 235));
                g.fillRect(0, y, ancho, ALTO_FILA);

                Color resultado = r.getGanancia() > 0 ? Estilo.VERDE_GANA
                        : r.getGanancia() < 0 ? Estilo.iluminar(Estilo.ROJO, 20) : Estilo.CREMA;
                String[] celdas = {r.getIdTexto(), r.getFechaHora(), String.valueOf(r.getApuesta()),
                    r.getTextoMano(), r.getTextoResultado()};
                int x = 0;
                for (int c = 0; c < COLUMNAS.length; c++) {
                    float tam = Estilo.tamanoQueCabe(g, Font.PLAIN, 21, COLUMNAS[c] - 8, celdas[c]);
                    Font f = Estilo.fuente(Font.PLAIN, tam);
                    FontMetrics fm = g.getFontMetrics(f);
                    Estilo.dibujarTextoSombra(g, celdas[c], f, c == 4 ? resultado : Estilo.CREMA,
                            x + (COLUMNAS[c] - fm.stringWidth(celdas[c])) / 2,
                            y + (ALTO_FILA - fm.getHeight()) / 2 + fm.getAscent());
                    x += COLUMNAS[c];
                }
            }
        }
    }
}
