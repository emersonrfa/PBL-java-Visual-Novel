# Imagens do jogo

O jogo funciona sem nenhuma imagem; cada arquivo colocado aqui passa a ser usado automaticamente,
sem mexer no código (resolvido por `service.AssetResolver`).

Nomes viram "slug": minúsculas, sem acento, espaços/símbolos viram `_`
("Sala de Estar" → `sala_de_estar`). Formatos: `png`, `jpg`, `jpeg`.

| Tipo | Caminho | Observação |
|------|---------|------------|
| Fundo | `backgrounds/<id>.png` | `<id>` = `.background("id")` da cena no `StoryRepository`; sem isso, o título da cena (`Quarto` → `quarto.png`). Cai em `backgrounds/default.png`. |
| Sprite | `characters/<personagem>/<expressão>.png` | Ex.: `characters/emily/surpresa.png`. Cai em `characters/<personagem>/default.png`. O Narrador não tem sprite. |
| Item | `items/<nome do item>.png` | Ex.: `items/chave_do_quarto.png`. Cai em `items/default.png`. |

Tamanho sugerido: fundos 1280x720; sprites PNG com transparência, mesma altura entre expressões.
