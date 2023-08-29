package DNS;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class VerArchivos extends Thread {
    private String direccion;
    ArrayList<Archivo> archivo = new ArrayList<>();
    private String archivoConfig;
    private int TTL = 5000;


    public VerArchivos(String direccion, String archivoConfig) {
        this.direccion = direccion;
        this.archivoConfig = archivoConfig;
        this.archivo = new ArrayList<>();
        cargarArchivo(); // Cargar la lista de archivos
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
    }

    public void cargarArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConfig))) {
            String line;
            int lineCount = 0; // Variable para llevar el conteo de líneas
    
            while ((line = reader.readLine()) != null) {
                lineCount++;
    
                if (lineCount <= 1) {
                    TTL = Integer.parseInt(line);
                    System.out.println("TTL: " + TTL);
                }else{
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        archivo.add(new Archivo(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
                        System.out.println("agregando " + parts[0] + " " + parts[1] + " " + parts[2]);
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
            for (Archivo archivo : archivo) {
                writer.write(archivo.nombre + "," + archivo.extension + "," + archivo.publicar + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
