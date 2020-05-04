package io.cratekube.cli.api

import groovy.util.logging.Slf4j

import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Every.everyItem
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

/**
 * {@code ProcessExecutor} provides an interface for abstracting process executions.  This is helpful when using
 * dependency injection frameworks and is also very useful for testing.
 */
interface ProcessExecutor {
  /**
   * Absolute path to the executable.
   *
   * @return path to the executable
   */
  String getExecutablePath()

  /**
   * Executes a command with the provided arguments. The process will be executed in the current working directory.
   *
   * @param args arguments to apply to command
   * @return the generated {@link java.lang.Process} reference
   */
  Process exec(String... args)

  /**
   * Executes a command with the provided arguments. The process will be executed in the provided directory.
   * If the execDirectory is null the command will be executed in the current working directory.
   *
   * @param execDirectory directory to execute command
   * @param args arguments to apply to command
   * @return the generated {@link java.lang.Process} reference
   */
  Process exec(File execDirectory, String... args)
}

/**
 * Partial implementation of {@link ProcessExecutor}.  Extending classes will only need to populate the
 * {@code executablePath} property to be complete.
 */
@Slf4j
// codenarc doesn't currently handle abstract classes implementing interfaces
@SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class DefaultProcessExecutor implements ProcessExecutor {
  @Override
  Process exec(File execDirectory = null, String... args) {
    require args?.toList(), allOf(notNullValue(), everyItem(notEmptyString()))

    def commandParts = [executablePath] + args?.toList() ?: []
    def command = commandParts.join(' ')
    log.debug 'executing command [{}] in directory [{}]', command, execDirectory?.path
    return command.execute(null, execDirectory)
  }
}

