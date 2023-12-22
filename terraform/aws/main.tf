terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region = var.aws_region
}

data "aws_ami" "amazon-linux" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }
  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }
  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

resource "aws_security_group" "math-pyramid-ec2-sg-tf" {
  name        = "math-pyramid-ec2-sg-tf"
  description = "Allow incoming traffic on port 5000 to math-pyramid app"
  vpc_id      = "vpc-8a5d84e0"

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Port 5000 access from anywhere
  ingress {
    from_port   = 5000
    to_port     = 5000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "tls_private_key" "math-pyramid-ec2-instance-key-tf" {
  algorithm = "RSA"
}
resource "aws_key_pair" "math-pyramid-ec2-instance-key-tf" {
  key_name   = "math-pyramid-ec2-instance-key-tf"
  public_key = tls_private_key.math-pyramid-ec2-instance-key-tf.public_key_openssh
}

resource "aws_iam_role" "math-pyramid-ec2-role-tf" {
  name = "math-pyramid-ec2-role-tf"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Sid       = ""
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })
}

data "aws_iam_policy" "amazon-s3-read-only-access-tf" {
  arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_policy_attachment" "math-pyramid-ec2-policy-attachment-tf" {
  name       = "math-pyramid-ec2-policy-attachment-tf"
  roles      = [aws_iam_role.math-pyramid-ec2-role-tf.name]
  policy_arn = data.aws_iam_policy.amazon-s3-read-only-access-tf.arn
}

resource "aws_iam_instance_profile" "math-pyramid-ec2-instance-profile-tf" {
  name = "math-pyramid-ec2-instance-profile-tf"
  role = aws_iam_role.math-pyramid-ec2-role-tf.name
}

resource "aws_instance" "math-pyramid-ec2-instance-tf" {
  instance_type               = var.instance_type
  ami                         = data.aws_ami.amazon-linux.id
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.math-pyramid-ec2-sg-tf.id]
  user_data                   = file("userdata.sh")
  key_name                    = aws_key_pair.math-pyramid-ec2-instance-key-tf.key_name
  iam_instance_profile        = aws_iam_instance_profile.math-pyramid-ec2-instance-profile-tf.name
}
