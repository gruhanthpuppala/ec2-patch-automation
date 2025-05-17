Simply follow the below steps to produce this:
- Install Docker by using REPO:software_installation
- docker pull jenkins/jenkins:jdk17
    - This install's java-17 and jenkins in your host machine.
- Run jenkins image, your application will be created.
- Login to Jenkins and create a Pipeline job and place this Jenkinsfile script inside the job.
- Give permission to Jenkins to connect with AWS and to perform the desired actions. (Create IAM user attach custom policy, assign access/secret keys for the user place them in jenkins which will be enabled by some jenkins plugins)


![Screenshot](https://github.com/gruhanthpuppala/ec2-patch-automation/blob/main/images/jk1.png?raw=true)
![Screenshot](https://github.com/gruhanthpuppala/ec2-patch-automation/blob/main/images/jk2.png?raw=true)
![Screenshot](https://github.com/gruhanthpuppala/ec2-patch-automation/blob/main/images/jk3.png?raw=true)
