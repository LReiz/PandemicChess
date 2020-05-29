# Projeto PandemicChess
## Equipe
* Leonardo Almeida Reis - RA: 239104
* João Vitor Baptista Moreira - RA: 237833

## Descrição
Jogo de Tabuleiro com um contexto de pandemia.

## Vídeo do Projeto
[Explicação do Projeto](youtube.com)

## Diagrama Geral de Componentes
(imagens aqui)

## Componente Peca
### Interfaces
(imagens interfaces)

## Componente Tabuleiro
### Interfaces
(imagens interfaces)

### Detalhamento das Interfaces
* **Interface IMovimento**

Interface que lida com os movimento no Tabuleiro
Método | Objetivo
------ | --------
`verificar_movimento` | Verificar se o movimento da peça pode ser realizado ou não

* **Interface ICha**

Interface que lida com a posição do Chá (item único) no Tabuleiro
Método | Objetivo
------ | --------
`getCha` | relatar a posição do Chá no Tabuleiro

* **Interface IPeca**

Interface que lida com o ataque entre as peças próximas
Método | Objetivo
------ | --------
`atacar` | faz uma peça atacar uma outra peça inimiga. A forma de ataque vária conforme o Peça e os               itens que ela possui


