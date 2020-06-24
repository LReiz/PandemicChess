# Projeto PandemicChess
## Equipe
* Leonardo Almeida Reis - RA: 239104
* João Vitor Baptista Moreira - RA: 237833

## Descrição
Jogo de Tabuleiro com um contexto de pandemia.

## Objetivo 
Dois jogadores competem com seus times pela vitória, cada um com seu objetivo. O time dos médicos procura controlar a doença e salvar toda a população, enquanto que o time dos Infectados tenta disseminar a doença a todo custo. 

## Itens
* **Algemas:** usadas pelos médicos para colocar infectados de quarentena
* **Chá de boldo:** usada pelos médicos para vencer o jogo
* **Máscaras:** usadas pelos médicos para se proteger dos infectados
* **Baús:** estão espalhados aleatoriamente pelo mapa e carregam um dos 3 tipos de itens

## Interações entre Entidades do Tabuleiro
* **Adquirir Item do Baú:** Ao chegar na vizinhança do baú, o médico adquire um dos seguintes itens: Máscara, Algema, Chá de Boldo.
* **Adquirir Chá de Boldo** (Cura): Caso um médico seja infectado com o Chá de Boldo na mão, esse item é largado no chão (na mesma célula onde ele se encontra) e pode ser coletado por um médico que chegue em sua vizinhança.
* **Perder Máscaras:** Caso um jogador saudável esteja a menos de duas casas de distância de um jogador infectado, ele perde uma máscara.
* **Infecção:** Se não tiver mais máscaras e estiver próximo de um jogador infectado, o jogador passa a ser do time dos infectados.
* **Quarentena:** Se um jogador com algema chegar na vizinhança de um jogador infectado, esse jogador entra em quarentena e sai do mapa. 
* **Cura do vírus:** Se um jogador chegar ao hospital com a cura do vírus(chá de boldo), o time dos médicos ganha.
* **Lockdown:** Se só sobram médicos no mapa, os médicos ganham.
* **Genocídio:** Se só sobram infectados no mapa, os Infectados ganham.

## Vídeo do Projeto
[Explicação do Projeto](https://www.youtube.com/watch?v=WkQB7zuo9eI)

## Slides do Projeto
[Link dos Slides](https://docs.google.com/presentation/d/1KDkpZwINmNoawVvCUoEiNdXEPe-MgW5p7afNmaXl2f0/edit#slide=id.p)

## Diagrama Geral de Componentes
![Diagrama Geral](./assets/diagrama-componentes.png)

## Componente Tabuleiro
<img src="./assets/componente-tabuleiro.png" alt="componente-tabuleiro" width="400" />

### Interfaces
<img src="./assets/interface-movimento.png" alt="interface-movimento" width="800" />

<img src="./assets/interface-ataque.png" alt="interface-ataque" width="800" />

<img src="./assets/interface-cria-cha.png" alt="interface-cria-cha" width="800" />

Campo | Valor
----- | -----
Classe | `<caminho completo da classe com pacotes>`
Autores | Leonardo Reis e João Vitor
Objetivo | Organizar as Celulas e Pecas em vetores 
Interface | `<interface em Java do componente>`
```java
public interface IMovimento{
	boolean verificarMovimento(int x_final, int y_final);
}
public interface IAtaque{
	void atacar(Peca inimigo);
}
public interface ICriaCha{
	void criaCha(Tabuleiro tab);
}
```

### Detalhamento das Interfaces
* **Interface IMovimento**

Interface que lida com os movimento no Tabuleiro
Método | Objetivo
------ | --------
`movimento` | Recebe como parâmetro as coordenadas do posição final e retorna true se for possível fazer o movimento ou false caso contrário.

* **Interface IAtaque**

Interface que lida com as mecânicas de ataque entre peças
Método | Objetivo
------ | --------
`atacar` | Recebe a peça inimiga como parâmetro e realiza o ataque à essa peça e retorna void

* **Interface ICriaCha**

Interface que cria o PecaCha no Tabuleiro
Método | Objetivo
------ | --------
`criaCha` | Recebe o próprio Tabuleiro e verifica se o número mínimo de rodadas da partida já foi atingido e tenta criar o chá. Retorna void

## Componente PecasMoveis
<img src="./assets/componente-peca-infectado.png" alt="componente-peca-infectado" width="400" />

<img src="./assets/componente-peca-medico.png" alt="componente-peca-medico" width="400" />

### Interfaces
<img src="./assets/interface-movimento.png" alt="interface-movimento" width="800" />

<img src="./assets/interface-ataque.png" alt="interface-ataque" width="800" />

Campo | Valor
----- | -----
Classe | `<caminho completo da classe com pacotes>`
Autores | Leonardo Reis e João Vitor
Objetivo | Representar as Pecas que semovimentam pelo tabuleiro
Interface | `<interface em Java do componente>`
```java
public interface IMovimento{
	boolean verificarMovimento(int x_final, int y_final);
}
public interface IAtaque{
	void atacar(Peca inimigo);
}
```

### Detalhamento das Interfaces
* **Interface IMovimento**

Interface que lida com os movimento no Tabuleiro
Método | Objetivo
------ | --------
`movimento` | Recebe como parâmetro as coordenadas do posição final e caso o Tabuleiro verifique que o movimento é válido, a peça executa o movimento.

* **Interface IAtaque**

Interface que lida com as mecânicas de ataque entre peças
Método | Objetivo
------ | --------
`atacar` | Recebe a peça como parametro, verifica se ela é diferente de null e inimiga, caso passe pelas condições chama o Tabuleiro para que este execute o ataque à peça inimiga. Retorna void

## Componente PecaMedico
<img src="./assets/componente-peca-medico.png" alt="componente-peca-medico" width="400" />

### Interfaces
* **Interface ITransferir**
<img src="./assets/interface-transferir.png" alt="interface-transferir" width="800" />

Campo | Valor
----- | -----
Classe | `<caminho completo da classe com pacotes>`
Autores | Leonardo Reis e João Vitor
Objetivo | Representar as Pecas do time dos médicos
Interface | `<interface em Java do componente>`
```java
public interface ITransferir{
	void transferirItens(PecaMedico medico);
}
```

### Detalhamento de Interfaces
* **Interface ITransferir**

Interface que lida com as transferências de itens entre uma PecaBau e uma PecaMedico
Método | Objetivo
------ | --------
`transferirItens` | Recebe a própria PecaMedico como parâmetro e verifica os PecaBau próximos. Caso encontre um PecaBau suficientemente próximo chama este para executar a transferêcia de itens. Retorna void

## Componente PecaBau
<img src="./assets/componente-peca-bau.png" alt="componente-peca-bau" width="400" />

### Interfaces
<img src="./assets/interface-transferir.png" alt="interface-transferir" width="800" />

Campo | Valor
----- | -----
Classe | `<caminho completo da classe com pacotes>`
Autores | Leonardo Reis e João Vitor
Objetivo | Representar os baús que gardam itens no jogo
Interface | `<interface em Java do componente>`
```java
public interface ITransferir{
	void transferirItens(PecaMedico medico);
}
public interface IGuardaCha{
	void verificarBau();
}
```

### Detalhamento de Interfaces
* **Interface ITransferir**

Interface que lida com as transferências de itens entre uma PecaBau e uma PecaMedico
Método | Objetivo
------ | --------
`transferirItens` | Recebe a PecaMedico como parâmetro e executa a transferência dos itens contidos na PecaBau para a PecaMedico. Retorna void

* **Interface IGuardaCha**

Interface que guarda o chá em uma PecaBau
Método | Objetivo
------ | --------
`verificarBau` | Guarda o PecaCha dentro do PecaBau correspondente. Retorna void

## Componente PecaCha
<img src="./assets/componente-peca-cha.png" alt="componente-peca-cha" width="400" />

### Interfaces
<img src="./assets/interface-guarda-cha.png" alt="interface-guarda-cha" width="800" />

<img src="./assets/interface-cria-cha.png" alt="interface-cria-cha" width="800" />

Campo | Valor
----- | -----
Classe | `<caminho completo da classe com pacotes>`
Autores | Leonardo Reis e João Vitor
Objetivo | Representar o item chá de boldo
Interface | `<interface em Java do componente>`
```java
public interface IGuardaCha{
	void verificarBau();
}
public interface ICriaCha{
	void criaCha(Tabuleiro tab);
}
```

### Detalhamento de Interfaces
* **Interface IGuardaCha**

Interface que guarda o chá em uma PecaBau
Método | Objetivo
------ | --------
`verificarBau` | Verifica se o Chá está dentro de uma PecaBau (mesma célula), caso esteja na mesma célula chama a PecaBau para guardá-lo neste. Retorna void.

* **Interface ICriaCha**

Interface que cria o PecaCha no Tabuleiro
Método | Objetivo
------ | --------
`criaCha` | Recebe o Tabuleiro e cria a PecaCha no jogo, que deve ser única. Retorna void

## Modelo do Plano de Exceções

### Diagrama da hierarquia de exceções

<img src="./assets/classes-erros-movimento.png" alt="interface-cria-cha" width="800" />

### Descrição das classes de exceção

Classe | Descrição 
------ | ---------
ErroMovimento | Engloba todos os erros relacionados ao movimento de pecas
ForaDeAlcance | Esse erro ocorre quando o jogador tenta se mover para fora do tabuleiro
NaoVazio | Esse erro ocorre quando o jogador tenta se mover para uma casa não-vazia do tabuleiro
MuitoDistante | Esse erro ocorre quando o jogador tenta se mover quando não é a sua vez
ForaDoTurno | Esse erro ocorre quando o jogador tenta mover uma peça em mais de uma casa
