package DNS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Middleware extends Thread{
    private ArrayList<ArchivoGlobales> archivoGlobal = new ArrayList<>(); // un array donde guargamos cada archivo
    private UDP servidor;
    private VerArchivos archivosLocales;
    private ActualizarMiddle actualizarTTL;
    private String archivoLong = System.getProperty("user.dir") + "\\DNS\\longGlobal.inf";;

    Middleware(UDP serv) {
        servidor = serv;
        actualizarTTL = new ActualizarMiddle(servidor, archivoGlobal);
        cargarLong();
    }

    @Override
    public void run() {
        actualizarTTL.start();
        while (true) {
            
        }
    }

    public void procesarMensaje(String message, DatagramPacket receivePacket){
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        System.out.println("Mensaje recibido en el servidor desde " + clientAddress + ":" + clientPort + ": " + message);
    }

    public void enviarMensaje(String message, InetAddress destinationAddress, int destinationPort){
        servidor.enviarMensaje(message, destinationAddress, destinationPort);
    }

    public void cargarLong() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoLong))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split de la línea usando comas
                String[] parts = line.split(",");
    
                // Verificamos que haya al menos 5 partes en la línea antes de intentar crear un objeto ArchivoGlobales
                if (parts.length >= 5) {
                    // Parseamos la IP de la cadena de texto
                    InetAddress ip = InetAddress.getByName(parts[2]);
    
                    // Parseamos el TTL de la cadena de texto
                    int ttl = Integer.parseInt(parts[4]);
    
                    // Creamos un objeto ArchivoGlobales y lo agregamos a la lista archivoGlobal
                    archivoGlobal.add(new ArchivoGlobales(parts[0], parts[1], ip, parts[3], ttl));
                    System.out.println("Agregando " + parts[0] + "." + parts[1] + " - IP: " + ip + ", TTL: " + ttl);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("El archivo de registro no existe. Se creará uno nuevo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void vincularArchivos(VerArchivos vincular){
        archivosLocales = vincular;
    }
}
