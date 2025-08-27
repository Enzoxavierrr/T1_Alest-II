import java.util.*;

public class Solucao2 {

    private static class Running {
        final String name;
        final long finish;
        Running(String n, long f) { name = n; finish = f; }
    }

    public static long simular(Main.Instance inst, Main.Policy policy, boolean trace) {
        Map<String,Integer> indeg = new HashMap<>(inst.indegree);
        Map<String,List<String>> adj = inst.adj;
        Map<String,Integer> dur = inst.duration;

        Comparator<String> readyCmp;
        if (policy == Main.Policy.MIN) {
            readyCmp = (a, b) -> {
                int c = Integer.compare(dur.get(a), dur.get(b));
                return (c != 0) ? c : a.compareTo(b);
            };
        } else {
            readyCmp = (a, b) -> {
                int c = Integer.compare(dur.get(b), dur.get(a));
                return (c != 0) ? c : a.compareTo(b);
            };
        }
        PriorityQueue<String> ready = new PriorityQueue<>(readyCmp);
        for (String v : inst.allTasks) if (indeg.get(v) == 0) ready.add(v);

        PriorityQueue<Running> running =
            new PriorityQueue<>(Comparator.comparingLong(r -> r.finish));

        long time = 0;
        int finished = 0;
        int total = inst.allTasks.size();
        int free = inst.processors;

        while (finished < total) {
            while (free > 0 && !ready.isEmpty()) {
                String t = ready.poll();
                long end = time + dur.get(t);
                running.add(new Running(t, end));
                if (trace) System.out.printf("[S2-%s] start %-20s at %d (dur=%d)%n",
                        policy, t, time, dur.get(t));
                free--;
            }

            if (running.isEmpty() && ready.isEmpty() && finished < total) {
                throw new IllegalStateException("Deadlock (ciclo no grafo?)");
            }

            Running ev = running.poll();
            time = ev.finish;
            if (trace) System.out.printf("[S2-%s] finish %-19s at %d%n", policy, ev.name, time);
            free++;
            finished++;

            for (String u : adj.get(ev.name)) {
                indeg.put(u, indeg.get(u) - 1);
                if (indeg.get(u) == 0) ready.add(u);
            }
        }
        if (trace) System.out.printf("[S2-%s] makespan = %d%n", policy, time);
        return time;
    }
}