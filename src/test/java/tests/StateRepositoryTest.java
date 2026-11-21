package tests;

import model.GameStates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.StateRepository;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Usa um diretório temporário: nunca lê nem sobrescreve os saves reais do jogador. */
public class StateRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Sem autosave, o estado atual é o padrão (capítulo 1, d1.1) e nada é gravado em disco")
    void testEstadoPadraoQuandoNaoHaAutosave() {
        File savesDir = tempDir.resolve("saves").toFile();

        StateRepository stateRepository = new StateRepository(savesDir.getPath());

        assertTrue(savesDir.isDirectory(), "A pasta de saves deve ser criada automaticamente.");
        assertFalse(stateRepository.slotExists(0), "Ler o estado padrão não deve criar autosave.");

        GameStates state = stateRepository.getCurrentStates();
        assertNotNull(state, "O estado atual não deve ser nulo.");
        assertEquals(1, state.getCurrentChapterId(), "O capítulo padrão deve ser 1.");
        assertEquals("d1.1", state.getCurrentDialogId(), "O diálogo padrão deve ser 'd1.1'.");
        assertEquals("Quarto", state.getCurrentSceneId(), "A cena padrão deve ser 'Quarto'.");
        assertNotNull(state.getStoryFlags(), "O mapa de storyFlags deve ser inicializado.");
        assertTrue(state.getStoryFlags().isEmpty(), "O mapa de storyFlags deve iniciar vazio.");
    }

    @Test
    @DisplayName("Deve persistir flags de história corretamente no arquivo JSON")
    void testPersistenciaDeStoryFlags() {
        String dir = tempDir.resolve("saves").toString();
        StateRepository stateRepository = new StateRepository(dir);
        GameStates state = stateRepository.getCurrentStates();

        state.setFlag("JANELAARTHUR", true);
        state.setFlag("NOTICIAS", false);
        state.setCurrentDialogId("d1.3");
        state.setCurrentSceneId("Cozinha");
        stateRepository.saveState(state);

        // novo repositório na mesma pasta: força a releitura direta do disco
        GameStates estadoCarregado = new StateRepository(dir).getCurrentStates();

        assertEquals("d1.3", estadoCarregado.getCurrentDialogId());
        assertEquals("Cozinha", estadoCarregado.getCurrentSceneId());
        assertTrue(estadoCarregado.hasFlag("JANELAARTHUR"), "A flag 'JANELAARTHUR' deve ser true.");
        assertFalse(estadoCarregado.hasFlag("NOTICIAS"), "A flag 'NOTICIAS' deve ser false.");
        assertFalse(estadoCarregado.hasFlag("FLAG_INEXISTENTE"), "Flag inexistente deve retornar false.");
    }
}
