package io.cratekube.cli.service

import com.github.jknack.handlebars.Handlebars
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.api.RkeApi
import io.cratekube.cli.model.RkeConfig
import io.cratekube.cli.module.annotations.RkeCommand

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

/**
 * Default implementation for the {@link RkeApi}
 */
class RkeService implements RkeApi {
  Handlebars handlebars
  ProcessExecutor rke

  @Inject
  RkeService(Handlebars handlebars, @RkeCommand ProcessExecutor rke) {
    this.handlebars = require handlebars, notNullValue()
    this.rke = require rke, notNullValue()
  }

  @Override
  String buildClusterConfig(RkeConfig rkeConfig) {
    require rkeConfig, notNullValue()

    def clusterConfigTemplate = handlebars.compile('rke/rke-cluster-config')
    return clusterConfigTemplate.apply(rkeConfig)
  }

  @Override
  void initializeCluster(String rkeDirectory) {
    require rkeDirectory, notEmptyString()

    def rkeProc = rke.exec(new File(rkeDirectory), 'up')
    rkeProc.waitForProcessOutput(System.out, System.err)
  }
}
