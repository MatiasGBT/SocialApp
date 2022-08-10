pipeline {
    agent { dockerfile true }
    stages {
        stage('Test') {
            steps {
                sh 'docker compose -p social_app_1 -f docker-compose_1.yml up -d'
                sh 'docker compose -p social_app_2 -f docker-compose_2.yml up -d'
            }
        }
    }
}
