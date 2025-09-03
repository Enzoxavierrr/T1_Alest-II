Escalonador de Tarefas (MIN/MAX) — Solução 1 e Solução 2

Simulador de escalonamento de tarefas com dependências em ambiente multiprocessador, calculando o makespan (tempo total) para duas políticas:
	•	MIN: prioriza a tarefa de menor duração;
	•	MAX: prioriza a tarefa de maior duração.

O projeto inclui duas abordagens:
	•	Solução 1 (Ingênua): seleção linear na lista de tarefas prontas;
	•	Solução 2 (Otimizada): heap / fila de prioridade para escolher a próxima tarefa.

 - Compilar e rodar (terminal) sem o while.
Dentro da pasta do projeto:

# 1) Rodar apontando um arquivo .txt
java -cp bin Main [nome do txt que queres testar]

Saída esperada 
=== Arquivo: caso100.txt ===
Processadores: 9
[Solução 1 - Ingênua]  MIN: 1234   MAX: 1370
[Solução 2 - Heaps  ]  MIN: 1234   MAX: 1370


    