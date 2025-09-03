pipeline {
	agent any

    tools {
		maven 'Maven-3.9.11'
        jdk 'jdk-17'
    }

    stages {
		stage('Checkout') {
			steps {
				git 'https://github.com/AnkitaMukherjee2002/bookmyshow-automation-framework.git'
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
