import java.io.FileWriter;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;

//definir una interfaz remota que contiene el metodo que deseas llamar de forma remota
interface RemoteCalculator extends Remote{
    int contador() throws RemoteException;
    int para() throws RemoteException;
}


//implementar la interfaz remota en una clase concreta
public class CalculatorImpl extends UnicastRemoteObject implements RemoteCalculator{

    // contador del servidor del numero de solicitudes
    private int contador;
    // numero de respuestas por cliente
    private int stopDePara;
    // numero de clientes conectados
    private int numClientes;
    // lista donde guardamos la ip de cada respuesta
    private List<String> hosts;

    public CalculatorImpl() throws RemoteException{
        super();
        // inicializamos las variables
        this.contador = 0;
        this.stopDePara = 100;
        this.hosts = new ArrayList<String>();
        this.numClientes = 0;
    }

    public int contador() throws RemoteException {
        try {
            // Obtenemos la IP del cliente
            String clientIP = RemoteServer.getClientHost();
            
            // Verificamos si la lista está vacía o si es la misma IP que la última en la lista
            if (hosts.isEmpty() || !clientIP.equals(hosts.get(hosts.size() - 1))) {
                // aumentamos el contador de solicitudes
                this.contador++;
                // verificamos si el cliente es nuevo
                if(!hosts.contains(clientIP)){
                    // aumentamos el numero de clientes
                    numClientes++;
                }
                // guardamos la ip
                hosts.add(clientIP);
                // Imprimimos la IP y el contador
                System.out.println("Client IP: " + clientIP + " response:" + this.contador + " --> numero de clientes:" + numClientes);
                
                guardarHosts(); // guardamos el nuevo registro en el long
                return this.contador; // regresamos el contador al cliente
            }
            return -1; // regresamos -1 si es la misma IP que la última en la lista
        } catch (ServerNotActiveException error) {
            error.printStackTrace();
            throw new RemoteException("IP no encontrada");
        }
    }

    public int para()throws RemoteException{
        // verificamos que el numero de clientes no sea 0
        if(numClientes == 0){
            return 100;
        }else{
            return (stopDePara/numClientes); // dividimos el numero de registros que maneja nuestro servidor entre el numero de clientes
        }
    }

    private void guardarHosts() {
        try {
            FileWriter writer = new FileWriter("hosts.inf",true); // abrimos el archivo para agregar información
            String host = hosts.get(hosts.size() - 1); // obtenemos el ultimo registro del host
            writer.write(contador + "," + host + "\n"); // guardamos el numero de solicitud y el host
            writer.close(); // cerramos el archivo
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}