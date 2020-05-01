package io.cratekube.cli.api

interface KeypairGenerator {
  /**
   * Generates a keypair in a given directory.
   *
   * @param targetDir {@code non-empty} path the target directory
   * @param keyName {@code non-empty} name for the keypair
   * @return status code for keypair gen operation
   */
  int createKeypair(String targetDir, String keyName)
}
