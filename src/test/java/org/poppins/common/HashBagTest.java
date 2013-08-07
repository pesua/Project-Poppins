package org.poppins.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Anton Chernetskij
 */
public class HashBagTest {

    @Test
    public void testAdd() {
        String[] data = new String[]{"aa", "bb", "bb", "bb", "cc", "aa"};
        HashBag<String> hashBag = new HashBag<String>();
        for (String d : data) {
            hashBag.put(d);
        }
        Assert.assertEquals(2, hashBag.get("aa"));
        Assert.assertEquals(3, hashBag.get("bb"));
        Assert.assertEquals(1, hashBag.get("cc"));
    }

    @Test
    public void testGetAll() {
        String[] data = new String[]{"aa", "bb", "bb", "bb", "cc", "aa"};
        HashBag<String> hashBag = new HashBag<String>();
        for (String d : data) {
            hashBag.put(d);
        }

        for (Map.Entry<String, Integer> entry : hashBag.getAll()) {
            System.out.println(entry);
        }
    }
}
