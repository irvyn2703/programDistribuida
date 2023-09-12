package DNS;

import java.net.InetAddress;

public class ArchivoGlobales {
    String nombre;
    String extension;
    InetAddress IP;
    String fecha;
    int TTL;

    public ArchivoGlobales(String nombre, String extension, InetAddress IP, String fecha, int TTL) {
        this.nombre = nombre;
        this.extension = extension;
        this.IP = IP;
        this.fecha = fecha; 
        this.TTL = TTL;
    }
}
