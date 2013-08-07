package org.poppins.transformation;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.poppins.common.Sentence;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class SemanticParser {

    private static final Logger LOGGER = Logger.getLogger(SemanticParser.class.getName());

    private StanfordCoreNLP pipeline;
    private Class<? extends CoreAnnotation<SemanticGraph>> graphType =
            SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class;

    public SemanticParser() {
        LOGGER.info("Initializing structure parser");
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref"); //todo remove ner & dcoref
        pipeline = new StanfordCoreNLP(props);
    }

    public StanfordCoreNLP getPipeline() {
        return pipeline;
    }

    public void setPipeline(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    public Class<? extends CoreAnnotation<SemanticGraph>> getGraphType() {
        return graphType;
    }

    public void setGraphType(Class<? extends CoreAnnotation<SemanticGraph>> graphType) {
        this.graphType = graphType;
    }

    public SemanticGraph parseStructure(Sentence sentence) {
        LOGGER.info("Processing sentence \"" + sentence + "\"");
        long start = System.currentTimeMillis();
        Annotation annotation = pipeline.process(sentence.getSentence());
        CoreMap coreMap = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        long finish = System.currentTimeMillis();
        LOGGER.info("Sentence processed by " + (finish - start) + "ms");

        StringBuilder builder = new StringBuilder();
        SemanticGraph dependencies = coreMap.get(graphType);
        builder.append("\n").append(graphType.getName()).append(":\n").append(dependencies.toString());
        LOGGER.info("Perse tree:" + dependencies.toString());
        return dependencies;
    }


}
