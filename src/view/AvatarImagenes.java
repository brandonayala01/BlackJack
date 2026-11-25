
package view;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

final class AvatarImagenes {

    private static final int TOTAL = 6;
    private static final BufferedImage[] IMAGENES = cargar();

    private AvatarImagenes() {
    }

    private static BufferedImage[] cargar() {
        BufferedImage[] imagenes = new BufferedImage[TOTAL];

        for (int i = 0; i < TOTAL; i++) {
            String ruta = "/recursos/avatares/avatar_" + (i + 1) + ".jpg";

            try (InputStream entrada =
                    AvatarImagenes.class.getResourceAsStream(ruta)) {

                if (entrada != null) {
                    imagenes[i] = ImageIO.read(entrada);
                }

            } catch (IOException e) {
                imagenes[i] = null;
            }
        }

        return imagenes;
    }

    static boolean dibujar(Graphics2D g0, int indice,
            double cx, double cy, double radio) {

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
