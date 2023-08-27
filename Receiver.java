public class Receiver {
    private int y;
    private int x;
    public int synAck;

    public Receiver(Mensaje mensaje) {
        y = mensaje.generateRandomSequence();
        synAck = mensaje.generateRandomSequence();
    }

    public void resibirSYN(int x) {
        System.out.println("Receiver: resiviendo SYN " + x);
        this.x = x+1;
    }

    public void enviarSYNACK(Sender sender) {
        System.out.println("Receiver: enviando SYN/ACK " + synAck);
        sender.recibirSYNACK(synAck,x,y);
    }

    public void recibirACK(String m, int x, int y) {
        System.out.println("Receiver: resiviendo ACK " + x + " y " + y + " con el mensaje: " + m);
        this.x = x+1;
        this.y = y+1;
    }

    public void enviarACK(Sender sender, String mensaje) {
        System.out.println("Receiver: enviando ACK " + x + " y " + y);
        sender.recibirACK(mensaje,x,y);
    }

    public int getY(){
        return y;
    }

    public int getX(){
        return x;
    }
}
