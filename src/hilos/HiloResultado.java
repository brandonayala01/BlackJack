package hilos;

public class HiloResultado extends HiloAnimacion {

    private static final int RETARDO = 1000;

    private final Runnable mostrarResultado;

    public HiloResultado(Runnable mostrarResultado) {
        super("hilo-resultado");
        this.mostrarResultado = mostrarResultado;
    }

    @Override
    public void run() {
        if (pausa(RETARDO)) {
            enPantalla(mostrarResultado);
        }
    }
}
