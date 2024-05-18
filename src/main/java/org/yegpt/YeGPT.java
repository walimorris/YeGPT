package org.yegpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YeGPT {
    private final File file;
    private String fileContent;
    private Map<Character, Integer> charMap;
    private Map<Integer, Character> intMap;
    private String uniqueCharacters;
    private int[] encodedContent;
    private String decodedContent;

    private static final String SPECIAL = "–—‘’“”!\"#$&…'()*+,-./:;?[] ";

    public YeGPT(String file) {
        this.file = new File(file);
    }

    public void train() {
        buildFileContent();
        buildUniqueCharacters();
    }

    public String getFileContent() {
        return this.fileContent;
    }

    private void buildFileContent() {
        if (this.file.canRead()) {
            StringBuilder tcb = new StringBuilder();
            BufferedReader buf;

            try {
                String cur;
                buf = new BufferedReader(new FileReader(this.file));
                while((cur = buf.readLine()) != null) {
                    tcb.append(cur).append("\n");
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
            if (!tcb.isEmpty()) {
                this.fileContent = tcb.toString();
            }
        }
    }

    private void buildUniqueCharacters() {
        // requires a language set with more than 10000 characters
        if (this.fileContent.length() > 10000) {
            Set<Character> set = new HashSet<>();
            StringTokenizer t = new StringTokenizer(this.fileContent, "\n");

            Iterator<Object> it = t.asIterator();
            while (it.hasNext()) {
                String token = (String) it.next();
                for (char c : token.toCharArray()) {
                    set.add(c);
                }
            }
            StringBuilder uniq = new StringBuilder();
            for (Character c : set) {
                uniq.append(c);
            }
            this.uniqueCharacters = uniq.toString();

            // create mapping from characters to integers
            // upper case values are treated the same as their lowercase counterparts
            Map<Character, Integer> s = new HashMap<>();
            Map<Integer, Character> i = new HashMap<>();
            for (Character c : this.uniqueCharacters.toCharArray()) {
                int nVal = Character.getNumericValue(c);
                if (isSpecial(c)) {
                    int index = SPECIAL.indexOf(c) + 66;
                    s.put(c, -1 + index);
                    i.put(-1 + index, c);
                } else {
                    s.put(c, nVal);
                    i.put(nVal, c);
                }
            }
            this.charMap = s;
            this.intMap = i;
        }
    }

    public int[] encode() throws IOException {
        if (this.fileContent == null) {
            throw new IOException("Data is null");
        } else if (this.encodedContent != null) {
            return this.encodedContent;
        }
        int[] a = new int[this.fileContent.length()];
        for (int i = 0; i < this.fileContent.length(); i++) {
            System.out.println(this.fileContent.charAt(i));
            if (this.fileContent.charAt(i) == '\n') {
                continue;
            }
            a[i] = this.charMap.get(this.fileContent.charAt(i));
        }
        this.encodedContent = a;
        return this.encodedContent;
    }

    public String decode() throws IOException {
        if (this.encodedContent == null) {
            throw new IOException("Encoded data is null");
        } else if (this.decodedContent != null) {
            return this.decodedContent;
        }
        StringBuilder str = new StringBuilder();
        for (int i : this.encodedContent) {
            str.append(intMap.get(i));
        }
        this.decodedContent = str.toString();
        return this.decodedContent;
    }

    private boolean isPureAscii(Character c) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(c);
    }

    private boolean isSpecial(Character c) {
        return SPECIAL.contains(String.valueOf(c));
    }
}
