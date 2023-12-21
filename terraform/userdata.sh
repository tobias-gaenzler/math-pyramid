#!/bin/bash
echo "Executing user data"
sudo yum -y install java-17-amazon-corretto

aws s3 cp s3://math-pyramid/math-pyramid.jar /home/ec2-user/math-pyramid.jar

sudo chmod 777 /home/ec2-user/math-pyramid.jar

nohup java -jar /home/ec2-user/math-pyramid.jar > /home/ec2-user/application.log &