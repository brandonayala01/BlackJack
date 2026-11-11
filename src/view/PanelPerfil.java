package view;

import controller.ControladorPerfil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import model.Estadisticas;
import model.Usuario;


public class PanelPerfil extends VentanaModal {

    private static final int CENTRO_AVATAR_X = 178;
    private static final int CENTRO_AVATAR_Y = 188;
    private static final int RADIO_AVATAR = 92;

    private final Usuario usuario;
    private final ControladorPerfil controlador;
    private final Estadisticas estadisticas;
    private int avatar;

    private final CampoTexto campoNombre;
    private final Etiqueta mensaje;

    public PanelPerfil(Usuario usuario, ControladorPerfil controlador, final Runnable alGuardar,
                       Runnable alCerrarX, final Runnable alCerrarMenu, final Runnable alCerrarSesion) {
        super(675, 478, true);
        this.usuario = usuario;
        this.controlador = controlador;
        this.estadisticas = usuario.getEstadisticas();
        this.avatar = usuario.getAvatar();
        setTitulo("PERFIL Y AJUSTES DE GUARDADO", 36, 30, new Color(0xF8EEDA), new Color(0xD8C39C));
        agregarCerrar(alCerrarX);

       
        add(crearFlecha(true));
        add(crearFlecha(false));

       
        campoNombre = new CampoTexto() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarLapiz((Graphics2D) g, getWidth() - 34, getHeight() / 2);
            }
        };
        campoNombre.setText(usuario.getNombre());
        campoNombre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 46));
        campoNombre.setBounds(56, 334, 262, 42);
        add(campoNombre);

        mensaje = new Etiqueta("", 18, Estilo.iluminar(Estilo.ROJO, 30), true);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        mensaje.setBounds(20, 378, 316, 22);
        add(mensaje);
        campoNombre.getDocument().addDocumentListener(new OyenteTexto() {
            @Override
            public void cambio() {
                mensaje.setText("");
            }
        });

        
        BotonMetalico sesion = new BotonMetalico("CERRAR SESIÓN", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(22).ubicar(366, 306, 264, 46);
        sesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrarSesion.run();
            }
        });
        add(sesion);

        BotonMetalico cerrar = new BotonMetalico("CERRAR VENTANA", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(21).ubicar(142, 412, 184, 46);
        cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrarMenu.run();
            }
        });
        add(cerrar);

        BotonMetalico guardar = new BotonMetalico("GUARDAR CAMBIOS", BotonMetalico.Tipo.MADERA)
                .tamanoTexto(21).ubicar(340, 412, 184, 46);
        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar(alGuardar);
            }
        });
        add(guardar);
    }

    private void guardar(Runnable alGuardar) {
        String error = controlador.guardarCambios(usuario, campoNombre.getText(), avatar);
        if (error != null) {
            mensaje.setForeground(Estilo.iluminar(Estilo.ROJO, 30));
            mensaje.setText(error);
            return;
        }
        campoNombre.setText(usuario.getNombre());   
        mensaje.setForeground(Estilo.VERDE_GANA);
        mensaje.setText("Cambios guardados.");
        alGuardar.run();
    }


    private JButton crearFlecha(final boolean izquierda) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = Estilo.suavizar(g0);
                g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(getModel().isRollover() ? Color.WHITE : new Color(0xF4E3BF));
                Path2D.Double flecha = new Path2D.Double();
                double cx = getWidth() / 2.0;
                double cy = getHeight() / 2.0;
                double d = izquierda ? -1 : 1;
                flecha.moveTo(cx - d * 9, cy - 22);
                flecha.lineTo(cx + d * 9, cy);
                flecha.lineTo(cx - d * 9, cy + 22);
                g.draw(flecha);
                g.dispose();
            }
        };
        b.setBounds(CENTRO_AVATAR_X + (izquierda ? -140 : 100), CENTRO_AVATAR_Y - 40, 40, 80);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avatar = izquierda ? controlador.anteriorAvatar(avatar) : controlador.siguienteAvatar(avatar);
                mensaje.setText("");
                repaint();
            }
        });
        return b;
    }

    private static void dibujarLapiz(Graphics2D g0, int cx, int cy) {
        Graphics2D g = Estilo.suavizar(g0);
        g.translate(cx, cy);
        g.rotate(Math.toRadians(-45));
        g.setColor(new Color(0xE6C79A));
        g.fill(new RoundRectangle2D.Double(-3.5, -11, 7, 18, 2, 2));
        Path2D.Double punta = new Path2D.Double();
        punta.moveTo(-3.5, 7);
        punta.lineTo(3.5, 7);
        punta.lineTo(0, 13);
        punta.closePath();
        g.setColor(new Color(0xF4E3BF));
        g.fill(punta);
        g.setColor(new Color(0xC0624A));
        g.fill(new RoundRectangle2D.Double(-3.5, -14, 7, 4, 2, 2));
        g.dispose();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);   // marco + título
        Graphics2D g = Estilo.suavizar(g0);

     
        RoundRectangle2D interior = new RoundRectangle2D.Float(15, 56, 639, 346, 14, 14);
        g.setColor(new Color(58, 28, 16, 235));
        g.fill(interior);
        g.setColor(new Color(40, 19, 10, 170));
        g.fillRect(342, 57, 311, 344);
        g.setStroke(new BasicStroke(1.8f));
        g.setColor(new Color(0xB08A5C));
        g.draw(interior);


        g.setPaint(new RadialGradientPaint(new Point2D.Double(CENTRO_AVATAR_X, CENTRO_AVATAR_Y),
                RADIO_AVATAR + 30f, new float[]{0.6f, 1f},
                new Color[]{new Color(124, 108, 224, 190), new Color(124, 108, 224, 0)}));
        g.fill(new Ellipse2D.Double(CENTRO_AVATAR_X - RADIO_AVATAR - 30, CENTRO_AVATAR_Y - RADIO_AVATAR - 30,
                2 * (RADIO_AVATAR + 30), 2 * (RADIO_AVATAR + 30)));
        DibujoAvatar.dibujar(g, avatar, CENTRO_AVATAR_X, CENTRO_AVATAR_Y, RADIO_AVATAR);
        g.setStroke(new BasicStroke(3f));
        g.setColor(new Color(0xF4E3BF));
        g.draw(new Ellipse2D.Double(CENTRO_AVATAR_X - RADIO_AVATAR, CENTRO_AVATAR_Y - RADIO_AVATAR,
                2 * RADIO_AVATAR, 2 * RADIO_AVATAR));

  
        Font fTitulo = Estilo.fuente(Font.BOLD, 36);
        String t = "MI PERFIL";
        Estilo.dibujarTextoDegradado(g, t, fTitulo, 362, 106, new Color(0xF0D2A8), new Color(0xC79868), 2);

        franja(g, 124, "ESTADÍSTICAS RÁPIDAS", null, true);
        franja(g, 176, "VICTORIAS: ", estadisticas.getPorcentajeVictorias() + "%", false);
        franja(g, 228, "RACHA ACTUAL: ", String.valueOf(estadisticas.getRachaActual()), true);
        g.dispose();
    }


    private void franja(Graphics2D g, int y, String etiqueta, String valor, boolean sombreada) {
        if (sombreada) {
            g.setColor(new Color(255, 225, 190, 22));
            g.fillRect(342, y, 311, 46);
        }
        String completo = valor == null ? etiqueta : etiqueta + valor;
        float tam = Estilo.tamanoQueCabe(g, Font.BOLD, 28, 290, completo);
        Font fe = Estilo.fuente(Font.PLAIN, tam);
        Font fv = Estilo.fuente(Font.BOLD, tam + 2);
        FontMetrics m = g.getFontMetrics(fe);
        int base = y + (46 - m.getHeight()) / 2 + m.getAscent();
        Color c = valor == null ? new Color(0xE2B88A) : Estilo.CREMA;
        Estilo.dibujarTextoSombra(g, etiqueta, fe, c, 362, base);
        if (valor != null) {
            Estilo.dibujarTextoSombra(g, valor, fv, Estilo.CREMA, 362 + m.stringWidth(etiqueta), base);
        }
    }
}
