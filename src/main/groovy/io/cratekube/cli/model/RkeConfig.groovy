package io.cratekube.cli.model

import groovy.transform.Immutable

@Immutable
class RkeConfig {
  String sshKeyPath
  List<ClusterNode> nodes
}

@Immutable
class ClusterNode {
  String address
  String user
  List<String> roles
}
