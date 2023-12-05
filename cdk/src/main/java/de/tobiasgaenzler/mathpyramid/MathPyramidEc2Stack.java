package de.tobiasgaenzler.mathpyramid;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

// run "cdk bootstrap (if CDKToolkit is not already present)", "cdk synth", "cdk deploy"
// This stack expects the following resources to exist:
// -- the default VPC
// -- the S3 bucket with name "math-pyramid" with the file "math-pyramid.jar"
// Cleanup: cdk destroy AND remove the CDKToolkit stack (if desired)
// aws cloudformation delete-stack --stack-name CDKToolkit
//        aws s3 ls | grep cdktoolkit # copy the name
//        aws s3 rb --force s3://cdktoolkit-stagingbucket-abcdef # replace the name here
public class MathPyramidEc2Stack extends Stack {
    public MathPyramidEc2Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MathPyramidEc2Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        IVpc vpc = Vpc.fromLookup(this, "vpc-8a5d84e0", VpcLookupOptions.builder().isDefault(true).build());
        Role ec2Role = new Role(this, "math-pyramid-ec2-role", RoleProps.builder().assumedBy(new ServicePrincipal("ec2.amazonaws.com")).build());
        ec2Role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("AmazonS3ReadOnlyAccess"));
        SecurityGroup securityGroup = new SecurityGroup(this, "math-pyramid-ec2-sg",
                SecurityGroupProps.builder()
                        .vpc(vpc)
                        .allowAllOutbound(true)
                        .securityGroupName("math-pyramid-ec2-sg")
                        .build());

        // Enable if you want to access the instance via SSH
//        securityGroup.addIngressRule(
//                Peer.anyIpv4(),
//                Port.tcp(22),
//                "Allows SSH access from Internet");

        securityGroup.addIngressRule(
                Peer.anyIpv4(),
                Port.tcp(5000),
                "Allows HTTP access on port 5000 from Internet"
        );

        CfnKeyPair ec2KeyPair = new CfnKeyPair(this, "math-pyramid-ec2-instance-key",
                CfnKeyPairProps.builder()
                        .keyName("math-pyramid-ec2-instance-key")
                        .build());

        UserData userData = UserData.forLinux();
        userData.addCommands("sudo yum -y install java-17-amazon-corretto",
                "aws s3 cp s3://math-pyramid/math-pyramid.jar /home/ec2-user/math-pyramid.jar",
                "sudo chmod 777 /home/ec2-user/math-pyramid.jar");
        userData.addOnExitCommands("nohup java -jar /home/ec2-user/math-pyramid.jar > /home/ec2-user/application.log &");

        Instance ec2Instance = new Instance(this, "math-pyramid-ec2-instance",
                InstanceProps.builder()
                        .vpc(vpc)
                        .role(ec2Role)
                        .securityGroup(securityGroup)
                        .instanceName("math-pyramid-ec2-instance")
                        .instanceType(InstanceType.of(InstanceClass.T3, InstanceSize.MICRO))
                        .machineImage(MachineImage.latestAmazonLinux2023())
                        .userData(userData)
                        .keyName(ec2KeyPair.getKeyName())
                        .build());

        new CfnOutput(this, "math-pyramid-ec2-output",
                CfnOutputProps.builder()
                        .value("http://")
                        .value(ec2Instance.getInstancePublicIp())
                        .value(":5000")
                        .build());

    }
}
