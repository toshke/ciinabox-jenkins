package ciinabox.ext

import javaposse.jobdsl.dsl.Job

/**
 * Created by nikolatosic on 9/03/2017.
 */
public interface ICiinaboxExtension {

    void setJobConfiguration(jobConfiguration)

    void extend(Job job)

}