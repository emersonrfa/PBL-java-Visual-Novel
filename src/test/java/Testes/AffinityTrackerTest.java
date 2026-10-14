package Testes;


import Service.AffinityTracker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AffinityTrackerTest {

    @Test
    public void normalAffinityTeste(){
       AffinityTracker tracker = new AffinityTracker();
        //para casos normais
        tracker.modifyAffinity("Arthur", 10);
        tracker.modifyAffinity("Arthur", 15);
        tracker.modifyAffinity("Arthur", -5);
        //soma tem que dar 20
        assertEquals(20,tracker.getAffinity("Arthur"));
    }

    @Test
    public void maxAffinityTest(){
        //teste de maximo
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Santo Pedro", 80);
        tracker.modifyAffinity("Santo Pedro", 60);

        // nao pode passar de 100
        assertEquals(100,tracker.getAffinity("Santo Pedro"));
    }


    @Test
    //modificar para nao aceitar valores negativos
    public void lowAffinityTest(){
        //teste de minimo
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Vampiro A", -10);
        tracker.modifyAffinity("Vampiro A", -20);


        // o minimo e -20
        assertEquals(-20, tracker.getAffinity("Vampiro A"));
    }

    @Test
    public void normalSuspection(){
        AffinityTracker tracker = new AffinityTracker();

        tracker.modifySuspicion("Daniel Lindo",40);
        tracker.modifySuspicion("Daniel Lindo",30);

        //a somo devedar 70
        assertEquals(70,tracker.getSuspicion("Daniel Lindo"));
    }

    @Test
    public void maxSuspection(){
        AffinityTracker tracker = new AffinityTracker();

        tracker.modifySuspicion("Emerson nao e tao bonito",70);
        tracker.modifySuspicion("Emerson nao e tao bonito",50);

        //nao pode superar 100
        assertEquals(100,tracker.getSuspicion("Emerson nao e tao bonito"));
    }

    @Test
    public void lowSuspection(){
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifySuspicion("OHH Gusta",20);
        tracker.modifySuspicion("OHH Gusta",-50);

        //nao pode assumir valores negativos
        assertEquals(0,tracker.getSuspicion("OHH Gusta"));
    }
}
