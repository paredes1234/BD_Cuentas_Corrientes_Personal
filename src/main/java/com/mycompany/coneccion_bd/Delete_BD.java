package com.mycompany.coneccion_bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Delete_BD {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/cuentas_corrientes_v3?useSSL=false&serverTimezone=UTC";
        String usuario = "root";
        String clave = "paredes1234";

        try {
            Connection con = DriverManager.getConnection(url, usuario, clave);

            System.out.println("Conexion exitosa a MySQL");
            System.out.println("Base de datos: cuentas_corrientes_v3\n");

            System.out.println("REGISTROS ANTES DE BORRAR");
            mostrarCantidadRegistros(con);

            borrarDatos(con);

            System.out.println("\nREGISTROS DESPUES DE BORRAR");
            mostrarCantidadRegistros(con);

            con.close();

        } catch (SQLException e) {
            System.out.println("Error al borrar datos");
            System.out.println(e.getMessage());
        }
    }

    public static void borrarDatos(Connection con) throws SQLException {

        Statement st = con.createStatement();

        try {
            st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            st.executeUpdate("DELETE FROM R1T_PRESTAMO_MOVIMIENTO");
            st.executeUpdate("DELETE FROM R1T_PRESTAMO_CALCULO");
            st.executeUpdate("DELETE FROM R1T_PRESTAMO");

            st.executeUpdate("DELETE FROM R1M_TRABAJADOR");

            st.executeUpdate("DELETE FROM R1Z_TIPO_TRABAJADOR");
            st.executeUpdate("DELETE FROM R1Z_CENTRO_COSTO");
            st.executeUpdate("DELETE FROM R1Z_ESTADO_TRABAJADOR");
            st.executeUpdate("DELETE FROM R1Z_TIPO_PRESTAMO");
            st.executeUpdate("DELETE FROM R1Z_TIPO_INTERES");
            st.executeUpdate("DELETE FROM R1Z_TIPO_MOVIMIENTO");

            st.executeUpdate("DELETE FROM GZZ_COMPANIA");
            st.executeUpdate("DELETE FROM GZZ_ESTADO_REGISTRO");

            System.out.println("\nDatos borrados correctamente");

        } finally {
            st.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
            st.close();
        }
    }

    public static void mostrarCantidadRegistros(Connection con) throws SQLException {

        contar(con, "GZZ_ESTADO_REGISTRO");
        contar(con, "GZZ_COMPANIA");

        contar(con, "R1Z_TIPO_TRABAJADOR");
        contar(con, "R1Z_CENTRO_COSTO");
        contar(con, "R1Z_ESTADO_TRABAJADOR");
        contar(con, "R1Z_TIPO_PRESTAMO");
        contar(con, "R1Z_TIPO_INTERES");
        contar(con, "R1Z_TIPO_MOVIMIENTO");

        contar(con, "R1M_TRABAJADOR");

        contar(con, "R1T_PRESTAMO");
        contar(con, "R1T_PRESTAMO_MOVIMIENTO");
        contar(con, "R1T_PRESTAMO_CALCULO");
    }

    public static void contar(Connection con, String tabla) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla);

        if (rs.next()) {
            System.out.println(tabla + ": " + rs.getInt(1) + " registros");
        }

        rs.close();
        st.close();
    }
}

    
