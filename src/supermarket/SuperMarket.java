
package supermarket;

import java.util.Random;


class Resultados{
    public static int ganancias;
    public static long tiempo_espera;
    public static int clientes_atendidos;
}

class Caja{
    private static final int MAX_TIME = 1000;

class Nodo{
    int cliente;
    Nodo sig;
    }
    Nodo raiz, fondo;
    
    public Caja(){
        raiz = null;
        fondo = null;
    }
    
    private boolean vacia(){
        if (raiz == null)
            return true;
        else
            return false;
    }
    
    synchronized  public void esperar (int id_cliente) throws InterruptedException{
        Nodo nuevo;
        nuevo = new Nodo();
        nuevo.cliente = id_cliente;
        nuevo.sig = null;
        if (vacia ()){
            raiz = nuevo;
            fondo = nuevo;
        }else{
            fondo.sig = nuevo;
            fondo = nuevo;
        }
        while (raiz.cliente != id_cliente){
            wait();
        }
    }
    
    synchronized public void atender (int pago) throws InterruptedException{
        if (raiz ==fondo){
            raiz = null;
            fondo = null;
        }else{
            raiz = raiz.sig;
        }
        int tiempo_atencion = new Random().nextInt(MAX_TIME);
        Thread.sleep(tiempo_atencion);
        Resultados.ganancias += pago;
        Resultados.clientes_atendidos++;
        notify();
    }
    
    synchronized public void imprimir(){
        Nodo reco = raiz;
        while (reco!=null){
            System.out.print(reco.cliente+"-");
            reco = reco.sig;
        }
        System.out.println();
    }
}

class Cliente extends Thread{
    private static final int MAX_DELAY = 2000;
    private static final int MAX_COST = 100;
    private int id;
    private Caja caja;
    
    Cliente (int id, Caja caja){
        this.id = id;
        this.caja = caja;
    }
    
    public void run(){
        try{
            System.out.println("Cliente " + id + " realizando compra");
            Thread.sleep(new Random().nextInt(MAX_DELAY));
            long s = System.currentTimeMillis();
            caja.esperar(id);
            System.out.print("Cliente " + id + " en cola con ");
            caja.imprimir();
            caja.atender(new Random().nextInt(MAX_COST));
            System.out.println("Cliente " + id + " atendido");
            
            long espera = System.currentTimeMillis() - s;
            Resultados.tiempo_espera += espera;
            System.out.println("Cliente " + id + " saliendo despues de esperar " + espera);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

public class SuperMarket {

    
    public static void main(String[] args) throws InterruptedException{
        int N = Integer.parseInt(args[0]);
        Caja cajas[] = new Caja[N];
        for (int i=0; i<N; i++){
            cajas[i]=new Caja();
        }
        
        int M = Integer.parseInt (args[1]);
        Cliente clientes[] = new Cliente[M];
        for (int i=0; i<M; i++){
            int j = new Random().nextInt(N);
            clientes[i] = new Cliente(i, cajas[j]);
            clientes[i].start();
        }
        try{
            for (int i=0; i< M; i++){
                clientes[i].join();
            }
        }catch (InterruptedException ex){
            System.out.println("Hilo principal interrumpido");
        }
        System.out.println("Supermercado cerrado");
        System.out.println("Ganancias: " + Resultados.ganancias);
        System.out.println("Tiempo medio de espera: " + (Resultados.tiempo_espera / Resultados.clientes_atendidos));
    }
    
}
