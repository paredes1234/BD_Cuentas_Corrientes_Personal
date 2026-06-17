package com.mycompany.coneccion_bd;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static Connection conectar() throws SQLException {
        String url = obtenerVariable("DB_URL");
        String usuario = obtenerVariable("DB_USER");
        String clave = obtenerVariable("DB_PASSWORD");

        return DriverManager.getConnection(url, usuario, clave);
    }

    private static String obtenerVariable(String nombre) {
        String valor = dotenv.get(nombre);

        if (valor == null || valor.isBlank()) {
            valor = System.getenv(nombre);
        }

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable: " + nombre);
        }

        return valor;
    }
}