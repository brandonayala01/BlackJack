package view;

import controller.ControladorJuego;
import controller.ControladorPrincipal;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import hilos.HiloAnimacion;
import hilos.HiloCrupier;
import hilos.HiloReparto;
import hilos.HiloResultado;
import model.Ronda;
import model.Usuario;

// La mesa de juego. Dibuja el estado de la Ronda (modelo) y traduce los clics a
// llamadas de ControladorJuego. Nunca decide reglas: solo pregunta a la Ronda
// (puedePedir(), puedeDividir()...) para habilitar o deshabilitar botones.
public class PanelMesa extends PanelFieltro {

    private final VentanaPrincipal ventana;
    private ControladorJuego juego;
    private Usuario usuario;

    // --- Componentes ---
    private final Etiqueta etiquetaNombre = new Etiqueta("", 30, Estilo.TEXTO, false);
    private final Etiqueta etiquetaMensaje = new Etiqueta("", 19, new Color(0xE6DCC4), false);
    private final BotonSaldo botonSaldo = new BotonSaldo();
    private final CuadroApuesta cuadroApuesta = new CuadroApuesta();
    private final PanelCrupier panelCrupier = new PanelCrupier();
    private final PanelJugador panelJugador = new PanelJugador();

    private final BotonMetalico botonPedir = new BotonMetalico("HIT\n(PEDIR)");
    private final BotonMetalico botonPlantarse = new BotonMetalico("STAND\n(PLANTARSE)");
    private final BotonMetalico botonDoblar = new BotonMetalico("DOUBLE\n(DOBLAR)");
    private final BotonMetalico botonDividir = new BotonMetalico("SPLIT\n(DIVIDIR)");
    private final BotonMetalico botonLimpiar = new BotonMetalico("LIMPIAR");
    private final BotonMetalico botonRepartir = new BotonMetalico("REPARTIR", BotonMetalico.Tipo.DORADO);
    private final BotonFicha[] botonesFicha = new BotonFicha[ControladorJuego.FICHAS.length];
    private final AvatarMesa avatarMesa = new AvatarMesa();
    private final PilaFichas pilaFichas = new PilaFichas();

    private final CapaModal capa = new CapaModal();

    // --- Animaciones (hilos) ---
    private HiloAnimacion hiloReparto;
    private HiloAnimacion hiloCrupier;
    private HiloAnimacion hiloResultado;
    private int pasoReparto;
    private int limiteJugador = -1;
    private int limiteCrupier = -1;
    private boolean repartiendo = false;

    public PanelMesa(VentanaPrincipal ventana) {
        super(true);
        this.ventana = ventana;

        // ---- Barra superior: menú y guía ----
        BotonEnlace volver = new BotonEnlace("\u2039 MENÚ", 24, Estilo.CREMA, false)
                .alinearDerecha(1190, 28);
        volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelMesa.this.ventana.volverAlMenu();
            }
        });
        add(volver);

        BotonMetalico guia = new BotonMetalico("?").tamanoTexto(32).ubicar(1204, 22, 48, 50);
        guia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGuia();
            }
        });
        add(guia);

        // ---- Nombre del jugador (clic = perfil) ----
        etiquetaNombre.setBounds(110, 146, 260, 36);
        etiquetaNombre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        etiquetaNombre.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirPerfil();
            }
        });
        add(etiquetaNombre);

        // ---- Avatar del jugador (clic = perfil) ----
        avatarMesa.setBounds(28, 124, 76, 76);
        avatarMesa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarMesa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirPerfil();
            }
        });
        add(avatarMesa);

        // ---- Pila con las fichas apostadas ----
        pilaFichas.setBounds(40, 430, 320, 130);
        add(pilaFichas);

        // ---- Saldo (clic = recargar) ----
        Etiqueta tituloSaldo = new Etiqueta("TU SALDO", 34, Estilo.TEXTO, false);
        tituloSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        tituloSaldo.setBounds(1008, 120, 264, 38);
        add(tituloSaldo);
        botonSaldo.setBounds(1008, 166, 272, 86);
        botonSaldo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRecarga();
            }
        });
        add(botonSaldo);

        // ---- Cuadros de cartas y apuesta ----
        panelCrupier.setBounds(378, 118, PanelCrupier.ANCHO_PANEL + 8, PanelCrupier.ALTO_PANEL + 10);
        add(panelCrupier);
        cuadroApuesta.setBounds(528, 309, 232, 76);
        add(cuadroApuesta);
        panelJugador.setBounds(378, 394, PanelJugador.ANCHO_PANEL + 8, PanelJugador.ALTO_PANEL + 10);
        add(panelJugador);

        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setBounds(278, 580, 724, 26);
        add(etiquetaMensaje);

        // ---- Repartir / Limpiar (solo durante la apuesta) ----
        botonLimpiar.tamanoTexto(30).ubicar(268, 313, 220, 62);
        botonLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.limpiarApuesta();
                refrescar();
            }
        });
        add(botonLimpiar);
        botonRepartir.tamanoTexto(32).ubicar(800, 313, 220, 62);
        botonRepartir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repartir();
            }
        });
        add(botonRepartir);

        // ---- Acciones de juego ----
        configurarAccion(botonPedir, 251, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.pedir();
                despuesDeAccion();
            }
        });
        configurarAccion(botonPlantarse, 450, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.plantarse();
                despuesDeAccion();
            }
        });
        configurarAccion(botonDoblar, 648, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.doblar();
                despuesDeAccion();
            }
        });
        configurarAccion(botonDividir, 846, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.dividir();
                despuesDeAccion();
            }
        });

        // ---- Fichas ----
        for (int i = 0; i < botonesFicha.length; i++) {
            final int valor = ControladorJuego.FICHAS[i];
            BotonFicha b = new BotonFicha(valor);
            b.setBounds(298 + Math.round(i * 103.6f), 690, 64, 64);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    juego.agregarFicha(valor);
                    refrescar();
                }
            });
            botonesFicha[i] = b;
            add(b);
        }

        // La capa de ventanas va al final del código pero al FRENTE (índice 0).
        capa.setBounds(0, 0, Estilo.ANCHO, Estilo.ALTO);
        add(capa, 0);
    }

    private void configurarAccion(BotonMetalico boton, int x, ActionListener accion) {
        boton.tamanoTexto(30).ubicar(x, 614, 184, 68);
        boton.addActionListener(accion);
        add(boton);
    }

    // ---------------------------------------------------------------
    // ENTRADA Y SALIDA DE LA PANTALLA
    // ---------------------------------------------------------------

    // La llama VentanaPrincipal cada vez que se muestra la mesa.
    public void alMostrar() {
        ControladorPrincipal principal = ventana.getControlador();
        juego = principal.getJuego();
        usuario = principal.getAutenticacion().getUsuarioActual();
        juego.iniciar(usuario);

        detener();
        capa.cerrar();
        limiteJugador = -1;
        limiteCrupier = -1;
        refrescar();

        // Si se cerró el juego a mitad de ronda, aquí se reanuda donde quedó.
        String fase = juego.getRonda().getFase();
        if (fase.equals(Ronda.FASE_CRUPIER)) {
            iniciarTurnoCrupier();
        } else if (fase.equals(Ronda.FASE_TERMINADA)) {
            iniciarResultado();
        }
        abrirSegunDestino(principal.getDestino());
    }

    // Se llama al salir de la mesa: detiene toda animación pendiente.
    public void detener() {
        detenerHilo(hiloReparto);
        detenerHilo(hiloCrupier);
        detenerHilo(hiloResultado);
        hiloReparto = null;
        hiloCrupier = null;
        hiloResultado = null;
        repartiendo = false;
    }

    private void detenerHilo(HiloAnimacion hilo) {
        if (hilo != null) {
            hilo.detener();
        }
    }

    // Si en el menú se eligió Historial o Ajustes, esa ventana se abre encima de la mesa.
    private void abrirSegunDestino(String destino) {
        if (destino.equals(ControladorPrincipal.DESTINO_HISTORIAL)) {
            abrirHistorial();
        } else if (destino.equals(ControladorPrincipal.DESTINO_AJUSTES)) {
            abrirPerfil();
        }
    }

    // ---------------------------------------------------------------
    // ESTADO DE LA RONDA -> PANTALLA
    // ---------------------------------------------------------------
    private void refrescar() {
        Ronda ronda = juego.getRonda();
        String fase = ronda.getFase();
        int saldo = usuario.getSaldo();

        etiquetaNombre.setText(usuario.getNombre().toUpperCase());
        avatarMesa.setAvatar(usuario.getAvatar());
        pilaFichas.setTotal(ronda.getApuestaTotal());
        botonSaldo.setSaldo(saldo);
        cuadroApuesta.setApuesta(ronda.getApuestaTotal());
        panelCrupier.setDatos(ronda.getCrupier(), limiteCrupier);
        panelJugador.setDatos(ronda, limiteJugador);

        // Apuesta
        boolean apostando = fase.equals(Ronda.FASE_APUESTA) && !repartiendo;
        for (int i = 0; i < botonesFicha.length; i++) {
            int valor = ControladorJuego.FICHAS[i];
            botonesFicha[i].setEnabled(apostando && ronda.getApuestaPendiente() + valor <= saldo);
        }
        botonLimpiar.setVisible(fase.equals(Ronda.FASE_APUESTA));
        botonRepartir.setVisible(fase.equals(Ronda.FASE_APUESTA));
        botonLimpiar.setEnabled(apostando && ronda.getApuestaPendiente() > 0);
        botonRepartir.setEnabled(apostando && ronda.puedeRepartir());

        // Acciones (el modelo decide cuáles son posibles)
        boolean jugando = fase.equals(Ronda.FASE_JUGADOR) && !repartiendo;
        botonPedir.setEnabled(jugando && ronda.puedePedir());
        botonPlantarse.setEnabled(jugando && ronda.puedePlantarse());
        botonDoblar.setEnabled(jugando && ronda.puedeDoblar());
        botonDividir.setEnabled(jugando && ronda.puedeDividir());

        etiquetaMensaje.setText(textoMensaje(ronda, fase, saldo));
        repaint();
    }

    private String textoMensaje(Ronda ronda, String fase, int saldo) {
        if (fase.equals(Ronda.FASE_APUESTA)) {
            if (saldo < 1) {
                return "Te quedaste sin fichas. Toca tu saldo para recargar.";
            }
            if (ronda.getApuestaPendiente() == 0) {
                return "Elige tus fichas para apostar.";
            }
            return "Pulsa REPARTIR cuando estés listo.";
        }
        if (fase.equals(Ronda.FASE_JUGADOR) && !repartiendo) {
            int total = ronda.getManos().size();
            if (total > 1) {
                return "Jugando la mano " + (ronda.getIndiceManoActual() + 1) + " de " + total + ".";
            }
            return "";
        }
        if (fase.equals(Ronda.FASE_CRUPIER)) {
            return "Turno del crupier...";
        }
        return "";
    }

    // ---------------------------------------------------------------
    // REPARTO ANIMADO
    // ---------------------------------------------------------------
    private void repartir() {
        juego.repartir();
        if (juego.getRonda().getFase().equals(Ronda.FASE_APUESTA)) {
            refrescar();   // no se pudo repartir
            return;
        }
        // Las 4 cartas ya están en el modelo; se muestran de a una: J, C, J, C.
        repartiendo = true;
        pasoReparto = 0;
        aplicarPasoReparto();
        hiloReparto = new HiloReparto(new Runnable() {
            @Override
            public void run() {
                avanzarReparto();
            }
        });
        hiloReparto.start();
    }

    private void aplicarPasoReparto() {
        int[][] pasos = {{1, 0}, {1, 1}, {2, 1}, {2, 2}};
        limiteJugador = pasos[pasoReparto][0];
        limiteCrupier = pasos[pasoReparto][1];
        refrescar();
    }

    private void avanzarReparto() {
        pasoReparto++;
        if (pasoReparto < 4) {
            aplicarPasoReparto();
            return;
        }
        hiloReparto = null;
        repartiendo = false;
        limiteJugador = -1;
        limiteCrupier = -1;
        refrescar();
        despuesDeAccion();   // por si hubo blackjack y la ronda ya terminó
    }

    // ---------------------------------------------------------------
    // TURNO DEL CRUPIER Y RESULTADO
    // ---------------------------------------------------------------

    // Tras cualquier acción: ¿sigue el jugador, juega el crupier o terminó la ronda?
    private void despuesDeAccion() {
        refrescar();
        String fase = juego.getRonda().getFase();
        if (fase.equals(Ronda.FASE_CRUPIER)) {
            if (hiloCrupier == null || !hiloCrupier.isAlive()) {
                iniciarTurnoCrupier();
            }
        } else if (fase.equals(Ronda.FASE_TERMINADA)) {
            iniciarResultado();
        }
    }

    // Lanza el hilo que juega el turno del crupier carta por carta.
    private void iniciarTurnoCrupier() {
        hiloCrupier = new HiloCrupier(juego,
                new Runnable() {
            @Override
            public void run() {
                refrescar();
            }
        },
                new Runnable() {
            @Override
            public void run() {
                mostrarResultado();
            }
        });
        hiloCrupier.start();
    }

    // Lanza el hilo que espera un momento y muestra el resultado.
    private void iniciarResultado() {
        detenerHilo(hiloResultado);
        hiloResultado = new HiloResultado(new Runnable() {
            @Override
            public void run() {
                mostrarResultado();
            }
        });
        hiloResultado.start();
    }

    private void mostrarResultado() {
        Ronda ronda = juego.getRonda();
        if (!ronda.getFase().equals(Ronda.FASE_TERMINADA) || capa.estaAbierta()) {
            return;
        }
        capa.mostrar(new PanelResultado(ronda, usuario,
                new Runnable() {
            @Override
            public void run() {
                siguienteRonda();
            }
        },
                new Runnable() {
            @Override
            public void run() {
                capa.cerrar();
                ventana.volverAlMenu();
            }
        }));
    }

    private void siguienteRonda() {
        capa.cerrar();
        juego.nuevaRonda();
        limiteJugador = -1;
        limiteCrupier = -1;
        refrescar();
    }

    // ---------------------------------------------------------------
    // VENTANAS EMERGENTES
    // ---------------------------------------------------------------
    private void abrirRecarga() {
        if (capa.estaAbierta()) {
            return;
        }
        capa.mostrar(new PanelRecarga(juego,
                new Runnable() {
            @Override
            public void run() {
                capa.cerrar();
                refrescar();
            }
        },
                new Runnable() {
            @Override
            public void run() {
                capa.cerrar();
            }
        }));
    }

    // Un Runnable que simplemente cierra la ventana abierta y refresca la mesa.
    private Runnable cerrarVentana() {
        return new Runnable() {
            @Override
            public void run() {
                capa.cerrar();
                refrescar();
            }
        };
    }

    private void abrirGuia() {
        if (capa.estaAbierta()) {
            return;
        }
        capa.mostrar(new PanelGuia(cerrarVentana()));
    }

    private void abrirHistorial() {
        if (capa.estaAbierta()) {
            return;
        }
        capa.mostrar(new PanelHistorial(usuario, ventana.getControlador().getHistorial(), cerrarVentana()));
    }

    private void abrirPerfil() {
        if (capa.estaAbierta()) {
            return;
        }
        capa.mostrar(new PanelPerfil(usuario, ventana.getControlador().getPerfil(),
                new Runnable() {      // al guardar: se actualiza el nombre en la mesa
            @Override
            public void run() {
                refrescar();
            }
        },
                cerrarVentana(),      // botón X
                cerrarVentana(),      // CERRAR VENTANA
                new Runnable() {      // CERRAR SESIÓN
            @Override
            public void run() {
                capa.cerrar();
                ventana.cerrarSesion();
            }
        }));
    }

    // ===============================================================
    // COMPONENTES PROPIOS DE LA MESA
    // ===============================================================

    // "1,500 FICHAS (>)" dentro de un cuadro de madera. Al hacer clic abre la recarga.
    private static class BotonSaldo extends JButton {

        private int saldo;

        BotonSaldo() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setSaldo(int saldo) {
            this.saldo = saldo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = Estilo.suavizar(g0);
            int w = getWidth() - 8;
            int h = getHeight() - 10;
            boolean sobre = getModel().isRollover();

            Estilo.dibujarPanelMadera(g, 0, 0, w, h, 26);
            g.setColor(new Color(0, 0, 0, 55));
            g.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 24, 24));
            g.setStroke(new BasicStroke(2f));
            g.setColor(sobre ? Estilo.DORADO : Estilo.conAlfa(Estilo.DORADO_OSCURO, 210));
            g.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 24, 24));

            String texto = String.format(Locale.US, "%,d FICHAS", saldo);
            float tam = 40;
            Font f = Estilo.fuente(Font.BOLD, tam);
            while (tam > 18 && g.getFontMetrics(f).stringWidth(texto) > w - 100) {
                tam -= 2;
                f = Estilo.fuente(Font.BOLD, tam);
            }
            FontMetrics fm = g.getFontMetrics(f);
            int base = (h - fm.getHeight()) / 2 + fm.getAscent();
            Estilo.dibujarTextoDegradado(g, texto, f, 20, base, new Color(0xF6E19A), new Color(0xC9962F), 2);

            // Círculo con flecha ">"
            double cx = w - 40;
            double cy = h / 2.0;
            g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(sobre ? Color.WHITE : Estilo.CREMA);
            g.draw(new Ellipse2D.Double(cx - 18, cy - 18, 36, 36));
            Path2D.Double flecha = new Path2D.Double();
            flecha.moveTo(cx - 3, cy - 8);
            flecha.lineTo(cx + 5, cy);
            flecha.lineTo(cx - 3, cy + 8);
            g.draw(flecha);
            g.dispose();
        }
    }

    // Avatar del jugador junto a su nombre.
    private static class AvatarMesa extends JComponent {

        private int avatar;

        void setAvatar(int avatar) {
            this.avatar = avatar;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = Estilo.suavizar(g0);
            double radio = Math.min(getWidth(), getHeight()) / 2.0 - 3;
            DibujoAvatar.dibujar(g, avatar, getWidth() / 2.0, getHeight() / 2.0, radio);
            g.setStroke(new BasicStroke(3f));
            g.setColor(Estilo.DORADO);
            g.draw(new Ellipse2D.Double(getWidth() / 2.0 - radio, getHeight() / 2.0 - radio, radio * 2, radio * 2));
            g.dispose();
        }
    }

    // Pila de fichas que representa la apuesta actual (se arma con las fichas de mayor valor).
    private static class PilaFichas extends JComponent {

        private static final int TAM = 46;
        private int total;

        void setTotal(int total) {
            this.total = total;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            if (total <= 0) {
                return;
            }
            Graphics2D g = Estilo.suavizar(g0);
            int[] fichas = ControladorJuego.FICHAS;
            int resto = total;
            int columna = 0;
            int columnas = 0;
            for (int i = fichas.length - 1; i >= 0; i--) {
                if (resto >= fichas[i]) {
                    columnas++;
                    resto %= fichas[i];
                }
            }
            int inicioX = (getWidth() - columnas * (TAM + 6)) / 2;
            resto = total;
            for (int i = fichas.length - 1; i >= 0; i--) {
                int cantidad = resto / fichas[i];
                if (cantidad == 0) {
                    continue;
                }
                resto %= fichas[i];
                int visibles = Math.min(cantidad, 10);
                for (int k = 0; k < visibles; k++) {
                    BotonFicha.dibujar(g, inicioX + columna * (TAM + 6), getHeight() - TAM - 6 - k * 5,
                            TAM, fichas[i], true);
                }
                columna++;
            }
            g.dispose();
        }
    }

    // "Bet: 50 FICHAS (APUESTA TOTAL)"
    private static class CuadroApuesta extends JComponent {

        private int apuesta;

        void setApuesta(int apuesta) {
            this.apuesta = apuesta;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = Estilo.suavizar(g0);
            int w = getWidth() - 8;
            int h = getHeight() - 9;
            Estilo.dibujarPanelMadera(g, 0, 0, w, h, 16);

            String parte2 = apuesta + " FICHAS";
            float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 34, w - 26, "Bet: " + parte2);
            Font f1 = Estilo.fuente(Font.PLAIN, tam);
            Font f2 = Estilo.fuente(Font.BOLD, tam);
            FontMetrics m1 = g.getFontMetrics(f1);
            FontMetrics m2 = g.getFontMetrics(f2);
            int total = m1.stringWidth("Bet: ") + m2.stringWidth(parte2);
            int x = (w - total) / 2;
            Estilo.dibujarTextoSombra(g, "Bet: ", f1, new Color(0xE6D5B8), x, 32);
            Estilo.dibujarTextoSombra(g, parte2, f2, Estilo.DORADO, x + m1.stringWidth("Bet: "), 32);

            Font f3 = Estilo.fuente(Font.PLAIN, 20);
            FontMetrics m3 = g.getFontMetrics(f3);
            String sub = "(APUESTA TOTAL)";
            Estilo.dibujarTextoSombra(g, sub, f3, new Color(0xD9C9AA), (w - m3.stringWidth(sub)) / 2, 57);
            g.dispose();
        }
    }
}
