package de.tobiasgaenzler.mathpyramid;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.List;

// Creates the fargate infrastructure, but does not run a task!

// run "cdk bootstrap (if CDKToolkit is not already present)", "cdk synth", "cdk deploy"
// This stack expects the following resources to exist:
// - the current docker image found in tobiasgaenzler/math-pyramid on dockerhub
// use e.g. "Build and push docker image to dockerhub" workflow to update image

// Cleanup: cdk destroy AND remove the CDKToolkit stack (if desired)
// aws cloudformation delete-stack --stack-name CDKToolkit
//        aws s3 ls | grep cdktoolkit # copy the name
//        aws s3 rb --force s3://cdktoolkit-stagingbucket-abcdef # replace the name here
public class MathPyramidFargateStack extends Stack {
    public MathPyramidFargateStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MathPyramidFargateStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        IVpc vpc = Vpc.fromLookup(this, "vpc-8a5d84e0", VpcLookupOptions.builder().isDefault(true).build());

        Cluster cluster = new Cluster(this, "math-pyramid-fargate-cluster",
                ClusterProps.builder()
                        .clusterName("math-pyramid-fargate")
                        .vpc(vpc)
                        .enableFargateCapacityProviders(true)
                        .build());
        cluster.addDefaultCapacityProviderStrategy(List.of(
                CapacityProviderStrategy.builder()
                        .weight(1)
                        .capacityProvider("FARGATE_SPOT")
                        .build()
        ));


        Role ecsTaskExecutionRole = new Role(this, "math-pyramid-fargate-role",
                RoleProps.builder()
                        .assumedBy(new ServicePrincipal("ecs-tasks.amazonaws.com"))
                        .description("Role assumed by ECS to run math-pyramid containers")
                        .build());
        ecsTaskExecutionRole.addManagedPolicy(
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AmazonECSTaskExecutionRolePolicy"));

        SecurityGroup securityGroup = new SecurityGroup(this, "math-pyramid-fargate-security-group",
                SecurityGroupProps.builder()
                        .vpc(vpc)
                        .allowAllOutbound(true)
                        .securityGroupName("math-pyramid-fargate-security-group")
                        .build());

        securityGroup.addIngressRule(
                Peer.anyIpv4(),
                Port.tcp(5000),
                "Allows HTTP access on port 5000 from Internet"
        );


        FargateTaskDefinition taskDefinition = new FargateTaskDefinition(this, "math-pyramid-fargate-task",
                FargateTaskDefinitionProps.builder()
                        .family("math-pyramid-fargate-task")
                        .runtimePlatform(
                                RuntimePlatform.builder()
                                        .cpuArchitecture(CpuArchitecture.X86_64)
                                        .operatingSystemFamily(OperatingSystemFamily.LINUX)
                                        .build())
                        .cpu(512)
                        .memoryLimitMiB(1024)
                        .executionRole(ecsTaskExecutionRole)
                        .build());

        taskDefinition.addContainer("math-pyramid-container",
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry("tobiasgaenzler/math-pyramid"))
                        .containerName("math-pyramid-fargate")
                        .essential(true)
                        .cpu(512)
                        .memoryLimitMiB(1024)
                        .portMappings(
                                List.of(
                                        PortMapping.builder()
                                                .name("math-pyramid-5000-tcp")
                                                .containerPort(5000)
                                                .hostPort(5000)
                                                .protocol(Protocol.TCP)
                                                .appProtocol(AppProtocol.getHttp())
                                                .build()))
                        .logging(LogDriver.awsLogs(
                                AwsLogDriverProps.builder()
                                        .logRetention(RetentionDays.ONE_DAY)
                                        .streamPrefix("ecs-math-pyramid-fargate")
                                        .build()))
                        .build());

        new FargateService(this, "math-pyramid-fargate-service",
                FargateServiceProps.builder()
                        .serviceName("math-pyramid-fargate-service")
                        .desiredCount(1)
                        .vpcSubnets(SubnetSelection.builder().subnets(vpc.getPublicSubnets()).build())
                        .securityGroups(List.of(securityGroup))
                        .cluster(cluster)
                        .assignPublicIp(true)
                        .taskDefinition(taskDefinition)
                        .build());

        new CfnOutput(this, "math-pyramid-fargate-output",
                CfnOutputProps.builder()
                        .value("TODO: find IP address, via aws ecs list-tasks --cluster fargate-cluster --service fargate-service")
                        .value("TODO: aws ecs describe-tasks --cluster fargate-cluster --tasks arn:aws:ecs:us-east-1:123456789012:task/service/EXAMPLE")
                        .value("TODO: aws ec2 describe-network-interfaces --network-interface-id  eni-0fa40520aeEXAMPLE")
                        .build());


    }
}
