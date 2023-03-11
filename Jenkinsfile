pipeline {
    agent any
    tools {
        maven "3.8.7"
        jdk "17"
    }

    stages {
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
            mail to: 'hello@hello.yeah',
                  subject: 'temat',
                  body: 'no i gitara'
        }
    }
}
