package Testes;
import Model.Chapter;
import Model.Player;
import Repository.InMemoryStoryRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//Classe para testar a camada repository

public class InMemoryStoryRepositoryTest {

    @Test
    public void  testLoadPrologue(){
        //criando o objeto
        InMemoryStoryRepository repo = new InMemoryStoryRepository();


        //construindo o capitulo, indo ate o repositorio e buscando as informacoes
        Chapter prologue = repo.buildPrologue();
        //verifiaca se o prologue nao e null
        assertNotNull(prologue);
        //verifica se o numero so capitulo esta certo
        assertEquals(1, prologue.getChapterNumber());
        //verifica se o titulo esta certo
        assertEquals("Prólogo: Um vislumbre do passado.", prologue.getTitle());

    }

}
