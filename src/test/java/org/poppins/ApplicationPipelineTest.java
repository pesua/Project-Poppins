package org.poppins;

import junit.framework.Assert;
import org.junit.Test;
import org.poppins.common.TextReader;
import org.poppins.transformation.sentencetransformer.Question;

import java.util.List;

/**
 * Created by Anton Chernetskij
 */
public class ApplicationPipelineTest {
    @Test
    public void testProcess() throws Exception {

        TextReader reader = new TextReader();
        String s = reader.readText("src/test/resources/Hoverberget.txt");

        ApplicationPipeline pipeline = new ApplicationPipeline();
        List<Question> result = pipeline.process(s, "Hoverberget");
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("What is a mountain on a peninsula in the southern part of the storsjön lake?", result.get(0).getQuestion());
        Assert.assertEquals("Hoverberget", result.get(0).getAnswer());
        for (Question question : result) {
            System.out.println(question);
        }
    }
}
