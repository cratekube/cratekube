package io.cratekube.cli.service

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static spock.util.matcher.HamcrestSupport.expect

class DefaultConfigServiceSpec extends Specification {
  @Subject DefaultConfigService subject

  FileSystemManager fs

  def setup() {
    fs = VFS.manager
    subject = new DefaultConfigService(fs, 'res:')
  }

  def 'should require valid constructor params'() {
    when:
    new DefaultConfigService(fsm, configPath)

    then:
    thrown RequireViolation

    where:
    fsm     | configPath
    null    | null
    this.fs | null
    this.fs | ''
  }

  def 'findByPath should return null when file is not found'() {
    given:
    subject = new DefaultConfigService(this.fs, '/tmp/cratekube')

    when:
    def result = subject.findByPath('some-test-file')

    then:
    expect result, nullValue()
  }

  def 'findByPath should return file when found'() {
    when:
    def result = subject.findByPath('logback.groovy')

    then:
    expect result, notNullValue()
  }

  def 'locateResource should return non-null file object when found'() {
    when:
    def result = subject.locateResource('logback.groovy')

    then:
    expect result, notNullValue(FileObject)
  }
}
