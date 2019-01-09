package com.wipro.ats.bdre.imcrawler.tests;

import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.junit.Test;


/**
 * Created by Avi on 8/19/2014.
 */
public class WebURLTest {

    @Test
    public void testNoLastSlash() {
        WebURL webUrl = new WebURL();
        webUrl.setURL("http://google.com");
    }
}