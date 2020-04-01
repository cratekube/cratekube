# Overview
This document specifies the process for developing a new component in the CrateKube project. This process is based on the Crate's team experience and industry standard best practices. 

# Stages
##  Proof-of-Concept [ Optional ]
A proof of concept is a small excercise to test the viability of a design or assumption. This is an optional stage for those issues where a solution may not be immediately clear or where the design relies on an untested assumption. A POC may be used to drive the architecture and design stage but it is not intended to circumvent the development process and should not be used to skip all of initial stages straight to implementation. 

##  Architecture

“Without good software design, programming is an art of adding bugs to an empty text file” -Louise Srygley

A well thought out design is essential to writing code that is efficient, maintainable and easy to understand. When creating a new component for CrateKube the first required step is to create a design in the cratekube/cratekube repository. The preferred method of documenting the design is using UML diagrams with text based descriptions. (Examples can be seen here ......). At a minimum the design should include a component breakdown with UML diagrams and a standalone requirements document. If relevant, a list of assumptions, open questions and design decisions should be included. 

The architecure design should be communicated to the Crate team early on to ensure all possible scenarios are covered. 

##  Code-level Design
The code-level design is the first coding step of the development process. This step begins with creating a repository for the new component (cratekube/newcomponentname). If the new component is a standard dropwizard microservice application the cratekube/dropwizard-groovy-template template can be used as a starting point. This template contains all the files needed to build a Groovy based dropwizard application, run tests and deploy to Crate's CI/CD pipeline. Once the repository is set up with all of the files needed to build and deploy the component it should be commited and a PR review should be submitted. This first PR should contain nothing but the essential project files so that later additions can be reviewed independently of project files. 

Once a PR is submitted for the initial project the actual design portion can begin. The first step is to create the model classes that specify the domain on which this component will operate. Once the model is complete we can define service level interfaces that specify the actions the component will perform internally. The last step is to create REST endpoints that will define how the components functions will be accessed. CrateKube components should always be designed with containerization in mind, all microservices should have a relevant docker-compose.yaml file and be fully runnable inside of a container. 

All of the code should have appropriate and relevant comments to describe the functionality in sufficient enough detail for someone else to implement.
- Comments should be work appropriate and focus on the content of the code.
- Detailed information on the contracts should appear on Interfaces
- Detailed information on the implementation should appear on the classes 

During the code-level design phase you may find that the original Architecture requires modification, this is normal. Simply update the Architecture section and open another PR for it as part of your design.  

##  Automated Testing
The CrateKube project follows a Test-Driven Development approach for creating components. Instead of writing code then tests, we ask that tests are written first. This process drives design by implementing use cases which the code must satisfy, i.e., tests should be structured such that an implementer has enough information to develop the microservice. If code needs to be written for which there are no tests, unit tests should be written first and the code should be built in an iterative way such that the tests help determine the design.

Where possible tests should be automated as part of the CI/CD process otherwise a manual testing section should be included in the projects README.md file. Tests should be done at the unit level (single class, mocked dependencies), internal integration (multiple internal classes tested together) and external end-to-end tests. External end-to-end tests should run against a containerized version of the component and all of it's dependencies. 

Since the implementation phase has not been completed most of the tests would fail when run. It is therefore imperative to annotate any tests that are expected to fail with Spock's `@PendingFeature` annotation until the implementation is complete. 

Creating tests often reveals gaps in the Architecture and Design. If this is the case please update those sections with the relevant modifications based on your improved understanding of the problem. 

##  Implementation 
Finally we have arrived at the last step, implementing the features of the new CrateKube component. At this point all of your components should have defined interfaces and well understood behaviours. While coding we ask that you follow standard software development practices and methodologies: DRY, SOLID, 12-Factor App and etc...

REST APIs should be designed following standard principles such as: using nouns not verbs, GET should never alter state, using single nouns and etc..

Implementation often reveals gaps in the tests, design and architecture of the component. Those should be modified as needed to ensure that they reflect the reality of the final implementation. 

### Security
Most Crate microservices rely on authentication to happen prior to receipt of request, e.g., an api gateway outsourcing authentication to a third party service. The upstream authenticator will provide a JWT that contains the roles that the authenticated user belongs to. Authorization is then handled by the Crate microservice, mapping the JWT roles to a set of application permissions. For Java/Groovy projects, a token based auth bundle for Keycloak is use to consume the JWT and make authorization determinations. 

## CI/CD
CrateKube components use Jenkins as the CI/CD pipeline management tool. Each project will have a /ci folder containing all the scripts required to run a build with tests and push the component image to a centralized repository. 

## Code Reviews
Code Reviews are performed after a PR is opened. For details on this process please review the contribution guidelines at https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md
