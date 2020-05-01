package io.cratekube.cli.service

import groovy.util.logging.Slf4j
import io.cratekube.cli.api.KeypairGenerator

import static io.cratekube.cli.model.Constants.CLUSTER_NAME

@Slf4j
class SshKeypairGenerator implements KeypairGenerator {
  @Override
  int createKeypair(String targetDir, String keyName) {
    log.info 'creating keypair [{}]', CLUSTER_NAME
    // create the cratekube ssh key
    def keyGenProc = [
      '/bin/bash',
      '-c',
      "cat /dev/zero | ssh-keygen -q -m PEM -t rsa -f ${targetDir}/${keyName} -N ''"
    ].execute()
    keyGenProc.waitForProcessOutput()

    return keyGenProc.exitValue()
  }
}
