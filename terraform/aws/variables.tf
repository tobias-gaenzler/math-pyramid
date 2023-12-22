variable "aws_region" {
  description = "The AWS region to create things in."
  default     = "eu-central-1"
}

variable "instance_type" {
  default     = "t3.micro"
  description = "AWS instance type"
}

variable "instance_name" {
  type    = string
  default = "math-pyramid-ec2-instance"
}
