package Repository;

import Model.Chapter;
import Model.Scene;
import Model.Dialogue;
import Model.Options;

public class InMemoryStoryRepository {

    public Chapter loadPrologue() {
        Chapter cap1 = new Chapter(1, "Prólogo: Um vislumbre do passado.");

        //------------------------------------------------------------------//
        // INTRODUÇÃO
        Scene cenaQuartoIntro = new Scene("Quarto", "Um quarto bagunçado. Porém aconchegante.");

        cenaQuartoIntro.addDialogue(new Dialogue("d1.1", "Você sente o vento frio balançando as cortinas do seu quarto.", "d1.2"));
        cenaQuartoIntro.addDialogue(new Dialogue("d1.2", "Sua janela aberta permite a entrada tanto da brisa constante da noite quanto da suave luz da lua.", "d1.3"));
        cenaQuartoIntro.addDialogue(new Dialogue("d1.3", "Seu confortável travesseiro nunca lhe abandona quando deseja-se paz e descanso. O quarto, apesar de bagunçado, aconchega-lhe o suficiente para te fazer sentir segura.", "d1.4"));

        Dialogue d1_4 = new Dialogue("d1.4", "Mas algo lhe incomoda, você não consegue dormir.");
        d1_4.addOption(new Options("Esticar-se e buscar seu celular", "d2.1"));
        d1_4.addOption(new Options("Vislumbrar a vista de sua janela", "d4.1"));
        cenaQuartoIntro.addDialogue(d1_4);

        cap1.addScene(cenaQuartoIntro);

        //------------------------------------------------------------------//
        // OLHANDO O CELULAR
        Scene cenaCelular = new Scene("Averiguando o celular", "Olhando para a tela de um celular ainda deitada na cama.");

        cenaCelular.addDialogue(new Dialogue("d2.1", "Você se esforça um pouco para pegar seu celular em sua mesa, próxima a cama. Ao ligá-lo, percebe que já é tarde da noite.", "d2.2"));
        cenaCelular.addDialogue(new Dialogue("d2.2", "Você abre suas anotações e lê algumas coisas que escreveu durante a semana:\n—> comprar duas caixas de leite\n—> marcar consulta e exames (importante)\n—> comer mais frutas\n—> arrumar o quarto..\n—> olhar emails!!!", "d2.3"));

        Dialogue d2_3 = new Dialogue("d2.3", "O que deseja fazer?");
        d2_3.addOption(new Options("Olhar os emails", "d3.1"));
        d2_3.addOption(new Options("Desligar o celular e ir para a janela", "d4.1"));
        d2_3.addOption(new Options("Esquecer as anotações e tentar dormir", "d5.1"));
        cenaCelular.addDialogue(d2_3);

        cap1.addScene(cenaCelular);

        //------------------------------------------------------------------//
        // OLHANDO EMAILS
        Scene cenaIntroEmails = new Scene("Emails do celular", "Ainda no celular, abre a caixa de emails, repleta de mensagens.");

        cenaIntroEmails.addDialogue(new Dialogue("d3.1", "Ao abrir sua caixa de email, nota-se algumas mensagens supérfluas e alguns spams. Porém, você visualiza uma mensagem favoritada há algumas semanas atrás. É uma proposta de emprego para uma organização aparentemente não muito confiável.", "d3.2"));
        cenaIntroEmails.addDialogue(new Dialogue("d3.2", "Então você se lembra, esta semana você deixou de receber seu auxílio desemprego, e agora é necessário conseguir uma renda.", "d3.3"));
        cenaIntroEmails.addDialogue(new Dialogue("d3.3", "Player", "Cansada", "“Ahh, mas eu já tentei tantas entrevistas e ainda não consegui nada… Eu deveria tentar esta do email. Qual era o nome mesmo?”", "d3.4"));
        cenaIntroEmails.addDialogue(new Dialogue("d3.4", "Enquanto você procura o nome da empresa, você se distrai ao explorar a mensagem lida, e começa a se lembrar de acontecimentos de seu passado.", "dLORE.1"));

        cap1.addScene(cenaIntroEmails);

        //------------------------------------------------------------------//
        // OLHANDO JANELA, DORMIR, LORE E DESFECHO
        // (Siga o mesmo padrão de criar instâncias de 'Dialogue' e passá-las para 'addDialogue' da Scene correspondente).

        return cap1;
    }
}
