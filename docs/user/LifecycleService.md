[![License](http://img.shields.io/badge/license-apache%202.0-yellow)](http://choosealicense.com/licenses/apache-2.0/)
[![Sponsored By Cisco](https://img.shields.io/badge/sponsored%20by-Cisco-blue)](https://www.cisco.com/c/en/us/solutions/cloud/multicloud-solutions.html)
[![Coded with Groovy](https://img.shields.io/badge/language-Groovy-green)](https://github.com/apache/groovy)
[![12 Factor App](https://img.shields.io/badge/app-12--factor-yellow)](https://12factor.net/)

# Lifecycle Service
_A service responsible for managing CrateKube platform services and upgrades_

## Introduction
This **_lifecycle service_** is part of an [MVaP architecture](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md) and set of [requirements](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md) for [CrateKube](https://cratekube.github.io/) that creates infrastructure [VPC](https://aws.amazon.com/vpc/)s, bootstraps, and configures [Kubernetes](https://kubernetes.io/) clusters on AWS [EC2](https://aws.amazon.com/ec2/pricing/) using [CloudFormation](https://aws.amazon.com/cloudformation/) templates and [Terraform](https://www.terraform.io/). The underlying objective of our product is to provide default secure, ephemeral, cloud-ready instances that will launch on [AWS](https://aws.amazon.com/ec2/). This approach is based on an Open Source Software initiative within Cisco called [NoOps](https://www.cio.com/article/3407714/what-is-noops-the-quest-for-fully-automated-it-operations.html), which takes a first iterative step towards modifying the IT lifecycle in order for zero human intervention to be necessary for mundane orchestration tasks.

## What does this service do?
The lifecycle-service is in charge of managing, deploying and upgrading itself and other CrateKube platform services (or managed components) running in a [Kubernetes cluster](https://kubernetes.io/docs/tutorials/kubernetes-basics/create-cluster/) using [kubectl](https://kubernetes.io/docs/reference/kubectl/overview/).  

There are three **managed components**: [cloud-mgmt-service](https://github.com/cratekube/cloud-mgmt-service), [cluster-mgmt-service](https://github.com/cratekube/cluster-mgmt-service), and [lifecycle-service](https://github.com/cratekube/lifecycle-service).
Each **managed component**, including the **lifecycle-service** itself, contains a `deployment.yml` file in the root of its GitHub repository with **Kubernetes** [NodePort Service](https://kubernetes.io/docs/concepts/services-networking/service/#nodeport) and [Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) resources. This `deployment.yml` file is used by the **lifecycle-service** to deploy the **managed component** . 

The **lifecycle-service** manages [Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)s using [matchLabels](https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/#resources-that-support-set-based-requirements). 
The **matchLabels** key used is `name` and its value is the name of the **managed component**s repository, for example: `lifecycle-service`, `cloud-mgmt-service` and `cluster-mgmt-service`. 
A good example of this is the **lifecycle-service**s [deployment.yml](https://github.com/cratekube/lifecycle-service/blob/master/deployment.yml).

Versions of `deployment.yml` are managed by release (tag) and if a **managed component** does not exist on the **Kubernetes cluster** the **lifecycle-service** is configured to manage, the latest version (retrieved from the repositories `/releases.atom`) is used to retrieve the `deployment.yml` and deploy it. 
This behavior can be overridden for each **managed compent** by providing the `CLUSTER_MGMT_ENABLED`, `CLOUD_MGMT_ENABLED`, or `LIFECYCLE_ENABLED` environment variables with `false` values when running the application

The **lifecycle-service** uses a [kubeconfig](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig/) file to communicate with the **Kubernetes cluster**. 
This defaults to `/app/kube/kubeconfig` and can be changed by providing the `KUBE_CONFIG_LOCATION` environment variable when running the application. 

When utilized as a component, this service can act as a stand-alone service for managing other deployable components, and can be easily extended by forking and [developing](https://github.com/cratekube/cratekube/blob/master/docs/Development.md) as a [CrateKube contributor](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md).

## How this service can be used
### Quickstart
To run this service locally, simply execute:
```bash
docker run -p 8080:9000 -v /path/to/kube/config:/app/kube/kubeconfig cratekube/lifecycle-service
```
Note: We are bind mounting the `/path/to/kube/config` file inside of the container at `/app/kube/kubeconfig` in order for **kubectl** to execute successfully.

### Building and Running locally with Docker
We strive to have our builds repeatable across development environments so we also provide a Docker build to generate 
the Dropwizard application container. The examples below should be executed from the root of the project.

##### Run the base docker build:
```bash
docker build -t lifecycle-service:local --target build .
```
Note: This requires docker 19.03.x or above. Docker 18.09 will throw errors for mount points and the `--target` flag.

##### Build the package target:
```bash
docker build -t lifecycle-service:local --target package .
```
##### Run the docker application locally on port 8080:
```bash
docker run -p 8080:9000 -v /path/to/kube/config:/app/kube/kubeconfig -d lifecycle-service:local
```

##### Fire up the Swagger specification by visiting the following URL in a browser:
```bash
http://localhost:8080/swagger
```
Note: The `POST` endpoint requires API bearer token authentication. 
The token value can be configured by providing the `ADMIN_APIKEY` environment variable when running the application. 
The default value is `eknvDrmcDtseeieSMTvngo`

### Using the API
The API has endpoints that allow you to retrieve component upgrade availability and deploy specific **managed component** versions. 

The resulting operations exist as REST endpoints, which you can hit in your browser or with a tool such as [Postman](https://www.postman.com/downloads/).

| HTTP Verb | Endpoint | Payload | Authorization | Function |
| --- | --- | --- | --- | --- |
| GET | /component/version | None | None | Retrieve a list of all managed components with upgrade availability |
| GET | /component/{name} | None | None |Retrieve a specific managed component with upgrade availability |
| POST | /component/{name}/version | <code>{"version":"string"}</code> | API bearer token | Deploy a specific version of a managed component |
