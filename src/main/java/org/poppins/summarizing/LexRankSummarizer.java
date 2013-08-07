package org.poppins.summarizing;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.poppins.common.HashBag;
import org.poppins.common.Sentence;
import org.poppins.common.Text;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Anton Chernetskij
 */
public class LexRankSummarizer implements Summarizer {

    private static final Logger LOGGER = Logger.getLogger(Summarizer.class.getName());
    protected static final double THRESHOLD = 0.065;
    private static final double DAMPING_FACTOR = 0.1;

    /**
     * Creates extractive summary of the text
     *
     * @param text
     * @param part fraction of sentences that must be present in summary. Part must be between 1 and 0. For example if
     *             the text contains 10 sentences and part=0.2 then summary will contain 2 sentences.
     * @return
     */
    public List<Sentence> summarize(Text text, double part) {

        if (!(part > 0 && part < 1.0)) {
            throw new IllegalArgumentException("Part must be between 1 and 0.");
        }

        LOGGER.info("Summarizing text " + text.getName());

        long start = System.currentTimeMillis();

        double[][] similarities = getSimilarities(text);
//        similarities = filter(similarities, THRESHOLD);

        LOGGER.info("Passed " + numNotZero(similarities) + " connections for " + text.numSentences() + "sentences.");

        double[] ranks = calcRanks(similarities);

        List<Sentence> sentences = text.getSentences();
        List<RankedSentence> rankedSentences = new ArrayList<RankedSentence>(text.numSentences());
        for (int i = 0; i < sentences.size(); i++) {
            Sentence sentence = sentences.get(i);
            rankedSentences.add(new RankedSentence(ranks[i], sentence));
        }

        long finish = System.currentTimeMillis();
        LOGGER.info("Summarizing " + text.getName() + " finished in " + (finish - start) + " ms");
        StringBuilder rankedSentencesList = new StringBuilder("RankedSentences:\n");
        for (RankedSentence rankedSentence : rankedSentences) {
            rankedSentencesList.append(rankedSentence.toString()).append("\n");
        }
        LOGGER.info(rankedSentencesList.toString());
        Collections.sort(rankedSentences);
        List<Sentence> result = new ArrayList<Sentence>((int) (text.numSentences() * part));
        int summarySize = (int) (rankedSentences.size() * part);
        for (int i = rankedSentences.size() - 1; i >= rankedSentences.size() - summarySize; i--) {
            result.add(rankedSentences.get(i).getSentence());
        }
        return result;
    }

    protected double[][] getSimilarities(Text text) {
        int numSentences = text.numSentences();
        double[][] similarities = new double[numSentences][numSentences];
        List<Sentence> sentences = text.getSentences();
        Map<String, Double> stringDoubleMap = inverseDocumentFrequency(text);
        for (int i = 0; i < numSentences; i++) {
            for (int j = 0; j < i; j++) {
                similarities[i][j] = similarity(sentences.get(i), sentences.get(j), stringDoubleMap);
                similarities[j][i] = similarities[i][j];
            }
            similarities[i][i] = 1;
        }
        return similarities;
    }

    protected double[][] filter(double[][] similarities, double threshold) {
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities.length; j++) {
                if (similarities[i][j] < threshold) {
                    similarities[i][j] = 0;
                }
            }
        }
        return similarities;
    }

    protected int numNotZero(double[][] similarities) {
        int result = 0;
        for (double[] similarityRow : similarities) {
            for (int j = 0; j < similarities.length; j++) {
                if (similarityRow[j] > 0) {
                    result++;
                }
            }
        }
        return result;
    }

    protected double similarity(Sentence sentence1, Sentence sentence2, Map<String, Double> idf) {
        List<String> words1 = sentence1.getWords();
        List<String> words2 = sentence2.getWords();
        HashBag<String> bag1 = new HashBag<String>(words1);
        HashBag<String> bag2 = new HashBag<String>(words2);
        Set<String> allWords = new TreeSet<String>();
        double s1 = 0;
        for (String word : words1) {
            s1 += Math.pow(bag1.get(word) * idf.get(word), 2);
            allWords.add(word);
        }
        double s2 = 0;
        for (String word : words2) {
            s2 += Math.pow(bag2.get(word) * idf.get(word), 2);
            allWords.add(word);
        }
        double mul = 0;
        for (String word : allWords) {
            mul += bag1.get(word) * bag2.get(word) * Math.pow(idf.get(word), 2);
        }
        return mul / Math.sqrt(s1 * s2);
    }

    protected Map<String, Double> inverseDocumentFrequency(Text text) {
        List<Sentence> sentences = text.getSentences();
        Set<String> allWords = new TreeSet<String>();
        for (Sentence sentence : sentences) {
            List<String> wordsOfSentence = sentence.getWords();
            for (String word : wordsOfSentence) {
                allWords.add(word);
            }
        }

        double textSize = sentences.size();
        Map<String, Double> result = new HashMap<String, Double>();
        for (String word : allWords) {
            int num = 0;
            for (Sentence sentence : sentences) {
                if (sentence.contains(word)) {
                    num++;
                }
            }
            result.put(word, Math.log(textSize / num));
        }
        return result;
    }

    protected double[][] normalize(double[][] similarities) {
        for (int i = 0; i < similarities.length; i++) {
            double[] similarity = similarities[i];
            double sum = 0;
            for (int j = 0; j < similarity.length; j++) {
                sum += similarity[j];
            }
            for (int j = 0; j < similarity.length; j++) {
                similarity[j] = similarity[j] / sum;
            }
        }
        return similarities;
    }

    protected double[] calcRanks(double[][] similarities) {
        similarities = normalize(similarities);
        final int n = similarities.length;
        double[] p = new double[n];
        for (int i = 0; i < p.length; i++) {
            p[i] = 1.0 / n;
        }
        double[][] kernelData = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                kernelData[i][j] = DAMPING_FACTOR / n + (1 - DAMPING_FACTOR) * similarities[i][j];
            }
        }
        RealMatrix transitionKernel = new Array2DRowRealMatrix(kernelData, false).transpose();
        RealMatrix ranks = new Array2DRowRealMatrix(p);
        double eps = 0;
        int iterations = 0;
        do {
            RealMatrix oldRanks = ranks;
            ranks = transitionKernel.multiply(ranks);
            eps = ranks.subtract(oldRanks).getNorm();
            iterations++;
        } while (eps > 0.0001);
        LOGGER.info("Computed kernel in " + iterations + " iterations.");
        double[] result = ranks.transpose().getData()[0];
        normalise(result);
        return result;
    }

    protected void normalise(double[] ranks) {
        double max = 0;
        double min = 1;
        for (int i = 0; i < ranks.length; i++) {
            if (max < ranks[i]) {
                max = ranks[i];
            }
            if (min > ranks[i]) {
                min = ranks[i];
            }
        }
        max = max - min;
        for (int i = 0; i < ranks.length; i++) {
            ranks[i] = (ranks[i] - min) / max;
        }
    }

    protected void printArr(double[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            double[] doubles = arr[i];
            StringBuilder builder = new StringBuilder("{");
            for (int j = 0; j < doubles.length; j++) {
                double val = doubles[j];
                builder.append(String.format("%f ,", val));
            }
            builder.append("},");
            System.out.println(builder.toString());
        }
    }

    public class RankedSentence implements Comparable<RankedSentence> {

        private double rank;
        private Sentence sentence;

        public RankedSentence(double rank, Sentence sentence) {
            this.rank = rank;
            this.sentence = sentence;
        }

        public double getRank() {
            return rank;
        }

        public Sentence getSentence() {
            return sentence;
        }

        public int compareTo(RankedSentence o) {
            return Double.compare(rank, o.rank);
        }

        @Override
        public String toString() {
            return "RankedSentence{" +
                    "rank=" + rank +
                    ", sentence=" + sentence +
                    '}';
        }
    }
}
