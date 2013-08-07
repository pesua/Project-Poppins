package org.poppins.summarizing;

import org.poppins.common.Sentence;
import org.poppins.common.Text;

import java.util.List;

/**
 * Created by Anton Chernetskij
 */
public interface Summarizer {

    List<Sentence> summarize(Text text, double part);

}
