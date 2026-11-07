package tests;

import model.Dialogue;
import model.Choice;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DialogueTest {
    @Test
    public void testDialogue0Options(){
        Dialogue d = new Dialogue("d1", "Apenas uma observação.");
        //verifica se existe opcoes
        assertFalse(d.hasChoices());
    }

    @Test
    public void testeDialogueWithOptions(){
        Dialogue d = new Dialogue("d1", "O que você faz?");
        //adicionando uma opcao
        d.addChoice(new Choice("Olhar celular", "d2"));

        //verificando se existe opcoes
        assertTrue(d.hasChoices());
        //verificando se a quantidade de opcoes e igual a 1
        assertEquals(1, d.getChoices().size());
    }
}
