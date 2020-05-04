package io.cratekube.cli.service

import com.aestasit.infrastructure.ssh.dsl.SshDslEngine
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.api.WorkerNodeApi
import io.cratekube.cli.model.WorkerNodeConfig

import javax.inject.Inject

import static io.cratekube.cli.model.Constants.CRATEKUBE_HOME_DIR
import static io.cratekube.cli.model.Constants.PRIVATE_KEY_NAME
import static io.cratekube.cli.model.Constants.PUBLIC_KEY_NAME
import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require

class WorkerNodeService implements WorkerNodeApi {
  SshDslEngine sshDslEngine
  ConfigApi configService

  @Inject
  WorkerNodeService(SshDslEngine sshDslEngine, ConfigApi configService) {
    this.sshDslEngine = require sshDslEngine, notNullValue()
    this.configService = require configService, notNullValue()
  }

  @Override
  void configureNode(WorkerNodeConfig config) {
    require config, notNullValue()

    def privateKey = configService.findByPath(config.privateKeyPath)
    def publicKey = configService.findByPath(config.publicKeyPath)
    def kubeConfig = configService.findByPath(config.kubeConfigPath)

    sshDslEngine.remoteSession(config.nodeUrl) {
      user = config.nodeUser
      keyFile = privateKey
      host = config.nodeDns

      connect()
      exec "sudo mkdir -p ${CRATEKUBE_HOME_DIR}"
      exec "sudo chown ${config.nodeUser}:root ${CRATEKUBE_HOME_DIR}"

      // copy keys and config to worker node
      scp {
        from { localFile(kubeConfig) }
        into { remoteFile("${CRATEKUBE_HOME_DIR}/kubeconfig") }
      }
      scp {
        from { localFile(publicKey) }
        into { remoteFile("${CRATEKUBE_HOME_DIR}/${PUBLIC_KEY_NAME}") }
      }
      scp {
        from { localFile(privateKey) }
        into { remoteFile("${CRATEKUBE_HOME_DIR}/${PRIVATE_KEY_NAME}") }
      }
      disconnect()
    }
  }
}
