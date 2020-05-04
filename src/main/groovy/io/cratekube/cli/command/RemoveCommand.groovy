package io.cratekube.cli.command

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import groovyjarjarpicocli.CommandLine
import io.cratekube.cli.api.ClusterApi

import javax.inject.Inject
import java.util.concurrent.Callable

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require

@Slf4j
@Canonical
@CommandLine.Command(
  name = 'remove',
  aliases = ['rm'],
  sortOptions = false,
  synopsisHeading = 'Usage:%n%n',
  descriptionHeading = '%nDescription:%n%n',
  description = 'Removes an existing Cratekube cluster',
  parameterListHeading = '%nParameters:%n',
  optionListHeading = '%nOptions:%n',
  commandListHeading = '%nCommands:%n'
)
class RemoveCommand implements Callable<String> {
  ClusterApi clusterApi

  @CommandLine.Option(names = ['-h', '--help'], usageHelp = true)
  boolean usageHelpRequested

  @Inject
  RemoveCommand(ClusterApi clusterApi) {
    this.clusterApi = require clusterApi, notNullValue()
  }

  @Override
  String call() throws Exception {
    def result = clusterApi.remove()
    return "cratekube cluster removed with state [${result}]"
  }
}
