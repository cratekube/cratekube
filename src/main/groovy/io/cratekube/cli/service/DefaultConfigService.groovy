package io.cratekube.cli.service

import groovy.util.logging.Slf4j
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.model.Constants
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class DefaultConfigService implements ConfigApi {
  FileSystemManager fs
  String defaultConfigPath

  DefaultConfigService(FileSystemManager fs, String defaultConfigPath) {
    this.fs = require fs, notNullValue()
    this.defaultConfigPath = require defaultConfigPath, notEmptyString()
  }

  @Inject
  DefaultConfigService(FileSystemManager fs) {
    this(fs, Constants.BASE_DIRECTORY)
  }

  @Override
  File findByPath(String path) {
    require path, notEmptyString()

    String resolvedPath = "${defaultConfigPath}${path}"
    log.debug 'attempting to resolve file {}', resolvedPath
    def fileObject = fs.resolveFile(resolvedPath)
    if (!fileObject.exists()) { return null }

    return new File(fileObject.name.path)
  }

  @Override
  FileObject locateResource(String path) {
    require path, notEmptyString()

    String resourcePath = "res:${path}"
    log.debug 'attempting to resolve resource {}', resourcePath

    return fs.resolveFile(resourcePath)
  }
}
