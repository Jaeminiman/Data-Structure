import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ArrayList;

public class Subway {
    static Hashtable<String, Point> NameHashtable = new Hashtable<>();
    static Hashtable<String, Vertex> IDHashtable = new Hashtable<>();
    static Hashtable<String, LinkedList<edgeNode>> adjacency;
    static StringBuilder path_sb = new StringBuilder();
    public static void main(String args[]) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out,true, "UTF-8");
        
        // I don't know what size of this hashtable. 100000 * 4byte = 400KB?

        // check there is input argument(txt path)
        if (args.length == 0) {
            out.println("No input argument file");
            System.exit(0);
        }

        // make input argument(txt path) to File instance
        // File file = new File(args[0]);

        // check if there is such a txt.file
        //if (file.exists()) {

            // fileReader_1 : read database source path
            // BufferedReader fileReader_1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
           
            // String databasePath = fileReader_1.readLine();
            // System.out.println(databasePath);
            // fileReader_1.close();
            // Local vs. virtual Environment
            String databasePath = args[0];
            // load database
            subwaySettingCommand(databasePath);

            while (true) {

                try {
                    // when user wants to Subway searching ex) chunho sadang
                    String input = br.readLine();
                    String output;
                    if (input.compareTo("QUIT") == 0){
                        output = path_sb.toString();
                        out.print(output);
                        break;
                    }
                    searchCommand(input);
                    
                } catch (IOException e) {
                    out.println(e.toString() + " : no search result");
                }
            }
        //} else {
        //    out.println("No such text file in argument path");
        //    System.exit(0);
        //}

    }

    private static void searchCommand(String input) throws IOException {
        // for initialization graph's cost, make new graph per one search
        // PrintStream out = new PrintStream(System.out,true, "UTF-8");
        String[] arr = input.split(" ");
        ArrayList<String> start_ID = NameHashtable.get(arr[0]).getID();
        // Reason for using Dijkstra_path
        // 1. visited checking by making set S
        // 2. for searching minimum cost vertex by tracking the route
        // 3. Shortest path tracking (rule of prev)
        MyArrayList Dijkstra_Path = new MyArrayList(100);
        MyArrayList ROI = new MyArrayList(100);
        

        // Dijkstra Algorithm

        // dist[start] = 0; S <= {startNode r};
        DijNode startNode = new DijNode(start_ID.get(0), 0);
        Dijkstra_Path.add(startNode);

        // Start.adj cost initialize
        LinkedList<edgeNode> list = adjacency.get(start_ID.get(0));
        for (int i = 0; i <= list.size() - 1; i++) {
            DijNode secondNode;
            // solve starting point's transfer duplication problem
            if(start_ID.contains(list.get(i).ID)){
                secondNode = new DijNode(list.get(i).ID, 0);         
            } else{
                secondNode = new DijNode(list.get(i).ID, list.get(i).distance);
                secondNode.prev = startNode;
            }
            ROI.add(secondNode);
        }

        // while(! endPoint in S)
        // S <= {v}, (cf) v = {DeleteMIN(ROI)}
        ArrayList<String> endID = NameHashtable.get(arr[1]).getID();
        while (!endID.contains(Dijkstra_Path.get(Dijkstra_Path.size() - 1).ID)) {
            DijNode Dij_v = DeleteMin(ROI);
            Dijkstra_Path.add(Dij_v);

            // for u in adj.v
            LinkedList<edgeNode> adj_List = adjacency.get(Dij_v.ID);
            for (int j = 0; j <= adj_List.size() - 1; j++) {
                edgeNode u = adj_List.get(j);

                // if u is not in S
                if (Dijkstra_Path.searchNode(u.ID) == null) {
                    // if u is not in ROI
                    DijNode Dij_u = ROI.searchNode(u.ID);
                    if (Dij_u == null) {
                        Dij_u = new DijNode(u.ID, Dij_v.cost + u.distance);
                        ROI.add(Dij_u);
                        Dij_u.prev = Dij_v;
                    }
                    // if u is already in ROI
                    else {
                        if (Dij_u.cost >= Dij_v.cost + u.distance) {
                            Dij_u.cost = Dij_v.cost + u.distance;
                            Dij_u.prev = Dij_v;
                        }
                    }
                }
                // if u is already in S
                else {
                    continue;
                }
            }
        }

        ArrayList<String> path_result = new ArrayList<>(100);
        DijNode tempNode = Dijkstra_Path.get(Dijkstra_Path.size() - 1);
        String final_cost = String.valueOf(tempNode.cost);
        while(tempNode != null){
            path_result.add(tempNode.ID);
            tempNode = tempNode.prev;
        }

        // treat transfer [station]
        ArrayList<String> nameList = new ArrayList<>(100);
        for(int j = path_result.size()-1 ; j>= 0 ; j--){
            String name = IDHashtable.get(path_result.get(j)).name;
            if(nameList.contains(name)){
                nameList.set(nameList.indexOf(name), "["+ name +"]");
            }else{
                nameList.add(name);
            }
        }
        for(String nm : nameList){
            path_sb.append(nm + " ");
        }
        path_sb.deleteCharAt(path_sb.length() -1);
        path_sb.append("\n" + final_cost);
        path_sb.append("\n");
    }

    private static DijNode DeleteMin(MyArrayList ROI) {
        int min = 1000000000;
        DijNode minNode = null;
        int minIdx = 1000000000;
        for (int i = 0; i <= ROI.size() - 1; i++) {
            if (min > ROI.get(i).cost) {
                minNode = ROI.get(i);
                min = minNode.cost;
                minIdx = i;
            }
        }
        try {
            ROI.remove(minIdx);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        return minNode;

    }

    private static void subwaySettingCommand(String path) throws IOException {
        // fileReader_2 : read subway data line by line
        // FileReader input korean broken successfully treated.
        // cf : https://shonm.tistory.com/307
        // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
        // FileOutputStream(file), "utf-8"));
        //
        //
        BufferedReader fileReader_2 = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

        String str;
        int vCnt = 0;

        // it's for making IDHashtable, NameHashtable
        while ((str = fileReader_2.readLine()).length() != 0) {
        

            String[] strArray = str.split(" ");
            // strArray[0] : ID, strArray[1] : name, strArray[2] : line

            

            // namehash : key is name , IDhash : key is ID

            // for transfer's duplication
            // if it's transfer point, there is an originalPoint
            if (NameHashtable.containsKey(strArray[1])) {
                // put new_info instance in original Point's info LinkedList
                Point originalPoint = NameHashtable.get(strArray[1]);
                originalPoint.add_info(strArray[0], strArray[2]);

            } else {
                Point newPoint = new Point(strArray[0], strArray[1], strArray[2]);
                NameHashtable.put(strArray[1], newPoint);
            }

            // key : ID, item = Vertex(no duplicate for transfer)
            Vertex newVertex = new Vertex(strArray[0], strArray[1], strArray[2]);
            IDHashtable.put(strArray[0], newVertex);

            // num of vertex counter
            vCnt++;
        }

        // it's for adjacency of vertices(Edge)
        // adjacency Array in hashtable, size = num of vertex
        adjacency = new Hashtable<>(vCnt);

        while ((str = fileReader_2.readLine()) != null) {
        
            
            String[] strArray = str.split(" ");

            // starting vertex's ID
            String sID = strArray[0];
            ArrayList<String> sID_list = NameHashtable.get(IDHashtable.get(sID).name).getID();
            // end Point
            // Vertex eVertex = IDHashtable.get(strArray[1]);
            edgeNode newEdge = new edgeNode(strArray[1], Integer.parseInt(strArray[2]));

            // if adjacency hasn't out degree vertex's ID
            if (adjacency.get(sID) == null) {
                
                // put newEdge and transferEdge
                for(int i = 0; i <= sID_list.size() -1 ; i++){
                    LinkedList<edgeNode> list = new LinkedList<>();
                    // Consider transfer Node
                    for(int j = 0; j <= sID_list.size() -1 ; j++){
                        if(sID_list.get(j).compareTo(sID_list.get(i)) != 0){
                            list.add(new edgeNode(sID_list.get(j),5));
                        }
                    }
                    if(sID_list.get(i).compareTo(sID) == 0){
                        list.add(newEdge);
                    }
                    adjacency.put(sID_list.get(i), list);
                }
                
            } 
            // if adjacency table already has out degree vertex's ID, just add newEdge
            else {
                adjacency.get(sID).add(newEdge);
            }

        }
        fileReader_2.close();
        
    }
}

// vertex information class
class V_info {
    private String id;
    private String line;

    public V_info(String id, String line) {
        this.id = id;
        this.line = line;
    }

    public String getID() {
        return this.id;
    }

    public String getLine() {
        return this.line;
    }
}

// For vertex hashtable searching class
class Point {
    LinkedList<V_info> info;
    String name;

    public Point(String Id, String nm, String line) {
        this.info = new LinkedList<>();
        this.info.add(new V_info(Id, line));
        this.name = nm;
    }

    public void add_info(String Id, String line) {
        this.info.add(new V_info(Id, line));
    }

    public ArrayList<String> getID() {
        ArrayList<String> result = new ArrayList<>(this.info.size());
        for (int i = 0; i <= this.info.size() - 1; i++) {
            result.add(this.info.get(i).getID());
        }
        return result;
    }

    public V_info get_transfer(String ID) {
        int i = 0;
        V_info tmp = this.info.get(i);
        V_info result = null;

        while (tmp.getID() != null) {
            if (tmp.getID() == ID) {
                result = tmp;
            }
        }

        if (result == null) {
            System.out.println("No subway station that has such line");
        }
        return result;
    }

}

// For represent edge by vertex to vertex
// what diff. between Point? => (distinguish transition station)
class Vertex {
    String name;
    V_info info;

    public Vertex(String ID, String nm, String line) {
        this.name = nm;
        V_info info = new V_info(ID, line);
        this.info = info;
    }
}

// have distance
class edgeNode {
    String ID;
    int distance;

    public edgeNode(String ID, int dist) {
        this.ID = ID;
        this.distance = dist;
    }
}

// For Dijkstra Algorithm
class DijNode {
    static final int INF = 1000000000;
    String ID;
    int cost;
    DijNode prev;

    public DijNode(String ID) {
        this.cost = INF;
        this.ID = ID;
        this.prev = null;
    }

    public DijNode(String ID, int cost) {
        this.cost = cost;
        this.ID = ID;
        this.prev = null;
    }

    public void initialize() {
        this.cost = INF;
        this.prev = null;
    }
}

// for searching based on ID
class MyArrayList extends ArrayList<DijNode> {
    public MyArrayList() {
        super();
    }

    public MyArrayList(int size) {
        super(size);
    }

    public DijNode searchNode(String ID) {
        for (int i = 0; i <= super.size() - 1; i++) {
            if (super.get(i).ID.compareTo(ID) == 0) {
                return super.get(i);
            }
        }

        return null;
    }
}