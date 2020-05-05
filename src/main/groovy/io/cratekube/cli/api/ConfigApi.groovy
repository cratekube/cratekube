package io.cratekube.cli.api

import org.apache.commons.vfs2.FileObject

/**
 * Interface for accessing configuration data.
 */
interface ConfigApi {
  /**
   * Finds a configuration file by its path.  The path will be appended to the base configuration
   * directory when searching
   *
   * @param path {@code non-empty} file path
   * @return the file if found, otherwise null
   */
  File findByPath(String path)

  /**
   * Finds a resource file by path.
   *
   * @param path {@code non-empty} resource file path
   * @return the file if found, otherwise null
   */
  FileObject locateResource(String path)
}
