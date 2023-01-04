/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ZIP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ZIP {

    static int heapSize;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("enter the location of file which you want to zip :");
        String path1 = s.next();
        System.out.println("enter the location where the zipped file should be saved: :");
        String path2 = s.next();
        if (path1.equals(path2)) {
            System.out.println("the location of 2 files must be different!!!");
        } else {
            Zip(path1, path2);
            System.out.println("zip completed!");
        }
    }

    public static int parent(int i) {
        return i / 2;
    }

    public static int left(int i) {
        return 2 * i;
    }

    public static int right(int i) {
        return 2 * i + 1;
    }

    public static void MinHeapify(Node[] A, int i) {
        int smallest;
        int l = left(i);
        int r = right(i);
        if (l < heapSize && A[l].freq < A[i].freq) {
            smallest = l;
        } else {
            smallest = i;
        }
        if (r < heapSize && A[r].freq < A[smallest].freq) {
            smallest = r;
        }
        if (smallest != i) {
            Node temp;
            temp = A[smallest];
            A[smallest] = A[i];
            A[i] = temp;
            MinHeapify(A, smallest);
        }
    }

    public static void BuildMinHeap(Node A[]) {
        heapSize = A.length;
        for (int i = (A.length - 1) / 2; i >= 0; i--) {
            MinHeapify(A, i);
        }
    }

    public static Node HeapExtractMin(Node A[]) {
        Node min;
        if (heapSize < 1) {
            System.out.println("heap underflow");
            System.exit(0);
            min = null;
        } else {
            min = A[0];
            A[0] = A[heapSize - 1];
            heapSize--;
            MinHeapify(A, 0);
        }
        return min;
    }

    public static void HeapDecreaseKey(Node[] A, int i, Node key) {
        if (key.freq > A[i].freq) {
            System.out.println("error in decrease key");
        }
        A[i] = key;
        while (i > 0 && A[parent(i)].freq > A[i].freq) {
            Node temp = A[i];
            A[i] = A[parent(i)];
            A[parent(i)] = temp;
            i = parent(i);
        }
    }

    public static void MinHeapInsert(Node A[], Node key) {
        heapSize++;
        A[heapSize - 1] = new Node(null, null, null, Integer.MAX_VALUE);
        HeapDecreaseKey(A, heapSize - 1, key);
    }

    public static Node Huffman(Node[] C) {
        int n = C.length;
        BuildMinHeap(C);
        for (int i = 0; i <= n - 2; i++) {
            Node z = new Node(null, null, null, -1);
            z.left = HeapExtractMin(C);
            z.right = HeapExtractMin(C);
            z.freq = z.left.freq + z.right.freq;
            MinHeapInsert(C, z);
        }
        return HeapExtractMin(C);
    }

    public static void GetCodes(Node root, String code, String plus, HashMap<String, String> map1) {
        if (root != null) {
            code += plus;
            if (root.left == null && root.right == null) {
                map1.put(root.word, code);
            } else {
                if (root.left != null) {
                    GetCodes(root.left, code, "0", map1);
                }
                if (root.right != null) {
                    GetCodes(root.right, code, "1", map1);
                }
            }
            return;
        }
        System.exit(0);

    }

    public static Node[] GetNumberOfWords(String path) throws FileNotFoundException, IOException {
        HashMap<String, Integer> map = new HashMap();

        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] subStrings;
        int lineCouner = 0;
        while ((line = br.readLine()) != null) {
            lineCouner++;
            subStrings = null;
            subStrings = line.split(" ");
            for (String splited : subStrings) {

                if (map.get(splited) == null) {
                    map.put(splited, 1);
                } else {
                    map.put(splited, map.get(splited) + 1);
                }

            }
            if (map.get("fasele") == null) {
                map.put("fasele", subStrings.length - 1);
            } else {
                map.put("fasele", map.get("fasele") + subStrings.length - 1);
            }
        }
        map.put("khattebaad", lineCouner);
        br.close();
        fr.close();
        Node[] words = new Node[map.size()];
        int i = 0;
        for (String word : map.keySet()) {
            words[i] = new Node(word, null, null, map.get(word));
            i++;
        }
        return words;
    }

    public static void Zip(String path1, String path2) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(path1);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(path2);
        BufferedWriter bw = new BufferedWriter(fw);
        String codedText = ZipStep1(path1, br, bw);
        ZipStep2(bw, codedText);
        bw.close();
        br.close();
        fr.close();
        fw.close();
    }

    public static String ZipStep1(String path1, BufferedReader br, BufferedWriter bw) throws FileNotFoundException, IOException {
        Node[] words = GetNumberOfWords(path1);
        SaveTableInZipFile(words, bw);
        Node root = Huffman(words);
        HashMap<String, String> codeMap = new HashMap();
        GetCodes(root, "", "", codeMap);
        String line;
        String[] subStrings;
        String codedText = "";
        while ((line = br.readLine()) != null) {
            subStrings = null;
            subStrings = line.split(" ");
            int counter = 0;
            for (String splited : subStrings) {
                if (counter != 0) {
                    codedText += codeMap.get("fasele");
                }
                codedText += codeMap.get(splited);
                counter++;
            }
            codedText += codeMap.get("khattebaad");
        }
        return codedText;
    }

    public static void ZipStep2(BufferedWriter bw, String codedText) throws IOException {
        int n = 8 - (codedText.length() % 8);
        bw.write(String.valueOf(n));
        bw.newLine();
        for (int j = 0; j < n; j++) {
            codedText += "0";
        }
        int k = 0;
        while (k != codedText.length()) {
            String subString = codedText.substring(k, k + 8);
            int asci = Integer.parseInt(subString, 2);
            bw.write((char) asci);
            k += 8;
        }

    }

    public static void SaveTableInZipFile(Node[] words, BufferedWriter bw) throws IOException {
        bw.write(String.valueOf(words.length));
        bw.newLine();
        for (Node word : words) {
            bw.write(word.word + " " + word.freq);
            bw.newLine();
        }
    }

}
