package view;

import controller.ControladorPrincipal;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public class VentanaPrincipal extends JFrame {

    private static final String MENU = "MENU";
    private static final String LOGIN = "LOGIN";
    private static final String REGISTRO = "REGISTRO";
    private static final String MESA = "MESA";

    private final ControladorPrincipal controlador;

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);

    private final PanelLogin panelLogin;
    private final PanelRegistro panelRegistro;
    private final PanelMesa panelMesa;

    public VentanaPrincipal(ControladorPrincipal controlador) {
        super("Blackjack Royale");
        this.controlador = controlador;

        panelLogin = new PanelLogin(this, controlador.getAutenticacion());
        panelRegistro = new PanelRegistro(this, controlador.getAutenticacion());
        panelMesa = new PanelMesa(this);

        contenedor.add(new PanelMenu(this), MENU);
        contenedor.add(panelLogin, LOGIN);
        contenedor.add(panelRegistro, REGISTRO);
        contenedor.add(panelMesa, MESA);

        setContentPane(contenedor);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        mostrarMenu();
    }

    public void mostrarMenu() {
        cartas.show(contenedor, MENU);
    }

    public void mostrarLogin() {
        cartas.show(contenedor, LOGIN);
        panelLogin.alMostrar(); 
    }

    public void mostrarRegistro() {
        panelRegistro.alMostrar();
        cartas.show(contenedor, REGISTRO);
        panelRegistro.alMostrar();
    }

    public void elegirDesdeMenu(String destino) {
        controlador.elegirDestino(destino);
        if (controlador.getAutenticacion().haySesion()) {
            abrirMesa();
        } else {
            mostrarLogin();
        }
    }

    public void alIniciarSesion() {
        abrirMesa();
    }

    public void abrirMesa() {
        panelMesa.alMostrar();
        cartas.show(contenedor, MESA);
    }

    public void volverAlMenu() {
        panelMesa.detener();
        controlador.getJuego().volverAlMenu();
        mostrarMenu();
    }

    public void cerrarSesion() {
        panelMesa.detener();
        controlador.getJuego().volverAlMenu();
        controlador.getAutenticacion().cerrarSesion();
        mostrarMenu();
    }

    public void salir() {
        controlador.guardarTodo();
        dispose();
        System.exit(0);
    }

    public ControladorPrincipal getControlador() {
        return controlador;
    }

    public static void atajoEscape(JComponent panel, final Runnable accion) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        panel.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }
}
