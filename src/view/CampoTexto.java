package view;

import java.awt.Graphics;
import javax.swing.JTextField;


public class CampoTexto extends JTextField {

    public CampoTexto() {
        Estilo.configurarCampo(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Estilo.pintarFondoCampo(g, this);
        super.paintComponent(g);
    }
}