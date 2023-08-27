public class Sender {
    private int x;
    private int y;
    public int synAck;

    public Sender(Mensaje mensaje) {
        x = mensaje.generateRandomSequence();
    }

    public void enviarSYN(Receiver receiver) {
        System.out.println("Sender: enviando SYN: " + x);
        receiver.resibirSYN(x);
    }

    public void recibirSYNACK(int SA, int x, int y) {
        System.out.println("Sender: resibiendo SYN/ACK " + SA);
        synAck = SA;
        this.x = x + 1;
        this.y = y + 1;
    }

    public void enviarACK(Receiver receiver, String mensaje) {
        System.out.println("Sender: enviando ACK " + x + " y " + y);
        receiver.recibirACK(mensaje,x,y);
    }

    public void recibirACK(String m, int x, int y) {
        System.out.println("Receiver: resiviendo ACK " + x + " y " + y + " con el mensaje: " + m);
        this.x = x+1;
        this.y = y+1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
