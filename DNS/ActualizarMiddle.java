package DNS;

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
        
        // Quiero que cuente el tiempo
        for (ArchivoGlobales archivoGlobales2 : archivoGlobales) {
            if (archivoGlobales2.TTL != 0) {
                // Verifica si el cociente entre tiempo y TTL es un número entero ejmplo 1000/1000 = 1 1500/1000 = 1.5
                if (tiempo % archivoGlobales2.TTL == 0) {
                    servidor.enviarMensaje("mensaje", archivoGlobales2.IP, 5000);
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


    public void actualizarArchivos(ArrayList<ArchivoGlobales> original){
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
