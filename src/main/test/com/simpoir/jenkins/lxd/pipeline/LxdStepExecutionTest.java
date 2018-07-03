package com.simpoir.jenkins.lxd.pipeline;

import org.junit.Assert;
import org.junit.Test;

public class LxdStepExecutionTest {

    @Test
    public void testHostnameFormatter() {
        Assert.assertEquals("asis", LxdStepExecution.formatHostname("asis"));
        Assert.assertEquals("With-some-symbols", LxdStepExecution.formatHostname("With_some symbols"));
        Assert.assertEquals("Number1", LxdStepExecution.formatHostname("Number1"));
        Assert.assertEquals("dashes", LxdStepExecution.formatHostname("-dashes-"));
        Assert.assertEquals("x1234", LxdStepExecution.formatHostname("1234"));
        String wayTooLong = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String expected = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        Assert.assertEquals(expected, LxdStepExecution.formatHostname(wayTooLong));
    }

}
