package com.simpoir.jenkins.lxd.pipeline;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.remoting.Channel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LxdLauncher extends Launcher {

    public final Launcher wrapped;
    public final String containerName;

    LxdLauncher(Launcher wrapped, String containerName) {
        super(wrapped);
        this.wrapped = wrapped;
        this.containerName = containerName;
    }

    @Override
    public Proc launch(ProcStarter starter) throws IOException {
        List<String> args = new ArrayList<>(Arrays.asList("lxc", "exec", containerName, "--"));
        args.addAll(starter.cmds());
        starter.cmds(args);
        return wrapped.launch(starter);
    }

    @Override
    public Channel launchChannel(String[] cmd, OutputStream out, FilePath workDir, Map<String, String> envVars)
            throws IOException, InterruptedException {
        return wrapped.launchChannel(cmd, out, workDir, envVars);
    }

    @Override
    public void kill(Map<String, String> modelEnvVars) throws IOException, InterruptedException {
        wrapped.kill(modelEnvVars);
    }
}
