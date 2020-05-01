package io.cratekube.cli.service

import com.github.jknack.handlebars.Handlebars
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.model.ClusterNode
import io.cratekube.cli.model.RkeConfig
import io.cratekube.cli.module.ProductionModule
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.expect

class RkeServiceSpec extends Specification {
  @Subject RkeService subject

  ProcessExecutor rke
  Handlebars handlebars

  def setup() {
    rke = Mock(ProcessExecutor)
    handlebars = ProductionModule.handlebarsProvider()
    subject = new RkeService(handlebars, rke)
  }

  def 'should require validate constructor params'() {
    when:
    new RkeService(hndlebars, null)

    then:
    thrown RequireViolation

    where:
    hndlebars << [null, this.handlebars]
  }

  def 'buildClusterConfig should require valid input param'() {
    when:
    subject.buildClusterConfig(null)

    then:
    thrown RequireViolation
  }

  def 'should return expected yaml config'() {
    given:
    def config = new RkeConfig(
      sshKeyPath: '/test/ssh/path',
      nodes: [
        new ClusterNode('master.node', 'test-user', ['controlplane', 'etcd']),
        new ClusterNode('worker.node', 'test-user', ['worker'])
      ]
    )
    def expected = getClass().classLoader.getResource('fixtures/expected-cluster-config.yml').text

    when:
    def result = subject.buildClusterConfig(config)

    then:
    expect result, equalTo(expected)
  }

  def 'createCluster should require valid input param'() {
    when:
    subject.initializeCluster(rkeDir)

    then:
    thrown RequireViolation

    where:
    rkeDir << [null, '']
  }

  def 'should call rke during createCluster'() {
    when:
    subject.initializeCluster('/path/to/rke/dir')

    then:
    1 * rke.exec(_, 'up') >> GroovyMock(Process)
  }
}
