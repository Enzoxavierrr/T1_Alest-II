import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String file = "";

        while (true) {
            if (args.length == 0) {
                System.out.println("\nOu selecione a opção de teste:");
                System.out.println("1 - Teste com 10 testes");
                System.out.println("2 - Teste com 20 testes");
                System.out.println("3 - Teste com 40 testes");
                System.out.println("4 - Teste com 60 testes");
                System.out.println("5 - Teste com 80 testes");
                System.out.println("6 - Teste com 100 testes");
                System.out.println("7 - Teste com 150 testes");
                System.out.println("8 - Teste com 200 testes");
                System.out.println("9 - Teste com 300 testes");
                System.out.println("10 - Teste com 400 testes");
                System.out.println("11 - Teste com 500 testes");
                System.out.println("12 - Sair");
                System.out.print("Digite a opção desejada: ");
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        file = "caso010.txt";
                        break;
                    case 2:
                        file = "caso020.txt";
                        break;
                    case 3:
                        file = "caso040.txt";
                        break;
                    case 4:
                        file = "caso060.txt";
                        break;
                    case 5:
                        file = "caso080.txt";
                        break;
                    case 6:
                        file = "caso100.txt";
                        break;
                    case 7:
                        file = "caso150.txt";
                        break;
                    case 8:
                        file = "caso200.txt";
                        break;
                    case 9:
                        file = "caso300.txt";
                        break;
                    case 10:
                        file = "caso400.txt";
                        break;
                    case 11:
                        file = "caso500.txt";
                        break;
                    case 12:
                        System.out.println("Fechando programa");
                        System.exit(0);
                }
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

            long t0 = System.nanoTime();
            long s1Min = Solucao1.simular(inst, Policy.MIN, false);
            long t1 = System.nanoTime();
            long s1Max = Solucao1.simular(inst, Policy.MAX, false);
            long t2 = System.nanoTime();

            long s2Min = Solucao2.simular(inst, Policy.MIN, false);
            long t3 = System.nanoTime();
            long s2Max = Solucao2.simular(inst, Policy.MAX, false);
            long t4 = System.nanoTime();

            System.out.println("\n=== Arquivo: " + file + " ===");
            System.out.println("Processadores: " + inst.processors);

            System.out.printf("[Solução 1] MIN=%d (%.2f ms)  MAX=%d (%.2f ms)%n",
                    s1Min, (t1 - t0) / 1e6, s1Max, (t2 - t1) / 1e6);

            System.out.printf("[Solução 2] MIN=%d (%.2f ms)  MAX=%d (%.2f ms)%n",
                    s2Min, (t3 - t2) / 1e6, s2Max, (t4 - t3) / 1e6);
        }
    }


     public enum Policy {
        MIN, MAX
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
                if (line.isEmpty() || line.equals("digraph G {") || line.equals("}"))
                    continue;

                if (line.startsWith("#")) {
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
        Map<String, Integer> duration = new HashMap<>();
        Map<String, List<String>> adj = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();

        if (deps != null) {
            for (String dep : deps) {
                if (dep == null)
                    continue;
                String s = dep.trim();
                if (s.isEmpty())
                    continue;
                if (!s.contains("->"))
                    continue;

                String[] parts = s.split("->");
                if (parts.length != 2)
                    continue;

                String left = parts[0].trim();
                String right = parts[1].trim();
                if (!left.matches(".*_\\d+$") || !right.matches(".*_\\d+$"))
                    continue;

                int dl = parseDuration(left);
                int dr = parseDuration(right);

                duration.putIfAbsent(left, dl);
                duration.putIfAbsent(right, dr);

                adj.computeIfAbsent(left, k -> new ArrayList<>()).add(right);
                adj.putIfAbsent(right, new ArrayList<>());

                indegree.putIfAbsent(left, 0);
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

    public static class Instance {
        public final int processors;
        public final Map<String, Integer> duration;
        public final Map<String, List<String>> adj;
        public final Map<String, Integer> indegree;
        public final List<String> allTasks;

        public Instance(int processors,
                Map<String, Integer> duration,
                Map<String, List<String>> adj,
                Map<String, Integer> indegree,
                List<String> allTasks) {
            this.processors = processors;
            this.duration = duration;
            this.adj = adj;
            this.indegree = indegree;
            this.allTasks = allTasks;
        }
    }
}