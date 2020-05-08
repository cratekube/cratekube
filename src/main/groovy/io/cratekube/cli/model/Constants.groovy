package io.cratekube.cli.model

final class Constants {
  // base constants
  public static final String BASE_DIRECTORY = '/app/cratekube'
  public static final String PRIVATE_KEY_NAME = 'cratekube_rsa'
  public static final String PUBLIC_KEY_NAME = "${PRIVATE_KEY_NAME}.pub"
  public static final String CLUSTER_NAME = 'cratekube-cluster'
  public static final String CRATEKUBE_HOME_DIR = '/var/lib/cratekube'

  // cloudformation constants
  public static final String CF_OUTPUT_MASTER_DNS = 'MasterNodeDNS'
  public static final String CF_OUTPUT_WORKER_DNS = 'WorkerNodeDNS'

  // deployment constants
  public static final String DEPLOYMENTS_PATH = '/deployments'
  public static final String DEPLOYMENT_NAME = 'deployment.yml'
}
