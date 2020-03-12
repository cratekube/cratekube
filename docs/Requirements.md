## Introduction

The following requirements are intended to provide guidance and structure for running Crate on Kubernetes. Each requirement has been identified as an essential part of the architecture and should be incorporated to maximize value to administrators and customers.

Scope
=====

These requirements are scoped to encompass everything needed for an enterprise grade Kubernetes deployment. Given the large amount of requirements that follow, it is recommended that multiple stages are created to bring the platform to life.

Requirements
============

Host configuration
------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want my container deployments repeatable and consistent between the underlying hosts, so that my deployment has improved operational stability, because hosts that drift from the expected configuration may result in unexpected deployment behavior.

### Use custom AMI/ISO

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want hosts to be booted using a custom AMI/ISO, so that security and boot time are prioritized, because using something off the shelf has more services configured out of the box that increases the attack surface and overall boot time.

### Ephemeral Hosts

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want hosts to be ephemeral, so that data is not persisted to host, because without enforcement future host upgrades may result in data loss.


Container configuration
-----------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want containers to be automatically hardened against common accidental or malicious risks, so that by default my container deployments enforce best-practices, because users may make assumptions about running containers and not all users may know about best practices.

### Pod security policies

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want pod security policies to protect the platform from compromising actions, so that my deployment is more reliable and resilient, because an unsecured platform may be compromised by malicious or accidental actions.

### Read-only container file systems

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want container file systems to be read-only by default and require opt-out to become read-write, so that I learn quickly that if I want file system data to be durable I must mount volumes to my containers, because deployments that assume the container file system are durable will eventually lose its data.

### User namespace mapping

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want docker user namespaces enabled, so that I may use root in my container and the host and my container are protected from container escapes, because otherwise root user inside a container will have root outside the container if it escapes.

### Docker 19.03+ Linux 4.8+

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want Docker 19.03+ and Linux kernel 4.8+, so that I may safely run docker-in-docker, because user mode Linux can now run docker without additional security capabilities.

Elastic provisioning
--------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want resource allocation changes delivered in a median time of 5 minutes or less, so that I may deliver and grow my deployments faster, because accurately forecasting resource needs and budget is prone to error which make static (or slow) resource management difficult.

### Resource pools instead of hosts

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want only a logical representation of resources allocated as "resource pools", so that I don't have to couple my deployment to specific hosts and configuration, because it's unavoidable that people may design their solution specifically using a host-to-container relationship when presented with hosts as the allocation unit of resources.

### External deployment accessibility

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want my deployment to be accessible from outside the Cisco WAN, so that partners and customers may access my deployment from their homes or offices, because partners and customers may not always have access to the Cisco VPN.

### AWS public cloud presence

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want Crate to have a public cloud presence on AWS, so that my requirement for external partner and customer access to my deployment can be fulfilled, because exceptions for inbound traffic to the Cisco WAN are difficult to obtain and leveraging AWS aligns with the Cisco Ops Organizational strategy.

### On-premise cloud

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want an on-premise presence, so that my deployment is not unnecessarily exposed to the public Internet, because not all deployments need to be externally accessible.

Automated operational insight
-----------------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want operational insights integrated automatically into my deployment, so that I may make informed and timely decisions in the interest of my users, because deploying and maintaining my own operational insights stack is non-trivial and is a duplicated effort across teams.

### Automated deployment monitoring

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want automated real-time and historical insight into my deployment health and resource metrics, so that I may make informed decisions based on the operational status and history of my deployment , because without operational visibility I can not make informed decisions on how to best serve my users.

### Automated deployment logging

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want automated real-time and historical insight into logs generated by my deployment, so that I may more easily meet compliance requirements and analyze logs for security, defect, or operational interests, because without logging I do not have a record of past events that have occurred in my deployment.

### Deployment status alerting

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want configurable notifications about my deployment health, resource metrics, and logs, so that I have improved situational awareness of my development, because operational insight is insufficient without a timely awareness of anomalies.

Tenancy isolation
-----------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want my tenancy isolated from others, so that I have a higher degree of resiliency and change management, because a shared environment may have unexpected or undesired side-effects caused by neighboring tenants.

### Isolated control planes

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want my deployment's control plane to be isolated from others, so I have a higher degree of resiliency and change management, because average global platform stability is easier to ensure using many smaller control planes.

### Event-based control-plane automation

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want Crate to use event-driven architecture to manage my deployment's control plane, so I have a higher degree of resiliency and change management, because events promote decoupling and can be more easily queued, retried and recovered than direct service invocation.

Secret management
-----------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want secret management integrated with my deployment, so that can more easily adhere to Cisco security policies, because managing secrets is non-trivial and is a duplicated effort across teams

### Etcd Secret Migration

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

### Cisco InfoSec Keeper

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want my deployment's secrets stored in [Keeper](https://confluence-eng-rtp1.cisco.com/conf/x/uoQYBQ), so that I have improved confidence in the security of the platform's secret management, because Keeper is a service directly managed by Cisco InfoSec.

Persistent storage
------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want persistent storage options for my deployment, so that my deployment is able to persist state, because very few deployments are designed so that they have durable state across ephemeral storage.

### Cisco legacy NFS automount

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want legacy NFS automount access on-premise, so that I may integrate with services and users that rely on NFS automounts, because NFS automounts are a 20-year legacy at Cisco and their use is unavoidable for many user workflows.

### Clustered file system

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want storage volumes to be backed by a clustered file system, so that I may share files between containers even when they are scheduled across hosts, because some some deployments expect containers to be collocated on the same host.

### VMDK-backed storage

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want VMDK-backed persistent volumes to be dynamically attached to my on-premise deployment's underlying hosts, so that I have persistent storage that is managed outside of the host, because deployments may periodically be rescheduled on to different underlying hosts and mounted volumes must be accessible after rescheduling.

### EBS-backed storage

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want EBS-backed persistent volumes to be dynamically attached to my aws-based deployment's underlying hosts, so that I have persistent storage that is managed outside of the host, because deployments may periodically be rescheduled on to different underlying hosts and mounted volumes must be accessible after rescheduling.

User experience
---------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want a full-featured experience managing my deployments favoring simplicity and a low-barrier-of entry, so that I can rapidly adopt containers for my deployment and ease my operational burden, because most cloud platform experiences favor technical capability over ease-of-use.

### Consumption-based billing

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want consumption-based billing for on-demand resources, so that I may better manage operational costs associated with my deployment, because I only want to pay for what my deployment consumes.

### Workflow-oriented command-line interface

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want the command-line interface centered on the top 3 goals each of developers, operators, and support agents, so that my time spent using the interface is minimized, because bringing focus to the most commonly access information and actions will improve productivity.

### Workflow-oriented web interface

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want the web UI centered on the top 3 goals each of developers, operators, and support agents, so that my time spent using the interface is minimized, because bringing focus to the most commonly access information and actions will improve productivity.

### Automation-oriented web interface

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want automated management of my deployment via a rest-based API, so that I can extend and enhance the platform with integrations specific to my use-cases, because it is not possible for Crate to capture everyone's use cases first-class in the platform.

### Developer-role (user) documentation

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want comprehensive developer documentation, so that I may understand how to build my deployment for the platform, because without developer documentation the specifics of implementing a deployment can not be self-learned.

### Operator-role (user) documentation

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want comprehensive operator documentation, so that I may understand how to manage my deployment on the platform, because without operator documentation the specifics of managing a deployment can not be self-learned.

### Support-role (user) documentation

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want comprehensive support documentation, so that I may understand how to troubleshoot and resolve issues with my deployment, because without support documentation the specifics of troubleshooting and resolving issues with a deployment can not be self-learned.

### User-centric error messages

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want error messages linked to my inputs rather than platform internals, so that I have actionable information about what triggered the error even if the error was internal, because without messages reflecting my inputs it is much harder to correct for or workaround issues.

#### Suggestions via machine learning

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want machine learning to securely suggest best practices and fixes for common problems, so that I spend less time encountering and troubleshooting issues with my deployment, because ML can be used to cross reference a deployment's configuration and failures with observed-good configuration of similar deployments elsewhere on the platform.

### docker-compose v2 deployment spec

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want support for docker-compose v2 yml, so that I can retain the simplicity that Crate currently offers, because Kurbenetes-yaml is verbose and difficult to understand.

### Template-based deployments

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want to use templates as a basis for repeated deployments, so that I may more easily specify changes to the deployment at deploy-time, because without template support repeated deployments becomes tedious and error-prone.

Value-add services
------------------

![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg) 

As a user, I want pre-integrated services to support my deployment, so that I do not have to research, deploy, or maintain additional services that are not core to my business, because production deployments have cross-cutting concerns that are inefficient for each tenant to independently solve.

### API Gateway

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want an API Gateway in front of my deployment, so that I may discretely manage my deployment's public API in a secure and robust way, because without an API Gateway tenants must solve for security, routing, rate-limiting, independently.

### DNS Management

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want automated public DNS names, so that I am able to reach my deployment via a human-friendly DNS name, because without automated DNS management tenant workloads must be bound to specific IPs.

### Certificate Management

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want automated HTTPS certificates, so that I can more easily comply with Cisco InfoSec requirements, because without automated cert management certificate provisioning is tedious and error-prone.

### DNS-based Failover

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want automated DNS-based failover, so that I can engineer my deployment without a single-point of failure, because without DNS-failover, all traffic will ultimately be dependent one host.

Shared Requirements
-------------------

### Compatible with Kubernetes

![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg) 

As a user, I want microservices to be compatible with Kubernetes, so that I receive all the benefits of a container orchestrator, because otherwise I have to treat my hosts as pets instead of pokemon.

### Self-Validate required dependencies

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want microservices to self-validate required dependencies, so that I can be certain the service will work as expected, because otherwise the service will not work.

### Self-Validate optional dependencies

![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)

As a user, I want microservices to self-validate optional dependencies, so that we can present features to the end user, because otherwise the service will either provide all features or no features.

### Self-Validate policy dependencies

![Generic badge](https://img.shields.io/badge/BUSINESS-POSTMVP-brightgreen) 

As a user, I want microservices to self-validate policy dependencies, so that compliance can be validated, because otherwise an errant service may be deployed that compromises internal policies.
