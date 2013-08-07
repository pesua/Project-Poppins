package org.poppins.transformation;

import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import org.junit.Ignore;
import org.junit.Test;
import org.poppins.common.Sentence;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 * <p/>
 * Helper class for analyzing semantic graph
 */
@Ignore
public class SemanticParserTest {

    private String[] sentences = new String[]{
            "Katharine Houghton Hepburn (May 12, 1907 – June 29, 2003) was an American actress of film, stage, and television.",
            "Her work came in a range of genres, from screwball comedy to literary drama, and she received four Academy Awards for Best Actress—a record for any performer.",
            "The season was nominated for nineteen Emmy Awards, four Golden Globe Awards, six Satellite Awards and fifty-seven other awards"
    };

    @Test
    public void testParseStructure() throws Exception {
        Logger logger = Logger.getLogger("CoolLog");
        SemanticParser parser = new SemanticParser();
        for (int i = 0; i < sentences.length; i++) {
            String s = parser.parseStructure(new Sentence(sentences[i])).toString();
            logger.info(s);
        }
    }

    @Test
    public void serializeTestGraphs() {
        final String[][] sentences = new String[][]{
                {"fox", "The quick brown fox jumps over the lazy dog."},
                {"actress", "Katharine Houghton Hepburn (May 12, 1907 – June 29, 2003) was an American actress of film, stage, and television."},
                {"war", "The Cleomenean War was fought by Sparta and its ally, Elis, against the Achaean League and Macedon."},
                {"nlp", "The Stanford NLP Group makes parts of our Natural Language Processing software available to the public."}
        };
        for (int i = 0; i < sentences.length; i++) {
            String[] sentence = sentences[i];
            serializeGraph(sentence[0], sentence[1]);
        }
    }

    private void serializeGraph(String name, String sentence) {
        SemanticParser parser = new SemanticParser();
        SemanticGraph graph = parser.parseStructure(new Sentence(sentence));
        FileOutputStream out = null;
        try {
            File file = new File("src/test/resources/graphs/" + name + ".data");
            file.createNewFile();
            out = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
