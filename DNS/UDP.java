package DNS;
import java.io.IOException;
import java.net.*;

public class UDP extends Thread{
    private DatagramSocket socket;

    public UDP() {
        try {
            socket = new DatagramSocket(50000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                obtenerMesaje(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obtenerMesaje(String message) {
        System.out.println("Mensaje recibido en el servidor: " + message);
    }

    public void enviarMensaje(String message, InetAddress destinationAddress, int destinationPort) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, destinationPort);
            socket.send(sendPacket);
            System.out.println("Mensaje enviado desde el cliente: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}