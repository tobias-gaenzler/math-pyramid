# math-pyramid
Vaadin app for math pyramid, a math exercise to train basic addition/subtraction skills.

# Deploy to AWS Elastic Beanstalk (in progress)
See https://exampledriven.wordpress.com/2017/01/09/spring-boot-aws-elastic-beanstalk-example/
- install elastic beanstalk cli
- eb create -s math-pyramid -> create environment without load balancer
- somehow set server port to 5000 (todo)
- eb deploy
- eb terminate --force math-pyramid



Use maven plugin: Beanstalk Maven Plugin – Beanstalker Maven Plugin - Usage ?



We are terminating the environment instead of pausing it (cleaner)
Add pause: https://jun711.github.io/aws/how-to-pause-or-stop-elastic-beanstalk-environment-from-running/
eb scale 0 math-pyramid

