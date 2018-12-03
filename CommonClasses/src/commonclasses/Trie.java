package commonclasses;

import java.io.Serializable;

public class Trie implements Serializable {

    private class TrieNode implements Serializable
    {
        private static final int ALPHABET_SIZE = 128;
        private char letter;
        private boolean isEndOfWord;
        private TrieNode[] children = new TrieNode[ALPHABET_SIZE];

        TrieNode(char letter){
            isEndOfWord = false;
            this.letter = letter;
            for (int i = 0; i < ALPHABET_SIZE; i++)
                children[i] = null;
        }
    };

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode(' ');
    }

    // If not present, inserts key into trie
    // If the key is prefix of trie node,
    // just marks leaf node
    public void insert(String key)
    {
        int level;
        int length = key.length();
        int index;

        TrieNode currentNode = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level);
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode(key.charAt(level));
            }

            currentNode = currentNode.children[index];
        }

        // mark last node as leaf
        currentNode.isEndOfWord = true;
    }

    // Returns true if key presents in trie, else false
    public boolean search(String key)
    {
        int level;
        int length = key.length();
        int index;
        TrieNode currentNode = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level);

            if (currentNode.children[index] == null)
                return false;

            currentNode = currentNode.children[index];
        }

        return (currentNode != null && currentNode.isEndOfWord);
    }
}
