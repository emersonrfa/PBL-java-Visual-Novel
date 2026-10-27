package Repository;

import Model.NPC;
import Model.Vampire;
import java.util.HashMap;
import java.util.Map;

public class NpcRepository {
    private Map<String, NPC> npcs;

    public NpcRepository() {
        this.npcs = new HashMap<>();
        loadNpcs();
    }

    private void loadNpcs() {
        // NPCs mortais

        npcs.put("Arthur", new NPC("Amigo de Emily", "Casa de Arthur", "Arthur",
                "Cabelo cacheado, bigode fino, moreno. Meio desajeitado, já é um amigo de Emily de longa" +
                        " data, apesar de nunca terem se aproximado tanto. Por ser vizinho de Emily é bem acessível" +
                        " no começo do jogo. Caso ganhe atenção suficiente no começo, ele poderá ser útil no futuro.",
                        "Masculino", 22));

        npcs.put("Santo Pedro", new NPC("Coordenador de Santa Prata", "Sede de Santa Prata",
                "Santo Pedro", "Coordenador geral da organização que caça criaturas das trevas." +
                " Um homem grande, forte, mais velho e que dá uma sensação de caçador. Ele é bruto e pode ser rude" +
                " às vezes. Branco de cabelo meio grisalho, barba um pouco grande.",
                "Masculino", 54));

        // Vampiros

        Vampire raphael = new Vampire("O Gentil", "Cozinha", "Raphael Domus",
                "Raphael Domus é um vampiro já vivo há muitos anos. Desde sempre se interessou por atividades " +
                        "humanas, como cozinhar. Apesar de ser um vampiro, é um ótimo cozinheiro, utilizando dos " +
                        "ingredientes mais exóticos possíveis para fazer as mais saborosas refeições. Ele é bem gentil " +
                        "com as pessoas, apesar de esse ser um de seus piores defeitos.", "Masculino", 243);
        raphael.addBloodPreference("O-");
        raphael.addBloodPreference("O+");
        raphael.addBloodPreference("A-");
        raphael.setSuspicionThreshold(100);

        Vampire gabriel = new Vampire("O Entusiasta", "Campo Fechado", "Gabriel Gradus",
                "Gabriel Gradus é o mais novo dos vampiros da casa. É otimista e adora criar fantasias de " +
                        "como captura e desseca suas presas. Ele pode ser meio sem noção às vezes falando sobre como " +
                        "tira o sangue de suas vítimas. É muito animado e sempre está propondo uma ideia de rolê nova.",
                "Masculino", 187);
        gabriel.addBloodPreference("O+");
        gabriel.addBloodPreference("B+");
        gabriel.addBloodPreference("B-");
        gabriel.addBloodPreference("A+");
        gabriel.setSuspicionThreshold(80);

        Vampire miguel = new Vampire("O Comandante", "Salão Principal", "Miguel Tointoskv",
                "Miguel ou Toin, podendo ser chamado das duas maneiras, é o segundo vampiro mais velho da " +
                        "casa. Ele é muito interessado em esgrima e combate, sendo o combatente da linha de frente " +
                        "quado há algum problema delicado com caçadores de vampiros. Charmoso e rigoroso, Miguel ou Toin " +
                        "pretende sempre se impor como o dominante da situação.", "Masculino", 657);
        miguel.addBloodPreference("AB-");
        miguel.addBloodPreference("AB+");
        miguel.addBloodPreference("A-");
        miguel.addBloodPreference("A+");
        miguel.setSuspicionThreshold(65);

        Vampire lilith = new Vampire("A Leitora", "Biblioteca", "Lilith Rex", "Lilith " +
                "é uma vampira peculiar, ela é baixa e usa óculos, e é uma amante de literatura. Vive maior parte do tempo " +
                "na biblioteca lendo algum romance ou literatura antiga. Normalmente tímida, ela se fecha para pessoas novas, " +
                "mas pode ser uma doçura de vampira quando conversando com alguém que realmente gosta.", "Feminino",
                341);
        lilith.addBloodPreference("O+");
        lilith.addBloodPreference("A+");
        lilith.addBloodPreference("B+");
        lilith.addBloodPreference("AB+");
        lilith.setSuspicionThreshold(100);

        Vampire luna = new Vampire("A Anfritriã", "Salão Principal", "Luna Nobilis", "Luna " +
                "é meio que a \"dona\" da casa. Sendo a vampira mais velha, todos a respeitam e a admiram, tornando ela bem " +
                "popular entre todos presentes daquele lugar. Apesar de ser mandona e intimidadora na maioria das vezes, ela " +
                "pode ser tranquila e muito amigável quando construída uma boa relação.", "Feminino", 867);
        luna.addBloodPreference("O-");
        luna.addBloodPreference("A-");
        luna.addBloodPreference("B-");
        luna.addBloodPreference("AB-");
        luna.setSuspicionThreshold(75);

        npcs.put("Raphael", raphael);
        npcs.put("Gabriel", gabriel);
        npcs.put("Miguel", miguel);
        npcs.put("Lilith", lilith);
        npcs.put("Luna", luna);
    }

    public NPC getNpc(String key) {
        return npcs.get(key);
    }
}