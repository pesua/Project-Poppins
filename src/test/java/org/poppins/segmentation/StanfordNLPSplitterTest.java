package org.poppins.segmentation;

import org.junit.Assert;
import org.junit.Test;
import org.poppins.common.Text;
import org.poppins.common.TextReader;

/**
 * Created by Anton Chernetskij
 */
public class StanfordNLPSplitterTest {

    private String[] expected = new String[]{
            "Lawrence \"Larry\" Page is an American computer scientist and internet entrepreneur.",
            "Larry Page was born in Lansing, Michigan.",
            "His father, Carl Page, earned a Ph.D. in computer science in 1965 when the field was in its infancy, " +
                    "and is considered a \"pioneer in computer science and artificial intelligence.\"",
            "As of 2012, his personal wealth is estimated to be $18.7 billion."

    };

    @Test
    public void testSplit() throws Exception {
        TextReader reader = new TextReader();
        String text = reader.readText("src/test/resources/Larry Page.txt");

        TextSplitter splitter = new StanfordNLPSplitter();
        Text actual = splitter.split(text, "Larry Page");

        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual.getSentences().get(i).toString());
        }
    }
}
