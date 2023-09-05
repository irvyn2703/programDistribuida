package DNS;

import java.io.*;
import java.util.*;
//import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

class VerArchivos extends Thread {
    private String direccion = ""; // direccion donde revisaremos los archivos
    private ArrayList<Archivo> archivo = new ArrayList<>(); // un array donde guargamos cada archivo
    private String archivoConfig = System.getProperty("user.dir") + "\\DNS\\config.inf"; // archivo config donde guardamos el TTL, ruda de la carpeta y los archivos
    private int TTL = 5000; // tiempo para revisar la carpeta
    private MenuGrafico menu; // menu grafico

    public VerArchivos(String direccion) {
        this.direccion = direccion;
        this.archivo = new ArrayList<>();
        cargarArchivo(); // Cargar la lista de archivos
    }

    public void agregarMenu(MenuGrafico m){
        menu = m; // llamamos el menu
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TTL); // Espera el tiempo antes de la siguiente actualización
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("actualizando ..."); 
            agregarArchivos(); // metodo para agregar archivos nuevos
            List<Archivo> archivosAEliminar = verificarArchivos(); // obtenemos una lista de los archivos a eliminar
            eliminarArchivos(archivosAEliminar); // eliminamos los archivos de la lista
            guardarArchivo(); // actualizamos el config
        }
    }

    private void agregarArchivos() {
        File folder = new File(direccion); // cargamos la direccion de la carpeta
        File[] files = folder.listFiles(); // cargamos los archivos

        for (File file : files) {
            if (file.isFile()) {
                String nombreCompleto = file.getName(); // obtenemos el nombre completo del archivo
                String extension = nombreCompleto.substring(nombreCompleto.lastIndexOf(".") + 1); // obtiene la extencion
                String nombre = nombreCompleto.substring(0, nombreCompleto.lastIndexOf(".")); // obtenemos el puro nombre sin extencion
                boolean publicar = false; // decidi que todos los archivos recien ingresados no estara publicados
                boolean existeEnLista = archivo.stream().anyMatch(f -> f.nombre.equals(nombre)); // con este metodo decidimos si el archivo ya existe

                if (!existeEnLista) { // en caso de no existir lo agregamos
                    archivo.add(new Archivo(nombre, extension, publicar)); // agregamos al objeto Archivo
                    System.out.println("agregando " + nombre + " " + extension + " " + publicar);
                }
            }
        }
    }

    private List<Archivo> verificarArchivos() {
        List<Archivo> archivosAEliminar = new ArrayList<>();// creamos el arreglo
        
        for (Archivo archivoEnLista : archivo) {
            File archivoActual = new File(direccion + "/" + archivoEnLista.nombre + "." + archivoEnLista.extension); //ponemos la direccion completa del archivo
            if (!archivoActual.exists()) {// si no existe en la direccion entonces se elimino y tenemos que eliminarlo
                System.out.println("El archivo " + archivoEnLista.nombre + "." + archivoEnLista.extension + " no está presente y será eliminado.");
                archivosAEliminar.add(archivoEnLista);// agregamos el archivo a la lista de eliminar
            }
        }
        
        return archivosAEliminar; // devolvemos los archivos a elminar
    }
    
    private void eliminarArchivos(List<Archivo> archivosAEliminar) {
        archivo.removeAll(archivosAEliminar); // eliminamos los archivos en la lista
        SwingUtilities.invokeLater(() -> menu.actualizarMenu());// actualizamos el menu
    }

    public void cargarArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConfig))) {
            String line;
            int lineCount = 0; // Variable para llevar el conteo de líneas
    
            while ((line = reader.readLine()) != null) {
                lineCount++;// aumentamos el contador
    
                if (lineCount == 1) {// la primera linea es el TTL
                    TTL = Integer.parseInt(line); // guardamos el TTL
                    System.out.println("TTL: " + TTL);
                }if (lineCount == 2) {// la linea 2 es la direccion de la carpeta compartida
                    direccion = line;
                    System.out.println("direccion: " + direccion);
                }if (lineCount == 3) {// la linea 3 es la direccion del config
                    archivoConfig = line;
                    System.out.println("config: " + archivoConfig);
                }else{// despues de la 3 linea son los archivos previamente guardados tiene una estructura como la siguiente
                    //nombre_archivo,extencion,(un boolean para saber si se comparte o no)
                    String[] parts = line.split(",");// obtenemos el nombre,extencion y el boolean
                    if (parts.length >= 3) {
                        archivo.add(new Archivo(parts[0], parts[1], Boolean.parseBoolean(parts[2])));// agregamos a la lista de archivos
                        System.out.println("agregando " + parts[0] + " " + parts[1] + " " + parts[2]);
                    }
                    for (Archivo archivo2 : archivo) {
                        System.out.println("lista ------> " + archivo2.nombre + "." + archivo2.extension + " - " + archivo2.publicar);
                    }
                }
    
            }
        } catch (FileNotFoundException e) {
            System.err.println("El archivo de registro no existe. Se creará uno nuevo.");// en caso de no existir el archivo mandamos el mensaje
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void guardarArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoConfig))) {// accedemos al config
            writer.write(Integer.toString(TTL));// guardamos el TTL
            writer.newLine();
            writer.write(direccion);// guardamos la direccion de la carpeta
            writer.newLine();
            writer.write(archivoConfig);// guardamos la direccion del archivo config
            writer.newLine();
            for (Archivo archivo : archivo) {// guardamos todos loa archivos en la lista de archivos
                writer.write(archivo.nombre + "," + archivo.extension + "," + archivo.publicar);
                writer.newLine();
            }
            SwingUtilities.invokeLater(() -> menu.actualizarMenu()); // actualizamos el menu grafico
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Archivo> getArchivos() {// este metodo nos servira en el menu grafico
        return archivo; 
    }

    public void cambiarPublicar(int index, boolean cambio){// cambiamos el boolean publicar(en el objeto de MenuGrafico)
        archivo.get(index).publicar = cambio;
    }

    public String getNombre(int index){// obtenemos el nombre
        return archivo.get(index).nombre;
    }

    public String getExtension(int index){// obtenemos la extensión
        return archivo.get(index).extension;
    }

    public boolean getPublicar(int index){// obtenemos el boleano de publicar
        return archivo.get(index).publicar;
    }
}
