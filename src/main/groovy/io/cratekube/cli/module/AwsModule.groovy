package io.cratekube.cli.module

import com.google.inject.AbstractModule
import io.cratekube.cli.api.CloudformationApi
import io.cratekube.cli.api.Ec2Api
import io.cratekube.cli.service.CloudformationService
import io.cratekube.cli.service.Ec2Service
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.ec2.Ec2Client

/**
 * Dependencies required for AWS components
 */
class AwsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind Ec2Client toInstance Ec2Client.builder().build()
    bind CloudFormationClient toInstance CloudFormationClient.builder().build()
    bind Ec2Api to Ec2Service
    bind CloudformationApi to CloudformationService
  }
}
