package DNS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

class ActualizarMiddle extends Thread{
    private ArrayList<ArchivoGlobales> archivoGlobales;
    private UDP servidor;
    private int tiempo = 0;
    private int ttlMaximo = 0;
    public int estadoPeticion = 0; // 0 = ninguna respuesta, 1 = respuesta afirmativa, 2 = respuesta negativa


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
            
            Iterator<ArchivoGlobales> iterator = archivoGlobales.iterator();
            while (iterator.hasNext()) {
                ArchivoGlobales archivoGlobales2 = iterator.next();
                if (archivoGlobales2.TTL != 0) {
                    // Verifica si el cociente entre tiempo y TTL es un número entero ejmplo 1000/1000 = 1 1500/1000 = 1.5
                    if (tiempo % archivoGlobales2.TTL == 0) {
                        System.out.println("verificando " + archivoGlobales2.nombre + "." + archivoGlobales2.extension);
                        estadoPeticion = 0;
                        servidor.enviarMensaje("100," + archivoGlobales2.nombre + "." + archivoGlobales2.extension, archivoGlobales2.IP, 5000);

                        // Esperamos hasta recibir la respuesta o hasta que pase un tiempo máximo
                        long tiempoInicial = System.currentTimeMillis();
                        long tiempoMaximoEspera = 8000; // Tiempo máximo de espera en milisegundos (30 segundos)

                        while (estadoPeticion == 0 && System.currentTimeMillis() - tiempoInicial < tiempoMaximoEspera) {
                            try {
                                Thread.sleep(10); // Pausa de 10 milisegundos (puedes ajustar el valor según tus necesidades)
                            } catch (InterruptedException e) {
                                // Manejo de la excepción, si es necesario
                            }
                        }
                        
                        // Verificamos el estado y eliminamos si es necesario
                        if (estadoPeticion == 1) {
                            System.out.println("archivo: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension + "   --->   verificado");
                        } else if (estadoPeticion == 2) {
                            System.out.println("archivo: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension + "   --->   fue eliminado");
                            iterator.remove(); // Eliminamos el elemento usando el iterador
                            System.out.println("Se eliminó el archivo global: " + archivoGlobales2.nombre + "." + archivoGlobales2.extension);
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
