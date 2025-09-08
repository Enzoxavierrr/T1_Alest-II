import java.util.*;

// O parâmetro "trace" permite habilitar ou desabilitar logs detalhados da simulação.
// Quando true, imprime no console cada início e término de tarefa, além do makespan final.
// Esse recurso ta sfimo util para depuração e entfimimento passo a passo do escalonamento,
// mas não influencia em nada no resultado final do algoritmo, só métricos.

public class Solucao1 {

    private static class Rodando {
        final String nome;
        final long finalizada;
        Rodando(String n, long f) { nome = n; finalizada = f; }
    }

    public static long simular(Main.Instance inst, Main.Policy policy, boolean trace) {
        Map<String,Integer> indeg = new HashMap<>(inst.indegree);
        final Map<String,List<String>> adj = inst.adj;
        final Map<String,Integer> dur = inst.duration;

        List<String> pronta = new ArrayList<>();
        for (String v : inst.allTasks) if (indeg.get(v) == 0) pronta.add(v);

        PriorityQueue<Rodando> Rodando =
            new PriorityQueue<>(Comparator.comparingLong(r -> r.finalizada));

        long tempo = 0L;
        int finalizadaed = 0;
        final int total = inst.allTasks.size();
        int procLivre = inst.processors;

        while (finalizadaed < total) {

            while (procLivre > 0 && !pronta.isEmpty()) {
                int idx = pegaIndice(pronta, dur, policy);  
                String t = pronta.remove(idx);              
                long fim = tempo + dur.get(t);
                Rodando.add(new Rodando(t, fim));
                if (trace) System.out.printf("[S1-%s] start %-20s at %d (dur=%d)%n",
                        policy, t, tempo, dur.get(t));
                procLivre--;
            }


            Rodando ev = Rodando.poll();
            tempo = ev.finalizada;
            if (trace) System.out.printf("[S1-%s] finalizada %-19s at %d%n", policy, ev.nome, tempo);
            procLivre++;
            finalizadaed++;

            for (String u : adj.get(ev.nome)) {
                indeg.put(u, indeg.get(u) - 1);
                if (indeg.get(u) == 0) pronta.add(u);
            }
        }
        if (trace) System.out.printf("[S1-%s] makespan = %d%n", policy, tempo);
        return tempo;
    }

    private static int pegaIndice(List<String> pronta, Map<String,Integer> dur, Main.Policy policy) {
        int best = 0;
        for (int i = 1; i < pronta.size(); i++) {
            String a = pronta.get(i);
            String b = pronta.get(best);
            int ca = dur.get(a);
            int cb = dur.get(b);
            int cmp = Integer.compare(ca, cb);

            if (policy == Main.Policy.MIN) {
                if (cmp < 0 || (cmp == 0 && a.compareTo(b) < 0)) best = i;
            } else { 
                if (cmp > 0 || (cmp == 0 && a.compareTo(b) < 0)) best = i;
            }
        }
        return best;
    }
}