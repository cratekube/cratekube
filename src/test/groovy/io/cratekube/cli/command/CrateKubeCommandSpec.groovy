package io.cratekube.cli.command

import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.nullValue
import static spock.util.matcher.HamcrestSupport.expect

class CrateKubeCommandSpec extends Specification {
  @Subject CrateKubeCommand subject

  def setup() {
    subject = new CrateKubeCommand()
  }

  def 'command callabe should be a no-op'() {
    when:
    def result = subject.call()

    then:
    expect result, nullValue()
  }
}
