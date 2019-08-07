import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.Bag;
import java.util.*;

public class BaseballElimination {
    private final int numberOfTeams;
    private Map<String, Integer> teamsNumbers;
    private String[] numbersTeams;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] against;
    private boolean[] isCalculated;
    private Bag<String>[] certificateOfElimination;
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        
        this.numberOfTeams            = Integer.parseInt(in.readLine());
        this.teamsNumbers             = new HashMap<>();
        this.numbersTeams             = new String[this.numberOfTeams];
        this.wins                     = new int[this.numberOfTeams];
        this.losses                   = new int[this.numberOfTeams];
        this.remaining                = new int[this.numberOfTeams];
        this.against                  = new int[this.numberOfTeams][this.numberOfTeams];
        this.isCalculated             = new boolean[this.numberOfTeams];
        this.certificateOfElimination = (Bag<String>[]) new Bag[this.numberOfTeams];
        for (int i = 0; i < this.numberOfTeams; i++) {
            certificateOfElimination[i] = new Bag<String>();
        }

        int numberOfLines = 0;
        while (in.hasNextLine()) {
            String line = in.readLine().trim();
            String[] array = line.split(" +");
            teamsNumbers.put(array[0], numberOfLines);
            numbersTeams[numberOfLines] = array[0];
            wins[numberOfLines]      = Integer.parseInt(array[1]);
            losses[numberOfLines]    = Integer.parseInt(array[2]);
            remaining[numberOfLines] = Integer.parseInt(array[3]);
            for (int i = 0; i < numberOfTeams; i++) {
                against[numberOfLines][i] = Integer.parseInt(array[4 + i]);
            }
            numberOfLines++;
        }

    }
    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }
    // all teams
    public Iterable<String> teams() {
        List<String> list = Arrays.asList(numbersTeams);
        return list;
    }
    // number of wins for given team
    public int wins(String team) {
        if (!teamsNumbers.containsKey(team))
            throw new IllegalArgumentException();
        return wins[teamsNumbers.get(team)];
    }
    // number of losses for given team
    public int losses(String team) {
        if (!teamsNumbers.containsKey(team))
            throw new IllegalArgumentException();
        return losses[teamsNumbers.get(team)];
    }
    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamsNumbers.containsKey(team))
            throw new IllegalArgumentException();
        return remaining[teamsNumbers.get(team)];
    }
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamsNumbers.containsKey(team1))
            throw new IllegalArgumentException();
        if (!teamsNumbers.containsKey(team2))
            throw new IllegalArgumentException();
        return against[teamsNumbers.get(team1)][teamsNumbers.get(team2)];
    }
    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teamsNumbers.containsKey(team))
            throw new IllegalArgumentException();

        if(isCalculated[teamsNumbers.get(team)])
            return certificateOfElimination[teamsNumbers.get(team)].size() != 0;

        calculate(team);

        return certificateOfElimination[teamsNumbers.get(team)].size() != 0;

    }
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teamsNumbers.containsKey(team))
            throw new IllegalArgumentException();

        if(isCalculated[teamsNumbers.get(team)]) {
            if (certificateOfElimination[teamsNumbers.get(team)].size() == 0)
                return null;
            return certificateOfElimination[teamsNumbers.get(team)];
        }

        calculate(team);

        if (certificateOfElimination[teamsNumbers.get(team)].size() == 0)
            return null;

        return certificateOfElimination[teamsNumbers.get(team)];
    }

    private void calculate(String team) {
        int numOfThisTeam = teamsNumbers.get(team);

        for(int i = 0; i < numberOfTeams; i++) 
            if (wins[numOfThisTeam] + remaining[numOfThisTeam] < wins[i]) {
                certificateOfElimination[numOfThisTeam].add(numbersTeams[i]);
                isCalculated[numOfThisTeam] = true;
                return;
            }

        // 下面构造Flownetwork
        
        // 首先，我们需要对除需要判断的team外的所以team, 他们之间的边, s, t编号
        // 对于需要编号的team, 我们就采用HashMap中的编号
        // 因为需要判断的team不需要编号，所以我们把它的编号给s
        // 我们把t编号为n, n即为总队伍数。
        // 至此我们已经把0--n编号都用完
        // 下面对连接已经编号的队伍之间的边进行编号
        
        // 一共有(n - 2)(n - 1) / 2条边需要编号
        // 我们创建2个(n - 2)(n - 1) / 2 + 1大小的一维数组
        // 第0个元素空着不用
        // 第一个数组保存边所连接的较小的顶点，第二个数组保存边多连接的较大的顶点，两个顶点唯一确定一条边
        // 这条边的真实编号为数组的下标 + n 
        int[] smallVertex = new int[(numberOfTeams - 1) * (numberOfTeams - 2) / 2 + 1];
        int[] bigVertex   = new int[(numberOfTeams - 1) * (numberOfTeams - 2) / 2 + 1];
        // keep smallVertex[0] and bigVertex[0] empty
        int index = 1;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == numOfThisTeam)
                continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == numOfThisTeam)
                    continue;
                smallVertex[index] = i;
                bigVertex[index]   = j;
                index++;
            }
        }
        // 从s流出的边的capacity的总和
        int valueFromS = 0;
        // 构造Flownetwork
        FlowNetwork flowNetwork = new FlowNetwork((numberOfTeams - 1) * (numberOfTeams - 2) / 2 + numberOfTeams + 1);
        for (int i = 1; i <= (numberOfTeams - 1) * (numberOfTeams - 2) / 2; i++) {
            valueFromS += against[smallVertex[i]][bigVertex[i]];
            // 添加从s出发的边，一共(numberOfTeams - 1) * (numberOfTeams - 2) / 2条
            flowNetwork.addEdge(new FlowEdge(numOfThisTeam, i + numberOfTeams, against[smallVertex[i]][bigVertex[i]]));
            // 添加从 连接队伍之间的边 到队伍的的边，共(numberOfTeams - 1) * (numberOfTeams - 2)条
            flowNetwork.addEdge(new FlowEdge(i + numberOfTeams, smallVertex[i], Double.POSITIVE_INFINITY));
            flowNetwork.addEdge(new FlowEdge(i + numberOfTeams, bigVertex[i], Double.POSITIVE_INFINITY));
        }
        // 添加从队伍到t的边，一共n - 1条
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == numOfThisTeam)
                continue;
            flowNetwork.addEdge(new FlowEdge(i, numberOfTeams, wins[numOfThisTeam] + remaining[numOfThisTeam] - wins[i]));
        }

        FordFulkerson fordFulk = new FordFulkerson(flowNetwork, numOfThisTeam, numberOfTeams);

        isCalculated[numOfThisTeam] = true;

        if (Math.abs(valueFromS - fordFulk.value()) < 0.01)
            return;
        
        for (int i = numberOfTeams - 1; i >= 0; i--) {
            if (i == numOfThisTeam)
                continue;
            if (fordFulk.inCut(i))
                certificateOfElimination[numOfThisTeam].add(numbersTeams[i]);
        }
        

        

    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
            for (String team : division.teams()) {
                if (division.isEliminated(team)) {
                    StdOut.print(team + " is eliminated by the subset R = { ");
                    for (String t : division.certificateOfElimination(team)) {
                        StdOut.print(t + " ");
                    }
                    StdOut.println("}");
                }
                else {
                    StdOut.println(team + " is not eliminated");
                }
            }        
    }
}