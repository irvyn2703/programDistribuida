public class Conexion {
    public static void main(String[] args) {
        Mensaje mensaje = new Mensaje();
        Sender sender = new Sender(mensaje);
        Receiver receiver = new Receiver(mensaje);

        sender.enviarSYN(receiver);
        receiver.enviarSYNACK(sender);
        sender.enviarACK(receiver, "hola");
        receiver.enviarACK(sender, "hola 2");
        sender.enviarACK(receiver, "hola 3");
    }
}
