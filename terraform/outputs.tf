output "ami" {
  value = data.aws_ami.amazon-linux.id
}

output "math-pyramid-public-ip" {
  value = aws_instance.math-pyramid-ec2-instance-tf.public_ip
}

output "private_key" {
  value     = tls_private_key.math-pyramid-ec2-instance-key-tf.private_key_pem
  sensitive = true
}

output "public_key" {
  value     = tls_private_key.math-pyramid-ec2-instance-key-tf.public_key_openssh
  sensitive = true
}
