package io.cratekube.cli.api

import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse
import software.amazon.awssdk.services.cloudformation.model.DeleteStackResponse
import software.amazon.awssdk.services.cloudformation.model.Stack as CFStack
import software.amazon.awssdk.services.cloudformation.model.StackStatus

import java.time.Duration

/**
 * Interface for interacting with the AWS Cloudformation API.
 */
interface CloudformationApi {
  /**
   * Queries the cloudformation API for a stack by name.
   *
   * @param stackName {@code non-empty} stack name
   * @return the stack if found, otherwise null
   */
  CFStack findCloudFormationStackByName(String stackName)

  /**
   * Creates a platform cluster stack using the provided cluster name.
   *
   * @param clusterName {@code non-empty} cluster name
   * @return cloudformation create stack response
   */
  CreateStackResponse createPlatformClusterStack(String clusterName)

  /**
   * Queries the cloudformation API for a stack by name.  The stack will be returned once the response
   * contains the required status.  The query for the stack will repeat until the status is satisfied,
   * a default timeout of 5 minutes is applied.
   *
   * @param stackName {@code non-empty} name
   * @param stackStatus {@code non-null} status
   * @return stack if the status is found, otherwise null
   */
  CFStack waitForStatus(String stackName, StackStatus stackStatus)

  /**
   * Queries the cloudformation API for a stack by name.  The stack will be returned once the response
   * contains the required status.  The query for the stack will repeat until the status is satisfied,
   * the retry interval and amount of retries will determine the how many checks are performed.
   *
   * @param stackName {@code non-empty} name
   * @param stackStatus {@code non-null} status
   * @param retryInterval the amount of time between checks
   * @param retries the amount of retries to perform
   * @return stack if the status is found, otherwise null
   */
  CFStack waitForStatus(String stackName, StackStatus stackStatus, Duration retryInterval, int retries)

  /**
   * Deletes a cloudformation stack by name.
   *
   * @param stackName {@code non-empty} name
   * @return delete response from AWS Cloudformation
   */
  DeleteStackResponse deleteStackByName(String stackName)
}
