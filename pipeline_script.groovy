pipeline {
    agent any
    stages {
        stage('Fetch instances') {
            steps {
                echo "========================================================================================================"
                echo 'Logging into AWS CLI'
                script {
                    withCredentials([aws(credentialsId: 'awscli_access_secrets', variablePrefix: 'AWS')]) {
                        sh 'aws sts get-caller-identity'
                        echo "Login sucessful"
                        echo 'Collecting the data of running instances from AWS env'
                        sh '''
                        aws ec2 describe-instances \\
                        --filters "Name=tag:Project,Values=SSH-AUTO" \\
                        "Name=instance-state-name,Values=running" \\
                        --query "Reservations[*].Instances[*].[PublicIpAddress]" \\
                        --output text > public_ips.txt
                        '''
                        echo "Output generated to public_ips.txt file"
                        sh 'cat public_ips.txt'
                    }
                    
                }
                echo "======================================================================================================"
            }
        }
        stage('SSH into instances') {
            steps {
                echo "======================================================================================================"
                script {
                    def ipList = readFile('public_ips.txt').split(/\s+/).findAll { it }
                    if (ipList.isEmpty()) {
                        echo "No IPs found. Skipping SSH step."
                        return
                    }
                    echo "Found ${ipList.size()} IP(s) to SSH into..."

                    sshagent(['private_keys']) {
                        ipList.each { 
                            ip -> echo ">>> Connecting to ${ip}..."
                            try {
                                sh "ssh -o StrictHostKeyChecking=no -o BatchMode=yes ec2-user@${ip} 'sudo yum update -y'"
                            } catch (e) {
                                echo ">>> SSH failed on ${ip}: ${e.getMessage()}"
                            }
                            }
                        }
                    }
                echo "======================================================================================================"
                }
            }
        }
        
    }

