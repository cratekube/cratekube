package io.cratekube.cli.api

/**
 * Interface for accessing configuration data.
 */
interface ConfigApi {
  /**
   * Finds a configuration file by its path.  The path will be appended to the base configuration
   * directory when searching
   *
   * @return the file if found, otherwise null
   */
  File findByPath(String path)
}
