package io.cratekube.cli.service

import org.apache.commons.vfs2.FileContent
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.valid4j.errors.RequireViolation
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.ImportKeyPairRequest
import software.amazon.awssdk.services.ec2.model.KeyPairInfo
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static spock.util.matcher.HamcrestSupport.expect

class Ec2ServiceSpec extends Specification {
  @Subject Ec2Service subject

  Ec2Client ec2
  FileSystemManager fs

  def setup() {
    ec2 = Mock(Ec2Client)
    fs = Mock(FileSystemManager)
    subject = new Ec2Service(ec2, fs)
  }

  def 'should require valid constructor params'() {
    when:
    new Ec2Service(ec2Client, fsMgr)

    then:
    thrown RequireViolation

    where:
    ec2Client | fsMgr
    null      | null
    this.ec2  | null
  }

  def 'findKeyPairByName should require valid input param'() {
    when:
    subject.findKeyPairByName(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  def 'findKeyPairByName should return null if not found'() {
    given:
    ec2.describeKeyPairs(_) >> { throw Ec2Exception.builder().build() }

    when:
    def result = subject.findKeyPairByName('test-keypair')

    then:
    expect result, nullValue()
  }

  def 'findKeyPairByName should return keypair if found'() {
    given:
    def name = 'test-keypair'
    def keypairInfo = KeyPairInfo.builder().keyName(name).build()
    ec2.describeKeyPairs(_) >> DescribeKeyPairsResponse.builder().keyPairs(keypairInfo).build()

    when:
    def result = subject.findKeyPairByName(name)

    then:
    expect result, equalTo(keypairInfo)
  }

  def 'importKeyPair should require valid input params'() {
    when:
    subject.importKeyPair(name, path)

    then:
    thrown RequireViolation

    where:
    name           | path
    null           | null
    ''             | null
    'test-keypair' | null
    'test-keypair' | ''
  }

  def 'importKeyPair should import resolved public key'() {
    given:
    def name = 'test-keypair'
    def keyPath = '/test/path'
    fs.resolveFile(keyPath) >> Mock(FileObject) {
      getContent() >> Mock(FileContent) {
        getInputStream() >> GroovyMock(InputStream) {
          getText() >> 'key value'
        }
      }
    }

    when:
    subject.importKeyPair(name, keyPath)

    then:
    1 * ec2.importKeyPair(notNullValue(ImportKeyPairRequest))
  }

  def 'deleteKeyPairByName should require valid input param'() {
    when:
    subject.deleteKeyPairByName(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  def 'deleteKeyPairByName should call ec2 api'() {
    when:
    subject.deleteKeyPairByName('test-keypair')

    then:
    1 * ec2.deleteKeyPair(_)
  }
}
