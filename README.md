# Math Pyramid
Vaadin app for math pyramid, a math exercise to train basic addition/subtraction skills.
![Math Pyramid](https://github.com/tobias-gaenzler/math-pyramid/blob/main/src/main/resources/images/help_start.jpg?raw=true)

## Technical Information
The application is implemented with spring boot, Vaadin and websockets (for multiplayer).


### CI/CD: GitHub Actions

Currently, three workflows are implemented:
- Build and push docker image to dockerhub (on push/merge): The application is continuously build and a docker image is pushed to [dockerhub](https://hub.docker.com/r/tobiasgaenzler/math-pyramid).
- Build and deploy to AWS Elastic Beanstalk (on demand only)
- Terminate AWS Elastic Beanstalk environment (on demand only)


