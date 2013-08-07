package org.poppins.transformation.sentencetransformer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import org.poppins.common.Sentence;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class QuestionToSubjectBuilder implements SentenceTransformer {

    private Logger LOGGER = Logger.getLogger(QuestionToSubjectBuilder.class.getName());

    public Question buildQuestion(Sentence sentence, SemanticGraph dependencies) throws NotApplicableTransformerException {
        String sentenceText = sentence.getSentence();
        IndexedWord subject = getSubject(sentenceText, dependencies);
        String pos = subject.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        if ("PRP".equals(pos)) {
            throw new NotApplicableTransformerException("Personal noun as subject in sentence \"" + sentenceText + "\" ");
        }

        String question = getQuestion(sentence, dependencies, subject);
        String answer = getAnswer(sentenceText, dependencies, subject);

        return new Question(question, answer);
    }

    protected IndexedWord getSubject(String sentence, SemanticGraph dependencies) throws NotApplicableTransformerException {
        Collection<IndexedWord> roots = dependencies.getRoots();
        IndexedWord subject = null;
        for (IndexedWord root : roots) {
            List<IndexedWord> rootChildes = dependencies.getChildList(root);
            for (IndexedWord child : rootChildes) {
                SemanticGraphEdge edge = dependencies.getEdge(root, child);
                GrammaticalRelation relation = edge.getRelation();
                if (isSubject(relation.getShortName())) {
                    subject = edge.getDependent();
                    LOGGER.info("Found subject " + subject);
                }
            }
        }
        if (subject == null) {
            throw new NotApplicableTransformerException("Bad subject in sentence \"" + sentence + "\" ");
        }
        return subject;
    }

    protected String getQuestion(Sentence sentence, SemanticGraph dependencies, IndexedWord subject) {
        List<IndexedWord> wordsToRemove = getAllChildes(dependencies, subject);
        wordsToRemove.add(subject);

        int subjectEnd = 0;
        List<String> words = sentence.getWords();
        for (IndexedWord wordToRemove : wordsToRemove) {
            int i = words.indexOf(wordToRemove.word().toLowerCase());
            if (i > subjectEnd) {
                subjectEnd = i;
            }
        }

        words = words.subList(subjectEnd + 1, words.size());
        StringBuilder question = new StringBuilder("What ");
        for (int i = 0; i < words.size() - 1; i++) {
            String word = words.get(i);
            question.append(word);
            if (!isSeparator(words.get(i + 1))) {
                question.append(" ");
            }
        }
        question.append("?");

        return question.toString();
    }

    private boolean isSeparator(String word) {
        return "?!...,".contains(word);
    }

    public List<IndexedWord> getAllChildes(SemanticGraph dependencies, IndexedWord word) {
        List<IndexedWord> childList = dependencies.getChildList(word);
        List<IndexedWord> result = new LinkedList<IndexedWord>(childList);
        for (IndexedWord child : childList) {
            result.addAll(getAllChildes(dependencies, child));
        }
        return result;
    }

    protected String getAnswer(String sentence, SemanticGraph dependencies, IndexedWord subject) {
        List<IndexedWord> childList = dependencies.getChildList(subject);
        StringBuilder answer = new StringBuilder();
        for (IndexedWord child : childList) {
            SemanticGraphEdge edge = dependencies.getEdge(subject, child);
            if (isModifyingSubject(edge.getRelation().getShortName())) {
                answer.append(child.word()).append(" ");
            }
        }
        answer.append(subject.word());
        return answer.toString();
    }

    private boolean isSubject(String relationCode) {
        return "nsubj".equals(relationCode) || "nsubjpass".equals(relationCode) || "xsubj".equals(relationCode);
    }

    private boolean isModifyingSubject(String relationCode) {
        return "nn".equals(relationCode) || "num".equals(relationCode) ||
                "amod".equals(relationCode) || "quantmod".endsWith(relationCode);
    }
}
