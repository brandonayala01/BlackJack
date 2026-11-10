package controller;

import model.Ronda;
import model.Usuario;
import persistence.HistorialRepositorio;
import persistence.PartidaRepositorio;
import persistence.UsuarioRepositorio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ControladorJuego {

    public static final int[] FICHAS = {1, 5, 10, 25, 50, 100};
    public static final int[] RECARGAS = {5, 10, 20, 30, 50, 100};

    private UsuarioRepositorio usuarios;
    private HistorialRepositorio historial;
    private PartidaRepositorio partidas;

    private Usuario usuario;
    private Ronda ronda;
    private boolean registroGuardado; 

    public ControladorJuego(UsuarioRepositorio usuarios, HistorialRepositorio historial,
                            PartidaRepositorio partidas) {
        this.usuarios = usuarios;
        this.historial = historial;
        this.partidas = partidas;
    }

    public void iniciar(Usuario usuarioNuevo) {
        if (usuario == usuarioNuevo && ronda != null) {
            return;
        }
        usuario = usuarioNuevo;
        Ronda guardada = partidas.obtener(usuario);
        if (guardada != null) {
            ronda = guardada;
        } else {
            ronda = new Ronda(usuario);
        }
        registroGuardado = false;
    }

    // ---------------------------------------------------------------
    // APUESTA
    // ---------------------------------------------------------------

    public boolean agregarFicha(int valor) {
        return ronda.agregarFicha(valor);
    }

    public void limpiarApuesta() {
        ronda.limpiarApuesta();
    }

    public void repartir() {
        ronda.repartir();
        actualizarGuardado();
    }

    // ---------------------------------------------------------------
    // ACCIONES DEL JUGADOR
    // ---------------------------------------------------------------

    public void pedir() {
        ronda.pedir();
        actualizarGuardado();
    }

    public void plantarse() {
        ronda.plantarse();
        actualizarGuardado();
    }

    public void doblar() {
        ronda.doblar();
        actualizarGuardado();
    }

    public void dividir() {
        ronda.dividir();
        actualizarGuardado();
    }

    // ---------------------------------------------------------------
    // TURNO DEL CRUPIER
    // ---------------------------------------------------------------

    public boolean pasoCrupier() {
        if (ronda.crupierDebePedir()) {
            ronda.crupierPide();
            actualizarGuardado();
            return true;
        }
        ronda.finalizarRonda();
        actualizarGuardado();
        return false;
    }

    // ---------------------------------------------------------------
    // DESPUÉS DE LA RONDA
    // ---------------------------------------------------------------

    public void nuevaRonda() {
        ronda.nuevaRonda();
        registroGuardado = false;
    }

    public void volverAlMenu() {
        if (ronda.getFase().equals(Ronda.FASE_TERMINADA)) {
            nuevaRonda();
        }
        guardarTodo();
    }

    public boolean recargarSaldo(int monto) {
        if (monto <= 0) {
            return false;
        }
        usuario.agregarSaldo(monto);
        usuarios.guardar();
        return true;
    }

    public void guardarTodo() {
        if (ronda != null) {
            actualizarGuardado();
        }
    }

    // ---------------------------------------------------------------
    // GUARDADO INTERNO
    // ---------------------------------------------------------------

    private void actualizarGuardado() {
        usuarios.guardar();

        String fase = ronda.getFase();
        if (fase.equals(Ronda.FASE_TERMINADA)) {
            if (!registroGuardado) {
                registrarRonda();
            }
            partidas.eliminar(usuario.getNombre());
        } else if (fase.equals(Ronda.FASE_JUGADOR) || fase.equals(Ronda.FASE_CRUPIER)) {
            partidas.guardar(usuario, ronda);
        }
    }

    private void registrarRonda() {
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        historial.agregar(ronda.crearRegistro(historial.siguienteId(), fechaHora));
        registroGuardado = true;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}