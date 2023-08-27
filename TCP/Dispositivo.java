package TCP;
import java.util.Random;
import java.util.Scanner;

public class Dispositivo extends Thread{
    // nos crea los numero aleatorios
    private Random random = new Random();
    // nombre que recibe el dispositivo para reconocerlo
    private String nombre;
    //0 = sin conexion
    //1 = SYN
    //2 = SYN/ACK
    //3 = ACK
    public int estado = 0;
    public int x = 0;
    public int y = 0;
    // bandera que nos sirve para saber si la conexión finalizó y solo mande ACK
    public boolean conexionLista = false;
    // nos indica si recibo un nuevo ACK para recibirlo
    public boolean nuevoMensaje = false;
    // guardamos el mensaje recibido 
    public String mensajeRecibido;
    // nos indica el dispositivo al cual debe enviar la informacion
    private Dispositivo otroDispositivo;
    // el objeto donde se almacenan las banderas de envio de paquetes
    private Mensaje m;
    // nos permite escribir un mensaje (app data)
    Scanner scanner = new Scanner(System.in);

    //metodos
    public Dispositivo(){}

    // inicializamos las variables con los datos correspondientes
    // n = nombre del dispositivo
    // m = el objeto donde estan nuestras banderas de los mensajes
    // otro = el dispositivo donde enviaremos los paquetes
    public void inicializar(String n,Mensaje m,Dispositivo otro,int e){
        nombre = n;
        this.m = m;
        otroDispositivo = otro;
        estado = e;
    }

    // nuestro run() siempre ejecutara el método establecerConexion y este ejecutara el envió de paquetes
    public void run() {
        while(true){
            establecerConexion(estado, otroDispositivo);
        }
    }

    // recibe como parametro el estado y el dispositivo donde se envia los paquetes
    public void establecerConexion(int estado,Dispositivo dispositivo){
        switch (estado) {
            // enviar SYN
            case 1:
                // crea un numero aleatorio para x
                x = random.nextInt(1000);
                // crea una variable SYN que nos mantendrá en el bucle hasta recibir la confirmación de que el paquete se envió correctamente
                boolean SYN = false;
                while (SYN == false) {
                    // envia x al otro dispositivo y cambia su estado a 2
                    System.out.println(nombre + ": enviando syn con un valor de x = " + x);
                    dispositivo.x = x;
                    dispositivo.estado = 2;
                    // cambia la bandera en el objeto mensaje para decir que se está enviando el SYN
                    m.enviandoSYN(true);
                    try {
                        // duerme el proceso para simular la espera de la confirmación del paquete
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // después del tiempo establecido previamente revisa si el paquete llego
                    // si el paquete llego sale del bucle, pero si no llego reenvia el paquete
                    if (m.revisarSYN() == false) {
                        SYN = true;
                    }
                }
            break;
            // enviar SYN/ACK
            case 2:
                // simula el tiempo en que se envía el SYN si es mayor que al tiempo de espera de la confirmación decimos que se perdio el paquete
                int tiempoEspera = random.nextInt(2000);
                try {
                    Thread.sleep(tiempoEspera);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (tiempoEspera < 1000) {
                    // crea un numero aleatorio para y
                    y = random.nextInt(1000);
                    System.out.println(nombre + ": recibi el syn con un valor de x = " + x);
                    // cambiamos la bandera por que recibimos el SYN
                    m.enviandoSYN(false);
                    // crea una variable SYNACK que nos mantendrá en el bucle hasta recibir la confirmación de que el paquete se envió correctamente
                    boolean SYNACK = false;
                    while (SYNACK == false) {
                        // envia x+1 y y al otro dispositivo y cambia su estado a 3
                        System.out.println(nombre + ": enviando syn/ack con un valor de x = " + (x+1) + " y un valor de y = " + y);
                        dispositivo.x = x + 1;
                        dispositivo.y = y;
                        dispositivo.estado = 3;
                        // cambia la bandera en el objeto mensaje para decir que se está enviando el SYNACK
                        m.enviandoSYNACK(true);
                        try {
                            // tiempo de espera para la confirmación
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // verifica si llego el paquete
                        if (m.revisarSYNACK() == false) {
                            SYNACK = true;
                        }
                    }
                }
            break;
            // enviar ACK
            case 3:
                // simula el tiempo en que se envia el SYNACK
                tiempoEspera = random.nextInt(2000);
                try {
                    Thread.sleep(tiempoEspera);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // si el paquete llega antes del tiempo de espera de la confirmación, entonces el paquete no se perdio y podemos continuar
                if (tiempoEspera < 1000) {
                    // si conexionLista es false entonces es el primer ACK que recibimos
                    if (conexionLista == false) {
                        System.out.println(nombre + ": recibi el syn/ack con un valor de x = " + x + " y un valor de y = " + y);
                        // cambiamos la bandera porque recibimos el SYNACK
                        m.enviandoSYNACK(false);
                        // creamos nuestra variable ACK para entrar en bucle
                        boolean ACK = false;
                        while (ACK == false) {
                            // creamos nuestra bandera para decir que estamos enviando nuestro ACK
                            m.enviandoACK(true);
                            System.out.println(nombre + ": enviando ack con un valor de x = " + (x+1) + ", un valor de y = " + (y+1) + " y el mensaje 'conexion establecida'" );
                            // enviamos x+1,y+1,el mensaje y cambiamos el estado a 3 en el otro dispositivo
                            dispositivo.mensajeRecibido = "conexion establecida";
                            dispositivo.x = x + 1;
                            dispositivo.y = y + 1;
                            dispositivo.estado = 3;
                            // le decimos al otro dispositivo que la conexion esta lista y que tiene un mensaje
                            dispositivo.conexionLista = true;
                            dispositivo.nuevoMensaje = true;
                            // en nuestro dispositivo también le decimos que la conexion esta lista, pero que no tiene mensaje (asi identificamos quien esta enviando y quien esta recibiendo)
                            conexionLista = true;
                            nuevoMensaje = false;
                            try {
                                //esperamos la confirmacion
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // verificamos el paquete
                            if (m.revisarACK() == false) {
                                ACK = true;
                            }
                        } 
                    }else{
                        // sabemos que no es nuestro primer ack ahora verificamos si estamos recibiendo mensaje o enviando con nuevoMensaje
                        if (nuevoMensaje == true) {
                            // desactivamos la bandera por si el otro dispositivo esta en el bucle
                            m.enviandoACK(false);
                            System.out.println(nombre + ": recibi el ack con un valor de x = " + x + ", un valor de y = " + y + " y el mensaje '" + mensajeRecibido + "'");
                            
                            System.out.print(nombre + ": envia el mensaje... ");
                            // por estetica del programa decidí que escribiera el mensaje a enviar
                            String envia = scanner.nextLine();
                            System.out.println(nombre + ": enviando ack con un valor de x = " + (x+1) + ", un valor de y = " + (y+1) + " y el mensaje '" + envia + "'" );
                            
                            // envia al otro dispositivo el mensaje,x+1,y+1,activa que tiene un nuevo mensaje
                            dispositivo.mensajeRecibido = envia;
                            dispositivo.x = x + 1;
                            dispositivo.y = y + 1;
                            dispositivo.nuevoMensaje = true;
                            // como ahora este dispositivo es el que envia, entonces cambiamos su bandera a false
                            nuevoMensaje = false;
                        }
                    }
                }
            break;
            default:
            break;
        }
    }
}
