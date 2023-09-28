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

            // llevamos el conteo de las respuesta obtenidas desde el servidor
            int numRespuestas = 0;

            // entramos en el while mientras que el numRespuestas sea menor que calculator.para() enviado desde el server
            while (numRespuestas < calculator.para()){
                // guardamos la respuesta del servidor
                int aux = calculator.contador();

                // verificamos que la respuesta sea distinta a -1
                if (aux != -1) {
                    // guardamos la respuesta
                    resultados.add(aux);
                    System.out.println("Response: " + aux);
                    // aumentanos el numero de respuestas
                    numRespuestas++;
                }
                try {
                    // dormimos el programa para ver mejor el proceso en el servidor
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
        }catch(Exception error){
            error.printStackTrace();
        }
    }

}