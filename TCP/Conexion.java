package TCP;

public class Conexion {
    public static void main(String[] args) {
        // creamos el objeto mensajes que nos ayuda con las banderas de los mensajes (SYN,SYN/ACK,ACK)
        Mensaje mensajes = new Mensaje();
        // inicializamos nuestros dispositivos sender y receiver
        Dispositivo sender = new Dispositivo();
        Dispositivo receiver = new Dispositivo();

        // damos los valores iniciales a cada dispositivo (el estado de receiver es 0 y el estado de sender 1)
        receiver.inicializar("receiver", mensajes, sender, 0);
        sender.inicializar("sender", mensajes, receiver, 1);

        // iniciamos los hilos
        receiver.start();
        sender.start();
    }
}
