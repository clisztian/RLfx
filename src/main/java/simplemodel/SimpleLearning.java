package simplemodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SimpleLearning {

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private int mazeWidth;
    private int mazeHeight;
    private int statesCount;
    private int numberStates;
    private final int penalty = -1;
    private final int reward = 10;

    private String[][] maze;
    private int[][] R; //reward lookup
    private double[][] Qmatrix;

    public static void main(String args[]) throws Exception {
        SimpleLearning ql = new SimpleLearning();

        ql.initMaze();
        ql.calculateQ(1000);
        ql.printQ();
        ql.printPolicy();
    }

    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + pickPolicyFromState(i));
        }
    }
    public void initMaze() throws Exception {

        ClassLoader classLoader = SimpleLearning.class.getClassLoader();
        File file = new File(classLoader.getResource("data/maze.txt").getFile());

        ArrayList<String> _maze = new ArrayList<>();
        try (BufferedReader fis = new BufferedReader(new FileReader(file))) {

            int i = 0;
            int j = 0;
            String content;
            // Read the maze from the input file
            while ((content = fis.readLine()) != null) {

                String[] line = content.split("[ ]+");
                i = line.length;

                _maze.addAll(Arrays.asList(line));

                j++;
            }

            mazeWidth = i;
            mazeHeight = j;
            statesCount = mazeHeight * mazeWidth;
            numberStates = statesCount;
            System.out.println(mazeWidth + " " + mazeHeight);
        }
        catch (Exception e) {
            throw new Exception(e.toString());
        }

        //initialize reward matrix
        R = new int[statesCount][statesCount];

        //initiate maze
        maze = new String[mazeHeight][mazeWidth];
        for(int i = 0; i < mazeHeight; i++) {
            for(int j = 0; j < mazeWidth; j++) {

                String v = _maze.get(j + i*mazeWidth);
                maze[i][j] = v;
            }
        }


        for(int n = 0; n < numberStates; n++) {

            int i = n / mazeWidth;
            int j = n - i * mazeWidth;

            for (int s = 0; s < statesCount; s++) {
                R[n][s] = -1;
            }

            if (!maze[i][j].equals("F")) {

                // Try to move left in the maze
                int goLeft = j - 1;
                if (goLeft >= 0) {
                    int target = i * mazeWidth + goLeft;
                    if (maze[i][goLeft].equals("0")) {
                        R[n][target] = 0;
                    } else if (maze[i][goLeft].equals("F")) {
                        R[n][target] = reward;
                    } else {
                        R[n][target] = penalty;
                    }
                }

                // Try to move right in the maze
                int goRight = j + 1;
                if (goRight < mazeWidth) {
                    int target = i * mazeWidth + goRight;
                    if (maze[i][goRight].equals("0")) {
                        R[n][target] = 0;
                    } else if (maze[i][goRight].equals("F")) {
                        R[n][target] = reward;
                    } else {
                        R[n][target] = penalty;
                    }
                }

                // Try to move up in the maze
                int goUp = i - 1;
                if (goUp >= 0) {
                    int target = goUp * mazeWidth + j;
                    if (maze[goUp][j].equals("0")) {
                        R[n][target] = 0;
                    } else if (maze[goUp][j].equals("F")) {
                        R[n][target] = reward;
                    } else {
                        R[n][target] = penalty;
                    }
                }

                // Try to move down in the maze
                int goDown = i + 1;
                if (goDown < mazeHeight) {
                    int target = goDown * mazeWidth + j;
                    if (maze[goDown][j].equals("0")) {
                        R[n][target] = 0;
                    } else if (maze[goDown][j].equals("F")) {
                        R[n][target] = reward;
                    } else {
                        R[n][target] = penalty;
                    }
                }
            }
        }

        initializeQ();
        printR(R);
    }

    //Set Q values to R values
    void initializeQ() {

        Qmatrix = new double[statesCount][statesCount];

        for (int i = 0; i < statesCount; i++){
            for(int j = 0; j < statesCount; j++){
                Qmatrix[i][j] = (double)R[i][j];
            }
        }
    }

    void printR(int[][] matrix) {
        System.out.printf("%25s", "States: ");
        for (int i = 0; i <= 8; i++) {
            System.out.printf("%4s", i);
        }
        System.out.println();

        for (int i = 0; i < statesCount; i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < statesCount; j++) {
                System.out.printf("%4s", matrix[i][j]);
            }
            System.out.println("]");
        }
    }

    public void calculateQ(int numberRounds) {

        long start = System.currentTimeMillis();

        Random rng = new Random();
        int currentState = 0;

        for(int i = 0; i < numberRounds; i++) {

            //for every simulation, pick random state
            currentState = rng.nextInt(numberStates);

            while(!isFinalState(currentState)) {

                //find the possible actions from current state
                int[] possibleActions = possibleActionsFromState(currentState);

                //grab a random state from the possible candidates
                int index = rng.nextInt(possibleActions.length);
                int nextAction = possibleActions[index];

                //compute the new q value
                //q(s, a) <- q(s, a) + eps * ( r(s, a) + alpha * max_q_all_actions(next_state, all_actions) + q(s, a))

                double q = Qmatrix[currentState][nextAction];
                double maxQ = maxQ(nextAction);
                int reward = R[currentState][nextAction];

                double new_q = q +  alpha * (reward + gamma * maxQ - q);

                Qmatrix[currentState][nextAction] = new_q;
                //now at the new state after taking new action
                currentState = nextAction;

            }
        }

        System.out.println((System.currentTimeMillis() - start));

    }

    int pickPolicyFromState(int currentState) {

        int[] actionsFromState = possibleActionsFromState(currentState);

        int policyGotoState = 0;
        double maxValue = Double.MIN_VALUE;
        for(int a : actionsFromState) {

            double value = Qmatrix[currentState][a];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = a;
            }
        }
        return policyGotoState;
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);
        //the learning rate and eagerness will keep the W value above the lowest reward
        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = Qmatrix[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }


    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < statesCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    boolean isFinalState(int state) {
        int i = state / mazeWidth;
        int j = state - i * mazeWidth;

        return maze[i][j].equals("F");
    }


    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Qmatrix.length; i++) {
            System.out.print("From state " + i + ":  ");
            for (int j = 0; j < Qmatrix[i].length; j++) {
                System.out.printf("%6.2f ", (Qmatrix[i][j]));
            }
            System.out.println();
        }
    }
}





