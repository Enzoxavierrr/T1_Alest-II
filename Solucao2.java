import java.util.*;

public class Solucao2 {

    private static class Rodando {
        final String nome;
        final long fim;
        Rodando(String n, long f) { nome = n; fim = f; }
    }

   
    public static long simular(Main.Instance inst, Main.Policy policy, boolean trace) {
        // Copiamos o grau de entrada, pois ele será mutado na simulação
        Map<String,Integer> indeg = new HashMap<>(inst.indegree);
        Map<String,List<String>> adj = inst.adj;       
        Map<String,Integer> dur = inst.duration;        

       
        Comparator<String> readyCmp;
        if (policy == Main.Policy.MIN) {
            readyCmp = (a, b) -> {
                int c = Integer.compare(dur.get(a), dur.get(b));
                return (c != 0) ? c : a.compareTo(b);   // desempate alfabético
            };
        } else {
            readyCmp = (a, b) -> {
                int c = Integer.compare(dur.get(b), dur.get(a));
                return (c != 0) ? c : a.compareTo(b);   
            };
        }

        PriorityQueue<String> ready = new PriorityQueue<>(readyCmp);
        for (String v : inst.allTasks) if (indeg.get(v) == 0) ready.add(v);

        PriorityQueue<Rodando> running = new PriorityQueue<>(Comparator.comparingLong(r -> r.fim));

        long tempo = 0;
        int finalizadas = 0;
        int total = inst.allTasks.size();
        int procLivre = inst.processors;

        while (finalizadas < total) {
            // Despacha enquanto houver processadores livres e tarefas prontas
            while (procLivre > 0 && !ready.isEmpty()) {
                String t = ready.poll();
                long tFim = tempo + dur.get(t);
                running.add(new Rodando(t, tFim));
                if (trace) System.out.printf("[S2-%s] start %-20s at %d (dur=%d)%n",
                        policy, t, tempo, dur.get(t));
                procLivre--;
            }

            Rodando ev = running.poll();
            tempo = ev.fim;
            if (trace) System.out.printf("[S2-%s] finish %-19s at %d%n", policy, ev.nome, tempo);
            procLivre++;
            finalizadas++;

            for (String u : adj.get(ev.nome)) {
                indeg.put(u, indeg.get(u) - 1);
                if (indeg.get(u) == 0) ready.add(u);
            }
        }

        if (trace) System.out.printf("[S2-%s] makespan = %d%n", policy, tempo);
        return tempo;
    }
}