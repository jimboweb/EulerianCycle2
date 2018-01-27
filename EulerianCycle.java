/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.sun.javafx.geom.Edge;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimstewart
 */
public class EulerianCycle {

    private int addEdgeOps;
    private int addNodeOps;
    private int buildCycleOps;

    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        ArrayList<int[]> inputs = new ArrayList<>();
        int[] in = new int[2];
        in[0] = scanner.nextInt();
        in[1] = scanner.nextInt();
        inputs.add(in);
        int n = inputs.get(0)[1];
        for (int i = 0; i < n; i++) {
            in = new int[2];
            in[0] = scanner.nextInt();
            in[1] = scanner.nextInt();
            inputs.add(in);
        }
        Graph g = buildGraph(inputs);
        Cycle c;
        if(!g.isGraphEven()){
            c = new Cycle(0);
        } else {
            c = g.makeEulerianCycle();
        }
        if(c.edges.size()==0){
            //either graph is even or couldn't find edge some other reason
            System.out.println("0");
        } else {
            System.out.println("1");
            for (int i : c.outputAsArray(g)) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

    }

    class Graph{
        private Edge[] edges;
        private Node[] nodes;
        int nodeNum;
        int edgeNum;
        public Graph(int n, int m){
            edges = new Edge[m];
            nodes = new Node[n];
            this.nodeNum = n;
            this.edgeNum = m;
        }
        public Edge addEdge(int ind, int from, int to){
            Edge e = new Edge(from, to);
            e.nodeNum = ind;
            try{
            edges[ind] = e;
            }catch(ArrayIndexOutOfBoundsException err){
                System.out.println();
            }
            return e;
        }
        public ArrayList<Integer> edgesFromEdge(int e){
            return edges[e].edgesOut;
        }
        public ArrayList<Integer> edgesToEdge(int e){
            return edges[e].edgesIn;
            
        }
        public int size(){
            return edges.length;
        }
        public Edge getEdge(int n){
            return edges[n];
        }

        public boolean isGraphEven(){
            for(Node n:nodes){
                if (n.edgesIn.size()!=n.edgesOut.size()){
                    return false;
                }
            }
            return true;
        }


        public Cycle makeEulerianCycle(){
            Cycle newCycle;
            Cycle oldCycle = new Cycle(edges.length);
            oldCycle.firstEdge = 0;
                //find new node to start from
            do{    
                newCycle= new Cycle(edges.length);
                newCycle.firstEdge = oldCycle.lastAvailableEdge(edges);
                if(newCycle.firstEdge==-1){
                    //return empty cycle because couldn't find available edge
                    return new Cycle(0);
                }
                newCycle.addEdge(newCycle.firstEdge);
                //do old cycle
                newCycle = growCycle(newCycle);
                newCycle.appendCycle(oldCycle);
                 //make new cycle
                oldCycle = newCycle.copy();
            } while (newCycle.edges.size() < edges.length);
           return newCycle;
        }


        Cycle growCycle(Cycle newCycle){
            int nextEdge = newCycle.firstEdge;
            while (edges[nextEdge].to!=edges[newCycle.firstEdge].from){
                for(Integer e:edgesFromEdge(nextEdge)){
                    if(!newCycle.visited[e]){
                        nextEdge = e;
                        newCycle.addEdge(nextEdge);
                        break;
                    }
                    buildCycleOps++;//debug
                }
            }
             
            return newCycle;
        }

    }
    
    
    class Edge{
        private int from;
        private int to;
        private int nodeNum;
        private ArrayList<Integer> edgesIn;
        private ArrayList<Integer> edgesOut;
        public Edge(int from, int to){
            this.from = from;
            this.to = to;
            this.edgesOut = new ArrayList<>();
            this.edgesIn = new ArrayList<>();
        }
        private int from(){
            return from;
        }
        private int to(){
            return to;
        }
//        For when I try to get rid of the nodes
        public void addEdgeIn(int n){
            edgesIn.add(n);
        }
        //[tk] move this to the edge instead
        public ArrayList<Integer> getEdgesIn(){
            return edgesIn;
        }
        public void addEdgeOut(int n){
            edgesOut.add(n);
        }
        public ArrayList<Integer> getEdgesOut(){
            return edgesOut;
        }
        public int getFromNode(){
            return from;
        }
        public int getToNode(){
            return to;
        }
    }
    
    class Node{
        private ArrayList<Integer> edgesIn;
        private ArrayList<Integer> edgesOut;
        private int nodeNum = -1;
        public Node(int nodeNum){
            edgesIn = new ArrayList<>();
            edgesOut = new ArrayList<>();
            this.nodeNum = nodeNum;
        }
        public void setNodeNum(int n){
            nodeNum = n;
        }
        public int getNodeNum(){
            return nodeNum;
        }
        public void addEdgeIn(int n){
            edgesIn.add(n);
        }
        public int getEdgeIn(int n){
            return edgesIn.get(n);
        }
        public ArrayList<Integer> getEdgesIn(){
            return edgesIn;
        }
        public void addEdgeOut(int n){
            edgesOut.add(n);
        }
        public int getEdgeOut(int n){
            return edgesOut.get(n);
        }
        public ArrayList<Integer> getEdgesOut(){
            return edgesOut;
        }
    }
    
    class Cycle{
        private ArrayList<Integer> edges;
        private int firstEdge;
        private boolean[] visited;
        private int graphSize;
        public Cycle(int graphSize){
            edges = new ArrayList<>();
            visited = new boolean[graphSize];
            this.graphSize=graphSize;
            firstEdge = -1;
        }

        /**
         * Finds a new first edge with unvisited edges out
         * from previous cycle
         * @return the edge number of the new first edge
         */
        private int lastAvailableEdge(Edge[] edges){
            if(size()==0)
                return 0;
            int edgeNumber = edges.length-1;
            int edge;
            int firstEdge = -1;
            while(firstEdge==-1 && edgeNumber>0){
                //TODO: duh ok I am only looking at the edges coming from the last edge I drew
                //I think I need to go around and look at each edge in the cycle
                for(int e:edges[edgeNumber].getEdgesOut()){
                    if(!visited[e]){
                        firstEdge = e;
                        break;
                    }
                    buildCycleOps++;//debug
                }
                edgeNumber--;
            }
            return firstEdge;
        }


        public void appendCycle(Cycle otherCycle){
            edges.addAll(otherCycle.edges);
        }

        public void addEdge(int e){
            edges.add(e);
            if(firstEdge == -1)
                firstEdge = e;
            visited[e] = true;
        }
        public int size(){
            return edges.size();
        }
        public int getEdge(int n){
            return edges.get(n);
        }
        public int getFirstEdge(){
            return firstEdge;
        }
        public int getLastEdge(){return edges.get(edges.size()-1);}
        public Cycle copy(){
            Cycle c = new Cycle(graphSize);
            c.edges = new ArrayList<>(edges);
            c.firstEdge = this.firstEdge;
            c.visited = Arrays.copyOf(visited, visited.length);
            return c;
        }
        public int[] outputAsArray(Graph graph){
            int[] rtrn = new int[edges.size()];
            for(int i=0;i<edges.size();i++){
                rtrn[i] = graph.edges[edges.get(i)].from+1;
            }
            return rtrn;
        }

    }
    public static void main(String[] args) {
      new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new EulerianCycle().run();
                        } catch (IOException ex) {
                            Logger.getLogger(EulerianCycle.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, "1", 1 << 26).start();
     }


    public Graph buildGraph(ArrayList<int[]> inputs){
        int n = inputs.get(0)[0];
        int m = inputs.get(0)[1];
        ArrayList<ArrayList<Integer>> edgesFromNode = new ArrayList<>();
        for(int i=0;i<n;i++){
            edgesFromNode.add(new ArrayList<>());
        }
        ArrayList<ArrayList<Integer>> edgesToNode = new ArrayList<>();
        for(int i=0;i<n;i++){
            edgesToNode.add(new ArrayList<>());
        }        
        Graph g = new Graph(n,m);
        for(int i=1;i<inputs.size();i++){
            int x=inputs.get(i)[0];
            int y=inputs.get(i)[1];
            int from = x-1;
            int to = y-1;
            int edgeInd = i-1;
            Edge e = g.addEdge(edgeInd, from, to);
            edgesFromNode.get(from).add(edgeInd);
            edgesToNode.get(to).add(edgeInd);
            addEdgeOps++; //debug
            g.nodes = addOrModifyNodes(g.nodes,edgeInd,from,to);
        }
        for(Edge e:g.edges){
            for(Integer edgeOut:edgesFromNode.get(e.to)){
                e.edgesOut.add(edgeOut);
                addEdgeOps++;//debug
            }
            for(Integer edgeIn:edgesToNode.get(e.from)){
                e.edgesIn.add((edgeIn));
                addEdgeOps++;//debug
            }
        }
        return g;
    }

    private Node[] addOrModifyNodes(Node[] nodes, int edgeNum, int from, int to){
        Node nextNode;
        addNodeOps++; //debug
        if(nodes[from]!=null){
            nextNode = nodes[from];
        } else {
            nextNode = new Node(from);
            nodes[from] = nextNode;
        }
        if(!nextNode.edgesOut.contains(edgeNum)){
            nextNode.edgesOut.add(edgeNum);
        }

        if(nodes[to]!=null){
            nextNode = nodes[to];
        } else {
            nextNode = new Node(to);
            nodes[to]=nextNode;
        }
        if(!nextNode.edgesIn.contains(edgeNum)){
            nextNode.edgesIn.add(edgeNum);
        }
        return nodes;
    }

}
