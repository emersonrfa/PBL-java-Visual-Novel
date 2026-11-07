package repository;

import model.Chapter;
import model.Choice;

/**
 * Conteúdo da história. Cada capítulo é escrito com {@link StoryBuilder}:
 * narrate / say / ask para as linhas, scene para separar cenas, pending para marcar o que ainda
 * falta escrever. A ordem de parâmetros é sempre (id, [personagem, expressão,] texto [, próximo]).
 *
 * Para criar um capítulo novo: escreva um método buildX() e registre-o em {@link #buildCatalog()}.
 * O teste StoryValidatorTest confere o grafo de todos os capítulos registrados.
 */
public class StoryRepository {
    private final ItemRepository itemRepo = new ItemRepository();

    /** Registra todos os capítulos do jogo. */
    public ChapterCatalog buildCatalog() {
        return new ChapterCatalog()
                .register(1, this::buildPrologue)
                .register(2, this::buildEntrevista);
    }

    // CAPÍTULO 1
    public Chapter buildPrologue() {
        return StoryBuilder.chapter(1, "Prólogo: Um vislumbre do passado.")

                //--------------------------------------------------------------------------------------------------//
                // INTRODUÇÃO
                .scene("Quarto", "Um quarto bagunçado. Porém aconchegante.")
                .narrate("d1.1", "Você sente o vento frio balançando as cortinas do seu quarto.")
                .narrate("d1.2", "Sua janela aberta permite a entrada tanto da brisa constante da" +
                        " noite quanto da suave luz da lua.")
                .narrate("d1.3", "Seu confortável travesseiro nunca lhe abandona quando deseja-se" +
                        " paz e descanso. O quarto, apesar de bagunçado, aconchega-lhe o suficiente para te fazer sentir segura.")
                .ask("d1.4", "Mas algo lhe incomoda, você não consegue dormir.",
                        new Choice("Esticar-se e buscar seu celular", "d2.1"),
                        new Choice("Vislumbrar a vista de sua janela", "d4.1").setFlagToSet("JANELAARTHUR"))

                //--------------------------------------------------------------------------------------------------//
                // OLHANDO O CELULAR
                .scene("Averiguando o celular", "Olhando para a tela de um celular ainda deitada na cama.")
                .narrate("d2.1", "Você se esforça um pouco para pegar seu celular em sua mesa, " +
                        "próxima a cama. Ao ligá-lo, percebe que já é tarde da noite.")
                .narrate("d2.2", "Você abre suas anotações e lê algumas coisas que escreveu durante" +
                        " a semana:\n—> comprar duas caixas de leite\n—> marcar consulta e exames (importante)\n—> comer mais" +
                        " frutas\n—> arrumar o quarto..\n—> olhar emails!!!")
                .ask("d2.3", "O que deseja fazer?",
                        new Choice("Olhar os emails", "d3.1"),
                        new Choice("Desligar o celular e ir para a janela", "d4.1"),
                        new Choice("Esquecer as anotações e tentar dormir", "d5.1"))

                //--------------------------------------------------------------------------------------------------//
                // OLHANDO EMAILS
                .scene("Emails do celular", "Ainda no celular, abre a caixa de emails, repleta de mensagens.")
                .narrate("d3.1", "Ao abrir sua caixa de email, nota-se algumas mensagens " +
                        "supérfluas e alguns spams. Porém, você visualiza uma mensagem favoritada há algumas semanas atrás." +
                        " É uma proposta de emprego para uma organização aparentemente não muito confiável.")
                .narrate("d3.2", "Então você se lembra, esta semana você deixou de receber seu " +
                        "auxílio desemprego, e agora é necessário conseguir uma renda.")
                .say("d3.3", "Player", "Cansada", "“Ahh, mas eu já tentei" +
                        " tantas entrevistas e ainda não consegui nada… Eu deveria tentar esta do email. Qual era o" +
                        " nome mesmo?”")
                .narrate("d3.4", "Enquanto você procura o nome da empresa, você" +
                        " se distrai ao explorar a mensagem lida, e começa a se lembrar de acontecimentos de" +
                        " seu passado.", "dLORE.1")

                //--------------------------------------------------------------------------------------------------//
                // OLHANDO JANELA
                .scene("Janela de Emily", "Olhando através da janela de seu quarto, você vislumbra uma paisagem distante de Nova Iorque.")
                .narrate("d4.1", "Você se levanta e abre sua janela. O céu está bonito, a vista de" +
                        " Nova Iorque se destaca na paisagem. Sua cidade, à esta hora, é silenciosa e calma. Você visualiza a " +
                        "lua e se encanta com sua beleza.")
                .narrate("d4.2", "Em um pequeno prédio próximo à janela, você vê que há alguns" +
                        " apartamentos de luz acesa. Aparentemente você não é a única acordada a esta hora.")
                .say("d4.3", "Player", "Cansada", "“Quem me dera não " +
                        "precisar dormir, me sinto tão cansada.”")
                .narrate("d4.4", "Você suspira e pensa um pouco sobre seu estado…", "dLORE.1")

                //--------------------------------------------------------------------------------------------------//
                // DORMIR DIRETO
                .scene("Indo dormir", "O travesseiro de sua cama lhe conforta como nunca.")
                .narrate("d5.1", "Você ignora suas anotações e se despede de seu celular. Ao se" +
                        " ajeitar para dormir e cobrir-se com seu lençol, você se sente próxima de seu sono, e espontâneamente" +
                        " começa a pensar sobre sua vida e seu passado antes de dormir…", "dLORE.1")

                //--------------------------------------------------------------------------------------------------//
                // EXPLICAÇÃO DA LORE
                .scene("Vislumbre do passado", "Uma onda de pensamentos passa por sua cabeça.")
                .narrate("dLORE.1", "O mundo em que você vive é um mundo tanto quanto exótico." +
                        " Pessoas detém uma força mágica dentro de si desde que nascem, elas podem ser aperfeiçoadas em" +
                        " escolas ou com o talento para fazer qualquer coisa que imaginar. Todavia, são poucas as que conseguem" +
                        " sequer despertar uma faísca desta força.")
                .narrate("dLORE.2", "Existem criaturas diversas que aterrorizam ou encantam seus" +
                        " arredores. Lobisomens, vampiros, gárgulas, unicórnios… Todos estes são encontrados por aí com um " +
                        "pouco de sorte.")
                .narrate("dLORE.3", "E dentre tantas criaturas e pessoas mágicas, você nasceu no" +
                        " interior de Nova Iorque, nos EUA. Seus pais não tiveram muito tempo de vida após seu nascimento" +
                        " e envolveram-se num acidente em um ritual.")
                .narrate("dLORE.4", "Você passou grande parte da sua vida em um orfanato." +
                        " Quando cresceu, conseguiu uma bolsa em uma universidade não muito longe de sua cidade natal." +
                        " E então se formou num curso de Biologia e Criaturas Mágicas.")
                .narrate("dLORE.5", "Essa foi sua vida desde então. Há alguns meses estava" +
                        " trabalhando em uma lojinha de acessórios, mas foi demitida… Você não se lembra exatamente o" +
                        " porquê.")
                .narrate("dLORE.6", "Após um minuto de vazio em sua mente, Emily se recorda " +
                        "vagamente de um nome familiar, aparentemente que veio de alguma mensagem que recebeu...", "dFIM.1")

                //--------------------------------------------------------------------------------------------------//
                // DESFECHO PRÓLOGO
                .scene("Enfim, o sono", "O travesseiro de sua cama lhe conforta como nunca.")
                .narrate("dFIM.1", "“Acho que era… Alvorada da Pratânia? Que nome esquisito…" +
                        " Amanhã eu tenho que procurar sobre.”\n\nSeus olhos tremem de cansaço, você se despede de seu celular" +
                        " e se ajeita em sua cama para dormir.")
                .narrateEnd("dFIM.2", "A noite passa em um instante enquanto seus sonhos" +
                        " recheiam-se de cenários utópicos.\n\n[ FIM DO PRÓLOGO ]")

                .build();
    }

    // CAPÍTULO 2 (roteiro em andamento: d4.6 e d4.7 ainda não foram escritos)
    public Chapter buildEntrevista() {
        return StoryBuilder.chapter(2, "A Entrevista de Emprego.")
                .pending("d4.6", "d4.7")

                //--------------------------------------------------------------------------------------------------//
                // AMANHECER
                .scene("Amanhecer no quarto", "A luz do sol atinge sua janela")
                .narrate("d1.1", "O dia amanheceu, você escuta o canto do pássaros vindos de lá de fora.")
                .say("d1.2", "Emily", "Cansada", "* Boceja e se espreguiça" +
                        " ainda na cama, após um sono tranquilo *")
                .narrate("d1.3", "Você se levanta de sua cama, acordando aos poucos. " +
                        "Então, troca de roupa e segue para a cozinha, procurando o que comer.")
                .ask("d1.4", "Emily", "Normal", "O que eu deveria comer?",
                        new Choice("Eu deveria tomar um café para aguentar o dia.", "d2.1.1").setEnergyChange(100),
                        new Choice("Um pãozinho com manteiga já está bom.", "d2.1.2").setEnergyChange(50),
                        new Choice("Acho que vou ficar de jejum hoje...", "d2.1.3").setEnergyChange(50))

                //--------------------------------------------------------------------------------------------------//
                // TOMANDO OU NÃO CAFÉ
                .scene("Cozinha", "Uma cozinha simples mas suficiente para suas necessidades")
                .narrate("d2.1.1", "Você checa sua cafeteira, ainda tem um pouco de café." +
                        " Então você o esquenta novamente e o toma com leite e açúcar.", "d2.2")
                .narrate("d2.1.2", "Você procura por um pedaço de pão que deixou no outro dia" +
                        " por não estar com fome. Ele lhe é útil agora.", "d2.2")
                .narrate("d2.1.3", "Você vai até sua cozinha, observa se não tem nada fora do" +
                        " lugar e não decide comer nada.", "d2.2")
                .narrate("d2.2", "Após isso, você para um pouco e observa seu quadro de anotações, " +
                        "perto da porta de seu apartamento.")
                .say("d2.3", "Emily", "Surpresa", "Ah! É verdade.")
                .say("d2.4", "Emily", "Surpresa", "Meu auxílio acabou e preciso" +
                        " procurar outro emprego.")
                .ask("d2.5", "Emily", "Pensando", "...",
                        new Choice("Checar o celular para ver as notícias", "d2.7")
                                .setFlagToSet("NOTICIAS")
                                .setItemToGive(itemRepo.getItem("Casaco Simples"))
                                .setItemToGive(itemRepo.getItem("Chave de Casa")),
                        new Choice("Descer o apartamento (sair de casa)", "d2.8")
                                .setItemToGive(itemRepo.getItem("Casaco Simples"))
                                .setItemToGive(itemRepo.getItem("Chave de Casa")))
                .narrate("d2.7", "Você checa seu celular, descobrindo que hoje é 15 de dezembro, " +
                        "uma terça feira. Recentemente uma loja de conveniências abriu em sua cidade.")
                .narrate("d2.8", "Então você decide sair. Logo busca seu casaco e a chave de " +
                        "casa.", "d3.1")

                //--------------------------------------------------------------------------------------------------//
                // FORA DO APARTAMENTO
                .scene("Frente do Apartamento", "Uma rua residencial de baixa classe, um" +
                        "pouco movimentada.")
                .narrate("d3.1", "Emily desce as escadas de seu apartamento, observando" +
                        " como as coisas estão todas no mesmo lugar.")
                .say("d3.2", "Emily", "Normal", "Parece que nada muda muito nessa parte da cidade.")
                .say("d3.3", "Emily", "Normal", "Antes de sair, irei dar uma " +
                        "olhada no mural da rua.", "d4.1")

                //--------------------------------------------------------------------------------------------------//
                // MURAL
                .scene("No Mural da Rua", "Um mural com vários papéis pendurados com" +
                        " objetivos diferentes.")
                .narrate("d4.1", "Ao se aproximar do mural da rua, antes mesmo de você conseguir " +
                        "ler uma palavra sequer do que está pendurado hoje, você nota uma pessoa familiar.")
                .say("d4.2", "Arthur", "Normal", "Oooh, Oi Emily!! *diz enquanto " +
                        "acena para você*")
                .ask("d4.3", "Arthur", "Normal", "Quanto tempo que não te vejo, " +
                                "como você está?",
                        new Choice("Oii! Estou bem e você?", "d4.4").setTargetNpc("Arthur").setAffinityChange(10),
                        new Choice("Ah oi, Arthur...", "d4.5").setTargetNpc("Arthur").setAffinityChange(5),
                        new Choice("Agora não Arthur, estou com pressa.", "d4.6").setTargetNpc("Arthur").setAffinityChange(-5))
                .say("d4.4", "Arthur", "Envergonhado",
                        "Ah, estou bem também! Que bom ver você... Há um tempo que não conversamos...", "d4.7")
                .say("d4.5", "Arthur", "Envergonhado", "Eh, então... Como você vai?", "d4.7")

                .build();
    }
}
