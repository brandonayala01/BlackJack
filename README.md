# Blackjack Royale

Proyecto de blackjack en Java Swing, listo para **Eclipse IDE**.

## Abrir en Eclipse

1. Descomprime el .zip.
2. En Eclipse: **File > Import > General > Existing Projects into Workspace**.
3. En *Select root directory* elige la carpeta `BlackjackRoyale` y pulsa **Finish**.
4. Ejecuta `src/Main.java` con clic derecho > **Run As > Java Application**
   (o usa la configuración `BlackjackRoyale.launch` incluida).

Gson (`lib/gson-2.10.1.jar`) ya está en el Build Path y la codificación del proyecto es UTF-8.
Los datos se guardan en `datos/` y los avatares están en `recursos/avatares/`.

## Paquetes

- `Main`: punto de entrada.
- `model`: reglas del juego (`Ronda`, `Mazo`, `Mano`, `Crupier`, `Usuario`...).
- `controller`: controladores entre la vista y el modelo.
- `persistence`: lectura y escritura de los JSON con Gson.
- `view`: pantallas y componentes Swing. Los hilos del reparto y del crupier están en `PanelMesa`.
