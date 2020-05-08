package io.cratekube.cli.model

import groovy.transform.Canonical

import static io.cratekube.cli.model.Constants.PRIVATE_KEY_NAME
import static io.cratekube.cli.model.Constants.PUBLIC_KEY_NAME

@Canonical
class WorkerNodeConfig {
  String nodeDns
  Integer sshPort = 22
  String nodeUser
  String publicKeyPath = "/${PUBLIC_KEY_NAME}"
  String privateKeyPath = "/${PRIVATE_KEY_NAME}"
  String kubeConfigPath = '/rke/kube_config_cluster.yml'
  UUID adminApiKey

  String getNodeUrl() {
    return "${nodeDns}:${sshPort}"
  }
}
