package io.cratekube.cli

import io.cratekube.cli.module.ProductionModule
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.instanceOf
import static spock.util.matcher.HamcrestSupport.expect

class AppSpec extends Specification {
  @Subject App subject
  @Rule EnvironmentVariables environmentVariables = new EnvironmentVariables()

  def setup() {
    subject = new App()
    environmentVariables.with {
      set 'AWS_ACCESS_KEY_ID', 'test-access-key'
      set 'AWS_SECRET_ACCESS_KEY', 'test-secret-key'
      set 'AWS_REGION', 'us-east-1'
    }
  }

  def 'default constructor should use production module'() {
    expect:
    expect subject.modules, hasItem(instanceOf(ProductionModule))
  }

  def 'should return successful exit code'() {
    when:
    def exitCode = subject.runCli()

    then:
    expect exitCode, equalTo(0)
  }

  def 'using main method should throw no exception'() {
    when:
    App.main('-h')

    then:
    noExceptionThrown()
  }
}
