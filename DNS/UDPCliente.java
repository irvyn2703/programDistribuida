package DNS;
import java.io.IOException;
import java.net.*;

public class UDPCliente {
    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();

            String message = "Hello, UDP Server!";
            InetAddress serverAddress = InetAddress.getByName("localhost"); // Cambia esto a la dirección IP del servidor si es necesario
            int serverPort = 50000;

            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);

            System.out.println("Message sent: " + message);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

