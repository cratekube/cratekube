package io.cratekube.cli.command

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import groovyjarjarpicocli.CommandLine
import io.cratekube.cli.api.ClusterApi
import io.cratekube.cli.model.CloudProvider

import javax.inject.Inject
import java.util.concurrent.Callable

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require

@Slf4j
@Canonical
@CommandLine.Command(
  name = 'init',
  sortOptions = false,
  synopsisHeading = 'Usage:%n%n',
  descriptionHeading = '%nDescription:%n%n',
  description = 'Initializes a CrateKube install for a cloud provider',
  parameterListHeading = '%nParameters:%n',
  optionListHeading = '%nOptions:%n',
  commandListHeading = '%nCommands:%n'
)
class InitCommand implements Callable<String> {
  ClusterApi clusterApi

  @CommandLine.Option(names = ['-p', '--provider'], defaultValue = 'aws', description = 'Cloud provider, must be one of: ${COMPLETION-CANDIDATES}')
  CloudProvider cloudProvider

  @CommandLine.Option(names = ['-h', '--help'], usageHelp = true)
  boolean usageHelpRequested

  @Inject
  InitCommand(ClusterApi clusterApi) {
    this.clusterApi = require clusterApi, notNullValue()
  }

  @Override
  String call() throws Exception {
    def result = clusterApi.create()
    return "cratekube cluster initialized with state [${result}]"
  }
}
