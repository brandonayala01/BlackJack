package view;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public abstract class OyenteTexto implements DocumentListener {

    public abstract void cambio();

    @Override
    public void insertUpdate(DocumentEvent e) {
        cambio();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        cambio();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        cambio();
    }
}