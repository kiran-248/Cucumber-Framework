pipeline {
    agent any

    tools {
        maven 'Maven 3.9.10'   // Make sure this matches your Jenkins Maven tool config
        jdk 'JDK 21.0.7'          // Configure this in Jenkins → Global Tool Configuration
    }

    environment {
        // Define any environment variables here
        BROWSER = "chrome"
        ENV = "qa"
    }


    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/kiran-248/Cucumber-Framework.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
