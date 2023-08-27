package TCP;

public class Mensaje {
    // creamos banderas para el envio de mensajes
    private boolean enviandoSYN = false;
    private boolean enviandoSYNACK = false;
    private boolean enviandoACK = false;

    public Mensaje() {}

    // en los siguientes metodos cambiamos el estado de las bandera dependiendo del estado que enviemos como parametro
    public synchronized void enviandoSYN(boolean estado){
        enviandoSYN = estado;
    }
    public synchronized void enviandoSYNACK(boolean estado){
        enviandoSYNACK = estado;
    }
    public synchronized void enviandoACK(boolean estado){
        enviandoACK = estado;
    }

    // En los siguientes métodos nos devuelve el estado de la bandera
    public synchronized boolean revisarSYN(){
        return enviandoSYN;
    }
    public synchronized boolean revisarSYNACK(){
        return enviandoSYNACK;
    }
    public synchronized boolean revisarACK(){
        return enviandoACK;
    }
}

