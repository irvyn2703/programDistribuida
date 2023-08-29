package DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class main {
    public static void main(String[] args){
        String folderPath = "C:\\Users\\irvyn\\OneDrive\\Documents\\beca";
        String logFilePath = "C:\\Users\\irvyn\\OneDrive\\Documents\\beca\\hola.txt";
        UDP servidor = new UDP();

        VerArchivos verArchivos = new VerArchivos(folderPath, logFilePath);
        verArchivos.start();
        servidor.start();

        /*InetAddress serverAddress;
        try {
            serverAddress = InetAddress.getByName("localhost");
            servidor.enviarMensaje("Hola desde el cliente", serverAddress, 50000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
    }
}
