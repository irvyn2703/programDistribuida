package DNS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Middleware extends Thread{
    private ArrayList<ArchivoGlobales> archivoGlobal = new ArrayList<>(); // un array donde guargamos cada archivo
    private UDP servidor;
    public boolean[] listaObtenida = new boolean[4]; // controla si obtuvimos las listas (se inicializa en false)
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
        // obtenemos las listas de los demas usuarios
        while (listaObtenida[0] == false || listaObtenida[1] == false || listaObtenida[2] == false || listaObtenida[3] == false) {
            for (int i = 0; i < listaObtenida.length - 1; i++) {
                if (listaObtenida[i] == false) {
                    // solicitamos la lista de la otra pc
                    servidor.enviarMensaje("200",archivosLocales.ipPc[i], 5000);
                }
            }
            try {
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (true) {
            actualizarTTL.start();
        }
    }

    public void procesarMensaje(String message, DatagramPacket receivePacket){
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        System.out.println("Mensaje recibido en el servidor desde " + clientAddress + ", " + clientPort);
        String[] elementos = message.split(",");
        int primerNumero = 0;
        List<String> elementosRestantes = new ArrayList<String>();
    
        if (elementos.length > 0) {
            try {
                primerNumero = Integer.parseInt(elementos[0].trim()); // Intenta convertir el primer elemento en un entero
                
                // Si no hay errores en la conversión, el primer elemento es un número
                System.out.println("codigo: " + primerNumero);

                for (int i = 1; i < elementos.length; i++) {
                    elementosRestantes.add(elementos[i].trim()); // Agrega los elementos restantes a la lista
                }

                System.out.println("Elementos restantes: " + elementosRestantes);
            } catch (NumberFormatException e) {
                // Si ocurre una excepción, el primer elemento no es un número
                System.out.println("Primer elemento no es un número. Valor del primer elemento: " + elementos[0].trim());

                // Puedes manejar esta excepción de acuerdo a tus necesidades
            }
        } else {
            try {
                primerNumero = Integer.parseInt(message);
                System.out.println("codigo: " + primerNumero);

            } catch (NumberFormatException e) {
                System.out.println("Primer elemento no es un número. Valor del primer elemento: " + message);
            }
        }

        switch (primerNumero) {
            case 100: // codigo de busqueda de un archivo
                String nombreArchivo = elementosRestantes.get(0);
                String extensionArchivo = elementosRestantes.get(1);
                if (archivosLocales.archivoExiste(nombreArchivo, extensionArchivo) == true) {
                    String mensaje = "101," + nombreArchivo + "," + extensionArchivo;
                    enviarMensaje(mensaje, clientAddress, clientPort);
                }else{
                    if (archivoExiste(nombreArchivo,extensionArchivo) == true){
                        String mensaje = "101," + nombreArchivo + "," + extensionArchivo;
                        enviarMensaje(mensaje, clientAddress, clientPort);
                    }else{
                        enviarMensaje("202", clientAddress, clientPort);
                    }
                    enviarMensaje(message, clientAddress, primerNumero);
                }
                break;
            case 200:
                break;
        
            default:
                break;
        }
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

    public boolean archivoExiste(String nombreArchivo, String extensionArchivo) {
    for (ArchivoGlobales archivo : archivoGlobal) {
        if (archivo.nombre.equals(nombreArchivo) && archivo.extension.equals(extensionArchivo)) {
            // Aquí se envía un mensaje y se espera una respuesta
            try (Socket socket = new Socket(archivo.IP, 5000)) {
                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

                // Enviar un mensaje al servidor
                outStream.writeObject("100" + "," + nombreArchivo + "," + extensionArchivo);

                // Esperar la respuesta del servidor
                String respuesta = (String) inStream.readObject();

                // Procesar la respuesta
                if (respuesta.equals("101" + "," + nombreArchivo + "," + extensionArchivo)) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    return false;
}   
}
