# Cannon Game

## Integrantes

- Walkiria Aparecida de Souza
- Allan Luiz Filipe Oliveira

## Objetivo do projeto

Desenvolver o jogo Cannon Game para Android, com base no Capítulo 6 do livro *Android 6 for Programmers: An App-Driven Approach (3rd Edition)*, aplicando conceitos de programação mobile, animação, desenho gráfico, eventos de toque, threads e reprodução de áudio.

## Funcionalidades implementadas

- Mira do canhão controlada por toque.
- Disparo de projéteis.
- Alvos com movimentação automática.
- Obstáculo com detecção de colisão.
- Detecção de colisões entre projéteis e elementos do jogo.
- Controle de tempo da partida.
- Recompensa de tempo ao atingir alvos e penalidade ao colidir com o obstáculo.
- Contador de disparos.
- Efeitos sonoros de disparo, acerto e colisão.
- Tela de resultado ao vencer ou esgotar o tempo.
- Opção de iniciar uma nova partida.
- Animação quadro a quadro utilizando `Canvas` e `Paint`.
- Game loop com thread para atualização dos elementos.
- Uso de `SurfaceView` e `SurfaceHolder`.
- Modo imersivo e tela cheia.
- Tela inicial com toque para começar.
- Sistema de pontuação com multiplicador de combo.
- Dificuldade progressiva por níveis.
- Efeito visual de partículas na destruição dos alvos.
- Feedback tátil com vibração nas colisões.
- Alvos gerados com cores e tamanhos variados.
- Cálculo de precisão de disparos com classificação no diálogo final.
- Opção de sair do jogo na tela final.

## Tecnologias utilizadas

- Java
- Android Studio
- Android SDK
- Canvas e Paint
- SurfaceView e SurfaceHolder
- Threads e game loop
- SoundPool
- Vibrator e feedback háptico

## Como executar a aplicação

1. Clone ou baixe este repositório do GitHub.
2. Abra o projeto no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Utilize um emulador Android ou conecte um dispositivo compatível.
5. Execute o aplicativo pelo botão **Run** do Android Studio.

## Como jogar

1. Toque na tela inicial para iniciar a partida.
2. Toque na tela e mova o dedo para ajustar a mira do canhão.
2. Solte o dedo para disparar.
3. Tente atingir todos os alvos antes que o tempo termine.
4. Evite atingir o obstáculo, pois ele provoca uma penalidade de tempo.
5. Ao terminar a partida, selecione **Jogar novamente** para recomeçar ou **Sair** para encerrar o jogo.

## Capturas de tela

<img width="1280" height="590" alt="1" src="https://github.com/user-attachments/assets/f0ace4cc-9b24-41be-84d0-0d6f6028c4d5" />
<img width="1280" height="590" alt="2" src="https://github.com/user-attachments/assets/5dde72af-3172-4a98-94c6-bbcbd9c5f60c" />
<img width="1280" height="590" alt="3" src="https://github.com/user-attachments/assets/6874ea33-54b2-4be5-bfdb-07373a1fee11" />
<img width="1280" height="590" alt="4" src="https://github.com/user-attachments/assets/0951a076-9c29-43e1-a577-9c4d727f2f35" />

## Referência

DEITEL, Paul; DEITEL, Harvey; WALD, Alexander. *Android 6 for Programmers: An App-Driven Approach*. 3. ed.

Projeto acadêmico desenvolvido para a disciplina de Programação III.
