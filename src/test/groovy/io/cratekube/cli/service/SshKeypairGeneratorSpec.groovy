package io.cratekube.cli.service

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.expect

class SshKeypairGeneratorSpec extends Specification {
  @Subject SshKeypairGenerator subject

  @Rule TemporaryFolder tmpDir = new TemporaryFolder()

  def setup() {
    subject = new SshKeypairGenerator()
  }

  def 'should return exit code of 0 when keypair is generated'() {
    when:
    def exitCode = subject.createKeypair(tmpDir.root.path, 'cratekube-test-key')

    then:
    expect exitCode, equalTo(0)
  }
}
