import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import java.util.*;

public class SAP {
    private final int V;
    private final int E;
    private Digraph G;
    private int lastPointV;
    private int lastPointW;
    private int lastSapLengthOfPoint;
    private int lastSapAncestorOfPoint;
    private Set<Integer> lastSetV;
    private Set<Integer> lastSetW;
    private int lastSapLengthOfSet;
    private int lastSapAncestorOfSet;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
        V = G.V();
        E = G.E();
        lastPointV = -1;
        lastPointW = -1;
        lastSetV = null;
        lastSetW = null;
    }

    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if ((v == lastPointV && w == lastPointW) || (w == lastPointV && v == lastPointW))
            return lastSapLengthOfPoint;

        pointSap(v, w);

        return lastSapLengthOfPoint;
    }

    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if ((v == lastPointV && w == lastPointW) || (w == lastPointV && v == lastPointW))
            return lastSapAncestorOfPoint;

        pointSap(v, w);
        return lastSapAncestorOfPoint;
    }

    private void pointSap(int v, int w) {
        lastSapLengthOfPoint = -1;
        lastSapAncestorOfPoint = -1;
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        for (int i = 0; i < V; i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if (lastSapLengthOfPoint == -1 || lastSapLengthOfPoint > (bfsV.distTo(i) + bfsW.distTo(i))) {
                    lastSapLengthOfPoint = bfsV.distTo(i) + bfsW.distTo(i);
                    lastSapAncestorOfPoint = i;
                }
            }
        }
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);
        if (setEquals(lastSetV, v) && setEquals(lastSetW, w))
            return lastSapLengthOfSet;

        if (setEquals(lastSetW, v) && setEquals(lastSetV, w))
            return lastSapLengthOfSet;

        setSap(v, w);

        return lastSapLengthOfSet;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);

        if (setEquals(lastSetV, v) && setEquals(lastSetW, w))
            return lastSapAncestorOfSet;

        if (setEquals(lastSetW, v) && setEquals(lastSetV, w))
            return lastSapAncestorOfSet;

        setSap(v, w);

        return lastSapAncestorOfSet;
    }

    private void setSap(Iterable<Integer> v, Iterable<Integer> w) {
        lastSapLengthOfSet = -1;
        lastSapAncestorOfSet = -1;
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        for (int i = 0; i < V; i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if (lastSapLengthOfSet == -1 || lastSapLengthOfSet > (bfsV.distTo(i) + bfsW.distTo(i))) {
                    lastSapLengthOfSet = bfsV.distTo(i) + bfsW.distTo(i);
                    lastSapAncestorOfSet = i;
                }
            }
        }
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new java.lang.IllegalArgumentException();
    }

    private void validateVertex(Iterable<Integer> v) {
        if (v == null)
            throw new java.lang.IllegalArgumentException();

        for (int i : v)
            validateVertex(i);
    }

    private boolean setEquals(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            return false;
        Set<Integer> tempV = new HashSet<Integer>();
        Set<Integer> tempW = new HashSet<Integer>();

        for (int i : v)
            tempV.add(i);

        for (int i : w)
            tempW.add(i);

        boolean isFullEqual = true;

        if (tempV.size() != tempW.size())
            return false;

        for (int i : tempW) {
            if (!tempV.contains(i))
                isFullEqual = false;
        }
        return isFullEqual;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
            G.addEdge(v, 0);
            length   = sap.length(v, w);
            ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

        }
    }
}