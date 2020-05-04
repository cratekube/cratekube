package io.cratekube.cli.api

import io.cratekube.cli.model.ClusterState

/**
 * Interface for managing CrateKube clusters.
 */
interface ClusterApi {
  /**
   * Creates a new CrateKube cluster.
   *
   * @return state of cluster after provisioning
   */
  ClusterState create()

  /**
   * Removes an existing CrateKube cluster if it exists.
   *
   * @return state of the cluster after removal
   */
  ClusterState remove()
}
