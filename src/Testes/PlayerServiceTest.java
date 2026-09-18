package Testes;

import Model.Player;
import Service.PlayerService;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

//classe para testar a clase Player
public class PlayerServiceTest {

    //testa o consumo de energia de forma normal
    @Test
    public void testconsueEnergydefult(){
        Player player = new Player("Emily", "Feminino", 22, "Personagem principal", 100);
        PlayerService service = new PlayerService();

        //consumindo 20 de energia do objeto player
        service.consumeEnergy(player,20);

        //verificacao( o valor retornado tem que ser 80)
        assertEquals(80,player.getEnergy());
    }

    @Test
    //NAO ESTA FUNCIONANDO
    //TESTE DE BORDA
    public void  testeNegativeEnergy(){
        //criando os objetos necessarios
        Player player = new Player("Emily", "Feminino", 22, "Personagem principal", 20);
        PlayerService service = new PlayerService();
        //tentar consumir mais energia do que a quantidade existente
        service.consumeEnergy(player, 50);
        // a energia tem que ficar 0 e nao assumir valores negarivos
        assertEquals(0, player.getEnergy());


    }


}
