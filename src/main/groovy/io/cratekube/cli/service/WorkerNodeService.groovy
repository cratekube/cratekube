package io.cratekube.cli.service

import com.aestasit.infrastructure.ssh.dsl.SshDslEngine
import com.github.jknack.handlebars.Handlebars
import groovy.util.logging.Slf4j
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.api.WorkerNodeApi
import io.cratekube.cli.model.WorkerNodeConfig
import io.cratekube.cli.module.annotations.KubectlCommand
import org.apache.commons.vfs2.FileSystemManager

import javax.inject.Inject

import static io.cratekube.cli.model.Constants.BASE_DIRECTORY
import static io.cratekube.cli.model.Constants.CRATEKUBE_HOME_DIR
import static io.cratekube.cli.model.Constants.DEPLOYMENTS_PATH
import static io.cratekube.cli.model.Constants.DEPLOYMENT_NAME
import static io.cratekube.cli.model.Constants.PRIVATE_KEY_NAME
import static io.cratekube.cli.model.Constants.PUBLIC_KEY_NAME
import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require

@Slf4j
class WorkerNodeService implements WorkerNodeApi {
  SshDslEngine sshDslEngine
  ConfigApi configService
  ProcessExecutor kubectl
  FileSystemManager fsm
  Handlebars handlebars

  @Inject
  WorkerNodeService(SshDslEngine sshDslEngine, ConfigApi configService, @KubectlCommand ProcessExecutor kubectl,
                    FileSystemManager fsm, Handlebars handlebars) {
    this.sshDslEngine = require sshDslEngine, notNullValue()
    this.configService = require configService, notNullValue()
    this.kubectl = require kubectl, notNullValue()
    this.fsm = require fsm, notNullValue()
    this.handlebars = require handlebars, notNullValue()
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

  @Override
  void deployServices(WorkerNodeConfig config) {
    require config, notNullValue()

    def deploymentYamlHbs = handlebars.compile("deployment/${DEPLOYMENT_NAME}")
    def deploymentYaml = deploymentYamlHbs.apply(config)
    def deployment = fsm.resolveFile("${BASE_DIRECTORY}${DEPLOYMENTS_PATH}/${DEPLOYMENT_NAME}")

    // write the yaml to the deployment directory
    deployment.content.outputStream.withWriter { it.write(deploymentYaml) }

    // apply the yml with kubectl
    def applyProc = kubectl.exec(
      new File("${BASE_DIRECTORY}${DEPLOYMENTS_PATH}"),
      '--kubeconfig', "${BASE_DIRECTORY}${config.kubeConfigPath}", 'apply', '-f', DEPLOYMENT_NAME
    )
    log.debug '\ndeployment output:\n'
    applyProc.waitForProcessOutput(System.out, System.err)
  }
}
