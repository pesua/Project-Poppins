package org.poppins.transformation.sentencetransformer;

import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.poppins.common.Sentence;
import org.poppins.common.Text;
import org.poppins.segmentation.StanfordNLPSplitter;
import org.poppins.segmentation.TextSplitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Anton Chernetskij
 */
@RunWith(value = Parameterized.class)
public class QuestionToSubjectBuilderTest {

    private SemanticGraph graph;
    private QuestionToSubjectBuilder builder;
    private Sentence sentence;
    private String question;
    private String answer;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {"src/test/resources/graphs/fox.data", "The quick brown fox jumps over the lazy dog.", "What jumps over the lazy dog?", "quick brown fox"},
                {"src/test/resources/graphs/actress.data", "Katharine Houghton Hepburn (May 12, 1907 – June 29, 2003) was an American actress of film, stage, and television.", "What was an american actress of film, stage, and television?", "Katharine Houghton Hepburn"},
                {"src/test/resources/graphs/war.data", "The Cleomenean War was fought by Sparta and its ally, Elis, against the Achaean League and Macedon.", "What was fought by sparta and its ally, elis, against the achaean league and macedon?", "Cleomenean War"},
                {"src/test/resources/graphs/nlp.data", "The Stanford NLP Group makes parts of our Natural Language Processing software available to the public.", "What makes parts of our natural language processing software available to the public?", "Stanford NLP Group"}
        };
        return Arrays.asList(data);
    }

    public QuestionToSubjectBuilderTest(String graphFile, String sentence, String question, String answer) {
        try {
            FileInputStream inputStream = new FileInputStream(graphFile);
            ObjectInputStream stream = new ObjectInputStream(inputStream);
            graph = (SemanticGraph) stream.readObject();

            builder = new QuestionToSubjectBuilder();

            TextSplitter splitter = new StanfordNLPSplitter();
            Text text = splitter.split(sentence, "text");
            this.sentence = text.getSentences().get(0);
            this.question = question;
            this.answer = answer;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBuildQuestion() throws Exception {
        System.out.println(graph);
        Question question = builder.buildQuestion(sentence, graph);
        System.out.println(question.getQuestion());
        System.out.println(question.getAnswer());
        System.out.println("####");
        Assert.assertEquals(this.question, question.getQuestion());
        Assert.assertEquals(answer, question.getAnswer());
    }
}
