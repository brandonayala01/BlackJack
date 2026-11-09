package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Un jugador registrado: sus datos, su saldo y sus estadísticas.
// La contraseña NUNCA se guarda en texto: solo su "hash" (huella SHA-256).
public class Usuario {

    public static final int SALDO_INICIAL = 1500;

    private String nombre;
    private String correo;
    private String contrasenaHash;
    private int saldo;
    private int avatar;
    private Estadisticas estadisticas;

    public Usuario(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenaHash = calcularHash(contrasena);
        this.saldo = SALDO_INICIAL;
        this.avatar = 0;
        this.estadisticas = new Estadisticas();
    }

    // Convierte un texto en su huella SHA-256 escrita en hexadecimal.
    public static String calcularHash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte b : bytes) {
                resultado.append(String.format("%02x", b));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 existe en todas las versiones de Java; esto no debería pasar.
            throw new RuntimeException("No se encontró el algoritmo SHA-256");
        }
    }

    // Compara la contraseña escrita con la guardada, comparando huellas.
    public boolean verificarContrasena(String contrasena) {
        return contrasenaHash.equals(calcularHash(contrasena));
    }

    public boolean tieneSaldo(int cantidad) {
        return saldo >= cantidad;
    }

    public void agregarSaldo(int cantidad) {
        saldo += cantidad;
    }

    public void restarSaldo(int cantidad) {
        saldo -= cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public int getSaldo() {
        return saldo;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
}