package hilos;

public class HiloReparto extends HiloAnimacion {

    private static final int RETARDO = 260;  
    private static final int PASOS = 4;

    private final Runnable avanzar;   

    public HiloReparto(Runnable avanzar) {
        super("hilo-reparto");
        this.avanzar = avanzar;
    }

    @Override
    public void run() {
        for (int i = 0; i < PASOS; i++) {
            if (!pausa(RETARDO)) {
                return;
            }
            enPantalla(avanzar);
        }
    }
}
