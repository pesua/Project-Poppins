package org.poppins.transformation;

import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import org.poppins.common.Sentence;
import org.poppins.transformation.sentencetransformer.NotApplicableTransformerException;
import org.poppins.transformation.sentencetransformer.Question;
import org.poppins.transformation.sentencetransformer.QuestionToSubjectBuilder;
import org.poppins.transformation.sentencetransformer.SentenceTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class QuestionsBuilder {

    private static final Logger LOGGER = Logger.getLogger(QuestionsBuilder.class.getName());
    private SemanticParser semanticParser;
    private List<SentenceTransformer> builders;

    public QuestionsBuilder() {
        semanticParser = new SemanticParser();
        builders = Arrays.asList((SentenceTransformer) new QuestionToSubjectBuilder());
    }


    public List<Question> buildQuestions(List<Sentence> sentences) {
        List<Question> questions = new ArrayList<Question>();
        for (Sentence sentence : sentences) {
            SemanticGraph graph = semanticParser.parseStructure(sentence);
            for (SentenceTransformer sentenceTransformer : builders) {
                try {
                    Question question = sentenceTransformer.buildQuestion(sentence, graph);
                    questions.add(question);
                } catch (NotApplicableTransformerException e) {
                    LOGGER.info("Sadness: " + e.getMessage());
                }
            }
        }
        return questions;
    }

}
