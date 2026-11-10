package view;

import java.awt.Graphics;
import javax.swing.JPasswordField;

public class CampoClave extends JPasswordField {

    public CampoClave() {
        Estilo.configurarCampo(this);
        setEchoChar('\u2022');
    }

    @Override
    protected void paintComponent(Graphics g) {
        Estilo.pintarFondoCampo(g, this);
        super.paintComponent(g);
    }
}