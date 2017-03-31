package ciinabox

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.JenkinsJobManagement
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.yaml.snakeyaml.Yaml
import jenkins.model.Jenkins
import java.nio.file.Paths


public class JobSeeder {

  private Jenkins jenkins

  public def seed(Jenkins instance) {
    this.jenkins = instance
    String ciinabox = System.getProperty('ciinabox')
    String ciinaboxes = System.getProperty('ciinaboxes', 'ciinaboxes')
    String username = System.getProperty('username')
    String password = System.getProperty('password') // password or token
    String jobFileToProcess = System.getProperty('jobfile')
    String jobToProcess = System.getProperty('job')
    String jenkinsOverrideUrl = System.getProperty('url')

    def baseDir = new File(".").absolutePath,
        ciinaboxesDir = new File(ciinaboxes)

    baseDir = baseDir.substring(0, baseDir.length() - 2)

    if (!ciinabox) {
      println 'usage: -Dciinabox=<ciinabox_name> [-Dciinaboxes=<ciinaboxes dir>] [-Durl=<jenkins_url>] [-Dusername=<username>] [-Dpassword=<password>] [-Djobfile=myjobs.yml]'
      System.exit 1
    }

    def yaml = new Yaml()
    def processedJobs = false
    new FileNameFinder().getFileNames("${ciinaboxesDir.absolutePath}/${ciinabox}/jenkins/", "*.yml").each {String jobsFile ->

      def matchingByJobfile =
              jobFileToProcess != null &&
                      (jobFileToProcess.equalsIgnoreCase(Paths.get(jobsFile).fileName.toString())
                              || jobFileToProcess.equalsIgnoreCase(FilenameUtils.removeExtension(Paths.get(jobsFile).fileName.toString())))


      if (jobFileToProcess == null || matchingByJobfile) {
        def jobs = (Map) yaml.load(new File(jobsFile).text)
        println "\nLoading jobs from file: $jobsFile"
        if (jenkinsOverrideUrl != null) {
          jobs['jenkins_url'] = jenkinsOverrideUrl
        }

        //if specific job is defined
        if (jobs['jobs'] && StringUtils.isNotEmpty(jobToProcess)) {
          jobs['jobs'] = jobs['jobs'].findAll {
            (it['name'] == null) || (it['name'].equalsIgnoreCase(jobToProcess))
          }
        }

        manageJobs(baseDir, username, password, jobs)
        processedJobs = true
      }
    }
    if (!processedJobs) {
      def jobFileName = jobFileToProcess ?: 'jobs.yml'
      println "no ${jobFileName} file found for ${ciinabox} found in ${ciinaboxesDir.absolutePath}/jenkins"
    }

  }

  private def manageJobs(def baseDir, def username, def password, def objJobFile) {

//    RestApiJobManagement jm = new RestApiJobManagement(objJobFile['jenkins_url'])
    JobManagement jm = new JenkinsJobManagement(System.out, [:], new File('.'))

    if (username && password) {
    //  jm.setCredentials username, password
    }

    def jobNames = []
    objJobFile['jobs'].each {job ->
      jobNames << job.get('folder', '') + '/' + job.get('name')
    }
    objJobFile['jobs'].each {job ->
      jm.parameters.clear()
      jm.parameters['baseDir'] = baseDir
      jm.parameters['jobBaseDir'] = "$baseDir/ciinabox-bootstrap/jenkins"

      if (objJobFile['defaults']) {
        jm.parameters['defaults'] = objJobFile['defaults']
      }
      def jobTemplate = new File("$baseDir/jobs/${job.get('type', 'default')}.groovy").text,
          jobName

      if (!job.containsKey('config')) {
        job.put('config', [:])
      }

      if (job.containsKey('type') && job.containsKey('repo') && job.get('type') != 'default') {
        jobName = job.get("name", "${job['repo'].split('/')[1]}-${job['type'].split('/')[1]}")
      }
      else if (job.containsKey('name')) {
        jobName = job.get('name')
      }
      else {
        throw new IllegalArgumentException('job requires either a type or a name')
      }

      if (!job['folder']) {
        job['folder'] = ''
      }

      println "\nprocessing job: $jobName"

      jm.parameters << job
      jm.parameters['jobName'] = jobName
      jm.parameters['jobNames'] = jobNames

      DslScriptLoader dsl = new DslScriptLoader(jm)
      dsl.runScript(jobTemplate)

      println 'theend'

    }
  }
}

