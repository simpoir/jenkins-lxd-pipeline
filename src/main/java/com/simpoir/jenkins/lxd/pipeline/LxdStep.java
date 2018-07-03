package com.simpoir.jenkins.lxd.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

public class LxdStep extends AbstractStepImpl implements Serializable {

    public final String imageName;

    @DataBoundConstructor
    public LxdStep(String imageName) {
        this.imageName = imageName;
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
