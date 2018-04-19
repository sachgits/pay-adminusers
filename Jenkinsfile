#!/usr/bin/env groovy

pipeline {
  agent any

  parameters {
    booleanParam(defaultValue: true, description: '', name: 'runEndToEndTestsOnPR')
    booleanParam(defaultValue: true, description: '', name: 'runAcceptTestsOnPR')
  }

  options {
    ansiColor('xterm')
    timestamps()
  }

  libraries {
    lib("pay-jenkins-library@master")
  }

  environment {
    DOCKER_HOST = "unix:///var/run/docker.sock"
    RUN_END_TO_END_ON_PR = "${params.runEndToEndTestsOnPR}"
    RUN_ACCEPT_ON_PR = "${params.runAcceptTestsOnPR}"
  }

  stages {
    stage('Maven Build') {
      steps {
        script {
          def long stepBuildTime = System.currentTimeMillis()

          sh 'docker pull govukpay/postgres:9.4.4'
          sh 'mvn clean package'
          postSuccessfulMetrics("adminusers.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("adminusers.maven-build.failure", 1)
        }
      }
    }

    stage('Docker Build') {
      steps {
        script {
          buildAppWithMetrics{
            app = "adminusers"
          }
        }
      }
      post {
        failure {
          postMetric("adminusers.docker-build.failure", 1)
        }
      }
    }
    stage('Tests') {
      failFast true
      parallel {
        stage('Card Payment End-to-End Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_END_TO_END_ON_PR', value: 'true'
                }
            }
            steps {
                runCardPaymentsE2E("adminusers")
            }
        }
        stage('Accept Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_ACCEPT_ON_PR', value: 'true'
                }
            }
            steps {
                runAccept("adminusers")
            }
        }
      }
    }
    stage('Docker Tag') {
      steps {
        script {
          dockerTagWithMetrics {
            app = "adminusers"
          }
        }
      }
      post {
        failure {
          postMetric("adminusers.docker-tag.failure", 1)
        }
      }
    }
    stage('Deploy') {
      when {
        branch 'master'
      }
      steps {
        deployEcs("adminusers")
      }
    }
    stage('Smoke Tests') {
      when {
        branch 'master'
      }
      steps {
        runProductsSmokeTest()
      }
    }
    stage('Complete') {
      failFast true
      parallel {
        stage('Tag Build') {
          when {
            branch 'master'
          }
          steps {
            tagDeployment("adminusers")
          }
        }
        stage('Trigger Deploy Notification') {
          when {
            branch 'master'
          }
          steps {
            triggerGraphiteDeployEvent("adminusers")
          }
        }
      }
    }
  }
  post {
    failure {
      postMetric(appendBranchSuffix("adminusers") + ".failure", 1)
    }
    success {
      postSuccessfulMetrics(appendBranchSuffix("adminusers"))
    }
  }
}
