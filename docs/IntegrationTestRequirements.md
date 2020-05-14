# Introduction
The following requirements are intended to provide guidance and structure for building integration tests for the CrateKube platform. 
Each requirement has been identified as an essential part of the architecture and should be incorporated to maximize value to maintainers and contributors.
                                                                                                                                     
# Scope
These requirements are scoped to encompass everything needed for integration level testing of the CrateKube platform.
# Requirements

### CrateKube integration tests should be placed in each individual microservice's repo
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want CrateKube's integration tests to reside in each individual microservice's repo so that I have a clear understanding of what is being tested. 

### CrateKube integration tests will be written using Spock to test REST endpoints 
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want CrateKube's integration tests to be written using Spock so that I have a clear, concise test structure

### CrateKube integration tests will be organized under a single Gradle task
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want CrateKube's integration tests to be under a single Gradle task so that they can be run independently of unit tests. 

### CrateKube integration tests will be run against an AWS account (where required) specified at runtime
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want AWS credentials for integration test's that require them to be stored in the environment so that this AWS account can remain secure.  

### CrateKube integration tests will be only be execute manually at the discretion of the maintainer team
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want CrateKube's integration tests to only execute manually so that the testing AWS account remains secure. 

### CrateKube integration tests will have the ability to be executed automatically 
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want CrateKube's integration tests to be written in a way that they can be run automatically so that eventually they can be run during automatic builds. 

### CrateKube integration tests will be executed using Docker and Gradle 
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)

As a contributor, I want integration tests to be executed with Docker and Gradle so that we have a standardized toolset for executing tests. 

## Design Decisions 
Having automated integration tests that run when a PR is open would allow anyone to create any resource they want in CrateKube's AWS account. 
Therefore we have decided to only run integration tests manually (for now) to protect us from this security vulnerability. 
Despite this restriction, CrateKube integration tests will still be designed in such a way that, in the future, they will be automatically executed during the CI process. 

Since integration tests will not be run automatically for each PR the CrateKube maintainer team will need to figure out a procedure to review PRs that ensures
that the system continues functioning as a whole. 
