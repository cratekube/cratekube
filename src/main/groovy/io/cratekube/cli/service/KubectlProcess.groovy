package io.cratekube.cli.service

import io.cratekube.cli.api.DefaultProcessExecutor

class KubectlProcess extends DefaultProcessExecutor {
  String executablePath = '/usr/local/bin/kubectl'
}
