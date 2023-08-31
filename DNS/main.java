package DNS;
// librerias para ser cliente
/*
import java.net.InetAddress;
import java.net.UnknownHostException;
*/
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class main {
    public static void main(String[] args){
        UDP servidor = new UDP();

        String direccion = "";
        boolean primerInicio = true;


        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\DNS\\config.inf"))) {
            primerInicio = false;
            String line;
            int lineCount = 0; // Variable para llevar el conteo de líneas
    
            while ((line = reader.readLine()) != null || lineCount < 3) {
                lineCount++;
                if (lineCount == 2) {
                    direccion = line;
                    System.out.println("direccion previa: " + direccion);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("primer inicio del programa");
            primerInicio = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (primerInicio == true) {
            SwingUtilities.invokeLater(() -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
                int result = fileChooser.showOpenDialog(null);
    
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    VerArchivos verArchivos = new VerArchivos(selectedDirectory.getAbsolutePath());
                    verArchivos.start();
                    SwingUtilities.invokeLater(() -> {
                        MenuGrafico menu = new MenuGrafico(verArchivos);
                        verArchivos.agregarMenu(menu);
                        menu.setVisible(true);
                    });
                }
            });
        }else{
            VerArchivos verArchivos = new VerArchivos(direccion);
            verArchivos.start();
            SwingUtilities.invokeLater(() -> {
                MenuGrafico menu = new MenuGrafico(verArchivos);
                verArchivos.agregarMenu(menu);
                menu.setVisible(true);
            });
        }

        
        servidor.start();

        /*
        InetAddress serverAddress;
        try {
            serverAddress = InetAddress.getByName("localhost");
            servidor.enviarMensaje("Hola desde el cliente", serverAddress, 50000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        */
    }
}
