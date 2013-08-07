package org.poppins.transformation.sentencetransformer;

import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import org.poppins.common.Sentence;

/**
 * Created by Anton Chernetskij
 */
public interface SentenceTransformer {

    public Question buildQuestion(Sentence sentence, SemanticGraph dependencies) throws NotApplicableTransformerException;

}
