pipeline {
    agent any
    stages {
        stage('git-pull') {
            steps { 
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y'
                git 'https://github.com/chinmayvpotdar/student-ui.git'
                sh 'ls'
            }
        }
        stage('build-maven') {
            steps { 
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install maven curl unzip -y'
                sh 'mvn clean package'
            }
        }
        stage('build-artifacts') {
            steps { 
                //sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                //sh 'unzip awscliv2.zip'
                //sh 'sudo ./aws/install'
                sh 'aws s3 cp **/*.war s3://asus-tuf/student-${BUILD_ID}.war'
            }
        }
        stage('tomcat-build') {
            steps { 
                withCredentials([sshUserPrivateKey(credentialsId: 'new-id', keyFileVariable: 'agentkey', usernameVariable: 'ubuntu')]) {            
                sh '''
                ssh -i ${agentkey} -o StrictHostKeyChecking=no ubuntu@35.78.175.212<<EOF
                sudo apt-get update -y
                sudo apt install unzip -y
                #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                #unzip awscliv2.zip
                #sudo ./aws/install
                aws s3 cp s3://tomcat-bucket-1/student-${BUILD_ID}.war /home/ubuntu/
                curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /home/ubuntu
                sudo sh /home/ubuntu/apache-tomcat-8.5.85/bin/shutdown.sh
                sudo cp -rv /home/ubuntu/student-${BUILD_ID}.war /home/ubuntu/studentapp.war
                sudo cp -rv /home/ubuntu/studentapp.war /home/ubuntu/apache-tomcat-8.5.85/webapps/
                sudo sh /home/ubuntu/apache-tomcat-8.5.85/bin/startup.sh
                '''
                }
            }
        }
    }
}
