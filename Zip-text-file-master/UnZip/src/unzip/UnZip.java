/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unzip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Scanner;

public class UnZip {

    static int heapSize;

    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("enter the location of file which you want to unZip :");
        String path1 = s.next();
        System.out.println("enter the location where the unzipped file should be placed:");
        String path2 = s.next();
        if (path1.equals(path2)) {
            System.out.println("two locations should be different!!!");
        } else {
            UnZip(path1, path2);
            System.out.println("unzip completed!");
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

    public static void UnZip(String path1, String path2) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(path1);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(path2);
        BufferedWriter bw = new BufferedWriter(fw);
        Node root = MakeHuffmanTreeFromZipedFile(br);
        int n = Integer.parseInt(br.readLine());
        String codedText = UnZipStep1(br);
        UnZipStep2(root, codedText, bw, n);
        bw.close();
        br.close();
        fw.close();
        fr.close();
    }

    public static Node MakeHuffmanTreeFromZipedFile(BufferedReader br) throws IOException {
        int n = Integer.parseInt(br.readLine());
        Node[] words = new Node[n];
        String[] subStrings;
        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            subStrings = line.split(" ");
            words[i] = new Node(subStrings[0], null, null, Integer.parseInt(subStrings[1]));
        }

        return Huffman(words);
    }

    public static String UnZipStep1(BufferedReader br) throws IOException {
        String CodedText = "";
        int read;
        while ((read = br.read()) != -1) {
            CodedText += Convert10RadixTo2Radix(read);
        }
        return CodedText;
    }

    public static void UnZipStep2(Node root, String codedText, BufferedWriter bw, int n) throws IOException {
        Node NodeCurrent = root;
        int length = codedText.length();
        for (int i = 0; i < length - n; i++) {
            if (IsLeaf(NodeCurrent)) {
                if (NodeCurrent.word.equals("khattebaad") || NodeCurrent.word.equals("fasele")) {
                    if (NodeCurrent.word.equals("khattebaad")) {
                        bw.newLine();
                    } else {
                        bw.write(" ");
                    }
                } else {
                    bw.write(NodeCurrent.word);
                }
                NodeCurrent = root;
            }
            char CharCurrent = codedText.charAt(i);
            if (CharCurrent == '0') {
                NodeCurrent = NodeCurrent.left;
            } else {
                NodeCurrent = NodeCurrent.right;
            }
        }
    }

    public static boolean IsLeaf(Node node) {
        return (node.left == null && node.right == null);
    }

    public static String Convert10RadixTo2Radix(int number) {
        String strNumber = String.valueOf(number);
        String result = Integer.toString(Integer.parseInt(strNumber, 10), 2);
        int n;
        if ((n = result.length()) != 8) {
            for (int i = 0; i < 8 - n; i++) {
                result = "0" + result;
            }
        }
        return result;
    }
}
