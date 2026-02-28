package com.mycompany.serviciosoap;
import jakarta.xml.ws.Endpoint;

public class Publicador {
    public static void main(String[] args) {
        Endpoint.publish("http://127.0.0.1:8080/escuela", new ControlEscolar());
        System.out.println("Servicio SOAP publicado en http://127.0.0.1:8080/escuela?wsdl");
    }
}
