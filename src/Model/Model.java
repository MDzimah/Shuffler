package Model;

import java.util.*;

public class Model {
    private ArrayList<String> sentences;
    private int ind;
    private String lastOfPriorList;
    private boolean listTraversed;

    public Model() {
        this.sentences = new ArrayList<>();
        this.ind = 0;
        this.lastOfPriorList = null;
        this.listTraversed = false;
    }

    public void addSentence(String s) { this.sentences.add(s); }

    public void setListOfSentences(ArrayList<String> newList) { this.sentences = newList;}

    public boolean isListTraversed() { return this.listTraversed; }
    
    public String getRandomSentence() {
        if (sentences.isEmpty()) return "";
        this.ind = this.ind % sentences.size();
        if (this.ind ==  0) {
            Collections.shuffle(sentences);
            this.listTraversed = false;
            if (this.lastOfPriorList != null && this.sentences.get(0).equals(this.lastOfPriorList)) {
                String temp = this.sentences.get(0);
                this.sentences.set(0, this.sentences.get(this.sentences.size()-1));
                this.sentences.set(this.sentences.size()-1, temp);
            }
        }
        if (this.ind == sentences.size()-1) {
            this.lastOfPriorList = sentences.get(this.ind);
            this.listTraversed = true;
        }
        return sentences.get(this.ind++);
    }

    public ArrayList<String> getSentences() { return new ArrayList<>(this.sentences); }

    public void removeSentence(String s) { this.sentences.remove(s); }
}
