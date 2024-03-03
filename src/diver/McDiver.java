package diver;

import game.*;
import graph.ShortestPaths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Collections;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /** See {@code SewerDriver} for specification. */

    private LinkedList<Long> visited = new LinkedList<>();
    private LinkedList<Long> visited2 = new LinkedList<>();

    @Override
    public void seek(SeekState state) {
        dfsWalk(state);
    }

    /** See {@code SewerDriver} for specification. */
    @Override
    public void scram(ScramState state) {
        scram1(state);
    }

    /** Walks to the exit and will eventually search through every node until exit is found
     * Sorts the list of neighbors of McDiver's current location so that the neighbors closest to
     * the ring are made sure to be searched first.
     * Parameter: the SeekState s input into seek()
     * */
    public void dfsWalk(SeekState s){
        if (s.distanceToRing() == 0){
            return;
        }
        ArrayList<NodeStatus> nodes = new ArrayList<>(s.neighbors());
        Collections.sort(nodes);
        long u = s.currentLocation();
        visited.add(u);
        for (NodeStatus w: nodes) {
            if (!visited.contains(w.getId())) {
                visited.add(w.getId());
                s.moveTo(w.getId());
                dfsWalk(s);
                if (s.distanceToRing() == 0) {
                    return;
                }
                s.moveTo(u);
            }

        }
    }


    /** Allows McDiver to roam around the maze collecting coins until the steps needed to get back
     * are less than or equal to the steps left in the round minus 30 (to account for the next step
     * after this condition is checked costing the maximum weight of 15).
     *
     * When roaming for coins, it checks for the maximum coin value of its neighbors. If no coin in
     * neighboring edges, the neighbors of the neighbors are checked, and McDiver moves in the
     * direction of that max value. If still no coin, third neighbor is checked. If still no coin
     * found, goToRandomCoin() is called.
     * Parameter: the ScramState s input into scram()
     * */
    public void scram1(ScramState s){
        LinkedList<Node> visited3 = new LinkedList<>();
        while (stepsBack(s) <= s.stepsToGo() - 30){
            Node curr = s.currentNode();
            Set<Node> n = curr.getNeighbors();
            ArrayList<Node> neighbors = new ArrayList<>(n);
            int randomPos = (int)(Math.random()*neighbors.size());
            Node best = neighbors.get(randomPos);
            boolean changed = false;
            for (Node y : neighbors){
                if (y.getTile().coins() > best.getTile().coins() && !visited3.contains(y)){
                    best = y;
                    changed = true;
                }
            }
            // Look two steps ahead
            if(!changed){
                for (Node y : neighbors){
                    for(Node x : y.getNeighbors()){
                        if(x.getTile().coins() > best.getTile().coins() && !visited3.contains(x)){
                            best = y;
                            changed = true;
                        }
                    }
                }
            }
            // Look three steps ahead
            if(!changed){
                for (Node y : neighbors){
                    for(Node x : y.getNeighbors()){
                        for (Node z : x.getNeighbors()){
                            if(z.getTile().coins() > best.getTile().coins() && !visited3.contains(x)) {
                                best = y;
                            }
                        }
                    }
                }
            }
            if (!changed){
                goToRandomCoin(s);
            }
            else{
                s.moveTo(best);
                visited3.add(best);
            }
        }
        goBack(s);
    }

    /** Creates a set of all nodes in the maze, loops through these nodes until a coin is found,
     * then sends McDiver to that coin.
     *
     * If steps run out, go straight to the exit. If a coin is passed on the way, return the method
     * (go back to scram1)
     *
     * Parameter: the ScramState s input into scram()
     * */
    public void goToRandomCoin(ScramState s){
        HashSet<Node> hashset = new HashSet<>(s.allNodes());
        Set<Node> set = hashset;
        Maze maze = new Maze(set);
        for (Node j : set){
            if (j.getTile().coins() > 0){
                ShortestPaths m = new ShortestPaths(maze);
                Node u = s.currentNode();
                m.singleSourceDistances(u);
                List<Edge> bestPath = m.bestPath(j);
                for (Edge e : bestPath){
                    if (stepsBack(s) > s.stepsToGo() - 30){
                        goBack(s);
                        return;
                    }
                    Node current = s.currentNode();
                    for (Node near : current.getNeighbors()){
                        if (near.getTile().coins() > 0){
                            return;
                        }
                    }
                    s.moveTo(e.destination());
                }
            }
        }
        goBack(s);
    }

    /** Sends McDiver straight to the exit by calling bestPath of the possible paths to the exit
     * Parameter: the ScramState s input into scram()
     * */
    public void goBack(ScramState s){
        Node u = s.currentNode();
        HashSet<Node> hashset = new HashSet<>(s.allNodes());
        Set<Node> set = hashset;
        Maze maze = new Maze(set);
        ShortestPaths m = new ShortestPaths(maze);
        m.singleSourceDistances(u);
        List<Edge> bestPath = m.bestPath(s.exit());
        for (Edge e : bestPath){
            s.moveTo(e.destination());
        }
    }

    /** Returns the weighted number of minimum steps needed to get to the exit from current
     * position. Calculates the shortest path to exit from current position then adds up weights
     * */
    public int stepsBack(ScramState s){
        Node u = s.currentNode();
        HashSet<Node> hashset = new HashSet<>(s.allNodes());
        Set<Node> set = hashset;
        Maze maze = new Maze(set);
        ShortestPaths m = new ShortestPaths(maze);
        m.singleSourceDistances(u);
        List<Edge> bestPath = m.bestPath(s.exit());
        int weights = 0;
        for (Edge e : bestPath){
            weights = weights + e.length();
        }
        return weights;
    }


}
