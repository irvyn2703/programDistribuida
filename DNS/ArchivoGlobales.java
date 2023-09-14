package DNS;

import java.net.InetAddress;

public class ArchivoGlobales {
    String nombre;
    String extension;
    InetAddress IP;
    int TTL;

    public ArchivoGlobales(String nombre, String extension, InetAddress IP, int TTL) {
        this.nombre = nombre;
        this.extension = extension;
        this.IP = IP;
        this.TTL = TTL;
    }
}
