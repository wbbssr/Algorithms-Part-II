import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.StdIn;
import java.util.*;

public class WordNet {
    private int V;
    private int E;
    private Map<String, Set<Integer>> synSetNounIdMap;
    private Map<Integer, Set<String>> synSetIdNounMap;
    private Set<String> nounSet;
    private SAP sap;
    private Digraph digraph;
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (null == synsets || null == hypernyms)
            throw new java.lang.IllegalArgumentException();

        synSetNounIdMap = new HashMap<>();
        synSetIdNounMap = new HashMap<>();
        nounSet = new HashSet<String>();

        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] subPart = line.split(",");
            int id = Integer.parseInt(subPart[0]);
            String[] subSubPart = subPart[1].split(" ");
            Set<String> synSet = new HashSet<String>();
            for (String s : subSubPart) {
                if (synSetNounIdMap.containsKey(s))
                    synSetNounIdMap.get(s).add(id);
                else {
                    Set<Integer> setId = new HashSet<Integer>();
                    setId.add(id);
                    synSetNounIdMap.put(s, setId);
                }

                synSet.add(s);

                if (!nounSet.contains(s))
                    nounSet.add(s);
            }
            synSetIdNounMap.put(id, synSet);
            V++;
        }

        digraph = new Digraph(V);
        in = new In(hypernyms);
        while(in.hasNextLine()) {
            String line = in.readLine();
            String[] subPart = line.split(",");
            for (String s : subPart) {
                if (subPart[0] != s)
                    digraph.addEdge(Integer.parseInt(subPart[0]), Integer.parseInt(s));
            }
        }

        DirectedCycle cycle = new DirectedCycle(digraph);
        if (cycle.hasCycle())
            throw new java.lang.IllegalArgumentException();

        int roots = 0;

        for (int v = 0; v < V; v++) {
            if (digraph.outdegree(v) == 0) {
                roots++;
            }
        }

        if (roots != 1)
            throw new java.lang.IllegalArgumentException();

        sap = new SAP(digraph);
    }
    // return all WordNet nouns
    public Iterable<String> nouns() {
        return nounSet;
    }
    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (null == word)
            throw new java.lang.IllegalArgumentException();
        return nounSet.contains(word);
    }
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();
        return sap.length(synSetNounIdMap.get(nounA), synSetNounIdMap.get(nounB));
    }
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined blow)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();

        int ancestorID = sap.ancestor(synSetNounIdMap.get(nounA), synSetNounIdMap.get(nounB));           
        String result = "";
        for (String s : synSetIdNounMap.get(ancestorID)) {
            result = result + s + " ";
        }
        return result.trim();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.distance(args[2], args[3]));
        System.out.println(wordnet.sap(args[2], args[3]));
    }
}