package co.optimak.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hashear y verificar contraseñas con BCrypt.
 * BCrypt es el algoritmo correcto para contraseñas: incluye sal aleatoria
 * y es deliberadamente lento para dificultar ataques de fuerza bruta.
 */
public class HashUtil {

    public static String hashear(String contrasena) {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt(10));
    }

    public static boolean verificar(String contrasena, String hash) {
        if (contrasena == null || hash == null) return false;
        return BCrypt.checkpw(contrasena, hash);
    }
}
