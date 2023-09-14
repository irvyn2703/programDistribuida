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
import java.util.Iterator;
import java.util.List;

public class Middleware extends Thread{
    private ArrayList<ArchivoGlobales> archivoGlobal = new ArrayList<>(); // un array donde guargamos cada archivo
    private UDP servidor;
    private VerArchivos archivosLocales;
    public boolean[] listaObtenida = new boolean[1]; // controla si obtuvimos las listas (se inicializa en false)
    private ActualizarMiddle actualizarTTL;
    private String archivoLong = System.getProperty("user.dir") + "\\DNS\\longGlobal.inf";;

    Middleware(UDP serv) {
        servidor = serv;
        actualizarTTL = new ActualizarMiddle(servidor, archivoGlobal);
        actualizarTTL.vincularArchivos(archivoGlobal);
        cargarLong();
    }

    @Override
    public void run() {
        // obtenemos las listas de los demas usuarios
        System.out.println("iniciando middleware");
        while (listaObtenida[0] == false /*|| listaObtenida[1] == false || listaObtenida[2] == false || listaObtenida[3] == false*/) {
            try {
                sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            for (int i = 0; i < listaObtenida.length; i++) {
                if (!listaObtenida[i]) {
                    System.out.println("pidiendo listas de los demás equipos");
                    // Solicitamos la lista de la otra pc
                    servidor.enviarMensaje("200", archivosLocales.ipPc[i], 5000);
        
                    // Esperamos hasta recibir la respuesta o hasta que pase un tiempo máximo
                    long tiempoInicial = System.currentTimeMillis();
                    long tiempoMaximoEspera = 8000; // Tiempo máximo de espera en milisegundos (30 segundos)
        
                    while (!listaObtenida[i] && System.currentTimeMillis() - tiempoInicial < tiempoMaximoEspera) {
                        // Aquí puedes agregar una espera corta (por ejemplo, sleep) para evitar un bucle de CPU al esperar.
                        // Pero ten en cuenta que podría aumentar el tiempo total de espera.
                    }
                }
            }
        }

        System.out.println("lista global obtenida");
        actualizarTTL.start();
    }

    public void procesarMensaje(String message, DatagramPacket receivePacket){
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        System.out.println("Mensaje recibido en el servidor desde " + clientAddress + ", " + clientPort + ":" + message);
        String[] elementos = message.split(",");
        int primerNumero = 0;
        List<String> elementosRestantes = new ArrayList<String>();

        if (elementos.length > 0) {
            try {
                primerNumero = Integer.parseInt(elementos[0].trim()); // Intenta convertir el primer elemento en un entero

                // Si no hay errores en la conversión, el primer elemento es un número
                System.out.println("codigo: " + primerNumero);

                for (int i = 1; i < elementos.length; i++) {
                    String[] elementos2 = elementos[i].split("\\.");
                    elementosRestantes.add(elementos2[0]); // Agrega el nombre
                    elementosRestantes.add(elementos2[1]); // Agrega la extensión
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
                    String mensaje = "101," + nombreArchivo + "." + extensionArchivo;
                    enviarMensaje(mensaje, clientAddress, clientPort); // respuesta autoritativa de mi servidor
                }else{
                    if (archivoExiste(nombreArchivo,extensionArchivo) == true){ 
                        String mensaje = "101," + nombreArchivo + "." + extensionArchivo; // respuesta autoritativa de otra maquina
                        enviarMensaje(mensaje, clientAddress, clientPort);
                    }else{
                        enviarMensaje("102", clientAddress, clientPort);// Nack el archivo no fue encontrado
                    }
                }
                break;
            case 200: // codigo para enviar la lista local
                enviarMensaje(("201," + archivosLocales.obtenerArchivosPublicados()), clientAddress, clientPort);
                break;

            case 201: // codigo para recibir la lista
                for (int i = 0; i < elementosRestantes.size(); i = i + 2) {
                    System.out.println("agregando " + elementosRestantes.get(i) + "." + elementosRestantes.get(i+1));
                    agregarArchivoGlobal(elementosRestantes.get(i), elementosRestantes.get(i+1), clientAddress);
                }
                for (int i = 0; i < archivosLocales.ipPc.length; i++) {
                    if (archivosLocales.ipPc[i].equals(clientAddress)) {
                        listaObtenida[i] = true;
                    }
                }

            case 300: // codigo para agregar archivo a la lista global
                agregarArchivoGlobal(elementosRestantes.get(0), elementosRestantes.get(1), clientAddress);
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
                    int ttl = Integer.parseInt(parts[3]);
    
                    // Creamos un objeto ArchivoGlobales y lo agregamos a la lista archivoGlobal
                    archivoGlobal.add(new ArchivoGlobales(parts[0], parts[1], ip, ttl));
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
    boolean res = false;
    for (ArchivoGlobales archivo : archivoGlobal) {
        if (archivo.nombre.equals(nombreArchivo) && archivo.extension.equals(extensionArchivo)) {
            // Aquí se envía un mensaje y se espera una respuesta
            try (Socket socket = new Socket(archivo.IP, 5000)) {
                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

                // Enviar un mensaje al servidor
                outStream.writeObject("100" + "," + nombreArchivo + "." + extensionArchivo);

                // Esperar la respuesta del servidor
                String respuesta = (String) inStream.readObject();

                // Procesar la respuesta
                if (respuesta.equals("101" + "," + nombreArchivo + "." + extensionArchivo)) {
                    res = true;
                }else{
                    eliminarArchivoGlobal(nombreArchivo, extensionArchivo);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    return res;
    }  

    public void agregarArchivoGlobal(String nombre, String extension, InetAddress ip) {
        // ArchivoGlobales archivo = new ArchivoGlobales(nombre, extension, ip, ttl);
        ArchivoGlobales archivo = new ArchivoGlobales(nombre, extension, ip, 5000);
        archivoGlobal.add(archivo);
    }

    public void eliminarArchivoGlobal(String nombre, String extension) {
        ArchivoGlobales archivoEliminar = null;
    
        for (ArchivoGlobales archivo : archivoGlobal) {
            if (archivo.nombre.equals(nombre) && archivo.extension.equals(extension)) {
                archivoEliminar = archivo;
                break; // Encontramos el archivo, salimos del bucle
            }
        }
    
        if (archivoEliminar != null) {
            archivoGlobal.remove(archivoEliminar);
        }
    }
    
    public void nuevoArchivoLocal(String nombre, String extension){
        for (int i = 0; i < archivosLocales.ipPc.length; i++) {
            if (listaObtenida[0] == true /*|| listaObtenida[1] == false || listaObtenida[2] == false || listaObtenida[3] == false*/) {
                enviarMensaje("300," + nombre + "." + extension, archivosLocales.ipPc[i], 5000);
            }
        }
    }
}
