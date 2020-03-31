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

Once a PR is submitted for the initial project the actual design portion can begin. The first step is to create the model classes that specify the domain on which this component will operate. Once the model is complete we can define service level interfaces that specify the actions the component will perform internally. The last step is to create REST endpoints that will define how the components functions will be accessed. 

All of the code should have appropriate and relevant comments to describe the functionality in sufficient enough detail for someone else to implement. 

##  Automated Testing

#### Use of Containers for Testing
##  Implementation 

## CI/CD
## Code Reviews

