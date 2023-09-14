package DNS;
import java.io.IOException;
import java.net.*;

public class UDP extends Thread{
    private DatagramSocket socket;
    private Middleware middleware;

    public UDP() {
        try {
            socket = new DatagramSocket(5000); // agregamos el puerto
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                // recibimos el mensaje en el servidor
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(message);
                middleware.procesarMensaje(message, receivePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  

    public void enviarMensaje(String message, InetAddress destinationAddress, int destinationPort) {// metodo para enviar mensaje
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, destinationPort);
            socket.send(sendPacket);// enviamos el mensaje
            System.out.println("Mensaje enviado a " + destinationAddress + ": " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void agregarMiddleware(Middleware mi){
        middleware = mi;
    }
}