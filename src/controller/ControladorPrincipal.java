package controller;

import persistence.HistorialRepositorio;
import persistence.PartidaRepositorio;
import persistence.UsuarioRepositorio;

// Punto de unión de la aplicación: crea todo y se lo entrega a la vista.
public class ControladorPrincipal {

    // Lo que el jugador eligió en el menú principal.
    public static final String DESTINO_JUGAR = "JUGAR";
    public static final String DESTINO_HISTORIAL = "HISTORIAL";
    public static final String DESTINO_AJUSTES = "AJUSTES";

    private ControladorAutenticacion autenticacion;
    private ControladorJuego juego;
    private ControladorHistorial historial;
    private ControladorPerfil perfil;
    private String destino;

    public ControladorPrincipal() {
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        HistorialRepositorio registros = new HistorialRepositorio();
        PartidaRepositorio partidas = new PartidaRepositorio();

        this.autenticacion = new ControladorAutenticacion(usuarios);
        this.juego = new ControladorJuego(usuarios, registros, partidas);
        this.historial = new ControladorHistorial(registros);
        this.perfil = new ControladorPerfil(usuarios, registros, partidas);
        this.destino = DESTINO_JUGAR;
    }

    // Si el jugador elige Historial o Ajustes sin haber iniciado sesión,
    // se le pide el login y, al entrar, se abre lo que había elegido.
    public void elegirDestino(String destino) {
        this.destino = destino;
    }

    public String getDestino() {
        return destino;
    }

    // Se llama al cerrar la ventana para no perder nada.
    public void guardarTodo() {
        juego.guardarTodo();
    }

    public ControladorAutenticacion getAutenticacion() {
        return autenticacion;
    }

    public ControladorJuego getJuego() {
        return juego;
    }

    public ControladorHistorial getHistorial() {
        return historial;
    }

    public ControladorPerfil getPerfil() {
        return perfil;
    }
}