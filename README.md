# cratekube
CrateKube is an open source container management platform for Kubernetes clusters

## Bootstrap CLI

The `cratekube` bootstrap CLI provides the ability to spin up the Cratekube administration cluster.

### How the CLI works

During cluster initialization the following actions take place:
- generate a ssh key to be used for EC2 instance access
- import the ssh key as an EC2 keypair
- deploy a Cloudformation stack to spin up required resources for the platform cluster
- create the K8s cluster (RKE) using the master and worker EC2 instances
- copy keypair and kubeconfig to the worker node at location `/var/lib/cratekube`

Cluster teardown will remove the imported EC2 keypair, and the cloudformation stack.  Once the teardown is complete all
`cratekube` resources should be removed from your AWS account.

### Running the CLI

The CLI is available via the docker image `cratekube/cratekube:<version>` requires environment variables for AWS API
access.  We use the AWS credentials to create the resources needed for a cluster, the resources created can be
viewed in the [cloudformation](src/main/resources/cloudformation) directory.

We recommend using a named volume or bind mount to persist the CLI application data.  An environment variable file can
also be used in place of individual `-e` flags to make an alias easier to define.

Example environment variable file:
```bash
AWS_ACCESS_KEY_ID=<value>
AWS_SECRET_ACCESS_KEY=<value>
AWS_REGION=<value>
```

Example alias:
```bash
alias cratekube='docker run --env-file /path/to/envfile -v cratekube-data:/app/cratekube --rm -it cratekube/cratekube:<version>'
```

Once the alias is available the CLI help can be accessed by calling `cratekube` or `cratekube --help`.  Every subcommand
has a help option that will display available options and inputs for a command.

### AWS requirements

In order to spin up a `cratekube` cluster your AWS API account will need access to create the following 
CloudFormation resources:
- `AWS::EC2::VPC`
- `AWS::EC2::Subnet`
- `AWS::EC2::InternetGateway`
- `AWS::EC2::VPCGatewayAttachment`
- `AWS::EC2::RouteTable`
- `AWS::EC2::Route`
- `AWS::EC2::SubnetRouteTableAssociation`
- `AWS::EC2::SecurityGroup`
- `AWS::EC2::Instance`

If your API credentials do not have access to create these resource types then the cluster will not be able to be properly
provisioned.
