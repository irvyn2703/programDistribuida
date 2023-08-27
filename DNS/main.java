package DNS;

public class main {
    public static void main(String[] args){
        String folderPath = "C:\\Users\\irvyn\\OneDrive\\Documents\\beca";
        String logFilePath = "C:\\Users\\irvyn\\OneDrive\\Documents\\beca\\hola.txt";
        UDP servidor = new UDP();

        VerArchivos verArchivos = new VerArchivos(folderPath, logFilePath);
        verArchivos.start();
        servidor.start();
    }
}
