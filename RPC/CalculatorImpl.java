import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;

//definir una interfaz remota que contiene el metodo que deseas llamar de forma remota
interface RemoteCalculator extends Remote{
    int add(int a, int b) throws RemoteException;
    int contador() throws RemoteException;
    int para() throws RemoteException;
}


//implementar la interfaz remota en una clase concreta
public class CalculatorImpl extends UnicastRemoteObject implements RemoteCalculator{

    private int contador;
    private int stopDePara;
    private List<String> hosts;

    public CalculatorImpl() throws RemoteException{
        super();
        this.contador = 0;
        this.stopDePara = 100;
        this.hosts = new ArrayList<String>();
    }

    public int add(int a, int b) throws RemoteException{
        return a+b;
    }

    public int contador() throws RemoteException {
        try {
            // Obtenemos la IP del cliente
            String clientIP = RemoteServer.getClientHost();
            
            // Verificamos si la lista está vacía o si es la misma IP que la última en la lista
            if (hosts.isEmpty() || !clientIP.equals(hosts.get(hosts.size() - 1))) {
                this.contador++;
                hosts.add(clientIP);
                // Imprimimos la IP y el contador
                System.out.println("Client IP: " + clientIP + " response:" + this.contador);
                return this.contador;
            }
            return -1;
        } catch (ServerNotActiveException error) {
            error.printStackTrace();
            throw new RemoteException("IP no encontrada");
        }
    }

    public int para()throws RemoteException{
        if (contador >= 100) {
            return 1;
        }
        return 0;
    }
}