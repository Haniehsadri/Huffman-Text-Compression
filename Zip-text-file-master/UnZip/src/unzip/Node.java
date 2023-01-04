/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unzip;

public class Node {

    String word;
    Node left;
    Node right;
    int freq;

    public Node(String word, Node left, Node right, int freq) {
        this.word = word;
        this.left = left;
        this.right = right;
        this.freq = freq;
    }

}
