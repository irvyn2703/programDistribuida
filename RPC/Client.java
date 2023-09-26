import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args){
        try{

            List<Integer> resultados = new ArrayList<>();

            //Conectar al registro RMI en el servidor
            Registry registry = LocateRegistry.getRegistry("192.168.100.14",1100);

            //Obtener una referencia al objeto remoto utilizando su nombre registrado
            RemoteCalculator calculator = (RemoteCalculator) registry.lookup("Calculator");
            int numRespuestas = 0;
            while (numRespuestas < calculator.para()){
                int aux = calculator.contador();
                if (aux != -1) {
                    resultados.add(aux);
                    System.out.println("Response: " + aux);
                    numRespuestas++;
                    System.out.println(numRespuestas);
                }
                try {
                    Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Manejar la excepción si se interrumpe el hilo durante la pausa
                        e.printStackTrace();
                    }
                }
        }catch(Exception error){
            error.printStackTrace();
        }
    }

}