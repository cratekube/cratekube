package io.cratekube.cli

import com.google.inject.Module
import groovyjarjarpicocli.CommandLine
import io.cratekube.cli.command.CrateKubeCommand
import io.cratekube.cli.module.CliGuiceFactory
import io.cratekube.cli.module.ProductionModule

import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.everyItem
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.collection.IsEmptyCollection.empty
import static org.valid4j.Assertive.require

/**
 * Default entrypoint for the CrateKube CLI.
 */
class App {
  List<Module> modules

  App(List<Module> modules) {
    this.modules = require modules, allOf(
      not(empty()),
      everyItem(notNullValue())
    )
  }

  App() {
    this([new ProductionModule()])
  }

  int runCli(String... args) {
    // setup arguments for command
    List<String> arguments = args.toList()
    if (arguments.empty) {
      arguments << '-h'
    }

    def dependencyFactory = new CliGuiceFactory(*modules)
    def cli = new CommandLine(new CrateKubeCommand(), dependencyFactory).tap {
      executionStrategy = new CommandLine.RunLast()
    }
    return cli.execute(*arguments)
  }

  static void main(String... args) {
    new App().runCli(args)
  }
}
