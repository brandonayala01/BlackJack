package hilos;

import controller.ControladorJuego;

public class HiloCrupier extends HiloAnimacion {

    private static final int RETARDO_CARTA = 800;       
    private static final int RETARDO_RESULTADO = 1000;  

    private final ControladorJuego juego;
    private final Runnable refrescar;
    private final Runnable mostrarResultado;
    private boolean sigue;

    public HiloCrupier(ControladorJuego juego, Runnable refrescar, Runnable mostrarResultado) {
        super("hilo-crupier");
        this.juego = juego;
        this.refrescar = refrescar;
        this.mostrarResultado = mostrarResultado;
    }

    @Override
    public void run() {
        sigue = true;
        while (sigue) {
            if (!pausa(RETARDO_CARTA)) {
                return;
            }
            enPantalla(new Runnable() {
                @Override
                public void run() {
                    sigue = juego.pasoCrupier();
                    refrescar.run();
                }
            });
            if (!estaActivo()) {
                return;
            }
        }

        if (pausa(RETARDO_RESULTADO)) {
            enPantalla(mostrarResultado);
        }
    }
}
