package io.cratekube.cli.service

import com.aestasit.ssh.mocks.MockSshServer
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.model.WorkerNodeConfig
import io.cratekube.cli.module.ProductionModule
import org.apache.commons.vfs2.FileContent
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

import static io.cratekube.cli.model.Constants.LIFECYCLE_SERVICE_DEPLOYMENT

class WorkerNodeServiceSpec extends Specification {
  @Subject WorkerNodeService subject

  ConfigApi configApi
  ProcessExecutor kubectl
  FileSystemManager fs

  def setup() {
    kubectl = Mock(ProcessExecutor)
    fs = Mock(FileSystemManager)
    configApi = new DefaultConfigService(VFS.manager, 'res:fixtures')
    subject = new WorkerNodeService(ProductionModule.sshDslEngineProvider(), configApi, kubectl, fs)
  }

  def setupSpec() {
    MockSshServer.with {
      command('^sudo mkdir -p.*$') { inp, out, err, callback, env ->
        out << '\n'
        callback.onExit(0)
      }

      command('^sudo chown.*$') { inp, out, err, callback, env ->
        out << '\n'
        callback.onExit(0)
      }

      startSshd(2233)
    }
  }

  def cleanupSpec() {
    MockSshServer.stopSshd()
  }

  def 'should require valid constructor param'() {
    when:
    new WorkerNodeService(engine, configService, kbectl, fsm)

    then:
    thrown RequireViolation

    where:
    engine                                  | configService   | kbectl                | fsm
    null                                    | null            | null                  | null
    ProductionModule.sshDslEngineProvider() | null            | null                  | null
    ProductionModule.sshDslEngineProvider() | Mock(ConfigApi) | null                  | null
    ProductionModule.sshDslEngineProvider() | Mock(ConfigApi) | Mock(ProcessExecutor) | null
  }

  def 'configureNode should require valid param'() {
    when:
    subject.configureNode(null)

    then:
    thrown RequireViolation
  }

  def 'configureNode should not throw exception'() {
    given:
    def config = new WorkerNodeConfig(
      nodeDns: 'localhost',
      nodeUser: 'test-user',
      sshPort: 2233
    )

    when:
    subject.configureNode(config)

    then:
    noExceptionThrown()
  }

  def 'deployServices should require valid input param'() {
    when:
    subject.deployServices(null)

    then:
    thrown RequireViolation
  }

  def 'deployServices should apply k8s files'() {
    given:
    def config = new WorkerNodeConfig()
    configApi.locateResource(_) >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getInputStream() >> Mock(InputStream) { getText() >> 'test file output' }
      }
    }
    fs.resolveFile(_) >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getOutputStream() >> GroovyMock(OutputStream)
      }
    }

    when:
    subject.deployServices(config)

    then:
    1 * kubectl.exec(
      _ as File,
      '--kubeconfig', '/app/cratekube/rke/kube_config_cluster.yml', 'apply', '-f', LIFECYCLE_SERVICE_DEPLOYMENT
    ) >> GroovyMock(Process)
  }
}
