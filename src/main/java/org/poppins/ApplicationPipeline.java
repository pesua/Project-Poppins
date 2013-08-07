package org.poppins;

import org.poppins.common.Sentence;
import org.poppins.common.Text;
import org.poppins.segmentation.StanfordNLPSplitter;
import org.poppins.segmentation.TextSplitter;
import org.poppins.summarizing.LexRankSummarizer;
import org.poppins.summarizing.Summarizer;
import org.poppins.transformation.QuestionsBuilder;
import org.poppins.transformation.sentencetransformer.Question;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class ApplicationPipeline {

    private static final Logger LOGGER = Logger.getLogger(ApplicationPipeline.class.getName());
    private Summarizer summarizer;
    private TextSplitter splitter;
    private QuestionsBuilder questionsBuilder;

    public ApplicationPipeline() {
        splitter = new StanfordNLPSplitter();
        summarizer = new LexRankSummarizer();
        questionsBuilder = new QuestionsBuilder();
    }

    public List<Question> process(String s, String name) {
        LOGGER.info("Starting \"" + name + "\" processing.");
        long start = System.currentTimeMillis();
        Text text = splitter.split(s, name);
        if (text.numSentences() < 6) {
            throw new RuntimeException("The text is too small to apply extractive summary");
        }
        double summarySize = 0.3;
        List<Sentence> summary = summarizer.summarize(text, summarySize);
        List<Question> questions = questionsBuilder.buildQuestions(summary);
        long finish = System.currentTimeMillis();
        LOGGER.info(String.format(
                "Processed text %s of %d sentences in %d ms", name, text.numSentences(), (finish - start)));
        return questions;
    }

    public Summarizer getSummarizer() {
        return summarizer;
    }

    public void setSummarizer(Summarizer summarizer) {
        this.summarizer = summarizer;
    }

    public TextSplitter getSplitter() {
        return splitter;
    }

    public void setSplitter(TextSplitter splitter) {
        this.splitter = splitter;
    }

    public QuestionsBuilder getQuestionsBuilder() {
        return questionsBuilder;
    }

    public void setQuestionsBuilder(QuestionsBuilder questionsBuilder) {
        this.questionsBuilder = questionsBuilder;
    }
}
