package com.base2.ciinabox.jenkins;

import ciinabox.JobSeeder;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.WithPlugin;

import static org.junit.Assert.assertTrue;

public class TestRunner {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @WithPlugin({
          <%= pluginsList %>
    })
    public void provision() throws Exception {

        //DO SOME TESTING
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new Shell("echo hello"));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        String s = FileUtils.readFileToString(build.getLogFile());

        new JobSeeder().seed(j);

        assertTrue(true);
    }
}