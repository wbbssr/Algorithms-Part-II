import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class Outcast {
    private WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        int maxDist = -1;
        String result = "";
        for (int i = 0; i < nouns.length; i++) {
            int tempDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                tempDist += wordnet.distance(nouns[i], nouns[j]);
            }

            if (tempDist > maxDist) {
                maxDist = tempDist;
                result = nouns[i];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        } 
    }
}