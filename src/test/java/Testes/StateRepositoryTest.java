package Testes;

import Model.GameStates;
import Repository.StateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class StateRepositoryTest {

    private static final String FILE_PATH = "state.json";
    private StateRepository stateRepository;

    @BeforeEach
    void setUp() {
        // Garante um ambiente limpo antes de cada teste
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Opcional: remove o arquivo gerado após a execução dos testes
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @DisplayName("Deve criar state.json com valores padrão quando o arquivo não existir")
    void testCriacaoArquivoDefault() {
        File file = new File(FILE_PATH);
        assertFalse(file.exists(), "O arquivo state.json não deveria existir antes do repositório iniciar.");

        stateRepository = new StateRepository();

        assertTrue(file.exists(), "O arquivo state.json deveria ter sido criado automaticamente.");

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
        stateRepository = new StateRepository();
        GameStates state = stateRepository.getCurrentStates();

        // Modifica o estado inserindo flags de escolha narrativa
        state.setFlag("JANELAARTHUR", true);
        state.setFlag("NOTICIAS", false);
        state.setCurrentDialogId("d1.3");
        state.setCurrentSceneId("Cozinha");

        // Salva a alteração
        stateRepository.saveState(state);

        // Instancia um novo repositório para forçar a releitura direta do disco
        StateRepository novoRepo = new StateRepository();
        GameStates estadoCarregado = novoRepo.getCurrentStates();

        assertEquals("d1.3", estadoCarregado.getCurrentDialogId());
        assertEquals("Cozinha", estadoCarregado.getCurrentSceneId());
        assertTrue(estadoCarregado.hasFlag("JANELAARTHUR"), "A flag 'JANELAARTHUR' deve ser true.");
        assertFalse(estadoCarregado.hasFlag("NOTICIAS"), "A flag 'NOTICIAS' deve ser false.");
        assertFalse(estadoCarregado.hasFlag("FLAG_INEXISTENTE"), "Flag inexistente deve retornar false.");
    }
}