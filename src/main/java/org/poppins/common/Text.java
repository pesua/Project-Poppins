package org.poppins.common;

import java.util.List;

/**
 * Created by Anton Chernetskij
 */
public class Text {
    private List<Sentence> sentences;
    private String name;

    public Text(String name) {
        this.name = name;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    @Override
    public String toString() {
        return sentences.toString();
    }

    public int numSentences() {
        return sentences.size();
    }

    public String getName() {
        return name;
    }
}
