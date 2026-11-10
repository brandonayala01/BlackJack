package hilos;

import javax.swing.SwingUtilities;

public abstract class HiloAnimacion extends Thread {

    private volatile boolean activo = true;

    public HiloAnimacion(String nombre) {
        super(nombre);
        setDaemon(true);   
    }


    public void detener() {
        activo = false;
        interrupt();
    }

    public boolean estaActivo() {
        return activo;
    }

    protected boolean pausa(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            activo = false;
        }
        return activo;
    }

    protected void enPantalla(Runnable tarea) {
        if (!activo) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait(tarea);
        } catch (InterruptedException e) {
            activo = false;
        } catch (Exception e) {
            System.out.println("Error al actualizar la pantalla: " + e.getMessage());
        }
    }
}
