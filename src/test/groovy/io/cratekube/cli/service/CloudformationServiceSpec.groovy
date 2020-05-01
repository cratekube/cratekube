package io.cratekube.cli.service

import org.apache.commons.vfs2.FileContent
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.valid4j.errors.RequireViolation
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse
import software.amazon.awssdk.services.cloudformation.model.Stack
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static software.amazon.awssdk.services.cloudformation.model.StackStatus.CREATE_COMPLETE
import static software.amazon.awssdk.services.cloudformation.model.StackStatus.CREATE_IN_PROGRESS
import static spock.util.matcher.HamcrestSupport.expect

class CloudformationServiceSpec extends Specification {
  @Subject CloudformationService subject

  CloudFormationClient cloudformation
  FileSystemManager fs

  def setup() {
    cloudformation = Mock(CloudFormationClient)
    fs = Mock(FileSystemManager)
    subject = new CloudformationService(cloudformation, fs)
  }

  def 'should require valid constructor params'() {
    when:
    new CloudformationService(cfClient, fsMgr)

    then:
    thrown RequireViolation

    where:
    cfClient            | fsMgr
    null                | null
    this.cloudformation | null
  }

  def 'findCloudFormationStackByName should require valid input param'() {
    when:
    subject.findCloudFormationStackByName(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  def 'findCloudFormationStackByName should return null when request finds no stacks'() {
    given:
    cloudformation.describeStacks(_) >> { throw CloudFormationException.builder().build() }

    when:
    def result = subject.findCloudFormationStackByName('test-stack')

    then:
    expect result, nullValue()
  }

  def 'findCloudFormationStackByName should object found by api'() {
    given:
    def stack = Stack.builder().build()
    cloudformation.describeStacks(_) >> DescribeStacksResponse.builder().stacks(stack).build()

    when:
    def result = subject.findCloudFormationStackByName('test-stack')

    then:
    expect result, notNullValue()
    expect result, equalTo(stack)
  }

  def 'createPlatformClusterStack should require valid parameters'() {
    when:
    subject.createPlatformClusterStack(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  def 'createPlatformClusterStack should call cloudformation api'() {
    given:
    fs.resolveFile(_) >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getInputStream() >> GroovyMock(InputStream) {
          getText() >> 'test value'
        }
      }
    }

    when:
    subject.createPlatformClusterStack('test-cluster')

    then:
    1 * cloudformation.createStack(_)
  }

  def 'waitForStatus should require valid parameters'() {
    when:
    subject.waitForStatus(name, status, retryInterval, 1)

    then:
    thrown RequireViolation

    where:
    name        | status          | retryInterval
    null        | null            | null
    ''          | null            | null
    'test-name' | null            | null
    'test-name' | CREATE_COMPLETE | null
  }

  def 'waitForStatus should iterate multiple times until status is reached'() {
    given:
    def inProgressStack = Stack.builder().stackStatus(CREATE_IN_PROGRESS).build()
    def completeStack = Stack.builder().stackStatus(CREATE_COMPLETE).build()
    cloudformation.describeStacks(_)
      >> DescribeStacksResponse.builder().stacks(inProgressStack).build()
      >>> DescribeStacksResponse.builder().stacks(completeStack).build()

    when:
    def result = subject.waitForStatus('test-cluster', CREATE_COMPLETE, Duration.ofSeconds(0), 3)

    then:
    expect result, equalTo(completeStack)
  }

  def 'waitForStatus should return null when status is not found'() {
    given:
    def inProgressStack = Stack.builder().stackStatus(CREATE_IN_PROGRESS).build()
    cloudformation.describeStacks(_)
      >> DescribeStacksResponse.builder().stacks(inProgressStack).build()
      >>> DescribeStacksResponse.builder().stacks(inProgressStack).build()

    when:
    def result = subject.waitForStatus('test-cluster', CREATE_COMPLETE, Duration.ofSeconds(0), 2)

    then:
    expect result, nullValue()
  }

  def 'deleteStackByName should require valid parameters'() {
    when:
    subject.deleteStackByName(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  def 'deleteStackByName should call cloudformation api'() {
    when:
    subject.deleteStackByName('test-cluster')

    then:
    1 * cloudformation.deleteStack(_)
  }
}
