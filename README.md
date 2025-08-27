Escalonador de Tarefas (MIN/MAX) — Solução 1 e Solução 2

Simulador de escalonamento de tarefas com dependências em ambiente multiprocessador, calculando o makespan (tempo total) para duas políticas:
	•	MIN: prioriza a tarefa de menor duração;
	•	MAX: prioriza a tarefa de maior duração.

O projeto inclui duas abordagens:
	•	Solução 1 (Ingênua): seleção linear na lista de tarefas prontas;
	•	Solução 2 (Otimizada): heap / fila de prioridade para escolher a próxima tarefa.

📁 Estrutura do projeto
/Código
 ├── Main.java          # Lê .txt no formato DOT e orquestra a simulação
 ├── Solucao1.java      # Abordagem ingênua (lista simples)
 ├── Solucao2.java      # Abordagem com heaps (fila de prioridade)
 ├── caso100.txt        # Casos de teste (formato DOT/Graphviz)
 ├── caso150.txt
 ├── caso200.txt
 ├── caso300.txt
 ├── caso400.txt
 ├── caso500.txt
 └── bin/               # Saída dos .class (gerada na compilação)


 🧩 Formato de entrada (.txt)
Formato DOT/Graphviz simplificado:
digraph G {
# Proc 9
a_123 -> b_456
c_78  -> d_90
...
}

	•	# Proc N → número de processadores.
	•	Cada linha origem_dur -> destino_dur define uma dependência (origem deve terminar antes do destino).
	•	A duração é o número após o último _ no nome (ex.: a_123 → 123).



⚙️ Compilar e rodar (terminal)
Dentro da pasta do projeto:

# 1) (Opcional) criar pasta para os .class
mkdir -p bin

# 2) Compilar para a pasta bin/
javac -d bin Main.java Solucao1.java Solucao2.java

# 3) Rodar apontando um arquivo .txt
java -cp bin Main caso100.txt

Saída esperada (limpa, sem trace)
=== Arquivo: caso100.txt ===
Processadores: 9
[Solução 1 - Ingênua]  MIN: 1234   MAX: 1370
[Solução 2 - Heaps  ]  MIN: 1234   MAX: 1370

🐛 Modo depuração (trace)

Para ver o passo a passo (início e término de tarefas), adicione --trace ao final:
java -cp bin Main caso100.txt --trace


🧪 Dicas de teste
	•	Comece com um caso pequeno (poucas arestas) e --trace ligado para validar a lógica.
	•	Depois rode os casos grandes (caso200, caso300, caso500) com trace desligado.
	•	Se quiser medir tempo de CPU das soluções (para colocar no relatório), adicione medição com System.nanoTime() no Main em volta das chamadas de simulação.


🧠 Resumo técnico (para o relatório)
	•	Modelagem: grafo direcionado (DAG), V tarefas / E dependências.
	•	Solução 1: lista simples para “prontos” → seleção linear a cada alocação.
	•	Tempo: O(V² + E) (pior caso).
	•	Espaço: O(V + E).
	•	Solução 2: heap (fila de prioridade) para “prontos” + heap de eventos.
	•	Tempo: O((V + E) log V).
	•	Espaço: O(V + E).

    