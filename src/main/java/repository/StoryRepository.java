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
                .register(3, this::buildEncontro);
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
                        new Choice("Eu deveria tomar um café para aguentar o dia.", "d2.1.1").setEnergyChange(100),
                        new Choice("Um pãozinho com manteiga já está bom.", "d2.1.2").setEnergyChange(50),
                        new Choice("Acho que vou ficar de jejum hoje...", "d2.1.3").setEnergyChange(50))

                //--------------------------------------------------------------------------------------------------//
                // TOMANDO OU NÃO CAFÉ
                .scene("Cozinha", "Uma cozinha simples mas suficiente para suas necessidades").background("cozinha")
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
                        new Choice("Oii! Estou bem e você?", "d4.4").setTargetNpc("Arthur").setAffinityChange(10),
                        new Choice("Ah oi, Arthur...", "d4.5").setTargetNpc("Arthur").setAffinityChange(5),
                        new Choice("Agora não Arthur, estou com pressa.", "d4.6").setTargetNpc("Arthur").setAffinityChange(-5))
                .say("d4.4", "Arthur", "Envergonhado",
                        "Ah, estou bem também! Que bom ver você... Há um tempo que não conversamos...", "d4.7")
                .say("d4.5", "Arthur", "Envergonhado", "Eh, então... Como você vai?", "d4.7")
                .say("d4.6", "Arthur", "Triste",
                        "Ah... Claro, desculpa atrapalhar, Emily. Boa sorte aí no que você for fazer!", "d5.1")
                .narrate("d4.7", "Arthur ajeita a alça da mochila, visivelmente animado por finalmente puxar assunto com você.")
                .ask("d4.8", "Emily", "Normal", "Como deseja continuar a conversa?",
                        new Choice("Comentar que viu a luz do quarto dele acesa ontem à noite", "d4.9.1")
                                .setRequiredFlag("JANELAARTHUR")
                                .setFlagToSet("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(10),
                        new Choice("Perguntar o que ele tem estudado ultimamente", "d4.9.2")
                                .setFlagToSet("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("Agradecer pela atenção, mas dizer que precisa ir para uma entrevista", "d4.9.3"))
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
                        new Choice("Ir a pé para poupar dinheiro (-20 Energia)", "d5.4.1").setEnergyChange(-20),
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
                        new Choice("Dizer a senha: 'Petrifique o obscuro e busque a luz.'", "d6.6"),
                        new Choice("Enrolar e ficar olhando as prateleiras sem dizer a senha", "d6.7"),
                        new Choice("Comprar um Energético Lunar (-$4,00 | +35 Energia)", "d6.7")
                                .setMoneyChange(-4.0)
                                .setEnergyChange(35)
                                .setItemToGive(itemRepo.getItem("Energetico Lunar")),
                        new Choice("Comprar uma Pastilha de Alho Extra Forte (-$3,00)", "d6.7")
                                .setMoneyChange(-3.0)
                                .setItemToGive(itemRepo.getItem("Pastilha de Alho")),
                        new Choice("Comprar um Amuleto de Prata Falsa (-$6,00)", "d6.7")
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
                        new Choice("Ser sincera: 'Meu auxílio acabou e preciso desesperadamente de um emprego.'", "d7.5.1")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(5),
                        new Choice("Destacar seu domínio teórico em Biologia e Criaturas Mágicas", "d7.5.2")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(10),
                        new Choice("Flertar: 'Digamos que o anúncio não mencionava um coordenador tão... impressionante.'", "d7.5.3")
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
                        new Choice("Isso é uma missão suicida! Eles vão sentir meu cheiro humano na hora!", "d7.10.1"),
                        new Choice("Quanto exatamente essa 'missão suicida' paga?", "d7.10.2")
                                .setTargetNpc("Santo Pedro")
                                .setAffinityChange(5))
                .say("d7.10.1", "Santo Pedro", "Normal",
                        "Não se você usar o que preparamos. Nossos alquimistas criaram artefatos justamente para anular seus sinais vitais.", "d7.11")
                .say("d7.10.2", "Santo Pedro", "Normal",
                        "O suficiente para pagar um ano inteiro do seu aluguel logo no primeiro pagamento. E você não irá desarmada.", "d7.11")
                .narrate("d7.11", "Santo Pedro desliza sobre a mesa um colar escuro, um estojo com lentes de contato vermelhas e um cartão amassado.")
                .ask("d7.12", "Santo Pedro", "Serio",
                        "Amanhã ao anoitecer, vá até o bar indicado neste cartão e peça para ver a matriarca. Alguma dúvida?",
                        new Choice("Pegar os itens e aceitar a missão", "d7.13")
                                .setItemToGive(itemRepo.getItem("Colar de Ocultacao"))
                                .setItemToGive(itemRepo.getItem("Lentes Carmesim"))
                                .setItemToGive(itemRepo.getItem("Cartao do Bar"))
                                .setFlagToSet("MISSAO_ACEITA"))
                .say("d7.13", "Santo Pedro", "Normal",
                        "Ótimo. Vá para casa, descanse e estude seu papel. Não nos decepcione.", "d8.1")

                //--------------------------------------------------------------------------------------------------//
                // RETORNO PARA CASA
                .scene("Retorno para Casa", "O fim de uma tarde inacreditável.").background("cozinha")
                .narrate("d8.1", "Você sai da loja de conveniência sentindo o peso do colar mágico no bolso do casaco.")
                .ask("d8.2", "Emily", "Pensando", "Como voltar para casa?",
                        new Choice("Voltar a pé (-20 Energia)", "d8.3.1").setEnergyChange(-20),
                        new Choice("Pegar o ônibus (-$4,00)", "d8.3.2").setMoneyChange(-4.0))
                .narrate("d8.3.1", "Você caminha de volta até seu bairro, tentando processar o fato de que agora é espiã de uma ordem de caçadores.", "d8.4")
                .narrate("d8.3.2", "Você pega o ônibus de volta e passa o trajeto inteiro encarando o Cartão do Bar Eclipse.", "d8.4")
                .ask("d8.4", "Emily", "Normal", "Ao chegar em casa, seu estômago ronca.",
                        new Choice("Preparar um almoço tardio e descansar (+30 Energia)", "d8.5").setEnergyChange(30))
                .narrate("d8.5", "Depois de almoçar, você passa o restante do dia repassando tudo o que sabe sobre vampiros " +
                        "e planejando como agir amanhã à noite.")
                .narrateEnd("d8.6", "A noite cai sobre a cidade enquanto Emily se prepara para sua primeira incursão no mundo das sombras.\n\n[ FIM DO CAPÍTULO 2 ]")

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
                        new Choice("Aceitar o lanche e agradecer (+25 Energia)", "d1.4")
                                .setEnergyChange(25)
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5))
                .say("d1.4", "Arthur", "Normal",
                        "E então, como foi aquela entrevista misteriosa ontem? Conseguiu a vaga?")
                .ask("d1.5", "Emily", "Pensando", "Como comentar sobre o emprego?",
                        new Choice("Dizer que conseguiu e que o chefe é enorme, forte e bem atraente...", "d1.6.1")
                                .setRequiredFlag("FLERTOU_PEDRO")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(-3),
                        new Choice("Contar que conseguiu um trabalho noturno bem peculiar de investigação", "d1.6.2")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("Brincar que seu novo chefe parece um caçador saído de um filme antigo", "d1.6.3")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5))
                .say("d1.6.1", "Arthur", "Envergonhado",
                        "*Engasga levemente com o suco e olha para os próprios braços magros* S-Sério? Forte tipo... lenhador? " +
                                "Bom, que bom que você gostou do ambiente de trabalho, né...", "d1.7")
                .say("d1.6.2", "Arthur", "Surpresa",
                        "Trabalho noturno? Puxa, cuidado andando por aí à noite nesse frio, Emily!", "d1.7")
                .say("d1.6.3", "Arthur", "Normal",
                        "Hahaha, espero que pelo menos ele pague em dia! Mas falando sério, isso tudo parece meio maluco.", "d1.7")
                .say("d1.7", "Emily", "Sorrindo",
                        "Pois é, parece loucura mesmo... Mas entre ficar sem pagar o aluguel e encarar algo estranho, " +
                                "eu prefiro a segunda opção. Além disso, confesso que fiquei curiosa.")
                .ask("d1.8", "Arthur", "Normal", "Arthur acompanha você até a porta.",
                        new Choice("Agradecer pelo apoio e dizer que mantém contato", "d1.9.1")
                                .setRequiredFlag("ARTHUR_GENTIL")
                                .setTargetNpc("Arthur")
                                .setAffinityChange(5),
                        new Choice("Despedir-se e voltar para o seu apartamento", "d1.9.2"))
                .say("d1.9.1", "Arthur", "Sorrindo",
                        "Se se sentir muito sozinha nessa missão, me manda mensagem quando quiser, tá? " +
                                "E como você foi super gentil comigo ontem... se precisar de qualquer coisa mesmo, pode contar comigo!", "d2.1")
                .say("d1.9.2", "Arthur", "Normal",
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
                        new Choice("Puxar o capuz sobre o rosto e ir até o Bar Eclipse (-15 Energia)", "d3.1")
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
                        new Choice("Contar uma história dramática de vampira errante fugindo de caçadores", "d4.9.1")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("Elogiar a 'entrada sombria' da dupla para ganhar a simpatia deles", "d4.9.2")
                                .setTargetNpc("Cassian")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("Dar uma explicação técnica demais usando termos acadêmicos de biologia", "d4.9.3")
                                .setTargetNpc("Gabriel")
                                .setSuspicionChange(30))
                .say("d4.9.1", "Gabriel", "Animado",
                        "Fugindo de caçadores?! Eu sabia! Adoro histórias de fuga noturna!", "d4.10")
                .say("d4.9.2", "Cassian", "Sorrindo",
                        "Viu, Gabriel? Alguém com sensibilidade estética! Apesar do pequeno incidente com os caixotes...", "d4.10")
                .say("d4.9.3", "Gabriel", "Confusa",
                        "Hematofagia? Biologia? Que vampiro fala igual a um professor universitário?", "d4.10")
                .say("d4.10", "Cassian", "Serio",
                        "Muito bem. Para entrar no programa de vampiros refugiados da nossa casa, precisamos confirmar seus instintos. " +
                                "Qual é o seu tipo sanguíneo favorito e por que busca abrigo?")
                .ask("d4.11", "Emily", "Pensando", "Escolha com cuidado:",
                        new Choice("Dizer que prefere sangue O+ fresco e só quer um refúgio seguro na mansão", "d4.12.1")
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("Afirmar que respeita as leis da Matriarca e sabe manter total discrição", "d4.12.2")
                                .setTargetNpc("Cassian")
                                .setAffinityChange(10)
                                .setSuspicionChange(-5),
                        new Choice("Gaguejar e responder que 'qualquer suco vermelho serve'", "d4.12.3")
                                .setTargetNpc("Gabriel")
                                .setSuspicionChange(35))
                .say("d4.12.1", "Gabriel", "Animado",
                        "O+ é ótimo! Clássico, energético! Eu tenho toda uma teoria sobre como capturar presas tipo O+...", "d4.13")
                .say("d4.12.2", "Cassian", "Normal",
                        "Excelente postura. Lady Luna aprecia hóspedes que conhecem a etiqueta da casa.", "d4.13")
                .say("d4.12.3", "Gabriel", "Serio",
                        "“Suco vermelho”?! Que tipo de piada sem graça é essa, Cassian?", "d4.13")
                .narrate("d4.13", "Gabriel dá um passo à frente, aproximando-se para farejar o ar ao seu redor e testar seus reflexos.")
                .ask("d4.14", "Gabriel", "Serio", "Deixe-me olhar bem para você...",
                        new Choice("Manter a calma e confiar no Colar de Ocultação Sombria", "d4.15")
                                .setRequiredItem("Colar de Ocultação Sombria", false)
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5)
                                .setSuspicionChange(-10),
                        new Choice("Sustentar o olhar carmesim sem piscar e exibir suas presas", "d4.15")
                                .setMaxSuspicionAllowed(55)
                                .setTargetNpc("Gabriel")
                                .setAffinityChange(5)
                                .setSuspicionChange(-5),
                        new Choice("Recuar em pânico, suando frio e cobrindo o pescoço", "FINAL_MORTE_BAR_VAMP"),
                        new Choice("Tentar desconversar gaguejando enquanto seu coração dispara", "FINAL_MORTE_BAR_VAMP"))
                .say("d4.15", "Gabriel", "Animado",
                        "*Abre um sorrisão e dá um tapinha no ombro de Cassian* Tudo certo por aqui! " +
                                "Seja bem-vinda ao programa de refugiados, novata!", "d4.16")
                .say("d4.16", "Cassian", "Normal",
                        "Aprovada. Aqui está sua carta de convite lacrada. Siga pela estrada antiga das colinas norte amanhã à noite; " +
                                "apresente este selo nos portões da Mansão Nobilis e não nos faça passar vergonha diante da Matriarca.")
                .ask("d4.17", "Emily", "Sorrindo", "Cassian estende um envelope preto selado com cera vermelha.",
                        new Choice("Pegar a Carta de Convite, agradecer e se despedir", "d5.1")
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
                        "e coloca o envelope lacrado sobre a mesa.")
                .narrate("d5.4", "Você respira fundo: conseguiu enganar dois vampiros e garantiu acesso direto à mansão. " +
                        "Amanhã, a verdadeira infiltração começa.")
                .narrateEnd("d5.5", "Com a Carta de Convite em mãos, Emily observa a lua pela janela antes de descansar.\n\n[ FIM DO CAPÍTULO 3 ]")

                .build();
    }
}