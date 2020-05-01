package io.cratekube.cli.api

import io.cratekube.cli.model.RkeConfig

/**
 * Base interface for interacting with RKE clusters.
 */
interface RkeApi {
  /**
   * Creates a cluster config yaml using the provided {@code rkeConfig}.  The {@code rkeConfig}
   * must be non-null.
   *
   * @param rkeConfig {@code non-null} configuration object
   * @return cluster config yaml
   */
  String buildClusterConfig(RkeConfig rkeConfig)

  /**
   * Creates a RKE cluster using the {@code rke up} command.
   */
  void initializeCluster(String rkeDirectory)
}
