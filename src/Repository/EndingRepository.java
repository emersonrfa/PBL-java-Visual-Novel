package Repository;

import Model.Ending;
import java.util.HashMap;
import java.util.Map;

public class EndingRepository {
    private Map<String, Ending> endings;

    public EndingRepository() {
        this.endings = new HashMap<>();
        loadEndings();
    }

    private void loadEndings() {
        endings.put("FINAL_MORTE_vamp_mansao", new Ending("FINAL_MORTE_vamp_mansao",
                " Emily não conseguiu manter sua identidade falsa de vampira. Sua vida e suas decisões" +
                        " viajam perante seus olhos enquanto o sangue se esvai de seu corpo e sua alma é corrompida" +
                        " pelo ritual sombrio. Os vampiros ritualistas enojam-se de você e nem mesmo decidiram lhe " +
                        "aceitar como uma nova integrante, seu crime é tão grande que você é banida deste mundo.",
                "Consumida pelo Ritual Sombrio."));

        // Acontece em qualquer momento que Emily escolher uma opção que lhe leve a morte por um vampiro DENTRO da Mansão

        endings.put("FINAL_VAMPIRA", new Ending("FINAL_VAMPIRA", " Desde o princípio seu destino estava " +
                "selado, Emily apenas não sabia ainda. Sua curiosidade e emocional criado pelos vampiros da côrte " +
                "resultou-lhe uma maldição eterna, sua pele torna-se pálida, seu sangue ferve com o menor raio solar," +
                " suas presas alongam-se em sua boca. Emily é uma vampira e agora viverá eternamente em uma vida total" +
                "mente diferente.", "O ser sombrio Emily."));

        // Acontece se Emily tiver os requerimentos necessários para convencer todos os vampiros de que ela está do lado
        // deles e quer se tornar uma vampira no ritual de ascenção (boa afinidade com todos os vampiros)

        endings.put("FINAL_O_FIM", new Ending("FINAL_O_FIM", " A organização de Santa Prata se vangloria e" +
                " deleita em seu sucesso de encontrar onde estavam escondidas todas as criaturas malignas que mais os" +
                " atormentavam. Graças à Emily, seu bom disfarce e seu bom trabalho, as criaturas das trevas são dizi" +
                "madas, e as ruas tornam-se mais seguras. Após isso, Emily continua trabalhando na organização, como" +
                "uma experiente e natural caçadora de vampiros.", "A glória de Santa Prata"));

        // Acontece se Emily conseguir se passar como vampira em todos os capítulos e no final decidir não contar tudo para
        // os vampiros, e então, de forma planejada, se encontrar com a organização para entregar a localização do ritual

        // mais finais a serem feitos...
    }

    public Ending getEnding(String key) {
        return endings.get(key);
    }
}
