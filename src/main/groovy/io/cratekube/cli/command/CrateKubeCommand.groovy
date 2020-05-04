package io.cratekube.cli.command

import groovy.transform.Canonical
import groovyjarjarpicocli.CommandLine

import java.util.concurrent.Callable

/**
 * Base command for the CrateKube CLI application.
 */
@Canonical
@CommandLine.Command(
  name = 'cratekube',
  sortOptions = false,
  header = [
    '@|bold,green    ______           __       __ __      __      |@',
    '@|bold,green   / ____/________ _/ /____  / //_/_  __/ /_  ___|@',
    '@|bold,green  / /   / ___/ __ `/ __/ _ \\/ ,< / / / / __ \\/ _ \\|@',
    '@|bold,green / /___/ /  / /_/ / /_/  __/ /| / /_/ / /_/ /  __/|@',
    '@|bold,green \\____/_/   \\__,_/\\__/\\___/_/ |_\\__,_/_.___/\\___/|@',
  ],
  synopsisHeading = '%nUsage:%n%n',
  descriptionHeading = '%nDescription:%n%n',
  description = 'CLI for managing CrateKube clusters',
  parameterListHeading = '%nParameters:%n',
  optionListHeading = '%nOptions:%n',
  commandListHeading = '%nCommands:%n',
  subcommands = [InitCommand, RemoveCommand]
)
class CrateKubeCommand implements Callable<String> {
  @CommandLine.Option(names = ['-h', '--help'], usageHelp = true)
  boolean usageHelpRequested

  @Override
  String call() throws Exception {
    // no-op, impl will occur in subcommands
    return null
  }
}
