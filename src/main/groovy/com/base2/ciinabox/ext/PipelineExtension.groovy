package com.base2.ciinabox.ext

import javaposse.jobdsl.dsl.Job

/**
 * Created by nikolatosic on 29/03/2017.
 */
class PipelineExtension extends ExtensionBase implements ICiinaboxExtension {

  public PipelineExtension(){}

  @Override
  String getConfigurationKey() {
    return 'pipeline'
  }

  @Override
  Map getDefaultConfiguration() {
    return [:]
  }

  @Override
  void extendDsl(Job job, def extensionConfiguration, def jobConfiguration) {
    def scriptLocation = "${defaultConfigValue('scripts_dir','ciinaboxes')}/${extensionConfiguration.file}",
        scriptContent = new File(scriptLocation).text

    job.definition {
      cps {
        script(scriptContent)
        if(extensionConfiguration.sandbox){
          sandbox()
        }
      }
    }
  }
}
