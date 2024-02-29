def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
    
pipeline {
    agent any
        environment {
        // Define environment variables
        ZAP_PATH = '"C:\\Program Files\\ZAP\\Zed Attack Proxy\\zap-2.14.0.jar"'
        def zapDir = "${WORKSPACE}"
        def OUTPUT_PATH = "${zapDir}\\results.html"
    }

    stages {
        stage('Deployed PHP CODE') {
            steps {
                // Copying files to XAMPP's htdocs directory
                bat "xcopy * E:\\xampp-portable-windows-x64-7.2.34-2-VC15\\xampp\\htdocs\\dashboard /Y"
                // Checking Newman version to ensure it's accessible
                bat "\"C:\\Users\\Ankush Jindal\\AppData\\Roaming\\npm\\newman\" -v"
            }
        }

        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('sonar') { // Use the SonarQube environment name configured in Jenkins
                    bat 'sonar-scanner'
                }
            }
        }

        // New stage for SonarQube Quality Gate check
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') { // Adjust the timeout as necessary
                    script {
                        def qg = waitForQualityGate() // This method will return a QualityGate object
                        if (qg.status != 'OK') {
                            error "Pipeline halted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('ZAP Security Test') {
            steps {
                script {
                    bat "java -jar ${env.ZAP_PATH} -cmd -quickurl http://localhost:9002/dashboard/LMS/views/ -quickprogress -quickout ${env.OUTPUT_PATH} -port 8090"
                    archiveArtifacts artifacts: 'results.html', onlyIfSuccessful: true
                }
            }
        }

        // Other stages can follow here
    }

    // Post actions
    post {
        always {
            emailext (
                subject: "Pipeline Status: ${BUILD_NUMBER}", 
                body: '''<html>
<body>
<p>Build Status: ${BUILD_STATUS}</p>
<p>Build Number: ${BUILD_NUMBER}</p>
<p>Check the <a href="${BUILD_URL}">console output</a> for more details.</p>
<p>ZAP Security Report attached.</p>
</body>
</html>''',
                to: 'ankush.rdev@gmail.com',
                from: 'jenkins@example.com',
                replyTo: 'jenkins@example.com',
                mimeType: 'text/html'
                // Ensure attachments: 'results.html' is correctly configured if you intend to attach the report
            )
        }
    }
}
}
