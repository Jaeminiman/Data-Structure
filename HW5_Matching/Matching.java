import java.io.*;
import java.util.LinkedList;

public class Matching {

    // Bag of words (words database)
    static hashTable<Input, Tuple<Integer, Integer>> hashtable;

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        hashtable = new hashTable<>(100);
        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("QUIT") == 0)
                    break;
                command(input);
            } catch (IOException e) {
                System.out
                        .println(e.toString());
            }
        }
    }

    private static void command(String input) throws IOException {

        if (input.charAt(0) == '@') {
            indexCommand(input);
        } else if (input.charAt(0) == '?') {

            patternCommand(input);
        } else {
            dataCommand(input);
        }

    }

    // input e.g.) @60 -> In hashtable, get Tree Structure by using hashcode 60 (total hash size = 100)
    private static void indexCommand(String input) {
        char[] arr = input.toCharArray();
        StringBuilder index_number = new StringBuilder();
        for (int i = 2; i <= arr.length - 1; i++) {
            index_number.append(String.valueOf(arr[i]));
        }
        AVLTree<Input, Tuple<Integer, Integer>> tree = hashtable.getTree(Integer.parseInt(index_number.toString()));

        StringBuilder sb = new StringBuilder();
        if (tree.getRoot().item != null) {
            preOrder(tree.getRoot(), sb);

            System.out
                    .println(sb.substring(0, sb.length() - 1));
        } else {
            System.out
                    .println("EMPTY");
        }
    }

    // In preOrder way, print all the @60 Tree's Nodes
    private static void preOrder(AVLNode<Input, Tuple<Integer, Integer>> tNode, StringBuilder sb) {

        if (tNode.item == null) {
            return;
        }
        sb.append(tNode.item.item + " ");
        preOrder(tNode.left, sb);
        preOrder(tNode.right, sb);
    }

    // e.g.) Input : ? boyboy => Output : row, column of that word 'boyboy' in the text file. like, (1,2) (3,1)
    private static void patternCommand(String input) {
        StringBuilder idx_total = new StringBuilder();
        char[] arr = input.toCharArray();
        StringBuilder pattern = new StringBuilder();
        int epoch = arr.length - 7;
        String result = "";
        LinkedList<Tuple<Integer, Integer>> indexList;

        // why cnt? because Database is comprised with only 6 length string. But we want to find a pattern more than 6 length. like "algorithm"
        for (int cnt = 0; cnt < epoch; cnt++) {

            for (int i = 2; i <= 7; i++) {
                pattern.append(arr[i + cnt]);
            }

            // first 6 words -> Its matching is a more critical view point than cnt >0. 
            if (cnt == 0) {
                indexList = hashtable.search(new Input(pattern.toString()));

                if (indexList == null) {
                    result = "(0, 0)";
                    break;
                }
                for (Tuple<Integer, Integer> idx_tuple : indexList) {
                    String idx_string = "(" + String.valueOf(idx_tuple.getA()) + ", " +
                            String.valueOf(idx_tuple.getB()) + ")";
                    idx_total.append(idx_string + " ");
                }

            } else if (cnt < epoch) {
                indexList = hashtable.search(new Input(pattern.toString()));
                if (indexList == null) {
                    result = "(0, 0)";
                    break;
                }
                for (Tuple<Integer, Integer> idx_tuple : indexList) {
                    String ex_string = "(" + String.valueOf(idx_tuple.getA()) + ", " + String.valueOf(
                            idx_tuple.getB() - cnt) + ")";

                    if (result.contains(ex_string)) {
                        idx_total.append(ex_string + " ");
                    }
                    if (idx_total.toString() == null) {
                        result = "(0, 0)";
                        break;
                    }
                }
            }

            result = idx_total.substring(0, idx_total.length() - 1).toString();
            
            // initialize for loop
            idx_total.delete(0, idx_total.length());
            pattern.delete(0, pattern.length());
        }

        System.out
                .println(result);
    }

    // e.g.) input : < data.txt => make all the words whose length is 6 in Database
    private static void dataCommand(String input) throws IOException {
        char[] arr = input.toCharArray();
        String filename = "";
        for (int i = 2; i <= arr.length - 1; i++) {
            filename = filename + String.valueOf(arr[i]);
        }
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        // buffer size control okay ex 16*1024 = 16KB

        // database Structure 
        hashtable = new hashTable<>(100);
        String str;
        String dictionary_string = "";
        int line_cnt = 0;
        while ((str = reader.readLine()) != null) {
            line_cnt++;
            char[] stringArray = str.toCharArray();
            for (int column_cnt = 0; column_cnt <= stringArray.length - 6; column_cnt++) {
                for (int j = 0; j < 6; j++) {
                    dictionary_string = dictionary_string + String.valueOf(
                            stringArray[column_cnt + j]);
                }

                // why Tuple ? < row, col > -> for indexing of words
                Tuple<Integer, Integer> idx_tuple = new Tuple<>(line_cnt, column_cnt + 1);
                hashtable.insert(new Input(dictionary_string), idx_tuple);
                dictionary_string = "";

            }
        }
        reader.close();

    }
}

// Generic type HashTable
class hashTable<K extends Comparable<K>, V> {
    private AVLTree<K, V>[] table;
    int numItems;

    public hashTable(int n) {
        table = new AVLTree[n];
        numItems = 0;
        for (int i = 0; i < n; i++) {
            table[i] = new AVLTree<>();
        }
    }

    private int hash(K newVal) {
        return newVal.hashCode();
    }

    public AVLTree<K, V> getTree(int slot) {
        return table[slot];
    }

    public void insert(K newVal, V idx) {
        int slot = hash(newVal);
        table[slot].insert(newVal, idx);
        numItems++;
    }

    public LinkedList<V> search(K val) {
        int slot = hash(val);
        if (table[slot].isEmpty()) {
            return null;
        } else {
            return table[slot].search(val);
        }

    }

    public boolean isEmpty() {
        return numItems == 0;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = new AVLTree<>();
        }
        numItems = 0;
    }
}

// It's for Hashcode 
class Input implements Comparable<Input> {
    private int ascii;
    String item;

    public Input(String input) {
        this.item = input;
        this.ascii = this.getAscii();
    }

    @Override
    public int compareTo(Input another) {
        if (this.item.compareTo(another.item) < 0) {
            return -1;
        } else if (this.item.compareTo(another.item) == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private int getAscii() {
        char[] arr = this.item
                .toCharArray();
        int num = 0;
        for (char ch : arr) {
            num += ch;
        }
        return num;
    }

    @Override
    public int hashCode() {
        return this.ascii % 100;
    }
}

class AVLNode<K extends Comparable<K>, V> {
    public K item;
    public LinkedList<V> bin;
    public AVLNode<K, V> left,
            right;
    public int height;

    public AVLNode(K newItem, V idx) {
        item = newItem;
        left = right = AVLTree.NIL;
        bin = new LinkedList<>();
        bin.add(idx);
        height = 1;
    }

    public AVLNode(K newItem, AVLNode<K, V> leftChild, AVLNode<K, V> rightChild, int h) {
        item = newItem;
        bin = new LinkedList<>();
        right = rightChild;
        left = leftChild;
        height = h;
    }

    public AVLNode(K newItem, V idx, AVLNode<K, V> leftChild, AVLNode<K, V> rightChild, int h) {
        item = newItem;
        bin = new LinkedList<>();
        bin.add(idx);
        right = rightChild;
        left = leftChild;
        height = h;
    }
}

class AVLTree<K extends Comparable<K>, V> {
    private AVLNode<K, V> root;
    static final AVLNode NIL = new AVLNode<>(null, null, null, 0);

    public AVLTree() {
        root = NIL;
    }
    
    public AVLNode<K, V> getRoot() {
        return root;
    }

    public LinkedList<V> search(K x) {
        AVLNode<K, V> tmp = searchItem(root, x);
        if (tmp == NIL) {
            return null;
        } else {
            return tmp.bin;
        }
    }

    public AVLNode<K, V> searchItem(AVLNode<K, V> tNode, K x) {
        if (tNode == NIL) {
            return NIL;
        } else if (x.compareTo(tNode.item) < 0) {
            return searchItem(tNode.left, x);
        } else if (x.compareTo(tNode.item) > 0) {
            return searchItem(tNode.right, x);
        } else {
            return tNode;
        }
    }

    public void insert(K x, V idx) {
        root = insertItem(root, x, idx);
    }

    public AVLNode<K, V> insertItem(AVLNode<K, V> tNode, K x, V idx) {

        if (tNode == NIL) {
            tNode = new AVLNode<K, V>(x, idx, NIL, NIL, 1);
        } else if (x.compareTo(tNode.item) < 0) {
            tNode.left = insertItem(tNode.left, x, idx);
            tNode.height = 1 + Math.max(tNode.right.height, tNode.left.height);
            int type = needBalance(tNode);
            if (type != NO_NEED) {
                tNode = balanceAVL(tNode, type);
            }
        } else if (x.compareTo(tNode.item) > 0) {
            tNode.right = insertItem(tNode.right, x, idx);
            tNode.height = 1 + Math.max(tNode.right.height, tNode.left.height);
            int type = needBalance(tNode);
            if (type != NO_NEED) {
                tNode = balanceAVL(tNode, type);
            }
        } else if (x.compareTo(tNode.item) == 0) {
            tNode.bin
                    .add(idx);
        }

        return tNode;
    }

    private AVLNode<K, V> balanceAVL(AVLNode<K, V> tNode, int type) {
        AVLNode returnNode = NIL;
        switch (type) {
            case LL:
                returnNode = rightRotate(tNode);
                break;
            case LR:
                tNode.left = leftRotate(tNode.left);
                returnNode = rightRotate(tNode);
                break;
            case RL:
                tNode.right = rightRotate(tNode.right);
                returnNode = leftRotate(tNode);
                break;
            case RR:
                returnNode = leftRotate(tNode);
                break;
            default:
                break;

        }
        return returnNode;
    }

    private AVLNode<K, V> rightRotate(AVLNode<K, V> tNode) {
        AVLNode<K, V> leftChild = tNode.left;

        if (leftChild == NIL) {
            System.out
                    .println(tNode.item + "'s Lchild shouldn't be NIL");
        } else {
            AVLNode<K, V> leftrightChild = leftChild.right;
            leftChild.right = tNode;
            tNode.left = leftrightChild;

            tNode.height = Math.max(tNode.left.height, tNode.right.height) + 1;
            leftChild.height = Math.max(leftChild.left.height, leftChild.right.height) + 1;
        }
        return leftChild;
    }

    private AVLNode<K, V> leftRotate(AVLNode<K, V> tNode) {

        AVLNode<K, V> RChild = tNode.right;
        if (RChild == NIL) {
            System.out
                    .println(tNode.item + "'s Rchild shouldn't be NIL");
        } else {
            AVLNode<K, V> RLChild = RChild.left;
            RChild.left = tNode;
            tNode.right = RLChild;

            tNode.height = Math.max(tNode.left.height, tNode.right.height) + 1;
            RChild.height = Math.max(RChild.left.height, RChild.right.height) + 1;
        }
        return RChild;

    }

    private final int LL = 1,
            LR = 2,
            RL = 3,
            RR = 4,
            NO_NEED = 0,
            ILLEGAL = -1;

    private int needBalance(AVLNode<K, V> tNode) {
        int type = ILLEGAL;
        if (tNode.left.height - tNode.right.height >= 2) { // L
            if (tNode.left.left.height - tNode.left.right.height >= 0) {
                // LL
                type = LL;
            } else {
                type = LR;
            }
        } else if (tNode.right.height - tNode.left.height >= 2) {
            // R type
            if (tNode.right.right.height - tNode.right.left.height >= 0) {
                // RR type
                type = RR;
            } else {
                type = RL;
            }
        } else {
            type = NO_NEED;
        }

        return type;
    }

    public boolean isEmpty() {
        return root == NIL;
    }
}

class Tuple<A, B> {

    private final A a;
    private final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Tuple<A, B> of(final A a, final B b) {
        return new Tuple<>(a, b);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}
