package io.cratekube.cli.command

import io.cratekube.cli.api.ClusterApi
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

class InitCommandSpec extends Specification {
  @Subject InitCommand subject

  ClusterApi clusterApi

  def setup() {
    clusterApi = Mock()
    subject = new InitCommand(clusterApi)
  }

  def 'should require valid constructor params'() {
    when:
    new InitCommand(null)

    then:
    thrown RequireViolation
  }

  def 'should invoke clusterApi when executing call'() {
    when:
    subject.call()

    then:
    1 * clusterApi.create()
  }
}
