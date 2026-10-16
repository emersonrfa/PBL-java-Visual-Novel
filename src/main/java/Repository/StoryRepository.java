package Repository;

import Model.Chapter;
import Model.Scene;
import Model.Dialogue;
import Model.Options;
import Repository.ItemRepository;
import Repository.NpcRepository;

public class StoryRepository {
    private ItemRepository itemRepo = new ItemRepository();
    private NpcRepository npcRepo = new NpcRepository();

    public Chapter buildPrologue() { // CAPÍTULO 1 CAP1

        Chapter cap1 = new Chapter(1, "Prólogo: Um vislumbre do passado.");

        //----------------------------------------------------------------------------------------------------------//
        // INTRODUÇÃO

        Scene cenaQuartoIntro = new Scene("Quarto", "Um quarto bagunçado. Porém aconchegante.");

        // sintaxe padrao: addSimpleDialogue(id, text, nextId)
        cenaQuartoIntro.addSimpleDialogue("d1.1", "Você sente o vento frio balançando as cortinas do seu quarto.", "d1.2");
        cenaQuartoIntro.addSimpleDialogue("d1.2", "Sua janela aberta permite a entrada tanto da brisa constante da" +
                        " noite quanto da suave luz da lua.", "d1.3");
        cenaQuartoIntro.addSimpleDialogue("d1.3", "Seu confortável travesseiro nunca lhe abandona quando deseja-se" +
                        " paz e descanso. O quarto, apesar de bagunçado, aconchega-lhe o suficiente para te fazer sentir segura."
                        , "d1.4");

        // diálogo com múltiplas escolhas (usa-se o construtor de opções simples)
        Dialogue d1_4 = new Dialogue("d1.4", "Mas algo lhe incomoda, você não consegue dormir.");
        d1_4.addOption(new Options("Esticar-se e buscar seu celular", "d2.1"));
        d1_4.addOption(new Options("Vislumbrar a vista de sua janela", "d4.1").setFlagToSet("JANELAARTHUR"));
        cenaQuartoIntro.addDialogue(d1_4);

        cap1.addScene(cenaQuartoIntro);

        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO O CELULAR

        Scene cenaCelular = new Scene("Averiguando o celular", "Olhando para a tela de um celular ainda deitada na cama.");

        cenaCelular.addSimpleDialogue("d2.1", "Você se esforça um pouco para pegar seu celular em sua mesa, " +
                "próxima a cama. Ao ligá-lo, percebe que já é tarde da noite.", "d2.2");
        cenaCelular.addSimpleDialogue("d2.2", "Você abre suas anotações e lê algumas coisas que escreveu durante" +
                " a semana:\n—> comprar duas caixas de leite\n—> marcar consulta e exames (importante)\n—> comer mais" +
                " frutas\n—> arrumar o quarto..\n—> olhar emails!!!", "d2.3");

        Dialogue d2_3 = new Dialogue("d2.3", "O que deseja fazer?");
        d2_3.addOption(new Options("Olhar os emails", "d3.1"));
        d2_3.addOption(new Options("Desligar o celular e ir para a janela", "d4.1"));
        d2_3.addOption(new Options("Esquecer as anotações e tentar dormir", "d5.1"));
        cenaCelular.addDialogue(d2_3);

        cap1.addScene(cenaCelular);

        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO EMAILS

        Scene cenaIntroEmails = new Scene("Emails do celular", "Ainda no celular, abre a caixa de emails, repleta de mensagens.");

        cenaIntroEmails.addSimpleDialogue("d3.1", "Ao abrir sua caixa de email, nota-se algumas mensagens " +
                "supérfluas e alguns spams. Porém, você visualiza uma mensagem favoritada há algumas semanas atrás." +
                " É uma proposta de emprego para uma organização aparentemente não muito confiável.", "d3.2");
        cenaIntroEmails.addSimpleDialogue("d3.2", "Então você se lembra, esta semana você deixou de receber seu " +
                        "auxílio desemprego, e agora é necessário conseguir uma renda.", "d3.3");
        cenaIntroEmails.addSimpleDialogue("d3.3", "Player", "Cansada", "“Ahh, mas eu já tentei" +
                        " tantas entrevistas e ainda não consegui nada… Eu deveria tentar esta do email. Qual era o" +
                        " nome mesmo?”", "d3.4");

        cenaIntroEmails.addSimpleDialogue("d3.4", "Enquanto você procura o nome da empresa, você" +
                        " se distrai ao explorar a mensagem lida, e começa a se lembrar de acontecimentos de" +
                        " seu passado.", "dLORE.1");

        cap1.addScene(cenaIntroEmails);

        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO JANELA

        Scene cenaOlhandoJanelaIntro = new Scene("Janela de Emily", "Olhando através da janela de seu quarto, você vislumbra uma paisagem distante de Nova Iorque.");

        cenaOlhandoJanelaIntro.addSimpleDialogue("d4.1", "Você se levanta e abre sua janela. O céu está bonito, a vista de" +
                " Nova Iorque se destaca na paisagem. Sua cidade, à esta hora, é silenciosa e calma. Você visualiza a " +
                "lua e se encanta com sua beleza.", "d4.2");
        cenaOlhandoJanelaIntro.addSimpleDialogue("d4.2", "Em um pequeno prédio próximo à janela, você vê que há alguns" +
                                " apartamentos de luz acesa. Aparentemente você não é a única acordada a esta hora.",
                        "d4.3");
        cenaOlhandoJanelaIntro.addSimpleDialogue("d4.3", "Player", "Cansada", "“Quem me dera não " +
                        "precisar dormir, me sinto tão cansada.”", "d4.4");
        cenaOlhandoJanelaIntro.addSimpleDialogue("d4.4", "Você suspira e pensa um pouco sobre seu estado…",
                        "dLORE.1");

        cap1.addScene(cenaOlhandoJanelaIntro);

        //----------------------------------------------------------------------------------------------------------//
        // DORMIR DIRETO

        Scene cenaDormirIntro = new Scene("Indo dormir", "O travesseiro de sua cama lhe conforta como nunca.");

        cenaDormirIntro.addSimpleDialogue("d5.1", "Você ignora suas anotações e se despede de seu celular. Ao se" +
                " ajeitar para dormir e cobrir-se com seu lençol, você se sente próxima de seu sono, e espontâneamente" +
                " começa a pensar sobre sua vida e seu passado antes de dormir…", "dLORE.1");

        cap1.addScene(cenaDormirIntro);

        //----------------------------------------------------------------------------------------------------------//
        // EXPLICAÇÃO DA LORE

        Scene cenaLoreIntro = new Scene("Vislumbre do passado", "Uma onda de pensamentos passa por sua cabeça.");

        cenaLoreIntro.addSimpleDialogue("dLORE.1", "O mundo em que você vive é um mundo tanto quanto exótico." +
                " Pessoas detém uma força mágica dentro de si desde que nascem, elas podem ser aperfeiçoadas em" +
                " escolas ou com o talento para fazer qualquer coisa que imaginar. Todavia, são poucas as que conseguem" +
                " sequer despertar uma faísca desta força.", "dLORE.2");
        cenaLoreIntro.addSimpleDialogue("dLORE.2", "Existem criaturas diversas que aterrorizam ou encantam seus" +
                        " arredores. Lobisomens, vampiros, gárgulas, unicórnios… Todos estes são encontrados por aí com um " +
                        "pouco de sorte.", "dLORE.3");
        cenaLoreIntro.addSimpleDialogue("dLORE.3", "E dentre tantas criaturas e pessoas mágicas, você nasceu no" +
                        " interior de Nova Iorque, nos EUA. Seus pais não tiveram muito tempo de vida após seu nascimento" +
                        " e envolveram-se num acidente em um ritual.", "dLORE.4");
        cenaLoreIntro.addSimpleDialogue("dLORE.4", "Você passou grande parte da sua vida em um orfanato." +
                        " Quando cresceu, conseguiu uma bolsa em uma universidade não muito longe de sua cidade natal." +
                        " E então se formou num curso de Biologia e Criaturas Mágicas.", "dLORE.5");
        cenaLoreIntro.addSimpleDialogue("dLORE.5", "Essa foi sua vida desde então. Há alguns meses estava" +
                " trabalhando em uma lojinha de acessórios, mas foi demitida… Você não se lembra exatamente o" +
                " porquê.", "dLORE.6");
        cenaLoreIntro.addSimpleDialogue("dLORE.6", "Após um minuto de vazio em sua mente, Emily se recorda " +
                "vagamente de um nome familiar, aparentemente que veio de alguma mensagem que recebeu...", "dFIM.1");

        cap1.addScene(cenaLoreIntro);

        //----------------------------------------------------------------------------------------------------------//
        // DESFECHO PRÓLOGO

        Scene cenaFimIntro = new Scene("Enfim, o sono", "O travesseiro de sua cama lhe conforta como nunca.");

        cenaFimIntro.addSimpleDialogue("dFIM.1", "“Acho que era… Alvorada da Pratânia? Que nome esquisito…" +
                " Amanhã eu tenho que procurar sobre.”\n\nSeus olhos tremem de cansaço, você se despede de seu celular" +
                " e se ajeita em sua cama para dormir.", "dFIM.2");

        // passa null (ou string vazia) como nextId para sinalizar fim
        cenaFimIntro.addSimpleDialogue("dFIM.2", "A noite passa em um instante enquanto seus sonhos" +
                        " recheiam-se de cenários utópicos.\n\n[ FIM DO PRÓLOGO ]", null);

        cap1.addScene(cenaFimIntro);

        return cap1;
    }

    public Chapter buildEntrevista() {

        Chapter cap2 = new Chapter(2, "A Entrevista de Emprego.");

        //----------------------------------------------------------------------------------------------------------//
        // AMANHECER

        Scene cenaQuartoManha = new Scene("Amanhecer no quarto", "A luz do sol atinge sua janela");

        cenaQuartoManha.addSimpleDialogue("d1.1", "O dia amanheceu, você escuta o canto do pássaros vindos de lá de fora.",
                "d1.2");
        cenaQuartoManha.addSimpleDialogue("d1.2", "Emily", "Cansada", "* Boceja e se espreguiça" +
                " ainda na cama, após um sono tranquilo *", "d1.3");
        cenaQuartoManha.addSimpleDialogue("d1.3", "Você se levanta de sua cama, acordando aos poucos. " +
                "Então, troca de roupa e segue para a cozinha, procurando o que comer.", "d1.4");

        Dialogue d1_1 = new Dialogue("d1.4", "Emily", "O que eu deveria comer?", "Normal");
        d1_1.addOption(new Options("Eu deveria tomar um café para aguentar o dia.", "d2.1.1").setEnergyChange(100));
        d1_1.addOption(new Options("Um pãozinho com manteiga já está bom.", "d2.1.2").setEnergyChange(50));
        d1_1.addOption(new Options("Acho que vou ficar de jejum hoje...", "d2.1.3").setEnergyChange(50));
        cenaQuartoManha.addDialogue(d1_1);

        cap2.addScene(cenaQuartoManha);

        //----------------------------------------------------------------------------------------------------------//
        // TOMANDO OU NÃO CAFÉ

        Scene cenaTomandoCafe = new Scene("Cozinha", "Uma cozinha simples mas suficiente para suas necessidades");

        cenaTomandoCafe.addSimpleDialogue("d2.1.1", "Você checa sua cafeteira, ainda tem um pouco de café." +
                " Então você o esquenta novamente e o toma com leite e açúcar.", "d2.2");
        cenaTomandoCafe.addSimpleDialogue("d2.1.2", "Você procura por um pedaço de pão que deixou no outro dia" +
                " por não estar com fome. Ele lhe é útil agora.", "d2.2");
        cenaTomandoCafe.addSimpleDialogue("2.1.3", "Você vai até sua cozinha, observa se não tem nada fora do" +
                " lugar e não decide comer nada.", "d2.2");
        cenaTomandoCafe.addSimpleDialogue("d2.2", "Após isso, você para um pouco e observa seu quadro de anotações, " +
                "perto da porta de seu apartamento.", "d2.3");
        cenaTomandoCafe.addSimpleDialogue("d2.3", "Emily", "Surpresa", "Ah! É verdade.", "d2.4");
        cenaTomandoCafe.addSimpleDialogue("d2.4", "Emily", "Surpresa", "Meu auxílio acabou e preciso" +
                " procurar outro emprego.", "d2.5");

        Dialogue d2_1 = new Dialogue("d2.5", "Emily", "...", "Pensando");
        d2_1.addOption(new Options("Checar o celular para ver as notícias", "d2.7")
                .setFlagToSet("NOTICIAS")
                .setItemToGive(ItemRepository.getItem("Casaco Simples"))
                .setItemToGive(ItemRepository.getItem("Chave de Casa"))
        );
        d2_1.addOption(new Options("Descer o apartamento (sair de casa)", "d2.8")
                .setItemToGive(ItemRepository.getItem("Casaco Simples"))
                .setItemToGive(ItemRepository.getItem("Chave de Casa"))
        );
        cenaTomandoCafe.addDialogue(d2_1);

        cenaTomandoCafe.addSimpleDialogue("d2.7", "Você checa seu celular, descobrindo que hoje é 15 de dezembro, " +
                "uma terça feira. Recentemente uma loja de conveniências abriu em sua cidade.", "d2.8");
        cenaTomandoCafe.addSimpleDialogue("d2.8", "Então você decide sair. Logo busca seu casaco e a chave de " +
                "casa.", "d3.1");

        cap2.addScene(cenaTomandoCafe);

        //----------------------------------------------------------------------------------------------------------//
        // FORA DO APARTAMENTO

        Scene cenaRuaMural = new Scene("Frente do Apartamento", "Uma rua residencial de baixa classe, um" +
                "pouco movimentada.");

        cenaRuaMural.addSimpleDialogue("d3.1", "Emily desce as escadas de seu apartamento, observando" +
                " como as coisas estão todas no mesmo lugar.", "d3.2");
        cenaRuaMural.addSimpleDialogue("d3.2", "Emily", "Normal", "Parece que nada muda muito nessa parte da cidade.",
                "d3.3");
        cenaRuaMural.addSimpleDialogue("d3.3", "Emily", "Normal", "Antes de sair, irei dar uma " +
                "olhada no mural da rua.", "d4.1");

        cap2.addScene(cenaRuaMural);

        //----------------------------------------------------------------------------------------------------------//
        // MURAL

        Scene cenaMuralArthur = new Scene("No Mural da Rua", "Um mural com vários papéis pendurados com" +
                " objetivos diferentes.");

        cenaMuralArthur.addSimpleDialogue("d4.1", "Ao se aproximar do mural da rua, antes mesmo de você conseguir " +
                "ler uma palavra sequer do que está pendurado hoje, você nota uma pessoa familiar.", "d4.2");
        cenaMuralArthur.addSimpleDialogue("d4.2", "Arthur", "Normal", "Oooh, Oi Emily!! *diz enquanto " +
                "acena para você*", "d4.3");

        Dialogue d4_3 = new Dialogue("d4.3", "Arthur", "Normal", "Quanto tempo que não te vejo, " +
                "como você está?");
        d4_3.addOption(new Options("Oii! Estou bem e você?", "d4.4").setTargetNpc("Arthur").setAffinityChange(10));
        d4_3.addOption(new Options("Ah oi, Arthur...", "d4.5").setTargetNpc("Arthur").setAffinityChange(5));
        d4_3.addOption(new Options("Agora não Arthur, estou com pressa.", "d4.6").setTargetNpc("Arthur").setAffinityChange(-5));
        cenaMuralArthur.addDialogue(d4_3);

        cenaMuralArthur.addSimpleDialogue("d4.4", "Arthur", "Envergonhado",
                "Ah, estou bem também! Que bom ver você... Há um tempo que não conversamos...", "d4.7");
        cenaMuralArthur.addSimpleDialogue("d4.5", "Arthur", "Envergonhado", "Eh, então... Como você vai?",
                "d4.7");

        cap2.addScene(cenaMuralArthur);




        return cap2;
    }
}