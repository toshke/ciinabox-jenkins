import jenkins.model.*
import java.util.UUID.*
import org.jenkinsci.plugins.configfiles.*
import org.jenkinsci.lib.configprovider.model.Config
import org.jenkinsci.plugins.managedscripts.ScriptConfig

def inst = Jenkins.getInstance()

def descriptor = 'org.jenkinsci.plugins.configfiles.GlobalConfigFiles'

def desc = inst.getDescriptor(descriptor)

configObj = new ScriptConfig(UUID.randomUUID().toString(),
        "test-from-console",
        "test-from-console-comment",
        """#!/usr/bin/env python
print "Hello world!!!!"
""", null)

configObj.setProviderId('org.jenkinsci.plugins.managedscripts.ScriptConfig')
GlobalConfigFiles.get().save(configObj)