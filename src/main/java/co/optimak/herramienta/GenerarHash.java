package co.optimak.herramienta;

import co.optimak.util.HashUtil;

/**
 * Herramienta de desarrollo — NO es parte del sistema en producción.
 *
 * Cómo usarla en IntelliJ:
 *   1. Abrir este archivo
 *   2. Clic derecho en el método main → "Run 'GenerarHash.main()'"
 *   3. Copiar los hashes que aparecen en la consola
 *   4. Pegarlos en datos_admin.sql y ejecutar ese script en MySQL Workbench
 */
public class GenerarHash {

    public static void main(String[] args) {
        String[] contrasenas = {"admin123", "operario123"};

        System.out.println("=== Hashes para datos_admin.sql ===");
        for (String c : contrasenas) {
            System.out.println("'" + c + "'  →  " + HashUtil.hashear(c));
        }
        System.out.println("====================================");
        System.out.println("Pegar cada hash entre comillas simples en el INSERT correspondiente.");
    }
}
