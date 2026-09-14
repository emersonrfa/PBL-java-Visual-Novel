package Controller;

import Model.Chapter;
import Model.Scene;
import Model.Dialogue;
import Model.NPC;
import Model.Player;
import Model.Options;
import java.util.ArrayList;

// CLASSE PARA INSTANCIAR E ESCREVER OS CAPÍTULOS, UTILIZAR CTRL+F PARA SE DESLOCAR

public class StoryBuilder {

    public static Chapter buildPrologue (Player player) { // CAPÍTULO 1 CAP1

        Chapter cap1 = new Chapter(1, "Prólogo: Um vislumbre do passado.");
        Scene cenaQuarto = new Scene("Quarto", "Um quarto bagunçado. Porém aconchegante.");

        //----------------------------------------------------------------------------------------------------------//
        // INTRODUÇÃO

        // sintaxe padrao: addSimpleDialogue(id, text, nextId)
        cenaQuarto.addSimpleDialogue("d1.1", "Você sente o vento frio balançando as cortinas do seu quarto.",
                "d1.2");
        cenaQuarto.addSimpleDialogue("d1.2", "Sua janela aberta permite a entrada tanto da brisa constante da" +
                " noite quanto da suave luz da lua.", "d1.3");
        cenaQuarto.addSimpleDialogue("d1.3", "Seu confortável travesseiro nunca lhe abandona quando deseja-se" +
                " paz e descanso. O quarto, apesar de bagunçado, aconchega-lhe o suficiente para te fazer sentir segura."
                , "d1.4");

        // diálogo com múltiplas escolhas (usa-se o construtor de opções simples)
        Dialogue d1_4 = new Dialogue("d1.4", "Mas algo lhe incomoda, você não consegue dormir.");
        d1_4.addOption(new Options("Esticar-se e buscar seu celular", "d2.1"));
        d1_4.addOption(new Options("Vislumbrar a vista de sua janela", "d4.1"));
        cenaQuarto.addDialogue(d1_4);


        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO O CELULAR

        cenaQuarto.addSimpleDialogue("d2.1", "Você se esforça um pouco para pegar seu celular em sua mesa, " +
                "próxima a cama. Ao ligá-lo, percebe que já é tarde da noite.", "d2.2");
        cenaQuarto.addSimpleDialogue("d2.2", "Você abre suas anotações e lê algumas coisas que escreveu durante" +
                " a semana:\n—> comprar duas caixas de leite\n—> marcar consulta e exames (importante)\n—> comer mais" +
                " frutas\n—> arrumar o quarto..\n—> olhar emails!!!", "d2.3");

        Dialogue d2_3 = new Dialogue("d2.3", "O que deseja fazer?");
        d2_3.addOption(new Options("Olhar os emails", "d3.1"));
        d2_3.addOption(new Options("Desligar o celular e ir para a janela", "d4.1"));
        d2_3.addOption(new Options("Esquecer as anotações e tentar dormir", "d5.1"));
        cenaQuarto.addDialogue(d2_3);


        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO EMAILS

        cenaQuarto.addSimpleDialogue("d3.1", "Ao abrir sua caixa de email, nota-se algumas mensagens " +
                "supérfluas e alguns spams. Porém, você visualiza uma mensagem favoritada há algumas semanas atrás." +
                " É uma proposta de emprego para uma organização aparentemente não muito confiável.", "d3.2");
        cenaQuarto.addSimpleDialogue("d3.2", "Então você se lembra, esta semana você deixou de receber seu " +
                "auxílio desemprego, e agora é necessário conseguir uma renda.", "d3.3");


        cenaQuarto.addSimpleDialogue("d3.3", "Player", "Cansada", "“Ahh, mas eu já tentei" +
                " tantas entrevistas e ainda não consegui nada… Eu deveria tentar esta do email. Qual era o" +
                " nome mesmo?”", "d3.4");

        cenaQuarto.addSimpleDialogue("d3.4", "Enquanto você procura o nome da empresa, você" +
                " se distrai ao explorar a mensagem lida, e começa a se lembrar de acontecimentos de" +
                " seu passado.", "dLORE.1");

        //----------------------------------------------------------------------------------------------------------//
        // OLHANDO JANELA

        cenaQuarto.addSimpleDialogue("d4.1", "Você se levanta e abre sua janela. O céu está bonito, a vista de" +
                " Nova Iorque se destaca na paisagem. Sua cidade, à esta hora, é silenciosa e calma. Você visualiza a " +
                "lua e se encanta com sua beleza.", "d4.2");
        cenaQuarto.addSimpleDialogue("d4.2", "Em um pequeno prédio próximo à janela, você vê que há alguns" +
                " apartamentos de luz acesa. Aparentemente você não é a única acordada a esta hora.",
                "d4.3");
        cenaQuarto.addSimpleDialogue("d4.3", "Player", "Cansada", "“Quem me dera não " +
                "precisar dormir, me sinto tão cansada.”", "d4.4");
        cenaQuarto.addSimpleDialogue("d4.4", "Você suspira e pensa um pouco sobre seu estado…",
                "dLORE.1");

        //----------------------------------------------------------------------------------------------------------//
        // DORMIR DIRETO

        cenaQuarto.addSimpleDialogue("d5.1", "Você ignora suas anotações e se despede de seu celular. Ao se" +
                " ajeitar para dormir e cobrir-se com seu lençol, você se sente próxima de seu sono, e espontâneamente" +
                " começa a pensar sobre sua vida e seu passado antes de dormir…", "dLORE.1");

        //----------------------------------------------------------------------------------------------------------//
        // EXPLICAÇÃO DA LORE

        cenaQuarto.addSimpleDialogue("dLORE.1", "O mundo em que você vive é um mundo tanto quanto exótico." +
                " Pessoas detém uma força mágica dentro de si desde que nascem, elas podem ser aperfeiçoadas em" +
                " escolas ou com o talento para fazer qualquer coisa que imaginar. Todavia, são poucas as que conseguem" +
                " sequer despertar uma faísca desta força.", "dLORE.2");
        cenaQuarto.addSimpleDialogue("dLORE.2", "Existem criaturas diversas que aterrorizam ou encantam seus" +
                " arredores. Lobisomens, vampiros, gárgulas, unicórnios… Todos estes são encontrados por aí com um " +
                "pouco de sorte.", "dLORE.3");
        cenaQuarto.addSimpleDialogue("dLORE.3", "E dentre tantas criaturas e pessoas mágicas, você nasceu no" +
                " interior de Nova Iorque, nos EUA. Seus pais não tiveram muito tempo de vida após seu nascimento" +
                " e envolveram-se num acidente em um ritual.", "dLORE.4");
        cenaQuarto.addSimpleDialogue("dLORE.4", "Você passou grande parte da sua vida em um orfanato." +
                " Quando cresceu, conseguiu uma bolsa em uma universidade não muito longe de sua cidade natal." +
                " E então se formou num curso de Biologia e Criaturas Mágicas.", "dFIM.1");

        //----------------------------------------------------------------------------------------------------------//
        // DESFECHO PRÓLOGO

        cenaQuarto.addSimpleDialogue("dFIM.1", "“Acho que era… Alvorada da Pratânia? Que nome esquisito…" +
                " Amanhã eu tenho que procurar sobre.”\n\nSeus olhos tremem de cansaço, você se despede de seu celular" +
                " e se ajeita em sua cama para dormir.", "dFIM.2");

        // passa null (ou string vazia) como nextId para sinalizar fim
        cenaQuarto.addSimpleDialogue("dFIM.2", "A noite passa em um instante enquanto seus sonhos" +
                " recheiam-se de cenários utópicos.\n\n[ FIM DO PRÓLOGO ]", null);

        cap1.addScene(cenaQuarto);
        return cap1;
    }
}
