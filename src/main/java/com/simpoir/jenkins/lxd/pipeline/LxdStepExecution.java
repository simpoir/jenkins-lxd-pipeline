package com.simpoir.jenkins.lxd.pipeline;

import com.google.inject.Inject;
import hudson.FilePath;
import hudson.Launcher;
import hudson.LauncherDecorator;
import hudson.model.Node;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.PrintStream;
import java.io.Serializable;


public class LxdStepExecution extends AbstractStepExecutionImpl {

    @Inject(optional = true)
    private transient LxdStep step;
    private BodyExecution body;
    private String containerName;

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        PrintStream printStream = context.get(TaskListener.class).getLogger();
        FilePath ws = context.get(FilePath.class);
        WorkflowJob workflowJob = context.get(WorkflowJob.class);
        containerName = formatHostname(String.format("%s-%d", workflowJob.getName(), workflowJob.getNextBuildNumber()));

        Launcher launcher = context.get(Launcher.class);
        launcher.launch().cmds("lxc", "launch", step.imageName, containerName, "--ephemeral",
                 // privileged to simplify mounting workspace in container without mapping UIDs
                "--config", "security.privileged=true"
        ).stdout(printStream).stderr(printStream).join();
        // XXX We're mounting the mounting the parent as some required resources and scripts are stored in temp
        // folders there. Ideally only required data would be mounted. (#1)
        String workspaceDir =  ws.getParent().absolutize().getRemote();
        launcher.launch().cmds("lxc", "config", "device", "add", containerName, "workspace", "disk",
                "source=" + workspaceDir, "path=" + workspaceDir).join();

        // wait for container to boot
        launcher.launch().cmds("lxc", "exec", containerName, "--", "sh", "-c",
                "while [ ! -e /var/lib/cloud/instance/boot-finished ]; do echo waiting for cloud-init; sleep 5; done ")
                .stdout(printStream).stderr(printStream).join();

        printStream.println("start of lxd block");
        body = context.newBodyInvoker()
                .withCallback(new Callback(containerName))
                .withContext(new LxdLauncherDecorator(containerName))
                .start();
        return false;   // body is async
    }

    /***
     * Format hostname with compliance to  https://tools.ietf.org/html/rfc952
     * @param name a name to be valid
     * @return a valid hosname
     */
    static String formatHostname(String name) {
        return name.replaceAll("\\P{Alnum}", "-")  // only alnum
                .replaceAll("^(\\p{Digit}+)$", "x$1")  // must contain letters
                .replaceAll("^-*([^\\-]*)-*$", "$1")   // can't start or end with dash
                .replaceAll("^(.{63}).*$", "$1");  // trim if larger than 63 chars
    }

    public static class LxdLauncherDecorator extends LauncherDecorator implements Serializable {
        final String containerName;

        LxdLauncherDecorator(String containerName) {
            this.containerName = containerName;
        }

        @Override
        public Launcher decorate(Launcher launcher, Node node) {
            return new LxdLauncher(launcher, containerName);
        }
    }

    public static class Callback extends BodyExecutionCallback.TailCall {
        private final String containerName;

        Callback(String containerName) {
            this.containerName = containerName;
        }

        @Override
        protected void finished(StepContext context) throws Exception {
            context.get(TaskListener.class).getLogger().printf("Destroying container %s\n", containerName);
            context.get(Launcher.class).launch().cmds("lxc", "delete", "--force", containerName).join();
        }
    }

}
