package DNS;

import java.io.*;
import java.util.*;
//import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

class VerArchivos extends Thread {
    private String direccion = "C:\\Users\\irvyn\\OneDrive\\Documents\\beca";
    private ArrayList<Archivo> archivo = new ArrayList<>();
    private String archivoConfig = System.getProperty("user.dir") + "\\DNS\\config.inf";
    private int TTL = 5000;
    private MenuGrafico menu;

    public VerArchivos(String direccion) {
        this.direccion = direccion;
        this.archivo = new ArrayList<>();
        System.out.println(archivoConfig);
        cargarArchivo(); // Cargar la lista de archivos
    }

    public void agregarMenu(MenuGrafico m){
        menu = m;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TTL); // Espera 5 segundos antes de la siguiente actualización
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("actualizando ...");
            agregarArchivos();
            List<Archivo> archivosAEliminar = verificarArchivos();
            eliminarArchivos(archivosAEliminar);
            guardarArchivo();
        }
    }

    private void agregarArchivos() {
        File folder = new File(direccion);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String nombreCompleto = file.getName();
                String extension = nombreCompleto.substring(nombreCompleto.lastIndexOf(".") + 1);
                String nombre = nombreCompleto.substring(0, nombreCompleto.lastIndexOf("."));
                boolean publicar = false;
                boolean existeEnLista = archivo.stream().anyMatch(f -> f.nombre.equals(nombre));

                if (!existeEnLista) {
                    archivo.add(new Archivo(nombre, extension, publicar));
                    System.out.println("agregando " + nombre + " " + extension + " " + publicar);
                }
            }
        }
    }

    private List<Archivo> verificarArchivos() {
        List<Archivo> archivosAEliminar = new ArrayList<>();
        
        for (Archivo archivoEnLista : archivo) {
            File archivoActual = new File(direccion + "/" + archivoEnLista.nombre + "." + archivoEnLista.extension);
            if (!archivoActual.exists()) {
                System.out.println("El archivo " + archivoEnLista.nombre + "." + archivoEnLista.extension + " no está presente y será eliminado.");
                archivosAEliminar.add(archivoEnLista);
            }
        }
        
        return archivosAEliminar;
    }
    
    private void eliminarArchivos(List<Archivo> archivosAEliminar) {
        archivo.removeAll(archivosAEliminar);
        SwingUtilities.invokeLater(() -> menu.actualizarMenu());
    }

    public void cargarArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConfig))) {
            String line;
            int lineCount = 0; // Variable para llevar el conteo de líneas
    
            while ((line = reader.readLine()) != null) {
                lineCount++;
    
                if (lineCount == 1) {
                    TTL = Integer.parseInt(line);
                    System.out.println("TTL: " + TTL);
                }if (lineCount == 2) {
                    direccion = line;
                    System.out.println("direccion: " + direccion);
                }if (lineCount == 3) {
                    archivoConfig = line;
                    System.out.println("config: " + archivoConfig);
                }else{
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        archivo.add(new Archivo(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
                        System.out.println("agregando " + parts[0] + " " + parts[1] + " " + parts[2]);
                    }
                    for (Archivo archivo2 : archivo) {
                        System.out.println("lista ------> " + archivo2.nombre + "." + archivo2.extension + " - " + archivo2.publicar);
                    }
                }
    
            }
        } catch (FileNotFoundException e) {
            System.err.println("El archivo de registro no existe. Se creará uno nuevo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void guardarArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoConfig))) {
            writer.write(Integer.toString(TTL));
            writer.newLine();
            writer.write(direccion);
            writer.newLine();
            writer.write(archivoConfig);
            writer.newLine();
            for (Archivo archivo : archivo) {
                writer.write(archivo.nombre + "," + archivo.extension + "," + archivo.publicar);
                writer.newLine();
            }
            SwingUtilities.invokeLater(() -> menu.actualizarMenu());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Archivo> getArchivos() {
        return archivo;
    }

    public void cambiarPublicar(int index, boolean cambio){
        archivo.get(index).publicar = cambio;
    }

    public String getNombre(int index){
        return archivo.get(index).nombre;
    }

    public String getExtension(int index){
        return archivo.get(index).extension;
    }

    public boolean getPublicar(int index){
        return archivo.get(index).publicar;
    }
}
