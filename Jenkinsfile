pipeline {
    agent any
    tools {
        maven "3.8.7"
        jdk "17.0.6"
    }

    triggers {
        // Trigger the pipeline whenever a commit is made to the specified branch
        pollSCM('*/5 * * * *') // check for changes every 5 minutes
        ignorePostCommitHooks(true)
        changeset {
            // Only build when there are new changes
            compareRemote {
                // Limit the number of changes to 1 (i.e., the latest commit)
                max(1)
            }
        }
    }
    stages {
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}