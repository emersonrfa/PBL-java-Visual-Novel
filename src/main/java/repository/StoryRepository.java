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
                .register(2, this::buildEntrevista)
                .register(3, this::buildEncontro)
                .register(4, this::buildMansao);
    }

    // ==================================================================================================
    // CAPÍTULO 1
    // ==================================================================================================
    public Chapter buildPrologue() {
        return StoryBuilder.chapter(1, "Prólogo: Um vislumbre do passado.")

                //--------------------------------------------------------------------------------------------------//
                // INTRODUÇÃO
                .scene("Quarto", "Um quarto bagunçado. Porém aconchegante.").background("quarto_baguncado")
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
                .scene("Averiguando o celular", "Olhando para a tela de um celular ainda deitada na cama.").background("celular_com_as_notas")
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
                .scene("Emails do celular", "Ainda no celular, abre a caixa de emails, repleta de mensagens.").background("celular_com_emails")
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
                .scene("Janela de Emily", "Olhando através da janela de seu quarto, você vislumbra uma paisagem distante de Nova Iorque.").background("janela_aberta")
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
                .scene("Indo dormir", "O travesseiro de sua cama lhe conforta como nunca.").background("quarto_baguncado")
                .narrate("d5.1", "Você ignora suas anotações e se despede de seu celular. Ao se" +
                        " ajeitar para dormir e cobrir-se com seu lençol, você se sente próxima de seu sono, e espontâneamente" +
                        " começa a pensar sobre sua vida e seu passado antes de dormir…", "dLORE.1")

                //--------------------------------------------------------------------------------------------------//
                // EXPLICAÇÃO DA LORE
                .scene("Vislumbre do passado", "Uma onda de pensamentos passa por sua cabeça.").background("pensamentos")
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
                .scene("Enfim, o sono", "O travesseiro de sua cama lhe conforta como nunca.").background("quarto_baguncado")
                .narrate("dFIM.1", "“Acho que era… Alvorada da Pratânia? Que nome esquisito…" +
                        " Amanhã eu tenho que procurar sobre.”\n\nSeus olhos tremem de cansaço, você se despede de seu celular" +
                        " e se ajeita em sua cama para dormir.")
                .narrateEnd("dFIM.2", "A noite passa em um instante enquanto seus sonhos" +
                        " recheiam-se de cenários utópicos.\n\n[ FIM DO PRÓLOGO ]")

                .build();
    }

    // ==================================================================================================
    // CAPÍTULO 2
    // ==================================================================================================
    public Chapter buildEntrevista() {
        return StoryBuilder.chapter(2, "A Entrevista de Emprego.")

                //--------------------------------------------------------------------------------------------------//
                // AMANHECER
                .scene("Amanhecer no quarto", "A luz do sol atinge sua janela").background("quarto_baguncado")
                .narrate("d1.1", "O dia amanheceu, você escuta o canto dos pássaros vindos de lá de fora.")
                .say("d1.2", "Emily", "Cansada", "* Boceja e se espreguiça" +
                        " ainda na cama, após um sono tranquilo *")
                .narrate("d1.3", "Você se levanta de sua cama, acordando aos poucos. " +
                        "Então, troca de roupa e segue para a cozinha, procurando o que comer.")
                .ask("d1.4", "Emily", "Normal", "O que eu deveria comer?",
                        new Choice("Eu deveria tomar um café para aguentar o dia (+100 Energia).", "d2.1.1").setEnergyChange(100),
                        new Choice("Um pãozinho com manteiga já está bom (+50 Energia).", "d2.1.2").setEnergyChange(50),
                        new Choice("Acho que vou ficar de jejum hoje...", "d2.1.3"))

                //--------------------------------------------------------------------------------------------------//
                // TOMANDO OU NÃO CAFÉ
                .scene("Cozinha", "Uma cozinha simples mas suficiente para suas necessidades").background("cozinha")
                .narrate("d2.1.1", "Você checa sua cafeteira, ainda tem um pouco de café." +
                        " Então você o esquenta novamente e o toma com leite e açúcar.", "d2.2")
                .narrate("d2.1.2", "Você procura por um pedaço de pão que deixou no outro dia" +
                        " por não estar com fome. Ele lhe é útil agora.", "d2.2")
                .narrate("d2.1.3", "Você vai até sua cozinha, observa se não tem nada fora do" +
                        " lugar e decide não comer nada.", "d2.2")
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
                        "uma terça-feira. As manchetes comentam sobre o frio atípico no interior de Nova Iorque e " +
                        "a inauguração de uma nova loja de conveniência na cidade.")
                .narrate("d2.8", "Então você decide sair. Logo busca seu casaco e a chave de " +
                        "casa.", "d3.1")

                //--------------------------------------------------------------------------------------------------//
                // FORA DO APARTAMENTO
                .scene("Frente do Apartamento", "Uma rua residencial de baixa classe, um " +
                        "pouco movimentada.").background("na_rua")
                .narrate("d3.1", "Emily desce as escadas de seu apartamento, observando" +
                        " como as coisas estão todas no mesmo lugar.")
                .say("d3.2", "Emily", "Normal", "Parece que nada muda muito nessa parte da cidade.")
                .say("d3.3", "Emily", "Normal", "Antes de sair, irei dar uma " +
                        "olhada no mural da rua.", "d4.1")

                //--------------------------------------------------------------------------------------------------//
                // MURAL E ENCONTRO COM ARTHUR
                .scene("No Mural da Rua", "Um mural com vários papéis pendurados com" +
                        " objetivos diferentes.").background("mural")
                .narrate("d4.1", "Ao se aproximar do mural da rua, antes mesmo de você conseguir " +
                        "ler uma palavra sequer do que está pendurado hoje, você nota uma pessoa familiar.")
                .say("d4.2", "Arthur", "Normal", "Oooh, Oi Emily!! *diz enquanto " +
                        "acena para você*")
                .ask("d4.3", "Arthur", "Normal", "Quanto tempo que não te vejo, " +
                                "como você está?",
                        new Choice("“Oii! Estou bem e você?”", "d4.4").setTargetNpc("Arthur").setAffinityChange(10),
                        new Choice("“Ah oi, Arthur...”", "d4.5").setTargetNpc("Arthur").setAffinityChange(5),
                        new Choice("“Agora não Arthur, estou com pressa.”", "d4.6").setTargetNpc("Arthur").setAffinityChange(-5))
                .say("d4.4", "Arthur", "Envergonhado",
                        "Ah, estou bem também! Que bom ver você... Há um tempo que não conversamos...", "d4.7")
                .say("d4.5", "Arthur", "Envergonhado", "Eh, então... Como você vai?", "d4.7")
                .say("d4.6", "Arthur", "Triste",
                        "Ah... Claro, desculpa atrapalhar, Emily. Boa sorte aí no que você for fazer!", "d5.1")
                .narrate("d4.7", "Arthur ajeita a alça da mochila, visivelmente animado por finalmente puxar assunto com você.")
                .ask("d4.8", "Emily", "Normal", "Como deseja continuar a conversa?",
                        new Choice("“Vi a luz do seu quarto acesa até tarde ontem à noite.”", "d4.9.1")
                                .setRequiredFlag("JANELAARTHUR")
                                .setFlagToSet("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(10),
                        new Choice("“E a faculdade, o que você anda estudando ultimamente?”", "d4.9.2")
                                .setFlagToSet("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("“Queria ficar conversando, mas tenho uma entrevista agora.”", "d4.9.3"))
                .say("d4.9.1", "Arthur", "Surpresa",
                        "Espera, você viu?! Eu nem percebi que minha janela dava direto para a sua... " +
                                "Fiquei virado até tarde revisando uns apontamentos!", "d4.10")
                .say("d4.9.2", "Arthur", "Normal",
                        "Ah, a faculdade está uma correria! Como entrei alguns semestres depois de você e em outro curso, " +
                                "ainda estou pegando várias matérias extras.", "d4.10")
                .say("d4.9.3", "Arthur", "Normal",
                        "Sem problemas! Vai lá e arrasa nessa entrevista, Emily. A gente se fala depois!", "d5.1")
                .say("d4.10", "Arthur", "Animado",
                        "Na verdade, eu estava estudando sobre vampiros! Vou apresentar uma palestra numa disciplina eletiva " +
                                "sobre mitos noturnos e hematofagia.")
                .say("d4.11", "Emily", "Sorrindo",
                        "Vampiros? Logo na minha área! Se quiser uma dica: não confie nos livros antigos sobre alho e espelhos, " +
                                "a fisiologia mágica deles é bem mais teimosa que isso.")
                .say("d4.12", "Arthur", "Envergonhado",
                        "Nossa, verdade, você se formou em Biologia e Criaturas Mágicas! Valeu mesmo pela dica, Emily. " +
                                "Bom, não vou mais te segurar. Boa sorte hoje!", "d5.1")

                //--------------------------------------------------------------------------------------------------//
                // TRAJETO ATÉ A ENTREVISTA
                .scene("A Caminho da Entrevista", "As ruas frias de dezembro rumo ao endereço do e-mail.").background("na_rua")
                .narrate("d5.1", "Você abre o e-mail favoritado no celular. Ao ler o cabeçalho, solta um suspiro constrangido: " +
                        "não era 'Alvorada da Pratânia' coisa nenhuma — o nome real é 'Ordem de Santa Prata'.")
                .narrate("d5.2", "O endereço aponta para a extremidade sul da cidade, bem ao lado de uma loja de conveniência recém-aberta.")
                .ask("d5.3", "Emily", "Pensando", "Como devo ir até lá?",
                        new Choice("Ir a pé pela calçada (-20 Energia)", "d5.4.1").setEnergyChange(-20),
                        new Choice("Pegar o ônibus urbano (-$4,00)", "d5.4.2").setMoneyChange(-4.0))
                .narrate("d5.4.1", "Você aperta o passo pela calçada gelada, economizando cada centavo enquanto o vento bate no seu rosto.", "d6.1")
                .narrate("d5.4.2", "Você paga a passagem e se acomoda perto da janela do ônibus, observando os prédios ficarem mais espaçados.", "d6.1")

                //--------------------------------------------------------------------------------------------------//
                // LOJA DE CONVENIÊNCIA E SENHA
                .scene("Loja de Conveniência", "Uma loja simples com luzes fluorescentes e uma estátua peculiar no balcão.").background("loja_conveniencia")
                .narrate("d6.1", "Ao descer no endereço indicado, você se depara com uma pequena loja de conveniência. " +
                        "Não há placa de escritório nem recepção corporativa.")
                .say("d6.2", "Emily", "Confusa", "Será que eu errei o número da rua? Isso aqui vende refrigerante e salgadinho...")
                .narrate("d6.3", "Você relê o e-mail com atenção e nota uma frase destacada em itálico no rodapé: " +
                        "“Petrifique o obscuro e busque a luz.”")
                .narrate("d6.4", "Você entra na loja. Atrás do balcão, uma atendente folheia uma revista entediada ao lado " +
                        "de uma pequena estátua de gárgula de pedra.")
                .ask("d6.5", "Clara", "Normal", "Bom dia. Precisa de alguma coisa?",
                        new Choice("“Petrifique o obscuro e busque a luz.”", "d6.6"),
                        new Choice("Caminhar pelos corredores fingindo olhar os produtos", "d6.7"),
                        new Choice("Pegar um Energético Lunar no freezer (-$4,00 | +35 Energia)", "d6.7")
                                .setMoneyChange(-4.0)
                                .setEnergyChange(35)
                                .setItemToGive(itemRepo.getItem("Energetico Lunar")),
                        new Choice("Comprar uma Pastilha de Alho Extra Forte (-$3,00)", "d6.7")
                                .setMoneyChange(-3.0)
                                .setItemToGive(itemRepo.getItem("Pastilha de Alho")),
                        new Choice("Comprar um Amuleto de Prata Falsa do expositor (-$6,00)", "d6.7")
                                .setMoneyChange(-6.0)
                                .setItemToGive(itemRepo.getItem("Amuleto de Prata Falsa")))
                .say("d6.6", "Clara", "Normal",
                        "Finalmente alguém que lê o e-mail inteiro. Pode entrar pela porta dos fundos, ele está esperando.", "d7.1")
                .narrate("d6.7", "Enquanto você faz hora perto do balcão, os olhos de pedra da estátua de gárgula brilham " +
                        "com uma luz dourada intensa. Uma voz grave ecoa pelo recinto: “Estávamos esperando por você, Emily.”")
                .say("d6.8", "Clara", "Entediada",
                        "Ótimo, a estátua dramática estragou o protocolo de novo. Vai logo pela porta dos fundos, moça.", "d7.1")

                //--------------------------------------------------------------------------------------------------//
                // ENTREVISTA COM SANTO PEDRO
                .scene("Sala Secreta de Santa Prata", "Uma sala reservada com cheiro de incenso, couro velho e prata polida.").background("sala_santa_prata")
                .narrate("d7.1", "Você atravessa a porta dos fundos e encontra um ambiente completamente diferente da loja: " +
                        "estantes com tomos antigos, mapas marcados e maletas metálicas.")
                .narrate("d7.2", "Atrás de uma mesa maciça está um homem alto, muito forte, de cabelos grisalhos e barba aparada. " +
                        "Ele veste roupas alinhadas, mas carrega frascos de água benta, estacas de prata e apetrechos de exorcismo.")
                .say("d7.3", "Santo Pedro", "Serio",
                        "Sente-se, Emily. Sou Santo Pedro, coordenador da Ordem de Santa Prata. Vamos direto ao ponto: " +
                                "por que alguém com seu diploma responderia a um anúncio nosso?")
                .ask("d7.4", "Emily", "Normal", "Como você responde?",
                        new Choice("“Meu auxílio acabou ontem e preciso pagar minhas contas. Não estou em posição de recusar.”", "d7.5.1")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(5),
                        new Choice("“Sou formada em Biologia e Criaturas Mágicas. Sei ler padrões onde outros só veem lendas.”", "d7.5.2")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(10),
                        new Choice("“Digamos que o anúncio não mencionava um coordenador com uma presença tão... marcante.”", "d7.5.3")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(5)
                                .setFlagToSet("FLERTOU_PEDRO"))
                .say("d7.5.1", "Santo Pedro", "Serio",
                        "Hmph. Pelo menos não tenta dourar a pílula. Gente com fome de sobreviver costuma prestar atenção.", "d7.6")
                .say("d7.5.2", "Santo Pedro", "Normal",
                        "Conhecimento técnico evita que novatos virem jantar na primeira noite. Bom.", "d7.6")
                .say("d7.5.3", "Santo Pedro", "Surpresa",
                        "*Tosse secamente e ajeita o colarinho, visivelmente desconcertado* Foco na entrevista, senhorita Emily.", "d7.6")
                .say("d7.6", "Santo Pedro", "Serio",
                        "Somos uma organização dedicada a caçar e conter criaturas das trevas. E, considerando seu perfil discreto, " +
                                "você está contratada. Seu trabalho começa agora.")
                .say("d7.7", "Emily", "Assustada",
                        "E-Espera aí! Caçar criaturas das trevas?! Eu achei que fosse algo de catalogação ou laboratório!")
                .say("d7.8", "Santo Pedro", "Serio",
                        "Há boatos sólidos de que uma família nobre no interior de Nova Iorque é formada por vampiros antigos. " +
                                "Eles estão acolhendo vampiros errantes em uma mansão isolada. Você vai fingir ser um deles, " +
                                "se infiltrar lá dentro, mapear o local e seus artefatos e abrir caminho para nossa invasão.")
                .ask("d7.9", "Emily", "Assustada", "Isso parece loucura...",
                        new Choice("“Isso é suicídio! Eles vão sentir meu cheiro humano no primeiro segundo!”", "d7.10.1"),
                        new Choice("“Quanto exatamente esse trabalho paga pelo risco?”", "d7.10.2")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(5))
                .say("d7.10.1", "Santo Pedro", "Normal",
                        "Não se você usar o que preparamos. Nossos alquimistas criaram artefatos justamente para anular seus sinais vitais.", "d7.11")
                .say("d7.10.2", "Santo Pedro", "Normal",
                        "O suficiente para pagar um ano inteiro do seu aluguel logo no primeiro pagamento. E você não irá desarmada.", "d7.11")
                .narrate("d7.11", "Santo Pedro desliza sobre a mesa um colar escuro, um estojo com lentes de contato vermelhas e um cartão amassado.")
                .ask("d7.12", "Santo Pedro", "Serio",
                        "Amanhã ao anoitecer, vá até o bar indicado neste cartão e peça para ver a matriarca. Alguma dúvida?",
                        new Choice("Guardar os equipamentos no bolso e confirmar o acordo", "d7.13")
                                .setItemToGive(itemRepo.getItem("Colar de Ocultacao"))
                                .setItemToGive(itemRepo.getItem("Lentes Carmesim"))
                                .setItemToGive(itemRepo.getItem("Cartao do Bar"))
                                .setFlagToSet("MISSAO_ACEITA"))
                .say("d7.13", "Santo Pedro", "Normal",
                        "Ótimo. Vá para casa, descanse e estude seu papel. Não nos decepcione.", "d8.1")

                //--------------------------------------------------------------------------------------------------//
                // TRAJETO DE VOLTA (CORRIGIDO: CENA NA RUA ANTES DE CHEGAR EM CASA)
                .scene("Caminho de Volta", "As ruas no fim da tarde enquanto Emily processa sua nova missão.").background("na_rua")
                .narrate("d8.1", "Você sai da loja de conveniência sentindo o peso frio do colar mágico no bolso do casaco.")
                .ask("d8.2", "Emily", "Pensando", "Como voltar para casa?",
                        new Choice("Voltar a pé (-20 Energia)", "d8.3.1").setEnergyChange(-20),
                        new Choice("Pegar o ônibus (-$4,00)", "d8.3.2").setMoneyChange(-4.0))
                .narrate("d8.3.1", "Você caminha de volta até seu bairro, tentando processar o fato de que agora é espiã de uma ordem de caçadores.", "d8.4")
                .narrate("d8.3.2", "Você pega o ônibus de volta e passa o trajeto inteiro encarando o Cartão do Bar Eclipse.", "d8.4")

                //--------------------------------------------------------------------------------------------------//
                // CHEGADA EM CASA
                .scene("De Volta ao Apartamento", "O conforto familiar da sua cozinha após uma tarde inacreditável.").background("cozinha")
                .narrate("d8.4", "Você finalmente sobe as escadas do prédio, tranca a porta do apartamento atrás de si e sente seu estômago roncar.")
                .ask("d8.5", "Emily", "Normal", "O dia já passou da hora do almoço.",
                        new Choice("Preparar um almoço tardio e descansar (+30 Energia)", "d8.6").setEnergyChange(30))
                .narrate("d8.6", "Depois de comer, você passa o restante do dia repassando tudo o que sabe sobre vampiros " +
                        "e planejando como agir amanhã à noite.")
                .narrateEnd("d8.7", "A noite cai sobre a cidade enquanto Emily se prepara para sua primeira incursão no mundo das sombras.\n\n[ FIM DO CAPÍTULO 2 ]")

                .build();
    }

    // ==================================================================================================
    // CAPÍTULO 3
    // ==================================================================================================
    public Chapter buildEncontro() {
        return StoryBuilder.chapter(3, "O Encontro.")

                //--------------------------------------------------------------------------------------------------//
                // VISITA AO ARTHUR
                .scene("Manhã na Casa de Arthur", "Um apartamento acolhedor repleto de livros e anotações universitárias.").background("casa_arthur")
                .narrate("d1.1", "Na manhã seguinte, antes de mergulhar de vez na missão, você decide atravessar o corredor e visitar seu vizinho Arthur.")
                .say("d1.2", "Arthur", "Animado",
                        "Emily! Que surpresa boa! Entra aí, eu acabei de passar café e fiz umas torradas.")
                .ask("d1.3", "Emily", "Normal", "O cheiro de café fresco enche a sala.",
                        new Choice("Aceitar o café com torradas e agradecer (+25 Energia)", "d1.4.1")
                                .setEnergyChange(25)
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("Agradecer, mas recusar dizendo que já tomou café antes de sair", "d1.4.2")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(2))
                .say("d1.4.1", "Arthur", "Sorrindo",
                        "Fico feliz que tenha gostado! Senta aí, a gente quase nunca tem tempo de conversar com calma.", "d1.5")
                .say("d1.4.2", "Arthur", "Normal",
                        "Ah, sem problemas! Fica à vontade no sofá mesmo assim, faz tempo que não colocamos o papo em dia.", "d1.5")
                .say("d1.5", "Arthur", "Normal",
                        "E então, como foi aquela entrevista misteriosa ontem? Conseguiu a vaga?")
                .ask("d1.6", "Emily", "Pensando", "Como comentar sobre o emprego?",
                        new Choice("“Consegui! E olha... o coordenador é enorme, forte e bem impressionante.”", "d1.7.1")
                                .setRequiredFlag("FLERTOU_PEDRO")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(-3),
                        new Choice("“Consegui um trabalho noturno bem peculiar, voltado para investigação.”", "d1.7.2")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("“Digamos que meu novo chefe parece um caçador saído de um filme antigo.”", "d1.7.3")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5))
                .say("d1.7.1", "Arthur", "Envergonhado",
                        "*Engasga levemente com o suco e olha para os próprios braços magros* S-Sério? Forte tipo... lenhador? " +
                                "Bom, que bom que você gostou do ambiente de trabalho, né...", "d1.8")
                .say("d1.7.2", "Arthur", "Surpresa",
                        "Trabalho noturno? Puxa, cuidado andando por aí à noite nesse frio, Emily!", "d1.8")
                .say("d1.7.3", "Arthur", "Normal",
                        "Hahaha, espero que pelo menos ele pague em dia! Mas falando sério, isso tudo parece meio maluco.", "d1.8")
                .say("d1.8", "Emily", "Sorrindo",
                        "Pois é, parece loucura mesmo... Mas entre ficar sem pagar o aluguel e encarar algo estranho, " +
                                "eu prefiro a segunda opção. Além disso, confesso que fiquei curiosa.")
                .ask("d1.9", "Arthur", "Normal", "Arthur acompanha você até a porta.",
                        new Choice("“Pode deixar, vou te mandando notícias quando puder.”", "d1.10.1")
                                .setRequiredFlag("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("“Valeu pelo papo, Arthur. A gente se vê por aí.”", "d1.10.2"))
                .say("d1.10.1", "Arthur", "Sorrindo",
                        "Se se sentir muito sozinha nessa missão, me manda mensagem quando quiser, tá? " +
                                "E como você foi super gentil comigo ontem... se precisar de qualquer coisa mesmo, pode contar comigo!", "d2.1")
                .say("d1.10.2", "Arthur", "Normal",
                        "Se você se sentir muito sozinha nesse turno da noite, pode me mandar mensagem a qualquer hora, beleza?", "d2.1")

                //--------------------------------------------------------------------------------------------------//
                // PREPARAÇÃO DO DISFARCE
                .scene("Preparando o Disfarce", "O crepúsculo tinge o quarto enquanto Emily assume uma nova identidade.").background("quarto_baguncado")
                .narrate("d2.1", "O dia passa rápido. Perto do anoitecer, você tranca a porta do quarto e espalha os itens de Santo Pedro sobre a cama.")
                .narrate("d2.2", "Você veste roupas antigas e gastas, sujando propositalmente a barra do casaco e as mangas " +
                        "para parecer uma vampira errante que passou dias fugindo.")
                .narrate("d2.3", "Em seguida, coloca as Lentes de Contato Carmesim e prende o Colar de Ocultação Sombria no pescoço. " +
                        "Uma onda fria percorre sua pele, silenciando o ritmo humano do seu coração aos sentidos sobrenaturais.")
                .narrate("d2.4", "Neste mundo, vampiros carregam marcas evidentes como olhos vermelhos e presas alongadas — " +
                        "e, por uma herança mágica própria, pessoas negras transformadas mantêm sua melanina, apenas suavizando levemente o tom. " +
                        "Com os feitiços e lentes de Santo Pedro ajustados, nenhum traço humano denuncia Emily à primeira vista.")
                .ask("d2.5", "Emily", "Seria", "Tudo pronto. Hora de sair.",
                        new Choice("Puxar o capuz sobre o rosto e seguir para o Bar Eclipse (-15 Energia)", "d3.1")
                                .setEnergyChange(-15))

                //--------------------------------------------------------------------------------------------------//
                // CHEGADA AO BAR ECLIPSE
                .scene("O Bar Eclipse", "Um bar velho, barulhento e enfumaçado nos limites da cidade.").background("bar_eclipse")
                .narrate("d3.1", "Você segue as instruções de Santo Pedro, mantém o capuz cobrindo parte do rosto e entra no Bar Eclipse. " +
                        "O lugar é velho, barulhento e cheio de clientes distraídos; ninguém nota sua chegada.")
                .narrate("d3.2", "Você atravessa o salão até o balcão, chama o atendente e diz em voz baixa e firme: " +
                        "“Vim ver a matriarca.”")
                .say("d3.3", "Atendente", "Normal",
                        "*Sem demonstrar qualquer surpresa, aponta com o queixo para uma cortina pesada no corredor dos fundos* " +
                                "Siga até o fim do corredor. Porta à esquerda.", "d4.1")

                //--------------------------------------------------------------------------------------------------//
                // ENCONTRO COM GABRIEL E CASSIAN
                .scene("Sala Escura dos Fundos", "Uma sala oculta e mergulhada em sombras no fundo do bar.").background("sala_escura_bar")
                .narrate("d4.1", "Você atravessa o corredor estreito e abre a porta indicada, entrando em uma sala escura e silenciosa.")
                .narrate("d4.2", "A porta se fecha atrás de você. No breu à frente, dois pares de olhos vermelhos se acendem na penumbra.")
                .narrate("d4.3", "Duas figuras emergem das sombras. O primeiro tem um sorriso animado e postura inquieta; " +
                        "o segundo usa um sobretudo gótico comprido demais e segura uma prancheta antiga.")
                .say("d4.4", "Gabriel", "Animado",
                        "Ora, ora! Mais uma alma sedenta vagando pela noite! Mal posso esperar para saber como você drena suas presas—")
                .narrate("d4.5", "*CRASH!* Ao tentar dar um passo dramático para o lado, o segundo vampiro tropeça na barra do próprio sobretudo " +
                        "e derruba uma pilha de caixotes vazios.")
                .say("d4.6", "Cassian", "Envergonhado",
                        "E-Esses caixotes mortais foram posicionados de forma totalmente antiestética! Gabriel, eu disse para você " +
                                "não começar com o discurso de dissecar vítimas logo na recepção!")
                .say("d4.7", "Gabriel", "Normal",
                        "Ah, qual é, Cassian! Dá clima! *vira-se novamente para você, tentando parecer sério* " +
                                "Enfim, novata encapuzada... quem é você e o que faz por aqui?")
                .ask("d4.8", "Emily", "Normal", "Os dois observam sua reação atentamente.",
                        new Choice("“Passei as últimas semanas fugindo de caçadores pelo norte. Só quero um abrigo.”", "d4.9.1")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Admito que a entrada das sombras foi dramática. Vocês sempre recebem visitas assim?”", "d4.9.2")
                                .setTargetNpc("Cassian")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Estava catalogando padrões de hematofagia na região antes de buscar refúgio.”", "d4.9.3")
                                .setTargetNpc("Gabriel")
                                .setSuspicionChange(30))
                .say("d4.9.1", "Gabriel", "Animado",
                        "Fugindo de caçadores?! Eu sabia! Adoro histórias de fuga noturna!", "d4.10")
                .say("d4.9.2", "Cassian", "Sorrindo",
                        "Viu, Gabriel? Alguém com sensibilidade estética! Apesar do pequeno incidente com os caixotes...", "d4.10")
                .say("d4.9.3", "Gabriel", "Confusa",
                        "Catalogando hematofagia? Que vampiro fala igual a um professor universitário?", "d4.10")
                .say("d4.10", "Cassian", "Serio",
                        "Muito bem. Para entrar no programa de vampiros refugiados da nossa casa, precisamos confirmar seus instintos. " +
                                "Qual é o seu tipo sanguíneo favorito e por que busca abrigo?")
                .ask("d4.11", "Emily", "Pensando", "Escolha com cuidado:",
                        new Choice("“Prefiro sangue O+ fresco, e no momento só procuro um lugar seguro para ficar.”", "d4.12.1")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Respeito a hierarquia da Matriarca e sei manter discrição absoluta.”", "d4.12.2")
                                .setTargetNpc("Cassian")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Qualquer coisa vermelha e quente já resolve o meu problema por hoje.”", "d4.12.3")
                                .setTargetNpc("Gabriel")
                                .setSuspicionChange(35))
                .say("d4.12.1", "Gabriel", "Animado",
                        "O+ é ótimo! Clássico, energético! Eu tenho toda uma teoria sobre como capturar presas tipo O+...", "d4.13")
                .say("d4.12.2", "Cassian", "Normal",
                        "Excelente postura. Lady Luna aprecia hóspedes que conhecem a etiqueta da casa.", "d4.13")
                .say("d4.12.3", "Gabriel", "Serio",
                        "“Qualquer coisa vermelha e quente”?! Que tipo de piada sem graça é essa, Cassian?", "d4.13")
                .narrate("d4.13", "Gabriel dá um passo à frente, aproximando-se para farejar o ar ao seu redor e testar seus reflexos.")
                .ask("d4.14", "Gabriel", "Serio", "Deixe-me olhar bem para você...",
                        new Choice("Ajeitar discretamente o colar sob a gola do casaco e permanecer imóvel", "d4.15")
                                .setRequiredItem("Colar de Ocultação Sombria", false)
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5)
                                .setSuspicionChange(-10),
                        new Choice("Sustentar o olhar carmesim sem piscar e deixar as presas à mostra", "d4.15")
                                .setMaxSuspicionAllowed(55)
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5)
                                .setSuspicionChange(-5),
                        new Choice("Dar um passo para trás levando a mão ao pescoço", "FINAL_MORTE_BAR_VAMP"),
                        new Choice("Forçar um riso nervoso e tentar mudar de assunto", "FINAL_MORTE_BAR_VAMP"))
                .say("d4.15", "Gabriel", "Animado",
                        "*Abre um sorrisão e dá um tapinha no ombro de Cassian* Tudo certo por aqui! " +
                                "Seja bem-vinda ao programa de refugiados, novata!", "d4.16")
                .say("d4.16", "Cassian", "Normal",
                        "Aprovada. Aqui está sua carta de convite lacrada. Siga pela estrada antiga das colinas norte amanhã; " +
                                "apresente este selo nos portões da Mansão Nobilis e não nos faça passar vergonha diante da Matriarca.")
                .ask("d4.17", "Emily", "Sorrindo", "Cassian estende um envelope preto selado com cera vermelha.",
                        new Choice("Guardar a Carta de Convite e se despedir da dupla", "d5.1")
                                .setItemToGive(itemRepo.getItem("Carta de Convite"))
                                .setFlagToSet("CONVITE_MANSAO_OBTIDO")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5))

                //--------------------------------------------------------------------------------------------------//
                // RETORNO PARA CASA
                .scene("De Volta ao Quarto", "O silêncio do apartamento após o primeiro contato com os vampiros.").background("quarto_baguncado")
                .narrate("d5.1", "Você sai do Bar Eclipse com o capuz erguido, guardando a carta de convite bem junto ao casaco.")
                .ask("d5.2", "Emily", "Cansada", "O caminho de volta parece bem mais silencioso agora.",
                        new Choice("Entrar em casa, trancar a porta e tirar o disfarce (-10 Energia)", "d5.3")
                                .setEnergyChange(-10))
                .narrate("d5.3", "Já dentro do apartamento, você retira as lentes carmesim, limpa a poeira das roupas " +
                        "e coloca o envelope lacrado sobre a mesa ao lado de um pequeno caderno de bolso.")
                .ask("d5.4", "Emily", "Pensando", "Antes de dormir, você organiza o que vai levar para a infiltração.",
                        new Choice("Colocar o Caderno de Anotações na bolsa e ir descansar (+40 Energia)", "d5.5")
                                .setItemToGive(itemRepo.getItem("Caderno de Anotacoes"))
                                .setEnergyChange(40))
                .narrateEnd("d5.5", "Com a Carta de Convite e o Caderno de Anotações prontos, Emily fecha os olhos antes do dia decisivo.\n\n[ FIM DO CAPÍTULO 3 ]")

                .build();
    }

    // ==================================================================================================
    // CAPÍTULO 4
    // ==================================================================================================
    public Chapter buildMansao() {
        StoryBuilder sb = StoryBuilder.chapter(4, "A Mansão Nobilis.")

                //--------------------------------------------------------------------------------------------------//
                // CHEGADA AOS PORTÕES (GABRIEL E CASSIAN)
                .scene("Portões da Mansão Nobilis", "Uma propriedade gótica imponente cercada por pinheiros nas colinas norte.").background("portoes_mansao")
                .narrate("d1.1", "Seguindo o mapa indicado por Cassian, você sobe a estrada antiga das colinas norte até avistar " +
                        "os altos portões de ferro da Mansão Nobilis.")
                .narrate("d1.2", "O Colar de Ocultação Sombria repousa frio contra sua pele e as lentes carmesim ajustam sua visão " +
                        "à penumbra sob os pinheiros.")
                .say("d1.3", "Cassian", "Serio",
                        "*Surge junto às grades acompanhado de Gabriel* Pontual. A Matriarca detesta atrasos. " +
                                "Mostre o selo de convite para registrarmos sua entrada.")
                .ask("d1.4", "Emily", "Normal", "Os dois aguardam junto ao portão.",
                        new Choice("Estender a Carta de Convite com o selo de cera intacto", "d1.5.1")
                                .setRequiredItem("Carta de Convite da Mansão", false)
                                .setTargetNpc("Cassian")
                                .setAffinityChange(5),
                        new Choice("“Vocês mesmos me entregaram o convite ontem à noite no Bar Eclipse.”", "d1.5.2")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5))
                .say("d1.5.1", "Cassian", "Normal",
                        "Impecável. Protocolo cumprido à risca.", "d1.6")
                .say("d1.5.2", "Gabriel", "Animado",
                        "Haha, verdade! O Cassian só gosta de fazer cena na frente dos portões!", "d1.6")
                .say("d1.6", "Gabriel", "Normal",
                        "Vem, novata! Lady Luna e o Toin já estão no Salão Principal esperando para conhecer você.", "d2.1")

                //--------------------------------------------------------------------------------------------------//
                // SALÃO PRINCIPAL: INTERAÇÃO SIMULTÂNEA COM LUNA E MIGUEL
                .scene("Salão Principal", "Um salão amplo com escadaria dupla, lustres antigos e tapeçarias escuras.").background("salao_principal")
                .narrate("d2.1", "As portas duplas se abrem para um salão grandioso. No centro, junto à lareira apagada, " +
                        "uma mulher elegante de olhar penetrante conversa com um homem de porte atlético e postura de esgrimista.")
                .say("d2.2", "Luna", "Normal",
                        "Então esta é a nova hóspede que nossos recrutadores encontraram. Seja bem-vinda à Mansão Nobilis. " +
                                "Sou Luna Nobilis, anfitriã desta casa.")
                .say("d2.3", "Miguel", "Serio",
                        "*Cruza os braços e observa você de cima a baixo* E eu sou Miguel Tointoskv — pode chamar de Toin. " +
                                "Cuido da segurança deste lugar. Nos últimos meses, caçadores andam farejando demais estas colinas.")
                .say("d2.4", "Luna", "Sorrindo",
                        "Não seja tão ríspido logo na chegada, Miguel. Ela parece exausta da viagem. " +
                                "Diga-me, querida: o que espera encontrar sob o nosso teto?")
                .ask("d2.5", "Emily", "Normal", "Luna e Miguel observam atentamente sua resposta.",
                        new Choice("“Apenas paz longe dos caçadores e lealdade a quem me oferece abrigo.”", "d2.6.1")
                                .setTargetNpc("Luna")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Sei me defender se preciso for, e respeito regras rígidas de segurança.”", "d2.6.2")
                                .setTargetNpc("Miguel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Na verdade, estou fascinada pela arquitetura antiga desta mansão.”", "d2.6.3")
                                .setTargetNpc("Luna")
                                .setAffinityChange(5)
                                .setTargetNpc("Miguel")
                                .setSuspicionChange(10))
                .say("d2.6.1", "Luna", "Sorrindo",
                        "Palavras sensatas. Esta casa sobreviveu por séculos graças à lealdade mútua.", "d2.7")
                .say("d2.6.2", "Miguel", "Normal",
                        "Hmph. Pelo menos não tem cara de quem entra em pânico ao ouvir o nome de um caçador. Veremos na prática.", "d2.7")
                .say("d2.6.3", "Miguel", "Serio",
                        "Arquitetura? Parece mais interessada na planta do prédio do que na nossa hospitalidade...", "d2.7")
                .say("d2.7", "Luna", "Normal",
                        "Gabriel e Cassian estão preparando uma suíte no corredor leste para você até o cair da noite. " +
                                "Até lá, sinta-se livre para caminhar pelas alas comuns e conhecer os demais moradores.", "d3.1")

                //--------------------------------------------------------------------------------------------------//
                // INTERVALO DE ANÁLISE 1 + ESCOLHA DA 1ª ALA
                .scene("Varanda Interna da Mansão", "Uma galeria superior com vista para o saguão silencioso.").background("varanda_mansao")
                .narrate("d3.1", "Assim que sai do Salão Principal, você encontra uma varanda discreta e puxa seu Caderno de Anotações.")
                .narrate("d3.2", "Luna comanda o lugar com autoridade natural, enquanto Miguel trata a mansão como uma fortaleza.")
                .ask("d3.3", "Emily", "Pensando", "O que passa pela sua cabeça neste momento?",
                        new Choice("Desenhar a planta do Salão Principal e anotar a postura defensiva de Miguel", "d3.4.1")
                                .setFlagToSet("FOCO_MISSAO_1"),
                        new Choice("Guardar o caderno e sorrir com o jeito protetor e teatral da dupla", "d3.4.2")
                                .setFlagToSet("DISTRACAO_VAMP_1"))
                .narrate("d3.4.1", "Você rabisca rapidamente as saídas do salão e observa que Miguel carrega uma lâmina oculta no cinto.", "d3.5")
                .narrate("d3.4.2", "Você fecha o caderno por um instante: nenhum deles se parece com os monstros irracionais descritos nos panfletos da Ordem.", "d3.5")
                .ask("d3.5", "Emily", "Normal", "Por onde deseja começar a explorar a mansão?",
                        new Choice("Seguir o aroma inesperado de temperos vindo da Cozinha", "c1_1"),
                        new Choice("Subir até a Biblioteca silenciosa no segundo andar", "b1_1"),
                        new Choice("Sair pelos fundos em direção ao Campo Fechado", "g1_1"));

        // ==============================================================================================
        // CAMINHO 1: COZINHA PRIMEIRO -> INTERVALO 2 -> BIBLIOTECA OU CAMPO
        // ==============================================================================================
        appendCozinha(sb, "c1_", "int2_c_1");

        sb.scene("Corredor dos Retratos", "Um corredor longo iluminado por arandelas suaves.").background("corredor_mansao")
                .narrate("int2_c_1", "Ao deixar a cozinha, você para diante de uma fileira de retratos antigos e abre novamente o caderno.")
                .ask("int2_c_2", "Emily", "Pensando", "Como você avalia o que acabou de ver?",
                        new Choice("Anotar a rotina pacífica de Raphael e o acesso fácil pela ala de serviço", "int2_c_3")
                                .setFlagToSet("FOCO_MISSAO_2"),
                        new Choice("Ficar pensando na gentileza genuína de Raphael e quase esquecer o relatório", "int2_c_3")
                                .setFlagToSet("DISTRACAO_VAMP_2"))
                .ask("int2_c_3", "Emily", "Normal", "Para qual ala seguir agora?",
                        new Choice("Visitar a Biblioteca no andar superior", "b2_c_1"),
                        new Choice("Ir até o Campo Fechado nos fundos", "g2_c_1"));

        // Sub-ramo: Cozinha -> Biblioteca -> Campo Fechado
        appendBiblioteca(sb, "b2_c_", "int3_cb_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_cb_1", "O sol já começa a baixar atrás das colinas. Você se senta em um banco de pedra entre samambaias.")
                .ask("int3_cb_2", "Emily", "Pensando", "Falta apenas conferir o pátio dos fundos.",
                        new Choice("Registrar no caderno os horários em que os vampiros ficam mais dispersos", "int3_cb_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Admirar a tranquilidade da mansão e pensar no papo agradável com Lilith", "int3_cb_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_cb_3", "Você guarda o caderno no bolso do casaco e segue para o Campo Fechado.", "g3_cb_1");
        appendCampoFechado(sb, "g3_cb_", "dNOITE.1");

        // Sub-ramo: Cozinha -> Campo Fechado -> Biblioteca
        appendCampoFechado(sb, "g2_c_", "int3_cg_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_cg_1", "O sol já começa a baixar atrás das colinas. Você se senta em um banco de pedra para respirar.")
                .ask("int3_cg_2", "Emily", "Pensando", "Falta apenas conhecer a biblioteca.",
                        new Choice("Anotar as brechas no muro externo do Campo Fechado para Santo Pedro", "int3_cg_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Dar risada sozinha lembrando das atrapalhadas de Gabriel e Cassian", "int3_cg_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_cg_3", "Você segue pelas escadas internas rumo à Biblioteca.", "b3_cg_1");
        appendBiblioteca(sb, "b3_cg_", "dNOITE.1");

        // ==============================================================================================
        // CAMINHO 2: BIBLIOTECA PRIMEIRO -> INTERVALO 2 -> COZINHA OU CAMPO
        // ==============================================================================================
        appendBiblioteca(sb, "b1_", "int2_b_1");

        sb.scene("Corredor dos Retratos", "Um corredor longo iluminado por arandelas suaves.").background("corredor_mansao")
                .narrate("int2_b_1", "Saindo da biblioteca silenciosa, você encosta na parede do corredor e abre o Caderno de Anotações.")
                .ask("int2_b_2", "Emily", "Pensando", "O que você registra após conversar com Lilith?",
                        new Choice("Anotar a existência de tomos antigos e possíveis artefatos arcanos no acervo", "int2_b_3")
                                .setFlagToSet("FOCO_MISSAO_2"),
                        new Choice("Sorrir lembrando de como Lilith ficou animada falando de livros", "int2_b_3")
                                .setFlagToSet("DISTRACAO_VAMP_2"))
                .ask("int2_b_3", "Emily", "Normal", "Para qual ala seguir agora?",
                        new Choice("Descer até a Cozinha da mansão", "c2_b_1"),
                        new Choice("Ir até o Campo Fechado nos fundos", "g2_b_1"));

        // Sub-ramo: Biblioteca -> Cozinha -> Campo Fechado
        appendCozinha(sb, "c2_b_", "int3_bc_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_bc_1", "A tarde chega ao fim enquanto você atravessa o jardim envidraçado rumo aos fundos.")
                .ask("int3_bc_2", "Emily", "Pensando", "Falta apenas o Campo Fechado.",
                        new Choice("Anotar os suprimentos e pontos de acesso da cozinha no caderno", "int3_bc_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Pensar que Raphael cozinha melhor que qualquer restaurante da cidade", "int3_bc_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_bc_3", "Você abre a porta lateral e sai para o Campo Fechado.", "g3_bc_1");
        appendCampoFechado(sb, "g3_bc_", "dNOITE.1");

        // Sub-ramo: Biblioteca -> Campo Fechado -> Cozinha
        appendCampoFechado(sb, "g2_b_", "int3_bg_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_bg_1", "O céu ganha tons violeta através dos vidros do jardim interno.")
                .ask("int3_bg_2", "Emily", "Pensando", "Falta apenas conferir a cozinha.",
                        new Choice("Registrar a rotina de vigilância de Gabriel e Cassian no pátio", "int3_bg_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Relaxar um pouco depois das palhaçadas da dupla no pátio", "int3_bg_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_bg_3", "Você segue o corredor térreo em direção à Cozinha.", "c3_bg_1");
        appendCozinha(sb, "c3_bg_", "dNOITE.1");

        // ==============================================================================================
        // CAMINHO 3: CAMPO FECHADO PRIMEIRO -> INTERVALO 2 -> COZINHA OU BIBLIOTECA
        // ==============================================================================================
        appendCampoFechado(sb, "g1_", "int2_g_1");

        sb.scene("Corredor dos Retratos", "Um corredor longo iluminado por arandelas suaves.").background("corredor_mansao")
                .narrate("int2_g_1", "Voltando do pátio externo para o interior da mansão, você aproveita o corredor vazio para abrir o caderno.")
                .ask("int2_g_2", "Emily", "Pensando", "Como você resume o encontro no Campo Fechado?",
                        new Choice("Mapear a altura dos muros externos e os pontos cegos do pátio", "int2_g_3")
                                .setFlagToSet("FOCO_MISSAO_2"),
                        new Choice("Balançar a cabeça rindo do jeito atrapalhado de Gabriel e Cassian", "int2_g_3")
                                .setFlagToSet("DISTRACAO_VAMP_2"))
                .ask("int2_g_3", "Emily", "Normal", "Para qual ala seguir agora?",
                        new Choice("Seguir o aroma vindo da Cozinha", "c2_g_1"),
                        new Choice("Subir para a Biblioteca silenciosa", "b2_g_1"));

        // Sub-ramo: Campo Fechado -> Cozinha -> Biblioteca
        appendCozinha(sb, "c2_g_", "int3_gc_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_gc_1", "O crepúsculo envolve a mansão enquanto você passa pelo jardim interno.")
                .ask("int3_gc_2", "Emily", "Pensando", "Falta apenas conhecer a biblioteca.",
                        new Choice("Registrar o perfil pacífico de Raphael nas anotações da Ordem", "int3_gc_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Sentir-se estranhamente acolhida depois da conversa com Raphael", "int3_gc_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_gc_3", "Você sobe as escadas rumo à Biblioteca.", "b3_gc_1");
        appendBiblioteca(sb, "b3_gc_", "dNOITE.1");

        // Sub-ramo: Campo Fechado -> Biblioteca -> Cozinha
        appendBiblioteca(sb, "b2_g_", "int3_gb_1");
        sb.scene("Jardim de Inverno", "Um pequeno jardim envidraçado onde a luz da tarde começa a cair.").background("jardim_inverno")
                .narrate("int3_gb_1", "O anoitecer finalmente alcança os vidros altos do jardim interno.")
                .ask("int3_gb_2", "Emily", "Pensando", "Falta apenas conhecer a cozinha.",
                        new Choice("Anotar a localização da biblioteca e os livros de magia antiga", "int3_gb_3")
                                .setFlagToSet("FOCO_MISSAO_3"),
                        new Choice("Ficar encantada com a timidez literária de Lilith", "int3_gb_3")
                                .setFlagToSet("DISTRACAO_VAMP_3"))
                .narrate("int3_gb_3", "Você desce até o térreo e segue para a Cozinha.", "c3_gb_1");
        appendCozinha(sb, "c3_gb_", "dNOITE.1");

        // ==============================================================================================
        // ENCERRAMENTO DO CAPÍTULO 4: QUARTO EXCLUSIVO DE EMILY NA MANSÃO
        // ==============================================================================================
        sb.scene("Quarto de Hóspedes da Mansão", "Um quarto espaçoso com cama de dossel, cortinas pesadas e escrivaninha de madeira.").background("quarto_mansao")
                .narrate("dNOITE.1", "Após um dia inteiro circulando pelos corredores da Mansão Nobilis e conhecendo todos os seus moradores, " +
                        "Cassian entrega a você a chave de um quarto exclusivo na ala leste.")
                .say("dNOITE.2", "Cassian", "Normal",
                        "Aqui está. Lençóis trocados, cortinas reforçadas contra o sol e privacidade absoluta. " +
                                "Amanhã à noite Lady Luna esperará você no jantar da casa.")
                .narrate("dNOITE.3", "Você tranca a porta por dentro, verifica discretamente se o Colar de Ocultação continua firme " +
                        "e se senta à escrivaninha sob a luz de um abajur antigo.")
                .ask("dNOITE.4", "Emily", "Pensando", "Antes de encerrar seu primeiro dia na mansão...",
                        new Choice("Revisar todas as pistas no Caderno de Anotações para o relatório de Santa Prata", "dNOITE.5.1")
                                .setFlagToSet("MANSAO_EXPLORADA"),
                        new Choice("Deitar-se na cama macia pensando em como cada vampiro da casa tem seu próprio charme", "dNOITE.5.2")
                                .setFlagToSet("MANSAO_EXPLORADA"))
                .narrate("dNOITE.5.1", "Você organiza página por página: acessos, temperamentos e hábitos de cada morador. " +
                        "Se Santo Pedro quiser invadir este lugar, terá informações precisas — restando apenas decidir até onde você irá.", "dNOITE.6")
                .narrate("dNOITE.5.2", "Você afunda nos travesseiros macios. A missão continua amanhã, mas pela primeira vez em meses " +
                        "você não se sente completamente sozinha.", "dNOITE.6")
                .narrateEnd("dNOITE.6", "Sob o teto silencioso da Mansão Nobilis, Emily encerra seu primeiro dia infiltrada.\n\n[ FIM DO CAPÍTULO 4 ]");

        return sb.build();
    }

    // --------------------------------------------------------------------------------------------------
    // Cenas modulares das alas da Mansão Nobilis (garantem visitas em qualquer ordem escolhida)
    // --------------------------------------------------------------------------------------------------

    private void appendCozinha(StoryBuilder sb, String p, String nextId) {
        sb.scene("Cozinha da Mansão", "Uma cozinha ampla com panelas de cobre, ervas penduradas e aroma de especiarias.").background("cozinha_mansao")
                .narrate(p + "1", "Você empurra a porta vaivém e encontra um vampiro de avental mexendo cuidadosamente uma panela fumegante.")
                .say(p + "2", "Raphael", "Sorrindo",
                        "Oh! Olá! Você deve ser a Emily! Sou Raphael Domus. Não repare na bagunça, " +
                                "estou testando uma redução de frutas vermelhas com especiarias raras.")
                .say(p + "3", "Emily", "Surpresa",
                        "Um vampiro... cozinhando pratos humanos?")
                .say(p + "4", "Raphael", "Normal",
                        "Pois é, todos aqui acham excêntrico! Mas preparar comida me lembra os tempos antigos, " +
                                "e sempre podemos harmonizar os aromas com bolsas de sangue O- ou A-. Quer provar uma colher?")
                .ask(p + "5", "Raphael", "Sorrindo", "Ele estende uma colher de prata com o molho aromático.",
                        new Choice("Provar um pouco e elogiar o equilíbrio exótico dos temperos", p + "6.1")
                                .setTargetNpc("Raphael")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Parece incrível, mas prefiro algo mais... fresco e direto da veia.”", p + "6.2")
                                .setTargetNpc("Raphael")
                                .setAffinityChange(5)
                                .setSuspicionChange(-5),
                        new Choice("“Nossa, isso tem alho?! Afasta isso de mim!”", p + "6.3")
                                .setTargetNpc("Raphael")
                                .setSuspicionChange(20))
                .say(p + "6.1", "Raphael", "Animado",
                        "Sério?! Finalmente alguém nesta casa com paladar refinado! Passe aqui sempre que quiser!", nextId)
                .say(p + "6.2", "Raphael", "Normal",
                        "Haha, justa resposta! Cada um com seus hábitos. Se mudar de ideia, a cozinha está sempre aberta!", nextId)
                .say(p + "6.3", "Raphael", "Confusa",
                        "Alho? Claro que não, eu jamais colocaria alho na minha própria cozinha... Que reação curiosa.", nextId);
    }

    private void appendBiblioteca(StoryBuilder sb, String p, String nextId) {
        sb.scene("Biblioteca Antiga", "Estantes do chão ao teto repletas de volumes encadernados em couro e escadas deslizantes.").background("biblioteca_mansao")
                .narrate(p + "1", "Entre pilhas organizadas de livros antigos, uma vampira baixa de óculos ergue os olhos assustada ao ouvir seus passos.")
                .say(p + "2", "Lilith", "Envergonhado",
                        "A-Ah! Desculpe, não ouvi você chegar... Eu sou Lilith Rex. Você é a nova hóspede, certo?")
                .say(p + "3", "Lilith", "Normal",
                        "Eu quase não saio daqui. Gosto mais de romances antigos e tratados sobre criaturas mágicas do que de festas no salão...")
                .ask(p + "4", "Lilith", "Envergonhado", "Ela segura um livro grosso contra o peito.",
                        new Choice("“Tratados sobre criaturas mágicas? Eu adoraria ver quais autores você coleciona.”", p + "5.1")
                                .setTargetNpc("Lilith")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Também prefiro lugares silenciosos a multidões barulhentas.”", p + "5.2")
                                .setTargetNpc("Lilith")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Achei que vampiros só ligassem para caçar, não para ler livros empoeirados.”", p + "5.3")
                                .setTargetNpc("Lilith")
                                .setAffinityChange(-5)
                                .setSuspicionChange(15))
                .say(p + "5.1", "Lilith", "Sorrindo",
                        "*Os olhos atrás dos óculos brilham de empolgação* Sério?! Pouquíssima gente aqui conversa sobre isso! " +
                                "Depois eu separo minhas edições favoritas para você!", nextId)
                .say(p + "5.2", "Lilith", "Sorrindo",
                        "Que alívio... Achei que seria difícil puxar assunto. Pode usar a mesa do canto sempre que quiser paz.", nextId)
                .say(p + "5.3", "Lilith", "Triste",
                        "Quando se vive séculos, a mente precisa de tanto alimento quanto o corpo... Com licença.", nextId);
    }

    private void appendCampoFechado(StoryBuilder sb, String p, String nextId) {
        sb.scene("Campo Fechado", "Um pátio interno cercado por muros altos de pedra e estátuas cobertas de hera.").background("campo_fechado")
                .narrate(p + "1", "No pátio dos fundos, Gabriel gesticula empolgado enquanto Cassian tenta ajustar a postura de guarda com um florete.")
                .say(p + "2", "Gabriel", "Animado",
                        "Olha só quem apareceu! Emily! Eu estava mostrando para o Cassian minha nova técnica de emboscada silenciosa!")
                .say(p + "3", "Cassian", "Serio",
                        "Gabriel, você gritou 'SURPRESA!' antes mesmo de saltar do muro. Isso anula o conceito de emboscada.")
                .ask(p + "4", "Gabriel", "Animado", "E aí, novata, quem de nós dois tem o estilo mais intimidador?",
                        new Choice("“O entusiasmo do Gabriel realmente pegaria qualquer vítima desprevenida.”", p + "5.1")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“A postura clássica do Cassian impõe muito mais respeito.”", p + "5.2")
                                .setTargetNpc("Cassian")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("“Se vocês discutirem assim numa luta real, os caçadores nem precisam se esforçar.”", p + "5.3")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5)
                                .setTargetNpc("Cassian")
                                .setAffinityChange(5))
                .say(p + "5.1", "Gabriel", "Animado",
                        "HA! Viu só, Cassian?! Alguém entende a minha visão artística!", nextId)
                .say(p + "5.2", "Cassian", "Sorrindo",
                        "Finalmente uma hóspede com discernimento tático e bom gosto.", nextId)
                .say(p + "5.3", "Cassian", "Envergonhado",
                        "Ahem... Estávamos apenas simulando um cenário de distração. Volte antes do anoitecer, Emily.", nextId);
    }
}