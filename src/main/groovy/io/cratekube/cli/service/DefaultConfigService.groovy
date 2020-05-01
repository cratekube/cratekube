package io.cratekube.cli.service

import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.model.Constants
import org.apache.commons.vfs2.FileSystemManager

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

class DefaultConfigService implements ConfigApi {
  FileSystemManager fs
  String defaultConfigPath

  @Inject
  DefaultConfigService(FileSystemManager fs) {
    this.fs = require fs, notNullValue()
    this.defaultConfigPath = Constants.BASE_DIRECTORY
  }

  DefaultConfigService(FileSystemManager fs, String defaultConfigPath) {
    this(fs)
    this.defaultConfigPath = require defaultConfigPath, notEmptyString()
  }

  @Override
  File findByPath(String path) {
    def fileObject = fs.resolveFile("${defaultConfigPath}${path}")
    if (!fileObject.exists()) { return null }

    return new File(fileObject.name.path)
  }
}
