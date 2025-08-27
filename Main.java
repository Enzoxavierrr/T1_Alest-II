import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public enum Policy { MIN, MAX }

    public static class Instance {
        public final int processors;
        public final Map<String,Integer> duration;           
        public final Map<String,List<String>> adj;           
        public final Map<String,Integer> indegree;           
        public final List<String> allTasks;                  

        public Instance(int processors,
                        Map<String,Integer> duration,
                        Map<String,List<String>> adj,
                        Map<String,Integer> indegree,
                        List<String> allTasks) {
            this.processors = processors;
            this.duration = duration;
            this.adj = adj;
            this.indegree = indegree;
            this.allTasks = allTasks;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java Main <arquivo.txt> [--trace]");
            return;
        }
        boolean trace = false;
        String file = null;
        for (String a : args) {
            if (a.equalsIgnoreCase("--trace")) trace = true;
            else file = a;
        }
        if (file == null) {
            System.out.println("Uso: java Main <arquivo.txt> [--trace]");
            return;
        }

        int processors;
        String[] deps;
        try {
            var read = readInstance(file);
            processors = read.processors;
            deps = read.deps;
        } catch (IOException e) {
            System.err.println("Erro lendo arquivo: " + e.getMessage());
            return;
        }

        Instance inst = fromArray(processors, deps);
        

        long s1Min = Solucao1.simular(inst, Policy.MIN, trace);
        long s1Max = Solucao1.simular(inst, Policy.MAX, trace);
        long s2Min = Solucao2.simular(inst, Policy.MIN, trace);
        long s2Max = Solucao2.simular(inst, Policy.MAX, trace);
    

        System.out.println("=== Arquivo: " + file + " ===");
        System.out.println("Processadores: " + inst.processors);
        System.out.println("[Solução 1 - Ingênua]  MIN: " + s1Min + "   MAX: " + s1Max);
        System.out.println("[Solução 2 - Heaps  ]  MIN: " + s2Min + "   MAX: " + s2Max);

        
    }

    private static class Raw {
        int processors;
        String[] deps;
    }

    private static Raw readInstance(String filename) throws IOException {
        Raw out = new Raw();
        List<String> deps = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.equals("digraph G {") || line.equals("}")) continue;

                if (line.startsWith("#")) {
                    // linha tipo "# Proc 9"
                    String low = line.toLowerCase(Locale.ROOT);
                    if (low.contains("proc")) {
                        for (String tok : line.split("\\s+")) {
                            if (tok.matches("\\d+")) {
                                out.processors = Integer.parseInt(tok);
                                break;
                            }
                        }
                    }
                } else if (line.contains("->")) {
                    deps.add(line.replace(";", "")); 
                }
            }
        }
        out.deps = deps.toArray(new String[0]);
        return out;
    }

    public static Instance fromArray(int processors, String[] deps) {
        Map<String,Integer> duration = new HashMap<>();
        Map<String,List<String>> adj = new HashMap<>();
        Map<String,Integer> indegree = new HashMap<>();

        if (deps != null) {
            for (String dep : deps) {
                if (dep == null) continue;
                String s = dep.trim();
                if (s.isEmpty()) continue;
                if (!s.contains("->")) continue;

                String[] parts = s.split("->");
                if (parts.length != 2) continue;

                String left = parts[0].trim();
                String right = parts[1].trim();
                if (!left.matches(".*_\\d+$") || !right.matches(".*_\\d+$")) continue;

                int dl = parseDuration(left);
                int dr = parseDuration(right);

                duration.putIfAbsent(left,  dl);
                duration.putIfAbsent(right, dr);

                adj.computeIfAbsent(left,  k -> new ArrayList<>()).add(right);
                adj.putIfAbsent(right, new ArrayList<>());

                indegree.putIfAbsent(left,  0);
                indegree.put(right, indegree.getOrDefault(right, 0) + 1);
            }
        }

        for (String v : duration.keySet()) {
            adj.putIfAbsent(v, new ArrayList<>());
            indegree.putIfAbsent(v, 0);
        }

        List<String> all = new ArrayList<>(duration.keySet());
        Collections.sort(all); 

        return new Instance(processors, duration, adj, indegree, all);
    }

    private static int parseDuration(String name) {
        int p = name.lastIndexOf('_');
        return Integer.parseInt(name.substring(p + 1));
    }
}