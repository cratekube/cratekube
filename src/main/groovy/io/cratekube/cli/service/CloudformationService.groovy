package io.cratekube.cli.service

import groovy.util.logging.Slf4j
import io.cratekube.cli.api.CloudformationApi
import org.apache.commons.vfs2.FileSystemManager
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest
import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse
import software.amazon.awssdk.services.cloudformation.model.DeleteStackRequest
import software.amazon.awssdk.services.cloudformation.model.DeleteStackResponse
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse
import software.amazon.awssdk.services.cloudformation.model.Parameter
import software.amazon.awssdk.services.cloudformation.model.Stack as CFStack
import software.amazon.awssdk.services.cloudformation.model.StackStatus

import javax.inject.Inject
import java.time.Duration

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class CloudformationService implements CloudformationApi {
  CloudFormationClient cloudformation
  FileSystemManager fs

  @Inject
  CloudformationService(CloudFormationClient cloudformation, FileSystemManager fs) {
    this.cloudformation = require cloudformation, notNullValue()
    this.fs = require fs, notNullValue()
  }

  @Override
  CFStack findCloudFormationStackByName(String stackName) {
    require stackName, notEmptyString()

    DescribeStacksResponse stackResponse = null
    try {
      def request = DescribeStacksRequest.builder().stackName(stackName).build()
      stackResponse = cloudformation.describeStacks(request as DescribeStacksRequest)
    } catch (CloudFormationException ex) {
      log.debug 'cloudformation stack [{}] not found', stackName
    }

    return stackResponse?.stacks()?[0]
  }

  @Override
  CreateStackResponse createPlatformClusterStack(String clusterName) {
    require clusterName, notEmptyString()

    log.info 'creating cloudformation stack [{}]', clusterName
    def templateObject = fs.resolveFile('res:cloudformation/platform-cluster.yaml')
    def template = templateObject.content.inputStream.text

    // create the cloudformation stack
    def request = CreateStackRequest.builder()
      .stackName(clusterName)
      .parameters(Parameter.builder().parameterKey('Keyname').parameterValue(clusterName).build())
      .templateBody(template)
      .build()
    return cloudformation.createStack(request as CreateStackRequest)
  }

  @Override
  CFStack waitForStatus(
    String stackName,
    StackStatus stackStatus,
    Duration retryInterval = Duration.ofSeconds(30),
    int retries = 10
  ) {
    require stackName, notEmptyString()
    require stackStatus, notNullValue()
    require retryInterval, notNullValue()

    CFStack stack = null
    def stackClosure = { ->
      def request = DescribeStacksRequest.builder().stackName(stackName).build()
      cloudformation.describeStacks(request as DescribeStacksRequest)
    }

    def attempts = 0
    while (stack?.stackStatus() != stackStatus && attempts < 10) {
      if (attempts > 0) {
        sleep retryInterval.toMillis()
      }
      def resp = stackClosure()
      stack = resp?.stacks()?[0]
      attempts++
    }

    return stack?.stackStatus() == stackStatus ? stack : null
  }

  @Override
  DeleteStackResponse deleteStackByName(String stackName) {
    require stackName, notEmptyString()
    def request = DeleteStackRequest.builder().stackName(stackName).build()
    return cloudformation.deleteStack(request as DeleteStackRequest)
  }
}
