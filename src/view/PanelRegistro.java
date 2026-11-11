package view;

import controller.ControladorAutenticacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.SwingConstants;

// Pantalla "CREAR CUENTA". Los textos de error los entrega ControladorAutenticacion.
public class PanelRegistro extends PanelFieltro {

    private final VentanaPrincipal ventana;
    private final ControladorAutenticacion autenticacion;

    private final CampoTexto campoUsuario = new CampoTexto();
    private final CampoTexto campoCorreo = new CampoTexto();
    private final CampoClave campoClave = new CampoClave();
    private final CampoClave campoConfirmar = new CampoClave();
    private final Etiqueta etiquetaError;

    public PanelRegistro(VentanaPrincipal ventana, ControladorAutenticacion autenticacion) {
        super(true);
        this.ventana = ventana;
        this.autenticacion = autenticacion;

        Etiqueta titulo = new Etiqueta("CREAR CUENTA", 36, Estilo.TEXTO, true);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(430, 126, 380, 46);
        add(titulo);

        agregarCampo("USUARIO", campoUsuario, 220);
        agregarCampo("CORREO ELECTRÓNICO", campoCorreo, 300);
        agregarCampo("CONTRASEÑA", campoClave, 380);
        agregarCampo("CONFIRMAR CONTRASEÑA", campoConfirmar, 460);

        BotonMetalico registrarse = new BotonMetalico("REGISTRARSE", BotonMetalico.Tipo.DORADO)
                .tamanoTexto(40).ubicar(420, 518, 400, 74);
        add(registrarse);

        etiquetaError = new Etiqueta("", 22, Estilo.iluminar(Estilo.ROJO, 30), true);
        etiquetaError.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaError.setBounds(270, 596, 700, 30);
        add(etiquetaError);

        Etiqueta yaTienes = new Etiqueta("¿YA TIENES CUENTA?", 26, Estilo.CREMA, true);
        yaTienes.setHorizontalAlignment(SwingConstants.CENTER);
        yaTienes.setBounds(430, 628, 380, 32);
        add(yaTienes);

        BotonEnlace iniciar = new BotonEnlace("INICIAR SESIÓN", 26, Estilo.DORADO, true)
                .centrarEn(620, 658);
        add(iniciar);

        BotonEnlace volver = new BotonEnlace("\u2039 MENÚ PRINCIPAL", 24, Estilo.CREMA, false)
                .alinearDerecha(Estilo.ANCHO - 30, 28);
        add(volver);

        // --- Acciones ---
        ActionListener alRegistrar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        };
        registrarse.addActionListener(alRegistrar);
        campoConfirmar.addActionListener(alRegistrar);   // Enter en el último campo
        encadenarEnter(campoUsuario, campoCorreo);
        encadenarEnter(campoCorreo, campoClave);
        encadenarEnter(campoClave, campoConfirmar);

        iniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelRegistro.this.ventana.mostrarLogin();
            }
        });
        volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelRegistro.this.ventana.mostrarMenu();
            }
        });

        OyenteTexto borrarError = new OyenteTexto() {
            @Override
            public void cambio() {
                etiquetaError.setText("");
            }
        };
        campoUsuario.getDocument().addDocumentListener(borrarError);
        campoCorreo.getDocument().addDocumentListener(borrarError);
        campoClave.getDocument().addDocumentListener(borrarError);
        campoConfirmar.getDocument().addDocumentListener(borrarError);

        VentanaPrincipal.atajoEscape(this, new Runnable() {
            @Override
            public void run() {
                PanelRegistro.this.ventana.mostrarMenu();
            }
        });
    }

    // Etiqueta pequeña encima + campo de texto (la etiqueta queda 34 px arriba).
    private void agregarCampo(String texto, javax.swing.JTextField campo, int y) {
        Etiqueta lbl = new Etiqueta(texto, 24, Estilo.TEXTO_SUAVE, false);
        lbl.setBounds(432, y - 34, 360, 30);
        add(lbl);
        campo.setBounds(430, y, 380, 40);
        add(campo);
    }

    private void encadenarEnter(javax.swing.JTextField origen, final javax.swing.JTextField destino) {
        origen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destino.requestFocusInWindow();
            }
        });
    }

    private void registrar() {
        char[] clave = campoClave.getPassword();
        char[] confirmar = campoConfirmar.getPassword();
        String error = autenticacion.registrar(campoUsuario.getText(), campoCorreo.getText(),
                new String(clave), new String(confirmar));
        Arrays.fill(clave, ' ');
        Arrays.fill(confirmar, ' ');

        if (error != null) {
            etiquetaError.setText(error);
            return;
        }
        ventana.alIniciarSesion();   // registrar() ya dejó la sesión iniciada
    }

    public void alMostrar() {
        campoUsuario.setText("");
        campoCorreo.setText("");
        campoClave.setText("");
        campoConfirmar.setText("");
        etiquetaError.setText("");
        campoUsuario.requestFocusInWindow();
    }
}