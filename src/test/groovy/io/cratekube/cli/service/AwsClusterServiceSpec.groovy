package io.cratekube.cli.service

import io.cratekube.cli.api.CloudformationApi
import io.cratekube.cli.api.Ec2Api
import io.cratekube.cli.api.KeypairGenerator
import io.cratekube.cli.api.RkeApi
import io.cratekube.cli.api.WorkerNodeApi
import org.apache.commons.vfs2.FileContent
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.valid4j.errors.RequireViolation
import software.amazon.awssdk.services.cloudformation.model.Output
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.ec2.model.KeyPairInfo
import spock.lang.Specification
import spock.lang.Subject

import static io.cratekube.cli.model.Constants.BASE_DIRECTORY
import static io.cratekube.cli.model.Constants.CF_OUTPUT_MASTER_DNS
import static io.cratekube.cli.model.Constants.CF_OUTPUT_WORKER_DNS
import static io.cratekube.cli.model.Constants.CLUSTER_NAME
import static io.cratekube.cli.model.Constants.PRIVATE_KEY_NAME
import static io.cratekube.cli.model.Constants.PUBLIC_KEY_NAME
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasProperty
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString
import static software.amazon.awssdk.services.cloudformation.model.StackStatus.CREATE_COMPLETE
import static spock.util.matcher.HamcrestSupport.expect

class AwsClusterServiceSpec extends Specification {
  @Subject AwsClusterService subject

  Ec2Api ec2
  CloudformationApi cloudFormation
  FileSystemManager fs
  KeypairGenerator sshKeyGen
  WorkerNodeApi workerNodeService
  RkeApi rke

  def setup() {
    rke = Mock(RkeApi)
    ec2 = Mock(Ec2Api)
    cloudFormation = Mock(CloudformationApi)
    sshKeyGen = Mock(KeypairGenerator)
    workerNodeService = Mock(WorkerNodeApi)
    fs = Mock(FileSystemManager)
    subject = new AwsClusterService(rke, ec2, cloudFormation, sshKeyGen, workerNodeService, fs)
  }

  def 'should require valid constructor params'() {
    when:
    new AwsClusterService(rkeApi, ec2Api, cfApi, keyGen, workerNodeSvc, null)

    then:
    thrown RequireViolation

    where:
    rkeApi   | ec2Api   | cfApi               | workerNodeSvc          | keyGen
    null     | null     | null                | null                   | null
    this.rke | null     | null                | null                   | null
    this.rke | this.ec2 | null                | null                   | null
    this.rke | this.ec2 | this.cloudFormation | null                   | null
    this.rke | this.ec2 | this.cloudFormation | this.workerNodeService | null
    this.rke | this.ec2 | this.cloudFormation | this.workerNodeService | this.sshKeyGen
  }

  def 'checkKeypair should not generate a key if one exists in AWS'() {
    given:
    ec2.findKeyPairByName(_) >> KeyPairInfo.builder().keyName('test-key').build()

    when:
    subject.checkKeypair()

    then:
    0 * sshKeyGen.createKeypair(_, _)
    0 * ec2.importKeyPair(_, _)
  }

  def 'checkKeypair should generate a key if not found in AWS'() {
    given:
    ec2.findKeyPairByName(_) >> null

    when:
    subject.checkKeypair()

    then:
    1 * sshKeyGen.createKeypair(_, _)
    1 * ec2.importKeyPair(_, _)
  }

  def 'createCloudFormationStack should return stack when found and create complete'() {
    given:
    def stack = Stack.builder().stackName('test-stack').stackStatus(CREATE_COMPLETE).build()
    cloudFormation.findCloudFormationStackByName(_) >> stack

    when:
    def result = subject.createCloudFormationStack()

    then:
    expect result, equalTo(stack)
    0 * cloudFormation.createPlatformClusterStack(_)
    0 * cloudFormation.waitForStatus(_, _)
  }

  def 'createCloudFormationStack should create new stack when not found'() {
    given:
    def createdStack = Stack.builder().stackName('test-stack').build()
    cloudFormation.findCloudFormationStackByName(_) >> null

    when:
    def result = subject.createCloudFormationStack()

    then:
    expect result, equalTo(createdStack)
    1 * cloudFormation.createPlatformClusterStack(_)
    1 * cloudFormation.waitForStatus(_, _) >> createdStack
  }

  def 'initRkeCluster should require valid parameters'() {
    when:
    subject.initRkeCluster(nodeUser, masterDns, workerDns)

    then:
    thrown RequireViolation

    where:
    nodeUser    | masterDns    | workerDns
    null        | null         | null
    ''          | null         | null
    'test-user' | null         | null
    'test-user' | ''           | null
    'test-user' | 'master.dns' | null
  }

  def 'initRkeCluster should create cluster config and call rke api'() {
    given:
    def nodeUser = 'test-user'
    def masterDns = 'master.dns'
    def workerDns = 'worker.dns'
    rke.buildClusterConfig(_) >> 'test config'
    fs.resolveFile(_) >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getOutputStream() >> GroovyMock(OutputStream)
      }
    }

    when:
    subject.initRkeCluster(nodeUser, masterDns, workerDns)

    then:
    1 * rke.initializeCluster(notEmptyString())
  }

  def 'configureWorkerNode should require valid params'() {
    when:
    subject.configureWorkerNode(nodeUser, nodeDns)

    then:
    thrown RequireViolation

    where:
    nodeUser    | nodeDns
    null        | null
    ''          | null
    'test-user' | null
    'test-user' | ''
  }

  def 'configureWorkerNode should call the worker node api'() {
    given:
    def nodeUser = 'test-user'
    def nodeDns = 'test.dns'

    when:
    subject.configureWorkerNode(nodeUser, nodeDns)

    then:
    1 * workerNodeService.configureNode(allOf(
      hasProperty('nodeUser', equalTo(nodeUser)),
      hasProperty('nodeDns', equalTo(nodeDns)),
    ))
    1 * workerNodeService.deployServices(allOf(
      hasProperty('nodeUser', equalTo(nodeUser)),
      hasProperty('nodeDns', equalTo(nodeDns)),
    ))
  }

  def 'should call correct apis when creating cluster'() {
    given:
    fs.resolveFile(BASE_DIRECTORY) >> Mock(FileObject) {
      exists() >> false
    }
    def cfStack = Stack.builder()
      .stackName(CLUSTER_NAME)
      .outputs(
        Output.builder().outputKey(CF_OUTPUT_MASTER_DNS).outputValue('master.dns').build(),
        Output.builder().outputKey(CF_OUTPUT_WORKER_DNS).outputValue('worker.dns').build(),
      )
      .build()

    when:
    subject.create()

    then:
    1 * sshKeyGen.createKeypair(BASE_DIRECTORY, PRIVATE_KEY_NAME)
    1 * ec2.importKeyPair(CLUSTER_NAME, "${BASE_DIRECTORY}/${PUBLIC_KEY_NAME}")
    1 * cloudFormation.createPlatformClusterStack(CLUSTER_NAME)
    1 * cloudFormation.waitForStatus(_, _) >> cfStack
    1 * rke.buildClusterConfig(_) >> 'test config'
    1 * fs.resolveFile("${BASE_DIRECTORY}/rke/cluster.yml") >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getOutputStream() >> GroovyMock(OutputStream)
      }
    }
    1 * rke.initializeCluster("${BASE_DIRECTORY}/rke")
    1 * workerNodeService.configureNode(_)
  }

  def 'should call correct apis when removing cluster'() {
    given:
    def privateKeyFile = Mock(FileObject)
    def publicKeyFile = Mock(FileObject)
    def rkeDir = Mock(FileObject) {
      exists() >> true
    }
    fs.resolveFile("${BASE_DIRECTORY}/${PRIVATE_KEY_NAME}") >> privateKeyFile
    fs.resolveFile("${BASE_DIRECTORY}/${PUBLIC_KEY_NAME}") >> publicKeyFile
    fs.resolveFile("${BASE_DIRECTORY}/rke") >> rkeDir

    when:
    subject.remove()

    then:
    1 * cloudFormation.findCloudFormationStackByName(_) >> Stack.builder().build()
    1 * cloudFormation.deleteStackByName(_)
    1 * cloudFormation.waitForStatus(_, _)
    1 * ec2.deleteKeyPairByName(_)
    1 * privateKeyFile.delete()
    1 * publicKeyFile.delete()
    1 * rkeDir.deleteAll()
  }
}
