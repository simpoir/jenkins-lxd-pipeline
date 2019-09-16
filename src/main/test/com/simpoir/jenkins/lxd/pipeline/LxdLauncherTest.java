package com.simpoir.jenkins.lxd.pipeline;

import hudson.FilePath;
import hudson.Launcher;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class LxdLauncherTest {


	@Test
	public void testRunWithEnv() throws Exception {
		Launcher mock_cmd = mock(Launcher.class);
		Launcher.ProcStarter mock_starter = mock(Launcher.ProcStarter.class);
		when(mock_starter.cmds()).thenReturn(Arrays.asList("./scriptName", "someArg"));
		when(mock_starter.envs()).thenReturn(new String[] {"FOO=bar"});

		LxdLauncher launcher = new LxdLauncher(mock_cmd, "brian");
		launcher.launch(mock_starter);

		verify(mock_starter, atLeastOnce()).cmds(Arrays.asList(
				"lxc", "exec", "brian", "--", "env", "FOO=bar",
				"./scriptName", "someArg"));
		verify(mock_cmd, times(1)).launch(mock_starter);
	}

	@Test
	public void testRun() throws Exception {
		Launcher mock_cmd = mock(Launcher.class);
		Launcher.ProcStarter mock_starter = mock(Launcher.ProcStarter.class);
		when(mock_starter.cmds()).thenReturn(Arrays.asList("./scriptName", "someArg"));
		when(mock_starter.pwd()).thenReturn(new FilePath(new File("/some/workdir")));

		LxdLauncher launcher = new LxdLauncher(mock_cmd, "brian");
		launcher.launch(mock_starter);

		verify(mock_starter, atLeastOnce()).cmds(Arrays.asList(
				"lxc", "exec", "brian", "--", "env", "-C/some/workdir",
				"./scriptName", "someArg"));
		verify(mock_cmd, times(1)).launch(mock_starter);
	}
}
