pipeline {
  agent any
  stages {
    stage('git-pull') {
      steps {
        echo 'git pull sucess'
        sh 'pwd'
      }
    }
    stage('Build') {
      steps {
        echo 'build code sucessfully'
      }
    }
    stage('test') {
      steps {
        echo 'testing completed'
      }
    }
    stage('deploy') {
      steps {
        echo 'code deployes'
      }
    }
}
}