package view;

import controller.ControladorAutenticacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.SwingConstants;

// Pantalla "INICIAR SESIÓN". Los textos de error los entrega ControladorAutenticacion.
public class PanelLogin extends PanelFieltro {

    private final VentanaPrincipal ventana;
    private final ControladorAutenticacion autenticacion;

    private final CampoTexto campoUsuario = new CampoTexto();
    private final CampoClave campoClave = new CampoClave();
    private final Etiqueta etiquetaError;

    public PanelLogin(VentanaPrincipal ventana, ControladorAutenticacion autenticacion) {
        super(true);
        this.ventana = ventana;
        this.autenticacion = autenticacion;

        Etiqueta titulo = new Etiqueta("INICIAR SESIÓN", 36, Estilo.TEXTO, true);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(430, 180, 380, 46);
        add(titulo);

        Etiqueta lblUsuario = new Etiqueta("USUARIO", 24, Estilo.TEXTO_SUAVE, false);
        lblUsuario.setBounds(432, 236, 300, 30);
        add(lblUsuario);
        campoUsuario.setBounds(430, 270, 380, 42);
        add(campoUsuario);

        Etiqueta lblClave = new Etiqueta("CONTRASEÑA", 24, Estilo.TEXTO_SUAVE, false);
        lblClave.setBounds(432, 336, 300, 30);
        add(lblClave);
        campoClave.setBounds(430, 370, 380, 42);
        add(campoClave);

        BotonMetalico ingresar = new BotonMetalico("INGRESAR").tamanoTexto(40).ubicar(420, 430, 400, 74);
        add(ingresar);

        etiquetaError = new Etiqueta("", 22, Estilo.iluminar(Estilo.ROJO, 30), true);
        etiquetaError.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaError.setBounds(270, 514, 700, 34);
        add(etiquetaError);

        Etiqueta nuevo = new Etiqueta("¿NUEVO JUGADOR?", 26, Estilo.TEXTO_SUAVE, false);
        nuevo.setHorizontalAlignment(SwingConstants.CENTER);
        nuevo.setBounds(430, 566, 380, 34);
        add(nuevo);

        BotonMetalico registrarse = new BotonMetalico("REGISTRARSE", BotonMetalico.Tipo.DORADO)
                .tamanoTexto(40).ubicar(422, 604, 400, 74);
        add(registrarse);

        BotonEnlace volver = new BotonEnlace("\u2039 MENÚ PRINCIPAL", 24, Estilo.CREMA, false)
                .alinearDerecha(Estilo.ANCHO - 30, 28);
        add(volver);

        // --- Acciones ---
        ActionListener alIngresar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ingresar();
            }
        };
        ingresar.addActionListener(alIngresar);
        campoClave.addActionListener(alIngresar);          // Enter en la contraseña
        campoUsuario.addActionListener(new ActionListener() {   // Enter en el usuario salta a la contraseña
            @Override
            public void actionPerformed(ActionEvent e) {
                campoClave.requestFocusInWindow();
            }
        });
        registrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelLogin.this.ventana.mostrarRegistro();
            }
        });
        volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelLogin.this.ventana.mostrarMenu();
            }
        });

        OyenteTexto borrarError = new OyenteTexto() {
            @Override
            public void cambio() {
                etiquetaError.setText("");
            }
        };
        campoUsuario.getDocument().addDocumentListener(borrarError);
        campoClave.getDocument().addDocumentListener(borrarError);

        VentanaPrincipal.atajoEscape(this, new Runnable() {
            @Override
            public void run() {
                PanelLogin.this.ventana.mostrarMenu();
            }
        });
    }

    private void ingresar() {
        char[] clave = campoClave.getPassword();
        String error = autenticacion.iniciarSesion(campoUsuario.getText(), new String(clave));
        Arrays.fill(clave, ' ');

        if (error != null) {
            campoClave.setText("");          // primero vaciar: esto dispara borrarError...
            etiquetaError.setText(error);    // ...y por eso el mensaje se pone después
            campoClave.requestFocusInWindow();
            return;
        }
        ventana.alIniciarSesion();
    }

    // Se llama cada vez que la pantalla aparece: deja todo limpio.
    public void alMostrar() {
        campoUsuario.setText("");
        campoClave.setText("");
        etiquetaError.setText("");
        campoUsuario.requestFocusInWindow();
    }
}