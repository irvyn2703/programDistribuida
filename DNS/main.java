package DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args){
        UDP servidor = new UDP();

        VerArchivos verArchivos = new VerArchivos();
        verArchivos.start();
        SwingUtilities.invokeLater(() -> {
            MenuGrafico menu = new MenuGrafico(verArchivos);
            verArchivos.agregarMenu(menu);
            menu.setVisible(true);
        });
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
