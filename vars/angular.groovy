def call(body) {
  def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
  pipeline {
    agent  any
	options { 
				timeout(time: 30, unit: 'MINUTES')		
			}
     stages {
        stage('Install npm Packages') {
            steps {
                sh "npm install"
            }
        }

		stage('Build') {
            steps {
                sh "node_modules/.bin/ng build --prod"
            }
        }
				
	//		stage('Sonar Code Analysis') {
   //         steps {
	//			withSonarQubeEnv('Sonar'){
   //             sh "npm run sonar"
             }
			}
        }	
			//stage("Quality Gate") {
            //steps {
              //timeout(time: 10, unit: 'MINUTES') {
                //waitForQualityGate abortPipeline: true
              //}
            //}
          //}	
		stage("Build Docker Image") {
		    steps {
			   // sh "docker image prune -f"
			    sh "docker build -t ank1999/angular:latest"
		    }
		}
		stage("Push Docker hub") {
	        steps {
      		  withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
               sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
               sh 'docker push ank1999/angular:latest'
			}
		}
//		stage("Pull and execute on dev-vm") {
	//        agent { label 'jenkins-slave'}
	//		steps {
		//		playBook(pipelineParams.yml_filename)
			//}
		//}
		stage("Delete Image from Jenkins") {
		    steps {
      		//	dockerRemoveImage(pipelineParams.image_name, pipelineParams.image_tag)
				//sh "docker image prune -f"
			    echo "just for passing"
        }
		}
    }
 
