pipeline {
	agent any

    tools {
		maven 'Maven-3.9.4'
        jdk 'OpenJDK-17'
    }

    stages {
		stage('Checkout') {
			steps {
				git 'https://github.com/your-repo/selenium-testng-project.git'
            }
        }

        stage('Build & Test') {
			steps {
				sh 'mvn clean test'
            }
        }

        stage('Publish Reports') {
			steps {
				publishTestNGResults testResultsPattern: '**/test-output/testng-results.xml'
            }
        }
    }
}
