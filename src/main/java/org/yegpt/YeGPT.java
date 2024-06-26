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

    /*
    These * SPECIAL values do not have an equivalent numeric value in ASCII charset.
    Therefore, they need special handling and given their own interpretations
    within the context of this language model.
     */
    private static final String SPECIAL = "–—‘’“”…' ";

    /*
    SV assigns unique values to characters outside the Standard ASCII charset in this language
    model. These characters, typically lacking representation in the standard American ASCII
    charset and often with Unicode values, are assigned values ranging from
    128 + index (based on their position in the string SPECIAL). By ensuring values start from 128,
     we avoid overlapping with the ASCII charset while providing unambiguous numeric values.
     */
    private static final int SV = 128;

    public YeGPT(String file) {
        this.file = new File(file);
    }

    public String getFileContent() {
        return this.fileContent;
    }

    public String getUniqueCharacters() {
        return this.uniqueCharacters;
    }

    public void train() {
        buildFileContent();
        buildUniqueCharacters();
    }

    public int[] encode() throws IOException {
        if (this.encodedContent != null) {
            return this.encodedContent;
        }
        if (this.fileContent == null) {
            throw new IOException("Data is null");
        }
        int[] a = new int[this.fileContent.length()];
        for (int i = 0; i < this.fileContent.length(); i++) {
            if (!(this.fileContent.charAt(i) == '\n')) {
                a[i] = this.charMap.get(this.fileContent.charAt(i));
            }
        }
        this.encodedContent = a;
        return this.encodedContent;
    }

    public String decode() throws IOException {
        if (this.decodedContent != null) {
            return this.decodedContent;
        }
        if (this.encodedContent == null) {
            throw new IOException("Encoded data is null");
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

    private void buildFileContent() {
        StringBuilder tcb = new StringBuilder();
        try (BufferedReader buf = new BufferedReader(new FileReader(this.file))) {
            String cur;
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

    private void buildUniqueCharacters() {
        // requires a language set with more than 10000 characters
        if (this.fileContent.length() > 10000) {
            Set<Character> set = new HashSet<>();
            for (char c : this.fileContent.toCharArray()) {
                set.add(c);
            }
            StringBuilder uniq = new StringBuilder();
            for (Character c : set) {
                uniq.append(c);
            }
            this.uniqueCharacters = uniq.toString();
            System.out.println(this.uniqueCharacters);

            // create mapping from characters to integers
            // upper case values are treated the same as their lowercase counterparts
            Map<Character, Integer> s = new HashMap<>();
            Map<Integer, Character> i = new HashMap<>();
            for (Character c : this.uniqueCharacters.toCharArray()) {
                int nVal = (int) c;
                System.out.println("char: " + c + " - value: " + (int) c);
                if (isSpecial(c)) {
                    int index = SPECIAL.indexOf(c);
                    s.put(c, SV + index);
                    i.put(SV + index, c);
                } else {
                    s.put(c, nVal);
                    i.put(nVal, c);
                }
            }
            this.charMap = s;
            this.intMap = i;
        }
    }
}
