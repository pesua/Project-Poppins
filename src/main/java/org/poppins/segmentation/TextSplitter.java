package org.poppins.segmentation;

import org.poppins.common.Text;

/**
 * Created by Anton Chernetskij
 */
public interface TextSplitter {
    Text split(String text, String name);
}
