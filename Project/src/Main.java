import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // Vector de dades
    private static int dadesSns = 0;
    private static int dadesBqg = 0;

    // Bloquejador de recursos
    private static ReentrantLock mutex = new ReentrantLock();

    // Main
    public static void main(String[] args) {
        
        // Creacio de futurs
        ArrayList<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int cnt = 0; cnt < 5; cnt++){
            futureList.add(CompletableFuture.runAsync((getRunnable(cnt))));
        }

        // Execucio dels futurs
        System.out.println("Esperant resultats");
        futureList.forEach(CompletableFuture::join); 

        // Mostrar resultats
        System.out.println("Resultats:");
        System.out.println("Dades sense bloqueig: " + dadesSns);
        System.out.println("Dades amb bloqueig: " + dadesBqg);
    }

    //Codi executat pels futurs
    static Runnable getRunnable (int pos) {
        return new Runnable () {
            @Override public void run () {

                // Modificar les dades sense bloqueig
                for (int cnt = 0; cnt < 10; cnt++) {
                    int val = dadesSns;
                    try {Thread.sleep(10);} 
                    catch (InterruptedException e) {e.printStackTrace();}
                    dadesSns = val + 1;
                }

                try {
                    // Bloquejem els recursos per assegurar la seva exclusivitat
                    mutex.lock();

                    // Modificar les dades amb bloqueig 
                    for (int cnt = 0; cnt < 10; cnt++) {
                        int val = dadesBqg;
                        try {Thread.sleep(10);} 
                        catch (InterruptedException e) {e.printStackTrace();}
                        dadesBqg = val + 1;
                    }

                } finally {
                    mutex.unlock();
                }
            }
        };
    }
}