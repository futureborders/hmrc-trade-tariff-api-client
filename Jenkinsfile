@Library('fbp-shared-library') _

pipeline {
  agent {
    label "jenkins-maven-java11"
  }
  environment {
    ORG = 'futureborders'
    APP_NAME = 'hmrc-trade-tariff-api-client'
  }
  stages {
    stage('CI Test') {
      when {
        anyOf {
          expression { env.CHANGE_ID ==~ /.*/ || env.BRANCH_NAME ==~ /^PR-.*/ }
          branch 'main'
        }
      }
      steps {
        container('maven') {
          sh "mvn verify"
          step([$class: 'JacocoPublisher',
              execPattern: '**/target/*.exec',
              classPattern: '**/target/classes',
              sourcePattern: 'src/main/java',
              exclusionPattern: 'src/test*'
            ]
          )
        }
      }
    }
    stage('Build Release') {
      when {
        branch 'main'
      }
      steps {
        container('maven') {
          mavenSettings(path: "${env.WORKSPACE}/settings.xml")
          deployLibraryV2(branch: BRANCH_NAME) {
            sh "mvn  clean deploy"
            // Below is to push to github
            //sh "mvn --settings ${env.WORKSPACE}/settings.xml clean deploy"          
          }
        }
      }
    }
  }
  post {
    always {
      cleanWs()
    }
  }
}
