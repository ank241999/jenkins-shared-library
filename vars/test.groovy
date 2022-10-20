def call(body) {
  def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
  
pipeline {
    agent any
    stages {
        stage('Hello') {
            steps {
                echo 'Hello ankush jankins World......'
            }
        }
        
        stage('build') {
            steps {
                echo 'Hello ankush jankins World......'
            }
        }
            
        stage('deploy') {
            steps {
                echo 'deploy......'
            }
        }
    
        stage('test') {
            steps {
                echo 'testing......'
            }
        }
    
        stage('release') {
            steps {
                echo 'releasing......'
            }
        }
    }
}  
  
 
}
