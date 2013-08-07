package org.poppins.summarizing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.poppins.common.Sentence;
import org.poppins.common.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton Chernetskij
 */
public class LexRankSummarizerTest extends LexRankSummarizer {

    private String[] sentences = new String[]{
            "Iraqi Vice President Taha Yassin Ramadan announced today, Sunday, that Iraq refuses to back down from its decision to stop cooperating with disarmament inspectors before its demands are met.",
            "Iraqi Vice president Taha Yassin Ramadan announced today, Thursday, that Iraq rejects cooperating with the United Nations except on the issue of lifting the blockade imposed upon it since the year 1990.",
            "Ramadan told reporters in Baghdad that ”Iraq cannot deal positively with whoever represents the Security Council unless there was a clear stance on the issue of lifting the blockade off of it.",
            "Baghdad had decided late last October to completely cease cooperating with the inspectors of the United Nations Special Commission (UNSCOM), in charge of disarming Iraq’s weapons, and whose work became very limited since the fifth of August, and announced it will not resume its cooperation with the Commission even if it were subjected to a military operation.",
            "The Russian Foreign Minister, Igor Ivanov, warned today, Wednesday against using force against Iraq, which will destroy, according to him, seven years of difficult diplomatic work and will complicate the regional situation in the area.",
            "Ivanov contended that carrying out air strikes against Iraq, who refuses to cooperate with the United Nations inspectors, “will end the tremendous work achieved by the international group during the past seven years and will complicate the situation in the region.”",
            "Nevertheless, Ivanov stressed that Baghdad must resume working with the Special Commission in charge of disarming the Iraqi weapons of mass destruction (UNSCOM).",
            "The Special Representative of the United Nations Secretary-General in Baghdad, Prakash Shah, announced today, Wednesday, after meeting with the Iraqi Deputy Prime Minister Tariq Aziz, that Iraq refuses to back down from its decision to cut off cooperation with the disarmament inspectors.",
            "British Prime Minister Tony Blair said today, Sunday, that the crisis between the international community and Iraq “did not end” and that Britain is still “ready, prepared, and able to strike Iraq.”",
            "In a gathering with the press held at the Prime Minister’s office, Blair contended that the crisis with Iraq “will not end until Iraq has absolutely and unconditionally respected its commitments” towards the United Nations.",
            "A spokesman for Tony Blair had indicated that the British Prime Minister gave permission to British Air Force Tornado planes stationed in Kuwait to join the aerial bombardment against Iraq."
    };

    private String[][] words = new String[][]{
            {"Iraqi", "Vice", "President", "Taha", "Yassin", "Ramadan", "announced", "today", ",", "Sunday", ",", "that", "Iraq", "refuses", "to", "back", "down", "from", "its", "decision", "to", "stop", "cooperating", "with", "disarmament", "inspectors", "before", "its", "demands", "are", "met", ".",},
            {"Iraqi", "Vice", "president", "Taha", "Yassin", "Ramadan", "announced", "today", ",", "Thursday", ",", "that", "Iraq", "rejects", "cooperating", "with", "the", "United", "Nations", "except", "on", "the", "issue", "of", "lifting", "the", "blockade", "imposed", "upon", "it", "since", "the", "year", "1990", ".",},
            {"Ramadan", "told", "reporters", "in", "Baghdad", "that", "''", "Iraq", "can", "not", "deal", "positively", "with", "whoever", "represents", "the", "Security", "Council", "unless", "there", "was", "a", "clear", "stance", "on", "the", "issue", "of", "lifting", "the", "blockade", "off", "of", "it", ".",},
            {"Baghdad", "had", "decided", "late", "last", "October", "to", "completely", "cease", "cooperating", "with", "the", "inspectors", "of", "the", "United", "Nations", "Special", "Commission", "-LRB-", "UNSCOM", "-RRB-", ",", "in", "charge", "of", "disarming", "Iraq", "'s", "weapons", ",", "and", "whose", "work", "became", "very", "limited", "since", "the", "fifth", "of", "August", ",", "and", "announced", "it", "will", "not", "resume", "its", "cooperation", "with", "the", "Commission", "even", "if", "it", "were", "subjected", "to", "a", "military", "operation", ".",},
            {"The", "Russian", "Foreign", "Minister", ",", "Igor", "Ivanov", ",", "warned", "today", ",", "Wednesday", "against", "using", "force", "against", "Iraq", ",", "which", "will", "destroy", ",", "according", "to", "him", ",", "seven", "years", "of", "difficult", "diplomatic", "work", "and", "will", "complicate", "the", "regional", "situation", "in", "the", "area", ".",},
            {"Ivanov", "contended", "that", "carrying", "out", "air", "strikes", "against", "Iraq", ",", "who", "refuses", "to", "cooperate", "with", "the", "United", "Nations", "inspectors", ",", "``", "will", "end", "the", "tremendous", "work", "achieved", "by", "the", "international", "group", "during", "the", "past", "seven", "years", "and", "will", "complicate", "the", "situation", "in", "the", "region", ".", "''",},
            {"Nevertheless", ",", "Ivanov", "stressed", "that", "Baghdad", "must", "resume", "working", "with", "the", "Special", "Commission", "in", "charge", "of", "disarming", "the", "Iraqi", "weapons", "of", "mass", "destruction", "-LRB-", "UNSCOM", "-RRB-", ".",},
            {"The", "Special", "Representative", "of", "the", "United", "Nations", "Secretary-General", "in", "Baghdad", ",", "Prakash", "Shah", ",", "announced", "today", ",", "Wednesday", ",", "after", "meeting", "with", "the", "Iraqi", "Deputy", "Prime", "Minister", "Tariq", "Aziz", ",", "that", "Iraq", "refuses", "to", "back", "down", "from", "its", "decision", "to", "cut", "off", "cooperation", "with", "the", "disarmament", "inspectors", ".",},
            {"British", "Prime", "Minister", "Tony", "Blair", "said", "today", ",", "Sunday", ",", "that", "the", "crisis", "between", "the", "international", "community", "and", "Iraq", "``", "did", "not", "end", "''", "and", "that", "Britain", "is", "still", "``", "ready", ",", "prepared", ",", "and", "able", "to", "strike", "Iraq", ".", "''",},
            {"In", "a", "gathering", "with", "the", "press", "held", "at", "the", "Prime", "Minister", "'s", "office", ",", "Blair", "contended", "that", "the", "crisis", "with", "Iraq", "``", "will", "not", "end", "until", "Iraq", "has", "absolutely", "and", "unconditionally", "respected", "its", "commitments", "''", "towards", "the", "United", "Nations", ".",},
            {"A", "spokesman", "for", "Tony", "Blair", "had", "indicated", "that", "the", "British", "Prime", "Minister", "gave", "permission", "to", "British", "Air", "Force", "Tornado", "planes", "stationed", "in", "Kuwait", "to", "join", "the", "aerial", "bombardment", "against", "Iraq", ".",}
    };

    private double[][] sim = new double[][]{
            {1.000, 0.234, 0.000, 0.000, 0.000, 0.000, 0.000, 0.265, 0.000, 0.000, 0.000},
            {0.234, 1.000, 0.177, 0.097, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000},
            {0.000, 0.177, 1.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000},
            {0.000, 0.097, 0.000, 1.000, 0.000, 0.000, 0.234, 0.092, 0.000, 0.076, 0.000},
            {0.000, 0.000, 0.000, 0.000, 1.000, 0.198, 0.000, 0.000, 0.000, 0.000, 0.000},
            {0.000, 0.000, 0.000, 0.000, 0.198, 1.000, 0.000, 0.000, 0.105, 0.115, 0.000},
            {0.000, 0.000, 0.000, 0.234, 0.000, 0.000, 1.000, 0.000, 0.000, 0.000, 0.000},
            {0.265, 0.000, 0.000, 0.092, 0.000, 0.000, 0.000, 1.000, 0.000, 0.000, 0.000},
            {0.000, 0.000, 0.000, 0.000, 0.000, 0.105, 0.000, 0.000, 1.000, 0.153, 0.106},
            {0.000, 0.000, 0.000, 0.076, 0.000, 0.115, 0.000, 0.000, 0.153, 1.000, 0.000},
            {0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.106, 0.000, 1.000}
    };

    private Text text;

    @Before
    public void setUp() {
        List<Sentence> sentencesList = new ArrayList<Sentence>();
        for (int i = 0; i < sentences.length; i++) {
            String sentenceText = sentences[i];
            List<String> sentenceWords = new ArrayList<String>(Arrays.asList(words[i]));
            Sentence sentence = new Sentence(sentenceText);
            sentence.setWords(sentenceWords);
            sentencesList.add(sentence);
        }
        text = new Text("News");
        text.setSentences(sentencesList);
    }

    @Test
    public void testSummarize() throws Exception {
        LexRankSummarizer summarizer = new LexRankSummarizer();
        List<Sentence> summary = summarizer.summarize(text, 0.3);
        Assert.assertEquals(sentences[3], summary.get(0).getSentence());
        Assert.assertEquals(sentences[7], summary.get(1).getSentence());
        Assert.assertEquals(sentences[0], summary.get(2).getSentence());
    }

    @Test
    public void testInverseDocumentFrequency() {
        LexRankSummarizer summarizer = new LexRankSummarizer();
        Map<String, Double> wordFrequencies = summarizer.inverseDocumentFrequency(text);

        Assert.assertEquals(1.0116, wordFrequencies.get("not"), 0.0001);
        Assert.assertEquals(1.7047, wordFrequencies.get("president"), 0.0001);
        Assert.assertEquals(1.2992, wordFrequencies.get("refuses"), 0.0001);
        Assert.assertEquals(2.3978, wordFrequencies.get("strike"), 0.0001);
    }

    @Test
    public void testGetSimilarities() {
        LexRankSummarizer summarizer = new LexRankSummarizer();
        double[][] similarities = filter(summarizer.getSimilarities(text), THRESHOLD);
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities[i].length; j++) {
                Assert.assertEquals(sim[i][j], similarities[i][j], 0.001);
            }
        }
    }

    @Test
    public void testSimilarity() {
        LexRankSummarizer summarizer = new LexRankSummarizer();
        Sentence sentence1 = text.getSentences().get(0);
        Sentence sentence2 = text.getSentences().get(1);
        Map<String, Double> idf = summarizer.inverseDocumentFrequency(text);
        double similarity = summarizer.similarity(sentence1, sentence2, idf);
        Assert.assertEquals(0.2340, similarity, 0.0001);
    }

    @Test
    public void testRanks() {
        LexRankSummarizer summarizer = new LexRankSummarizer();
        double[][] similarities = summarizer.getSimilarities(text);
        similarities = summarizer.filter(similarities, THRESHOLD);
        double[] actual = summarizer.calcRanks(similarities);
        double[] expected = {0.8471, 0.9784, 0.0, 1.0, 0.1905, 0.9262, 0.1263, 0.3689, 0.7811, 0.5691, 0.1248};
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i], 0.0001);
        }
    }
}
