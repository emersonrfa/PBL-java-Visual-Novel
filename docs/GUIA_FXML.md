# Guia de FXML para o Visual Novel

Para quem já sabe Java, mas ainda não conhece JavaFX. Aqui você aprende o mínimo necessário para
**montar e mudar as telas do jogo sem mexer na lógica**.

- [1. A ideia em 2 minutos](#1-a-ideia-em-2-minutos)
- [2. Como rodar e onde ficam as coisas](#2-como-rodar-e-onde-ficam-as-coisas)
- [3. Anatomia de um arquivo FXML](#3-anatomia-de-um-arquivo-fxml)
- [4. Layouts: como posicionar as coisas](#4-layouts-como-posicionar-as-coisas)
- [5. Controles mais usados](#5-controles-mais-usados)
- [6. Ligando o FXML ao Java (controller)](#6-ligando-o-fxml-ao-java-controller)
- [7. Estilo com CSS](#7-estilo-com-css)
- [8. Imagens](#8-imagens)
- [9. Navegação entre telas](#9-navegação-entre-telas)
- [10. Passo a passo: criando uma tela nova](#10-passo-a-passo-criando-uma-tela-nova)
- [11. A tela do jogo (game.fxml)](#11-a-tela-do-jogo-gamefxml)
- [12. Scene Builder (opcional)](#12-scene-builder-opcional)
- [13. Erros comuns](#13-erros-comuns)
- [14. Cola rápida](#14-cola-rápida)

---

## 1. A ideia em 2 minutos

Uma janela JavaFX é montada como uma **árvore de elementos** (chamados *nós*, `Node`):

```
Stage (a janela)
└── Scene (o conteúdo da janela)
    └── StackPane            ← um "layout": decide onde ficam os filhos
        ├── ImageView        ← uma imagem
        └── VBox             ← outro layout (coloca os filhos um embaixo do outro)
            ├── Label        ← um texto
            └── Button       ← um botão
```

Você pode montar essa árvore **com código Java** (`new VBox(...)`, `box.getChildren().add(...)`)
ou **descrevendo-a em um arquivo XML**, o **FXML**. O FXML é para a tela o que o HTML é para uma
página web: ele diz *o que existe e onde*. O CSS diz *como parece*. O Java diz *o que acontece*.

| Parte | Arquivo | Responsabilidade |
|-------|---------|------------------|
| Estrutura | `src/main/resources/fxml/*.fxml` | Quais elementos existem e como se organizam |
| Aparência | `src/main/resources/css/style.css` | Cores, fontes, espaçamentos |
| Comportamento | `src/main/java/ui/screens/*Controller.java` | O que cada botão faz |

Vantagem: você pode redesenhar uma tela inteira editando só o `.fxml` e o `.css`, sem recompilar
lógica nem arriscar quebrar o jogo.

---

## 2. Como rodar e onde ficam as coisas

Rodar a interface gráfica: `mvn javafx:run` (ou execute a classe `main.Launcher` na IDE).
O jogo em console continua em `main.Main`.

```
src/main/
├── java/
│   ├── main/App.java               ← abre a janela e registra as telas
│   └── ui/
│       ├── navigation/             ← troca de telas (ScreenNavigator, ScreenId)
│       ├── screens/                ← controllers das telas de menu
│       └── game/                   ← controller e lógica da tela do jogo
└── resources/
    ├── fxml/                       ← AS TELAS (uma por arquivo)
    │   ├── main_menu.fxml          Menu Inicial
    │   ├── play.fxml               Jogar (Novo Jogo / Carregar)
    │   ├── new_game.fxml           Novo Jogo
    │   ├── load_save.fxml          Carregar Jogo
    │   ├── settings.fxml           Configurações
    │   ├── achievements.fxml       Galeria de Conquistas
    │   ├── endings.fxml            Galeria de Finais
    │   ├── credits.fxml            Créditos
    │   └── game.fxml               Tela do jogo
    ├── css/style.css               ← estilo de todas as telas
    └── images/                     ← fundos, sprites e itens (ver images/README.md)
```

> **Dica:** depois de editar um `.fxml` ou o `.css`, basta rodar de novo (`mvn javafx:run`).
> Se a IDE copiar recursos para uma pasta de saída (`out/` ou `target/`), pode ser preciso
> reconstruir o projeto para ela enxergar a mudança.

---

## 3. Anatomia de um arquivo FXML

Veja o `play.fxml`, comentado:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- 1. IMPORTS: toda classe usada como tag precisa ser importada, igual ao Java -->
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>

<!-- 2. ELEMENTO RAIZ: o layout que contém a tela inteira.
        xmlns:fx          habilita os atributos que começam com "fx:"
        fx:controller     classe Java que controla esta tela
        styleClass        classe(s) de CSS aplicada(s) -->
<StackPane xmlns:fx="http://javafx.com/fxml" fx:controller="ui.screens.PlayController"
           styleClass="screen">

    <!-- 3. FILHOS: cada tag é um objeto Java; cada atributo é uma propriedade dele.
            <VBox spacing="14"/>  equivale a  new VBox(); box.setSpacing(14); -->
    <VBox alignment="CENTER" spacing="14" maxWidth="360" maxHeight="-Infinity">
        <Label text="Jogar" styleClass="title"/>

        <!-- onAction="#onNewGame" chama o método onNewGame() do controller ao clicar -->
        <Button text="Novo Jogo" onAction="#onNewGame" styleClass="menu-button" maxWidth="Infinity"/>
        <Button text="Voltar" onAction="#onBack" styleClass="menu-button" maxWidth="Infinity"/>
    </VBox>
</StackPane>
```

Regras que valem para qualquer FXML:

- **Tag = classe, atributo = propriedade.** `<Button text="OK"/>` é `new Button()` + `setText("OK")`.
  Quase todo `setAlgo(...)` do Java vira o atributo `algo="..."`.
- **Filhos** de um layout ficam dentro da tag dele. Isso corresponde ao `getChildren().add(...)`.
- **`fx:id="nome"`** dá um nome ao elemento para o Java conseguir acessá-lo (ver seção 6).
- **`onAction="#metodo"`** liga um evento a um método do controller (o `#` é obrigatório).
- **Comentários** são `<!-- ... -->`. Não podem ficar antes de `<?xml ... ?>`.
- **Texto com quebra de linha** num atributo: use `&#10;` (ex.: `text="Linha 1&#10;Linha 2"`).
- **Valores especiais de tamanho:** `maxWidth="Infinity"` = "cresça até ocupar todo o espaço";
  `maxHeight="-Infinity"` = "use só o tamanho necessário para o conteúdo".

---

## 4. Layouts: como posicionar as coisas

O layout (o "contêiner") decide a posição dos filhos. Você não usa coordenadas; escolhe o layout
certo e ele organiza.

| Layout | O que faz | Quando usar |
|--------|-----------|-------------|
| `VBox` | Filhos **um embaixo do outro** | Listas de botões, formulários |
| `HBox` | Filhos **lado a lado** | Barras de botões, linhas de uma lista |
| `StackPane` | Filhos **empilhados** (o último fica por cima) | Fundo + sprite + caixa de texto; centralizar algo |
| `BorderPane` | 5 regiões: `top`, `bottom`, `left`, `right`, `center` | Título em cima, lista no meio, botão voltar embaixo |
| `GridPane` | Grade de linhas e colunas | Formulários alinhados, grades de itens |
| `ScrollPane` | Barra de rolagem para um filho grande | Listas longas (conquistas, inventário) |

Atributos de layout que você mais vai usar:

```xml
<VBox spacing="12"                  <!-- espaço ENTRE os filhos -->
      alignment="CENTER"            <!-- alinhamento dos filhos: CENTER, TOP_LEFT, CENTER_LEFT, BOTTOM_CENTER... -->
      maxWidth="400"                <!-- largura máxima -->
      prefHeight="300"              <!-- altura preferida -->
      style="-fx-padding: 20;">     <!-- margem INTERNA (também pode ficar no CSS) -->
```

Posicionando um filho dentro de um `StackPane` (como em `game.fxml`):

```xml
<VBox StackPane.alignment="BOTTOM_CENTER"> ... </VBox>   <!-- cola embaixo, centralizado -->

<VBox StackPane.alignment="TOP_RIGHT">                    <!-- canto superior direito, com margem de fora -->
    <StackPane.margin>
        <Insets top="70" right="20"/>                     <!-- precisa de <?import javafx.geometry.Insets?> -->
    </StackPane.margin>
</VBox>
```

Empurrar elementos para as pontas de um `HBox` (ex.: título à esquerda, botões à direita):

```xml
<HBox>
    <Label text="Energia"/>
    <Region HBox.hgrow="ALWAYS"/>   <!-- "mola": ocupa todo o espaço livre -->
    <Button text="Menu"/>
</HBox>
```

Em um `BorderPane`, cada região é uma tag filha:

```xml
<BorderPane>
    <top>    <Label text="Título"/> </top>
    <center> <ScrollPane> ... </ScrollPane> </center>
    <bottom> <Button text="Voltar"/> </bottom>
</BorderPane>
```

---

## 5. Controles mais usados

| Controle | Para quê | Atributos/métodos úteis |
|----------|----------|--------------------------|
| `Label` | Texto | `text`, `wrapText="true"` (quebra linha), `textAlignment` |
| `Button` | Botão | `text`, `onAction`, `disable` |
| `CheckBox` | Marcar/desmarcar | `text`, `selected`; no Java `isSelected()` |
| `ChoiceBox` | Lista suspensa | itens preenchidos no Java: `getItems().addAll(...)`; `getValue()` |
| `TextField` | Campo de texto | `promptText`; no Java `getText()` |
| `Slider` | Barra deslizante | `min`, `max`, `value`; no Java `getValue()` |
| `ImageView` | Mostra uma imagem | `fitWidth`, `fitHeight`, `preserveRatio` |
| `ScrollPane` | Rolagem | `fitToWidth="true"` |
| `Region` | Espaço vazio/retângulo estilizável | `minWidth`, `prefHeight` |

Para usar um controle novo, **importe-o** no topo (`<?import javafx.scene.control.TextField?>`).
Qualquer propriedade de uma classe JavaFX pode ser um atributo; a documentação oficial
(`openjfx.io`, "JavaFX API") lista todas.

---

## 6. Ligando o FXML ao Java (controller)

O FXML só desenha. Para o botão "fazer algo" e para o Java alterar um texto, ligamos os dois por
**nome**. Há dois tipos de ligação.

### 6.1 Eventos: `onAction="#nome"` → método

No FXML:
```xml
<Button text="Novo Jogo" onAction="#onNewGame"/>
```
No controller (`PlayController.java`), um método com **exatamente esse nome**:
```java
@FXML
private void onNewGame() {
    navigator.goTo(ScreenId.NEW_GAME);
}
```

### 6.2 Elementos: `fx:id="nome"` → campo

No FXML:
```xml
<Label fx:id="statusLabel" text=""/>
```
No controller, um campo com **o mesmo nome e o tipo da tag**:
```java
@FXML private Label statusLabel;
...
statusLabel.setText("Configurações salvas.");
```

### 6.3 O método `initialize()`

Se o controller tiver um método `@FXML private void initialize()`, o JavaFX o chama sozinho logo
depois de criar a tela e preencher os campos. É o lugar certo para preencher listas, ajustar
valores iniciais etc. (`NewGameController` usa para encher as opções de slot e tipo sanguíneo.)

### Regras de ouro

1. **Nomes idênticos** (FXML ↔ Java), respeitando maiúsculas e minúsculas.
2. Campos e métodos ligados levam **`@FXML`** (podem ser `private`).
3. Um `fx:id` **sem** campo correspondente é ignorado; um campo `@FXML` **sem** `fx:id` no FXML
   fica `null` e dá `NullPointerException` quando você o usa.
4. O `fx:controller` do elemento raiz precisa ser o **nome completo** da classe
   (`ui.screens.PlayController`).

Se algo estiver fora de sincronia, o erro costuma aparecer ao abrir a tela (ver seção 13).

---

## 7. Estilo com CSS

Há três formas de estilizar, da mais recomendada para a menos:

**1. Classes de estilo (recomendado).** No FXML: `styleClass="menu-button"`. No `style.css`:
```css
.menu-button { -fx-font-size: 18px; -fx-padding: 10 24 10 24; }
```
Todos os elementos com essa classe mudam de uma vez. Dá para ter várias: `styleClass="menu-button destaque"`.

**2. Estilo direto no elemento**, para ajustes pontuais:
```xml
<Label text="Olá" style="-fx-text-fill: red; -fx-font-size: 30px;"/>
```

**3. Pelo Java:** `label.getStyleClass().add("toast")` ou `label.setStyle("...")`.

O CSS do JavaFX é parecido com o da web, mas as propriedades começam com **`-fx-`**:

| Quero... | Propriedade |
|----------|-------------|
| cor do texto | `-fx-text-fill: white;` |
| cor de fundo | `-fx-background-color: #1d1d33;` (aceita `rgba(0,0,0,0.5)` para transparência) |
| tamanho/negrito da fonte | `-fx-font-size: 20px; -fx-font-weight: bold;` |
| margem interna | `-fx-padding: 10 20 10 20;` (cima direita baixo esquerda) |
| borda | `-fx-border-color: white; -fx-border-width: 2;` |
| cantos arredondados | `-fx-background-radius: 8; -fx-border-radius: 8;` |
| centralizar texto | `-fx-text-alignment: center;` |

No topo do `style.css` há **variáveis de cor** (`-cor-fundo`, `-cor-painel`, `-cor-destaque`...):
mude lá para trocar o tema inteiro.

Para estilizar **de um jeito diferente só um botão de uma tela**, crie uma classe nova no CSS
(ex.: `.botao-perigo`) e use-a no FXML (`styleClass="menu-button botao-perigo"`).

Para usar **outra fonte**, coloque o `.ttf` em `resources/fonts/` e, no CSS:
`-fx-font-family: "Nome da Fonte";`. Antes, carregue-a uma vez no `App` com
`Font.loadFont(App.class.getResourceAsStream("/fonts/arquivo.ttf"), 16)`.

---

## 8. Imagens

**Imagem fixa num FXML** (logo do jogo, ícone, moldura):
```xml
<ImageView fitWidth="300" preserveRatio="true">
    <image>
        <Image url="@/images/ui/logo.png"/>   <!-- precisa de <?import javafx.scene.image.Image?> -->
    </image>
</ImageView>
```
O `@` significa "caminho dentro de `resources`".

**Imagens do jogo** (fundo de cada cena, sprite de cada personagem, imagem de cada item) **não**
vão no FXML: o código as escolhe sozinho pelo nome do arquivo (veja
`src/main/resources/images/README.md`). No `game.fxml` o `ImageView` `background` e o `sprite`
só reservam o lugar onde elas aparecem.

---

## 9. Navegação entre telas

Toda troca de tela passa pelo `navigator` (que o controller já recebe). Os destinos são os valores
de `ScreenId`.

| Chamada | Efeito |
|---------|--------|
| `navigator.goTo(ScreenId.SETTINGS)` | Abre a tela e **guarda a atual** para poder voltar |
| `navigator.back()` | Volta à tela anterior (botão "Voltar") |
| `navigator.replaceWith(ScreenId.GAME, argumento)` | Abre a tela **sem** guardar a atual no histórico (ex.: depois de iniciar o jogo) |
| `navigator.resetTo(ScreenId.MAIN_MENU)` | Abre a tela e **apaga o histórico** (ex.: sair do jogo para o menu) |

Um botão "Voltar" no FXML é só: `<Button text="Voltar" onAction="#onBack"/>` e, no controller,
`@FXML private void onBack() { navigator.back(); }`.

Para **passar informação** à próxima tela (ex.: qual partida iniciar), use o segundo parâmetro, como
o Novo Jogo faz: `navigator.replaceWith(ScreenId.GAME, new GameRequest(player, state))`.

---

## 10. Passo a passo: criando uma tela nova

Exemplo: uma tela de **Ajuda**, aberta por um botão no Menu Inicial.

**1) Crie `src/main/resources/fxml/help.fxml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>

<StackPane xmlns:fx="http://javafx.com/fxml" fx:controller="ui.screens.HelpController"
           styleClass="screen">
    <VBox alignment="CENTER" spacing="14" maxWidth="640" maxHeight="-Infinity">
        <Label text="Ajuda" styleClass="title"/>
        <Label text="Clique na caixa de texto, ESPAÇO ou ENTER para continuar." styleClass="text" wrapText="true"/>
        <Button text="Voltar" onAction="#onBack" styleClass="menu-button"/>
    </VBox>
</StackPane>
```

**2) Crie `src/main/java/ui/screens/HelpController.java`** (copie o `CreditsController` e troque o nome):
```java
package ui.screens;

import javafx.fxml.FXML;
import main.AppContext;
import ui.navigation.ScreenNavigator;

public class HelpController extends ScreenController {

    private HelpController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static HelpController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/help.fxml", new HelpController(navigator, context));
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
```

**3) Acrescente `HELP` em `ui/navigation/ScreenId.java`.**

**4) Registre a tela em `main/App.java`**, junto das outras:
```java
.register(ScreenId.HELP, () -> HelpController.create(navigator, context))
```

**5) Abra-a a partir do menu.** Em `main_menu.fxml`:
```xml
<Button text="Ajuda" onAction="#onHelp" styleClass="menu-button" maxWidth="Infinity"/>
```
e em `MainMenuController.java`:
```java
@FXML
private void onHelp() {
    navigator.goTo(ScreenId.HELP);
}
```

Pronto: rode `mvn javafx:run`.

---

## 11. A tela do jogo (game.fxml)

É a **única** tela em que o jogo acontece: troca de fundo, sprite, texto e escolhas conforme a
história avança. Fica em `fxml/game.fxml`, controlada por `ui/game/GameController.java`.
Os elementos são **empilhados** num `StackPane` (o último do arquivo aparece por cima):

1. `background`: fundo da cena
2. `sprite`: personagem que fala
3. HUD (energia, dinheiro, botões)
4. caixa de diálogo
5. avisos (toasts)
6. título do capítulo
7. janelas: inventário, salvar, fim de jogo, confirmar saída

### O que o código procura no FXML (não remova nem renomeie)

| `fx:id` / handler | Tipo | Para quê |
|---|---|---|
| `root` | `StackPane` | Raiz; recebe o teclado (ESPAÇO/ENTER) |
| `background` | `ImageView` | Imagem de fundo da cena |
| `sprite` | `ImageView` | Sprite do personagem |
| `energyLabel`, `moneyLabel` | `Label` | HUD |
| `speakerLabel` | `Label` | Nome de quem fala (some na narração) |
| `textLabel` | `Label` | Texto do diálogo (com efeito de digitação) |
| `choicesBox` | `VBox` | Onde os botões de escolha são criados |
| `continueHint` | `Label` | "▼ continuar" nos diálogos lineares |
| `toastBox` | `VBox` | Avisos temporários |
| `chapterBanner` | `Label` | Título do capítulo |
| `inventoryOverlay` + `inventoryList` | `StackPane` + `VBox` | Janela do inventário (somente leitura) |
| `saveOverlay` + `saveSlotsBox` | `StackPane` + `VBox` | Janela de salvar |
| `endOverlay` + `endTitle` + `endText` | `StackPane` + `Label`s | Final / fim do capítulo / avisos |
| `confirmOverlay` | `StackPane` | "Voltar ao menu?" |
| `#onInventory`, `#onSave`, `#onMenu`, `#onDialogueClicked`, `#onCloseInventory`, `#onCloseSave`, `#onStay`, `#onLeave` | métodos | Botões e cliques |

### O que você PODE mudar à vontade

- **Posição e tamanho** de qualquer elemento (`StackPane.alignment`, margens, `prefHeight`...).
- **Aparência**: tudo no `style.css` (classes `.dialogue-box`, `.speaker`, `.dialogue-text`,
  `.hud`, `.choice-button`, `.toast`, `.panel`, `.modal-layer`...).
- **Textos fixos** ("Inventário", "Salvar", "Menu"...).
- **Trocar o tipo do contêiner** (ex.: a caixa de diálogo virar um `HBox` com um retrato ao lado),
  mantendo os `fx:id` dos elementos que o código usa.
- **Acrescentar elementos decorativos** (molduras, imagens, linhas): qualquer coisa sem `fx:id`
  é só enfeite.

### Quando precisar de algo que o código ainda não faz

Exemplo: um segundo `Label` mostrando o nome do capítulo o tempo todo. Adicione-o ao FXML com
um `fx:id` novo, declare `@FXML private Label capituloLabel;` no `GameController` e preencha-o no
método `showChapterTitle(...)`. A regra: **o que mostrar** vem do `GameScreenPresenter`
(lógica); **como mostrar** é o `GameController` + FXML + CSS.

---

## 12. Scene Builder (opcional)

O **Scene Builder** (gratuito, da Gluon) é um editor visual de FXML: você arrasta controles e vê o
resultado. Baixe em `gluonhq.com/products/scene-builder`, abra o `.fxml` e edite.

Cuidados:
- Ele **reescreve** o arquivo ao salvar; os comentários explicativos podem sumir. Faça commit
  antes de abrir.
- Em *Code* (painel direito) defina `fx:id` e os nomes de `On Action` (digite `onNewGame`, ele
  coloca o `#`).
- Telas com listas montadas por código (Carregar, Conquistas, Finais) mostram o contêiner vazio no
  Scene Builder; as linhas só existem em execução. Isso é normal.
- Para ver o CSS no Scene Builder: *Preview → Scene Style Sheets → Add* e escolha `style.css`.

Editar o XML à mão (como neste guia) e usar o Scene Builder funcionam juntos.

---

## 13. Erros comuns

| Mensagem / sintoma | Causa provável | Como resolver |
|--------------------|----------------|---------------|
| `Arquivo FXML não encontrado: /fxml/x.fxml` | Nome/caminho errado ou arquivo fora de `src/main/resources/fxml` | Confira o nome em `load("/fxml/x.fxml", ...)` |
| `javax.xml.stream.XMLStreamException` / `LoadException ... line N` | XML mal formado (tag não fechada, aspas faltando) | Vá à linha indicada; toda tag precisa fechar (`<Label/>` ou `<Label></Label>`) |
| `ClassNotFoundException: javafx.scene...` / `... is not a valid type` | Falta `<?import ...?>` da classe | Adicione o import no topo |
| `No property ... found` / `Unable to coerce` | Atributo que a classe não tem, ou valor inválido (ex.: `spacing="abc"`) | Confira o nome do atributo e o tipo do valor |
| `... has no event handler for onXxx` / `Error resolving onAction='#x'` | Handler `#x` sem método `@FXML` de mesmo nome | Crie o método no controller com `@FXML` |
| `NullPointerException` ao usar um campo | Campo `@FXML` sem `fx:id` igual no FXML (ou nome diferente) | Igualar nomes; lembrar de maiúsculas |
| `NullPointerException` em `initialize()` | Usou algo que só existe depois (ex.: `view()` ainda é nulo ali) | No `initialize()` use só os campos `@FXML` |
| Mudei o FXML e nada mudou | A IDE usa recursos antigos | Reconstrua o projeto / `mvn clean javafx:run` |
| CSS não aplica | Classe digitada errada ou propriedade sem `-fx-` | Confira `styleClass` e o nome no `.css` |
| Imagem não aparece | Caminho/nome errado ou formato não suportado | Use PNG/JPG; confira `images/README.md` (nomes em minúsculas, sem acento) |
| `Error: JavaFX runtime components are missing` | Executou `App` direto em vez do `Launcher` | Rode `mvn javafx:run` ou a classe `main.Launcher` |

Dica geral: leia a mensagem **de baixo para cima** até encontrar a primeira linha com
`Caused by:`; ela costuma dizer o arquivo e a linha do problema.

---

## 14. Cola rápida

```xml
<!-- estrutura mínima de uma tela -->
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.layout.StackPane?>
<StackPane xmlns:fx="http://javafx.com/fxml" fx:controller="ui.screens.MeuController" styleClass="screen">
    <Button fx:id="meuBotao" text="Clique" onAction="#onClique" styleClass="menu-button"/>
</StackPane>
```

```java
// controller mínimo
public class MeuController extends ScreenController {
    @FXML private Button meuBotao;                       // = fx:id="meuBotao"

    private MeuController(ScreenNavigator n, AppContext c) { super(n, c); }
    public static MeuController create(ScreenNavigator n, AppContext c) {
        return load("/fxml/meu.fxml", new MeuController(n, c));
    }

    @FXML private void initialize() { /* roda ao abrir */ }
    @FXML private void onClique()   { navigator.back(); } // = onAction="#onClique"
}
```

Lembretes: tag = classe, atributo = propriedade · `fx:id` ↔ campo · `#metodo` ↔ método ·
`styleClass` ↔ `.classe` no CSS · `-Infinity` = tamanho do conteúdo, `Infinity` = ocupar tudo.
