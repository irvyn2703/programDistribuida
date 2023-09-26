import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args){
        try{
            //Crear una instancia de la implementacion y registrarla en el registro RMI
            RemoteCalculator calculator = new CalculatorImpl();
            Registry registry = LocateRegistry.createRegistry(1100);
            registry.bind("Calculator",calculator);

            System.out.println("Servidor RPC en ejecucion...");
 
        }catch(Exception error){
            error.printStackTrace();
        }
    }

}  