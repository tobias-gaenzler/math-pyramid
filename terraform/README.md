# Deploy with terraform

## Preconditions:

This deployment expects the following resources to exist:

* the default VPC
* the S3 bucket with name "math-pyramid" with the file "math-pyramid.jar"

## Deployment

Execute the following steps:

* terraform init
* terraform plan
* terraform apply
* terraform destroy

It seems, that the deployment with terraform is faster than the deployment with CDK (see *MathPyramidEc2Stack*)
