import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args){
        try{

            List<Integer> resultados = new ArrayList<>();

            //Conectar al registro RMI en el servidor
            Registry registry = LocateRegistry.getRegistry("localhost",1100);

            //Obtener una referencia al objeto remoto utilizando su nombre registrado
            RemoteCalculator calculator = (RemoteCalculator) registry.lookup("Calculator");
            
            //llamar al metodo remoto
            //int result = calculator.contador();
            //System.out.println("Resultado : " + result);
            do{
                int aux = calculator.contador();
                if (aux != -1) {
                    resultados.add(aux);
                    System.out.println("Response: " + aux);
                }
            }while (calculator.para() == 0);

        }catch(Exception error){
            error.printStackTrace();
        }
    }

}