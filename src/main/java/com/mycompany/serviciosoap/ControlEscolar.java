package com.mycompany.serviciosoap;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;


@WebService
public class ControlEscolar {

    private static final String DB_URL = "jdbc:mysql://:3306/escuela";
    private static final String DB_USUARIO = "root";
    private static final String DB_PASS = "password";

    @WebMethod
    public boolean verificarUsuario(
        @WebParam(name = "correo") String correo,
        @WebParam(name = "ocupacion") String ocupacion,
        @WebParam(name = "contraseña") String contraseña) {
        
        boolean existe = false;

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
             PreparedStatement consulta = conexion.prepareStatement(
                 "SELECT 1 FROM " + ocupacion + " WHERE BINARY correo=? AND BINARY contraseña=?")) {
            
            consulta.setString(1, correo);
            consulta.setString(2, contraseña);

            ResultSet resultSet = consulta.executeQuery();
            existe = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    @WebMethod
    public Persona obtenerPersona(
        @WebParam(name = "correo") String correo,
        @WebParam(name = "ocupacion") String ocupacion) {
        
        Persona persona = null;

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
             PreparedStatement consulta = conexion.prepareStatement(
                 "SELECT * FROM " + ocupacion + " WHERE BINARY correo=?")) {
            
            consulta.setString(1, correo);

            ResultSet resultSet = consulta.executeQuery();
            if (resultSet.next()) {
                persona = new Persona();
                persona.setId(resultSet.getInt("id"));
                persona.setNombre(resultSet.getString("nombre"));
                persona.setCorreo(resultSet.getString("correo"));
                persona.setContraseña(resultSet.getString("contraseña"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return persona;
    }

    @WebMethod
public boolean registrarPersona(
    @WebParam(name = "nombre") String nombre,
    @WebParam(name = "contraseña") String contraseña,
    @WebParam(name = "ocupacion") String ocupacion) {

    boolean registrado = false;

    try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
         PreparedStatement consulta = conexion.prepareStatement(
             "INSERT INTO " + ocupacion + " (nombre, contraseña) VALUES (?, ?)",
             Statement.RETURN_GENERATED_KEYS)) { 

        consulta.setString(1, nombre);
        consulta.setString(2, contraseña);

        int filas = consulta.executeUpdate();
        registrado = filas > 0;

        try (ResultSet rs = consulta.getGeneratedKeys()) {
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                String correo = ocupacion + idGenerado + "@mail.com";

                try (PreparedStatement actualizar = conexion.prepareStatement(
                        "UPDATE " + ocupacion + " SET correo = ? WHERE id = ?")) {

                    actualizar.setString(1, correo);
                    actualizar.setInt(2, idGenerado);
                    actualizar.executeUpdate();
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return registrado;
}

    
    
    @WebMethod
    public boolean darPermiso(
        @WebParam(name = "ID") String ID,
        @WebParam(name = "ocupacion") String ocupacion){

        boolean modificado = false;

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
             PreparedStatement consulta = conexion.prepareStatement(
                 "UPDATE admin SET admin_"+ ocupacion + " =1 WHERE id=?")) {

            consulta.setString(1, ID);

            modificado = consulta.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modificado;
    }
    @WebMethod
    public boolean quitarPermiso(
        @WebParam(name = "ID") String ID,
        @WebParam(name = "ocupacion") String ocupacion){

        boolean modificado = false;

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
             PreparedStatement consulta = conexion.prepareStatement(
                 "UPDATE admin SET admin_"+ ocupacion + " =0 WHERE id=?")) {

            consulta.setString(1, ID);

            modificado = consulta.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modificado;
    }
    @WebMethod
    public boolean actualizarNombre(
        @WebParam(name = "ID") String ID,
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "ocupacion") String ocupacion){

        boolean modificado = false;

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
             PreparedStatement consulta = conexion.prepareStatement(
                 "UPDATE "+ ocupacion +" SET  nombre=? WHERE id=?")) {

            consulta.setString(1, nombre);
            consulta.setString(2, ID);

            modificado = consulta.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modificado;
    }
@WebMethod
public String consultarPermisos(
    @WebParam(name = "ID") String ID) {

    String permisos = "";

    try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
         PreparedStatement consulta = conexion.prepareStatement(
             "SELECT * FROM admin WHERE id=?")) {

        consulta.setString(1, ID);
        ResultSet rs = consulta.executeQuery();

        if (rs.next()) {
            if (rs.getInt("admin_alumno") == 1) {
                permisos += "alumno;";
            }
            if (rs.getInt("admin_profesor") == 1) {
                permisos += "profesor;";
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return permisos;
}
@WebMethod
public boolean modificarPersona(
    @WebParam(name = "ID") int ID,
    @WebParam(name = "nombre") String nombre,
    @WebParam(name = "correo") String correo,
    @WebParam(name = "contraseña") String contraseña,
    @WebParam(name = "ocupacion") String ocupacion) {

    boolean modificado = false;

    try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_PASS);
         PreparedStatement consulta = conexion.prepareStatement(
             "UPDATE " + ocupacion + " SET nombre=?, correo=?, contraseña=? WHERE id=?")) {

        consulta.setString(1, nombre);
        consulta.setString(2, correo);
        consulta.setString(3, contraseña);
        consulta.setInt(4, ID);

        modificado = consulta.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return modificado;
}


}
