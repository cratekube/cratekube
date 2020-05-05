package io.cratekube.cli.api

import io.cratekube.cli.model.WorkerNodeConfig

/**
 * Applies configuration to worker nodes after a cluster has been created.
 * This API should be used to place required configurations and services needed to run the Cratekube
 * platform cluster.
 */
interface WorkerNodeApi {
  /**
   * Copies over the keypair used for platform cluster node access.
   *
   * @param config {@code non-null} configuration object for worker node
   */
  void configureNode(WorkerNodeConfig config)

  /**
   * Deploys any required k8s yaml specifications needed for the platform cluster.
   *
   * @param config {@code non-null} configuration object for worker node
   */
  void deployServices(WorkerNodeConfig config)
}
