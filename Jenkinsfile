pipeline {
    agent any
    stages {
        stage('AWS env setup & Fetch Instance details') {
            steps {
                echo "========================================================================================================"
                echo 'Logging into AWS CLI'
                script {
                    try {
                        echo "Fetching creds from the secrets"
                        withCredentials([aws(credentialsId: 'awscli_access_secrets', variablePrefix: 'AWS')]) {
                            echo "Setting AWS ENV..."
                            sh 'aws sts get-caller-identity'
                            echo "Login sucessful" 
                            echo "Fetching running instances for the TAG Project=SSH-AUTO"
                            // Comment:
                            // future update: we can bring a feature to this pipeline to give a user input to fetch the users choice of TAG
                            // so this can be use to fetch instances of multiple tags to perform package updates  
                            sh '''
                            aws ec2 describe-instances \\
                            --filters "Name=tag:Project,Values=SSH-AUTO" \\
                            "Name=instance-state-name,Values=running" \\
                            --query "Reservations[*].Instances[*].[PublicIpAddress]" \\
                            --output text > public_ips.txt
                            '''
                        }
                    } catch (e) {
                        echo "Error occured with exit code:Error while fetching the Instance list for TAG Project=SSH-AUTError while fetching the Instance list for TAG Project=SSH-AUT ${e.getMessage()}"
                        error("Halting pipeline; No running instances found / AWS env config issue.")
                        }
                }
                echo ("Collecting the data required.....")
                echo "========================================================================================================"
            }
        }
        stage('Data Existance Check') {
            steps {
                echo "========================================================================================================"
                script {
                    try {
                        def status = sh(script: 'test -f public_ips.txt', returnStatus: true)
                        if (status == 0) {
                            echo "Data exist and ready to update the packages"
                        } 
                        else {
                            echo "No file/data found. Exiting....."
                            currentBuild.result = 'FAILURE'
                            return
                        }
                    } catch (e) {
                        currentBuild.result = 'FAILURE'
                        echo "No file found: ${e.getMessage()}"
                    }
                }
                echo "========================================================================================================"
            }
        }
        stage('Login into instance to update packages') {
            steps {
                echo "========================================================================================================"
                script {

                    def ipList = readFile('public_ips.txt').split(/\s+/).findAll { it }

                    if (ipList.isEmpty()) {
                        echo "File exist but No IPs found. Skipping SSH step."
                        return
                    }

                    echo "Found ${ipList.size()} IP(s), performing SSH into each and update the packages"

                    sshagent(['private_keys']) {
                        ipList.each {
                            ip -> echo "Trying to connect ${ip}"
                            try {
                                def status = sh(
                                    script: "ssh -o StrictHostKeyChecking=no -o BatchMode=yes ec2-user@${ip} 'sudo yum update -y'",
                                    returnStatus: true
                                )
                                if (status == 0) {
                                    echo "Connected to ${ip} and performed the update."
                                    echo "Update completed"
                                } else {
                                    echo "Unable to connect to ${ip}, SSH Error with exit code: ${status} "
                                }
                            } catch (e) {
                                echo ">>> SSH failed on ${ip}: ${e.getMessage()}"
                            }
                        }
                    }
                }
                echo "========================================================================================================"
            }
        }
    }
}