package Controller;

import java.io.*;
import java.util.*;

import Model.Model;

public class Controller {
    private Model m;

    public Controller(Model m) {
        this.m = m;
    }

    public void addSentence(String s) { m.addSentence(s); }

    public void removeSentence(String s) { m.removeSentence(s);}

    public void clearSentences() { m.setListOfSentences(new ArrayList<>()); }

    public void load(InputStream in) throws IOException{
        Scanner scanner = new Scanner(in);
        ArrayList<String> sentences = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                sentences.add(line);
            }
        }
        scanner.close();
        m.setListOfSentences(sentences);
    }

    public void save(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        List<String> sentences = m.getSentences();

        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                writer.println(sentence);
            }
        }
        writer.close();
    }

    public String getRandomSentence() { return m.getRandomSentence();}

    public boolean isListTraversed() { return m.isListTraversed(); }

    public ArrayList<String> getSentences(){ return m.getSentences(); }
}
