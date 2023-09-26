package DNS;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class main {
    public static void main(String[] args){
        UDP servidor = new UDP(); // creamos el objeto UDP
        Middleware middleware;
        String direccion = "";// iniciamos la direcion de la carpeta
        boolean primerInicio = true;// controlamos si es el primer inicio
        VerArchivos verArchivos = new VerArchivos();

        middleware = new Middleware(servidor);
        servidor.agregarMiddleware(middleware);
        middleware.vincularArchivos(verArchivos);

        //try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\DNS\\config.inf"))) {
            // linea para linux
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/DNS/config.inf"))) {
            // si entramos aqui entonces el archivo config existe y no es el primer inicio del programa
            primerInicio = false;// no el el primer inicio
            String line;
            int lineCount = 0; // Variable para llevar el conteo de líneas
    
            while ((line = reader.readLine()) != null || lineCount < 3) {
                lineCount++;
                if (lineCount == 2) {
                    direccion = line;// leemos la dirección de la carpeta
                    System.out.println("direccion previa: " + direccion);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("primer inicio del programa");
            primerInicio = true;// es el primer inicio del programa
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (primerInicio == true) {
            SwingUtilities.invokeLater(() -> {// creamos un menu para obtener la direccion de la carpeta compartida
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
                int result = fileChooser.showOpenDialog(null);
    
                if (result == JFileChooser.APPROVE_OPTION) { // cuando tenemos la direccion
                    File selectedDirectory = fileChooser.getSelectedFile();
                    verArchivos.agregarDireccion(selectedDirectory.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {// creamos el menu grafico
                        MenuGrafico menu = new MenuGrafico(verArchivos);// creamo y pasamos el objeto de verArchivos al menu
                        verArchivos.agregarMenu(menu);// vinculamos el menu al objeto de verArchivos
                        verArchivos.start();
                        verArchivos.vincularMiddleware(middleware);
                        menu.setVisible(true);// mostramos el menu
                    });
                }
            });
        }else{// no es el primer inicio del programa
            verArchivos.agregarDireccion(direccion);
            SwingUtilities.invokeLater(() -> {
                MenuGrafico menu = new MenuGrafico(verArchivos);// creamo y pasamos el objeto de verArchivos al menu
                verArchivos.agregarMenu(menu);// vinculamos el menu
                verArchivos.vincularMiddleware(middleware);
                verArchivos.start();
                menu.setVisible(true);// mostrmos el menu
            });
        }

        servidor.start();
        middleware.start();
    }
}
