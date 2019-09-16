package com.simpoir.jenkins.lxd.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.Map;

public class LxdStep extends AbstractStepImpl implements Serializable {

    final String imageName;
    private final Map<String, String> lxdConfig;
    private final Map<String, String> lxdDisks;

    @DataBoundConstructor
    public LxdStep(String image, Map<String, String> config, Map<String, String> disks) {
        this.imageName = image;
        this.lxdConfig = config;
        this.lxdDisks = disks;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(LxdStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "lxd";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }
    }
}
