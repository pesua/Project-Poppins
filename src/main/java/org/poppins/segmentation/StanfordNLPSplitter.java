package org.poppins.segmentation;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.poppins.common.Sentence;
import org.poppins.common.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class StanfordNLPSplitter implements TextSplitter {

    private static final Logger LOGGER = Logger.getLogger(StanfordNLPSplitter.class.getName());
    private StanfordCoreNLP pipeline;

    public StanfordNLPSplitter() {
        LOGGER.info("Initializing text splitter");
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        pipeline = new StanfordCoreNLP(props);
        LOGGER.info("Text splitter has initialized");
    }

    public Text split(String text, String name) {
        LOGGER.info("Splitting text");

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> coreMaps = document.get(CoreAnnotations.SentencesAnnotation.class);

        List<Sentence> sentences = new ArrayList<Sentence>(coreMaps.size());
        for (CoreMap coreMap : coreMaps) {
            List<String> words = new ArrayList<String>();
            for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                words.add(word);
            }
            Sentence sentence = new Sentence(coreMap.toString());
            sentence.setWords(words);
            sentences.add(sentence);
        }

        LOGGER.info("Splitting finished");
        Text resultText = new Text(name);
        resultText.setSentences(sentences);
        return resultText;
    }

}
