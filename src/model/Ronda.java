package model;

import java.util.ArrayList;

public class Ronda {

    public static final String FASE_APUESTA = "APUESTA";
    public static final String FASE_JUGADOR = "JUGADOR";
    public static final String FASE_CRUPIER = "CRUPIER";
    public static final String FASE_TERMINADA = "TERMINADA";

    private static final int MAX_MANOS = 4;

    private transient Usuario usuario;

    private Mazo mazo;
    private Crupier crupier;
    private ArrayList<Mano> manos;
    private ArrayList<ResultadoMano> resultados;
    private int manoActual;
    private int apuestaPendiente;
    private String fase;
    private int gananciaNeta;

    public Ronda(Usuario usuario) {
        this.usuario = usuario;
        this.mazo = new Mazo();
        this.crupier = new Crupier();
        this.manos = new ArrayList<Mano>();
        this.resultados = new ArrayList<ResultadoMano>();
        this.manoActual = 0;
        this.apuestaPendiente = 0;
        this.fase = FASE_APUESTA;
        this.gananciaNeta = 0;
    }

    // ---------------------------------------------------------------
    // FASE DE APUESTA
    // ---------------------------------------------------------------

    public boolean agregarFicha(int valor) {
        if (!fase.equals(FASE_APUESTA)) {
            return false;
        }
        if (apuestaPendiente + valor > usuario.getSaldo()) {
            return false;
        }
        apuestaPendiente += valor;
        return true;
    }

    public void limpiarApuesta() {
        if (fase.equals(FASE_APUESTA)) {
            apuestaPendiente = 0;
        }
    }

    public boolean puedeRepartir() {
        return fase.equals(FASE_APUESTA) && apuestaPendiente > 0;
    }

    public void repartir() {
        if (!puedeRepartir()) {
            return;
        }
        if (mazo.necesitaBarajar()) {
            mazo.barajar();
        }

        usuario.restarSaldo(apuestaPendiente);
        manos.clear();
        resultados.clear();
        gananciaNeta = 0;
        manoActual = 0;
        crupier.nuevaMano();

        Mano mano = new Mano(apuestaPendiente);
        manos.add(mano);
        apuestaPendiente = 0;

        mano.agregarCarta(mazo.repartir());
        crupier.getMano().agregarCarta(mazo.repartir());
        mano.agregarCarta(mazo.repartir());
        crupier.getMano().agregarCarta(mazo.repartir());

        fase = FASE_JUGADOR;

        if (mano.esBlackjack() || crupier.getMano().esBlackjack()) {
            pasarAlCrupier();
        }
    }

    // ---------------------------------------------------------------
    // TURNO DEL JUGADOR
    // ---------------------------------------------------------------

    public boolean puedePedir() {
        return fase.equals(FASE_JUGADOR) && getManoActiva().getPuntaje() < 21;
    }

    public void pedir() {
        if (!puedePedir()) {
            return;
        }
        Mano mano = getManoActiva();
        mano.agregarCarta(mazo.repartir());
        if (mano.getPuntaje() >= 21) {
            siguienteMano();
        }
    }

    public boolean puedePlantarse() {
        return fase.equals(FASE_JUGADOR);
    }

    public void plantarse() {
        if (!puedePlantarse()) {
            return;
        }
        siguienteMano();
    }

    public boolean puedeDoblar() {
        if (!fase.equals(FASE_JUGADOR)) {
            return false;
        }
        Mano mano = getManoActiva();
        return mano.puedeDoblar() && usuario.tieneSaldo(mano.getApuesta());
    }

    public void doblar() {
        if (!puedeDoblar()) {
            return;
        }
        Mano mano = getManoActiva();
        usuario.restarSaldo(mano.getApuesta());
        mano.doblar();
        mano.agregarCarta(mazo.repartir());
        siguienteMano();
    }

    public boolean puedeDividir() {
        if (!fase.equals(FASE_JUGADOR)) {
            return false;
        }
        Mano mano = getManoActiva();
        return manos.size() < MAX_MANOS
                && mano.puedeDividir()
                && usuario.tieneSaldo(mano.getApuesta());
    }

    public void dividir() {
        if (!puedeDividir()) {
            return;
        }
        Mano actual = getManoActiva();
        usuario.restarSaldo(actual.getApuesta());

        Carta segunda = actual.quitarSegundaCarta();
        Mano nueva = new Mano(actual.getApuesta());
        nueva.agregarCarta(segunda);

        actual.setVieneDeDivision(true);
        nueva.setVieneDeDivision(true);

        actual.agregarCarta(mazo.repartir());
        nueva.agregarCarta(mazo.repartir());

        manos.add(manoActual + 1, nueva);

        if (segunda.esAs()) {
            manoActual++;
            siguienteMano();
        } else if (actual.getPuntaje() >= 21) {
            siguienteMano();
        }
    }

    private void siguienteMano() {
        manoActual++;
        if (manoActual >= manos.size()) {
            pasarAlCrupier();
        } else if (manos.get(manoActual).getPuntaje() >= 21) {
            siguienteMano();
        }
    }

    // ---------------------------------------------------------------
    // TURNO DEL CRUPIER
    // ---------------------------------------------------------------

    private void pasarAlCrupier() {
        crupier.revelar();
        if (crupierDebeJugar()) {
            fase = FASE_CRUPIER;
        } else {
            terminar();
        }
    }

    private boolean crupierDebeJugar() {
        if (crupier.getMano().esBlackjack()) {
            return false;
        }
        for (Mano mano : manos) {
            if (!mano.sePaso() && !mano.esBlackjack()) {
                return true;
            }
        }
        return false;
    }

    public boolean crupierDebePedir() {
        return fase.equals(FASE_CRUPIER) && crupier.debePedir();
    }

    public void crupierPide() {
        if (!crupierDebePedir()) {
            return;
        }
        crupier.getMano().agregarCarta(mazo.repartir());
    }

    public void finalizarRonda() {
        if (fase.equals(FASE_CRUPIER)) {
            terminar();
        }
    }

    public void jugarCrupier() {
        while (crupierDebePedir()) {
            crupierPide();
        }
        finalizarRonda();
    }

    // ---------------------------------------------------------------
    // FIN DE LA RONDA
    // ---------------------------------------------------------------

    private void terminar() {
        resultados.clear();
        gananciaNeta = 0;

        for (Mano mano : manos) {
            ResultadoMano resultado = new ResultadoMano(mano, crupier.getMano());
            resultados.add(resultado);
            usuario.agregarSaldo(resultado.getPago());
            gananciaNeta += resultado.getGanancia();
        }

        usuario.getEstadisticas().registrarRonda(gananciaNeta);
        fase = FASE_TERMINADA;
    }

    public void nuevaRonda() {
        if (!fase.equals(FASE_TERMINADA)) {
            return;
        }
        manos.clear();
        resultados.clear();
        crupier.nuevaMano();
        manoActual = 0;
        apuestaPendiente = 0;
        gananciaNeta = 0;
        fase = FASE_APUESTA;
    }

    public String getTituloResultado() {
        if (gananciaNeta > 0) {
            return "¡GANASTE LA RONDA!";
        }
        if (gananciaNeta < 0) {
            return "PERDISTE LA RONDA";
        }
        return "EMPATASTE LA RONDA";
    }

    public String getDetalleResultado() {
        int total = getApuestaTotal();
        if (gananciaNeta > 0) {
            return "(Ganas " + gananciaNeta + " fichas de la apuesta de " + total + ")";
        }
        if (gananciaNeta < 0) {
            return "(Pierdes " + (-gananciaNeta) + " fichas de la apuesta de " + total + ")";
        }
        return "(Recuperas tu apuesta de " + total + " fichas)";
    }

    public RegistroPartida crearRegistro(int id, String fechaHora) {
        String manosTexto = "";
        for (int i = 0; i < manos.size(); i++) {
            if (i > 0) {
                manosTexto += " / ";
            }
            manosTexto += manos.get(i).getPuntaje();
        }

        String texto = "Empate";
        if (gananciaNeta > 0) {
            texto = "Gana";
        } else if (gananciaNeta < 0) {
            texto = "Pierde";
        }

        return new RegistroPartida(id, usuario.getNombre(), fechaHora, getApuestaTotal(),
                manosTexto, crupier.getMano().getPuntaje(), gananciaNeta, texto);
    }

    // ---------------------------------------------------------------
    // CONSULTAS PARA LA VISTA Y EL CONTROLADOR
    // ---------------------------------------------------------------

    public Mano getManoActiva() {
        if (manoActual >= 0 && manoActual < manos.size()) {
            return manos.get(manoActual);
        }
        return null;
    }


    public int getApuestaTotal() {
        if (fase.equals(FASE_APUESTA)) {
            return apuestaPendiente;
        }
        int total = 0;
        for (Mano mano : manos) {
            total += mano.getApuesta();
        }
        return total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFase() {
        return fase;
    }

    public Crupier getCrupier() {
        return crupier;
    }

    public ArrayList<Mano> getManos() {
        return manos;
    }

    public int getIndiceManoActual() {
        return manoActual;
    }

    public ArrayList<ResultadoMano> getResultados() {
        return resultados;
    }

    public int getGananciaNeta() {
        return gananciaNeta;
    }

    public int getApuestaPendiente() {
        return apuestaPendiente;
    }
}