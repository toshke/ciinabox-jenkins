package com.base2.rest

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient


class RestApiScriptRunner {
  final RESTClient restClient

  RestApiScriptRunner(String baseUrl) {
    if (!baseUrl != null && !baseUrl.endsWith("/")) {
      baseUrl += "/"
    }
    println("using jenkins url: ${baseUrl}")
    restClient = new RESTClient(baseUrl)
    restClient.ignoreSSLIssues()
    restClient.handler.failure = { it }
  }

  void setCredentials(String username, String password) {
    restClient.headers['Authorization'] = 'Basic ' + "$username:$password".bytes.encodeBase64()
  }

  void executeScript(String scriptPath) {
    if (!new File(scriptPath).exists()) {
      throw new RuntimeException("Jenkins script ${scriptPath} not found")
    }
    String scriptContent = new File(scriptPath).text
    def resp = restClient.post(
            path: '/scriptText',
            contentType: 'application/x-www-form-urlencoded',
            body: [script : scriptContent]
    )
    println "\n\nJenkins Output:\n\n"
    println resp?.data.toString()

  }

}

