package view;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

// Carga los retratos una sola vez y los recorta de forma circular al pintarlos.
final class AvatarImagenes {

    private static final int TOTAL = 6;
    private static final BufferedImage[] IMAGENES = cargar();

    private AvatarImagenes() {
    }

    private static BufferedImage[] cargar() {
        BufferedImage[] imagenes = new BufferedImage[TOTAL];
        Path[] carpetas = {
            Paths.get("recursos", "avatares"),
            Paths.get("src", "recursos", "avatares")
        };

        for (int i = 0; i < imagenes.length; i++) {
            String nombre = "avatar_" + (i + 1) + ".jpg";
            for (Path carpeta : carpetas) {
                Path archivo = carpeta.resolve(nombre);
                if (Files.isRegularFile(archivo)) {
                    try {
                        imagenes[i] = ImageIO.read(archivo.toFile());
                    } catch (IOException e) {
                        imagenes[i] = null;
                    }
                    if (imagenes[i] != null) {
                        break;
                    }
                }
            }
        }
        return imagenes;
    }

    static boolean dibujar(Graphics2D g0, int indice, double cx, double cy, double radio) {
        int index = ((indice % TOTAL) + TOTAL) % TOTAL;
        BufferedImage imagen = IMAGENES[index];
        if (imagen == null) {
            return false;
        }

        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizado(g);
        int diametro = (int) Math.round(radio * 2);
        int x = (int) Math.round(cx - radio);
        int y = (int) Math.round(cy - radio);
        g.clip(new Ellipse2D.Double(x, y, diametro, diametro));
        g.drawImage(imagen, x, y, diametro, diametro, null);
        g.dispose();
        return true;
    }
}
