package io.cratekube.cli.api

import io.cratekube.cli.model.WorkerNodeConfig

interface WorkerNodeApi {
  void configureNode(WorkerNodeConfig config)
}
