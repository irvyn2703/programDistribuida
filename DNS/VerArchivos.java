package DNS;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class VerArchivos extends Thread {
    private String direccion;
    private List<Archivo> archivo;
    private String archivoConfig;


    public VerArchivos(String direccion, String archivoConfig) {
        this.direccion = direccion;
        this.archivoConfig = archivoConfig;
        this.archivo = new ArrayList<>();
        cargarArchivo(); // Cargar la lista de archivos
    }

    @Override
    public void run() {
        List<Archivo> newFileList = obtenerArchivos();
        while (true) {
            newFileList = obtenerArchivos();
            if (!archivo.equals(newFileList)) {
                archivo = newFileList;
                guardarArchivo();
            }
            try {
                Thread.sleep(5000); // Espera 5 segundos antes de la siguiente actualización
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Archivo> obtenerArchivos() {
        File folder = new File(direccion);
        File[] files = folder.listFiles();
        List<Archivo> newFileList = new ArrayList<>();


        if (files == null) {
            return newFileList;
        }

        for (File file : files) {
            if (file.isFile()) {
                String nombreCompleto = file.getName();
                String extension = nombreCompleto.substring(nombreCompleto.lastIndexOf(".") + 1);
                String nombre = nombreCompleto.substring(0, nombreCompleto.lastIndexOf("."));
                boolean publicar = false;
                boolean existeEnLista = archivo.stream().anyMatch(f -> f.nombre.equals(nombreCompleto));

                if (!existeEnLista) {
                    newFileList.add(new Archivo(nombre, extension, publicar));
                }
                
            }
        }

        return newFileList;
    }

    private void cargarArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConfig))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    archivo.add(new Archivo(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
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
            for (Archivo archivo : archivo) {
                writer.write(archivo.nombre + "," + archivo.extension + "," + archivo.publicar + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
