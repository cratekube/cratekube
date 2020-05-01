package io.cratekube.cli.service

import io.cratekube.cli.api.DefaultProcessExecutor

class RkeProcess extends DefaultProcessExecutor {
  String executablePath = '/usr/local/bin/rke'
}
