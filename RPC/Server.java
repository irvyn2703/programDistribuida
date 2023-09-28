import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args){
        // al inicar el serve crea o borra el contenido del long 
        try {
            FileWriter writer = new FileWriter("long.inf");
            writer.write("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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