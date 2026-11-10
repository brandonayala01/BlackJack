package model;

public class Crupier {

    private Mano mano;
    private boolean segundaOculta;

    public Crupier() {
        this.mano = new Mano();
        this.segundaOculta = true;
    }

    public void nuevaMano() {
        this.mano = new Mano();
        this.segundaOculta = true;
    }

    public void revelar() {
        segundaOculta = false;
    }

    public boolean isSegundaOculta() {
        return segundaOculta;
    }

    public boolean debePedir() {
        return mano.getPuntaje() < 17;
    }

    public String getTextoPuntaje() {
        if (segundaOculta) {
            return "-";
        }
        return String.valueOf(mano.getPuntaje());
    }

    public Mano getMano() {
        return mano;
    }
}