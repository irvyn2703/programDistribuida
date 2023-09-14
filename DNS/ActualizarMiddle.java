package DNS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ActualizarMiddle extends Thread{
    private ArrayList<ArchivoGlobales> archivoGlobales;
    private UDP servidor;
    private int tiempo = 0;
    private int ttlMaximo = 0;

    // Constructor que acepta UDP y ArrayList<ArchivoGlobales>
    public ActualizarMiddle(UDP serv, ArrayList<ArchivoGlobales> archivos) {
        servidor = serv;
        archivoGlobales = archivos;
    }

    @Override
    public void run() {
        while (true) {
            ttlMaximo = encontrarTTLMaximo();

            // Incrementa el tiempo en 1 milisegundo en cada iteración
            tiempo = tiempo + 1;

            // Si el tiempo supera el TTL máximo, reinicia el tiempo para evitar errores por numeros altos
            if (tiempo > ttlMaximo + 1) {
                tiempo = 0;
            }
            
            for (ArchivoGlobales archivoGlobales2 : archivoGlobales) {
            if (archivoGlobales2.TTL != 0) {
                // Verifica si el cociente entre tiempo y TTL es un número entero ejmplo 1000/1000 = 1 1500/1000 = 1.5
                if (tiempo % archivoGlobales2.TTL == 0) {
                    System.out.println("verificando: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension);
                    try (Socket socket = new Socket(archivoGlobales2.IP, 5000)) {
                        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

                        // Enviar un mensaje al servidor
                        outStream.writeObject("100" + "," + archivoGlobales2.nombre + "." + archivoGlobales2.extension);

                        // Esperar la respuesta del servidor
                        String respuesta = (String) inStream.readObject();

                        // Procesar la respuesta según sea necesario
                        if (respuesta.equals("102")) {
                            // La respuesta es la esperada
                            System.out.println("El archivo: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension + "verificado");
                        } else {
                            // La respuesta no es la esperada
                            archivoGlobales.remove(archivoGlobales2);
                            System.out.println("Se eliminó el archivo global: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
            
            
            try {
                Thread.sleep(1); // Espera 1 milisegundo antes de la siguiente iteración
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void vincularArchivos(ArrayList<ArchivoGlobales> original){
        archivoGlobales = original;
    }

    private int encontrarTTLMaximo() {
        int maximo = 0;
        for (ArchivoGlobales archivo : archivoGlobales) {
            if (archivo.TTL > maximo) {
                maximo = archivo.TTL;
            }
        }
        return maximo;
    }
}
