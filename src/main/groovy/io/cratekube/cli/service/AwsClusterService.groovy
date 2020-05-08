package io.cratekube.cli.service

import groovy.util.logging.Slf4j
import io.cratekube.cli.api.CloudformationApi
import io.cratekube.cli.api.ClusterApi
import io.cratekube.cli.api.Ec2Api
import io.cratekube.cli.api.KeypairGenerator
import io.cratekube.cli.api.RkeApi
import io.cratekube.cli.api.WorkerNodeApi
import io.cratekube.cli.model.CloudProvider
import io.cratekube.cli.model.ClusterNode
import io.cratekube.cli.model.ClusterState
import io.cratekube.cli.model.RkeConfig
import io.cratekube.cli.model.WorkerNodeConfig
import org.apache.commons.vfs2.FileSystemManager
import software.amazon.awssdk.services.cloudformation.model.Stack as CFStack
import software.amazon.awssdk.services.cloudformation.model.StackStatus

import javax.inject.Inject

import static io.cratekube.cli.model.Constants.BASE_DIRECTORY
import static io.cratekube.cli.model.Constants.CF_OUTPUT_MASTER_DNS
import static io.cratekube.cli.model.Constants.CF_OUTPUT_WORKER_DNS
import static io.cratekube.cli.model.Constants.CLUSTER_NAME
import static io.cratekube.cli.model.Constants.PRIVATE_KEY_NAME
import static io.cratekube.cli.model.Constants.PUBLIC_KEY_NAME
import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class AwsClusterService implements ClusterApi {
  RkeApi rke
  Ec2Api ec2
  CloudformationApi cloudformation
  KeypairGenerator sshKeyGen
  WorkerNodeApi workerNodeService
  FileSystemManager fs

  @Inject
  AwsClusterService(RkeApi rke, Ec2Api ec2, CloudformationApi cloudformation, KeypairGenerator sshKeyGen,
                    WorkerNodeApi workerNodeService, FileSystemManager fs) {
    this.rke = require rke, notNullValue()
    this.ec2 = require ec2, notNullValue()
    this.cloudformation = require cloudformation, notNullValue()
    this.sshKeyGen = require sshKeyGen, notNullValue()
    this.workerNodeService = require workerNodeService, notNullValue()
    this.fs = require fs, notNullValue()
  }

  @Override
  ClusterState create() {
    log.debug 'initializing cluster [{}] for cloud provider [{}]', CLUSTER_NAME, CloudProvider.aws
    // setup the working directory
    def bootstrapDir = fs.resolveFile(BASE_DIRECTORY)
    if (!bootstrapDir.exists()) {
      bootstrapDir.createFolder()
    }

    // create the cratekube keypair and persist the private key
    checkKeypair()

    // create the cloudformation stack
    def stack = createCloudFormationStack()

    // capture stack outputs
    def outputs = stack.outputs()
    def masterDns = outputs.find { it.outputKey() == CF_OUTPUT_MASTER_DNS }?.outputValue()
    def workerDns = outputs.find { it.outputKey() == CF_OUTPUT_WORKER_DNS }?.outputValue()

    // setup the rke cluster config and create cluster
    def nodeUser = 'ec2-user'
    initRkeCluster(nodeUser, masterDns, workerDns)

    // copy the kube config to the worker host
    configureWorkerNode(nodeUser, workerDns)

    return ClusterState.CREATED
  }

  void checkKeypair() {
    def keyPair = ec2.findKeyPairByName(CLUSTER_NAME)
    if (keyPair) { return }

    log.info 'creating keypair [{}]', CLUSTER_NAME
    sshKeyGen.createKeypair(BASE_DIRECTORY, PRIVATE_KEY_NAME)

    // import the keypair to aws
    ec2.importKeyPair(CLUSTER_NAME, "${BASE_DIRECTORY}/${PUBLIC_KEY_NAME}")
  }

  CFStack createCloudFormationStack() {
    // check if stack already exists
    def existingStack = cloudformation.findCloudFormationStackByName(CLUSTER_NAME)
    if (existingStack) { return existingStack }

    // create the stack
    cloudformation.createPlatformClusterStack(CLUSTER_NAME)

    // wait for status to become complete
    return cloudformation.waitForStatus(CLUSTER_NAME, StackStatus.CREATE_COMPLETE)
  }

  void initRkeCluster(String nodeUser, String masterDns, String workerDns) {
    require nodeUser, notEmptyString()
    require masterDns, notEmptyString()
    require workerDns, notEmptyString()

    log.info 'building k8s cluster'
    log.info ' | master node [{}]', masterDns
    log.info ' | worker node [{}]', workerDns

    def rkeConfig = new RkeConfig(
      sshKeyPath: "${BASE_DIRECTORY}/${PRIVATE_KEY_NAME}",
      nodes: [
        new ClusterNode(address: masterDns, user: nodeUser, roles: ['controlplane', 'etcd']),
        new ClusterNode(address: workerDns, user: nodeUser, roles: ['worker'])
      ]
    )

    // build and store the cluster yml
    def clusterConfig = rke.buildClusterConfig(rkeConfig)
    def configFileObject = fs.resolveFile("${BASE_DIRECTORY}/rke/cluster.yml")
    configFileObject.content.outputStream.withWriter { it.write(clusterConfig) }

    // run rke up
    rke.initializeCluster("${BASE_DIRECTORY}/rke")
  }

  void configureWorkerNode(String nodeUser, String nodeDns) {
    require nodeUser, notEmptyString()
    require nodeDns, notEmptyString()

    def config = new WorkerNodeConfig(nodeDns: nodeDns, nodeUser: nodeUser, adminApiKey: UUID.randomUUID())
    log.info 'generated admin API key: {}', config.adminApiKey

    workerNodeService.with {
      log.info 'configuring worker node'
      configureNode config
      log.info 'deploying services'
      deployServices config
    }
  }

  @Override
  ClusterState remove() {
    log.debug 'removing cluster [{}]', CLUSTER_NAME
    log.debug '  | deleting cloudformation stack', CLUSTER_NAME
    cloudformation.deleteStackByName(CLUSTER_NAME)

    log.debug '  | deleting keypair', CLUSTER_NAME
    ec2.deleteKeyPairByName(CLUSTER_NAME)

    log.debug '  | removing keypair from local storage'
    ["${BASE_DIRECTORY}/${PRIVATE_KEY_NAME}", "${BASE_DIRECTORY}/${PUBLIC_KEY_NAME}"].each {
      fs.resolveFile(it).delete()
    }

    log.debug '  | removed rke configuration from local storage'
    def rkeDir = fs.resolveFile("${BASE_DIRECTORY}/rke")
    if (rkeDir.exists()) {
      rkeDir.deleteAll()
    }
    log.debug 'cluster removal complete'

    return ClusterState.DELETED
  }
}
