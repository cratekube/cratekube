package io.cratekube.cli.service

import com.aestasit.ssh.mocks.MockSshServer
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.model.WorkerNodeConfig
import io.cratekube.cli.module.ProductionModule
import org.apache.commons.vfs2.VFS
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

class WorkerNodeServiceSpec extends Specification {
  @Subject WorkerNodeService subject

  ConfigApi configApi

  def setup() {
    configApi = new DefaultConfigService(VFS.manager, 'res:fixtures')
    subject = new WorkerNodeService(ProductionModule.sshDslEngineProvider(), configApi)
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
    new WorkerNodeService(engine, configService)

    then:
    thrown RequireViolation

    where:
    engine                                  | configService
    null                                    | null
    ProductionModule.sshDslEngineProvider() | null
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
}
