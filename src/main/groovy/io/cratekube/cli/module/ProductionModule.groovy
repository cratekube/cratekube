package io.cratekube.cli.module

import com.aestasit.infrastructure.ssh.SshOptions
import com.aestasit.infrastructure.ssh.dsl.SshDslEngine
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.StringHelpers
import com.google.inject.AbstractModule
import com.google.inject.Provides
import io.cratekube.cli.api.ClusterApi
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.api.KeypairGenerator
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.api.RkeApi
import io.cratekube.cli.api.WorkerNodeApi
import io.cratekube.cli.module.annotations.KubectlCommand
import io.cratekube.cli.module.annotations.RkeCommand
import io.cratekube.cli.service.AwsClusterService
import io.cratekube.cli.service.DefaultConfigService
import io.cratekube.cli.service.KubectlProcess
import io.cratekube.cli.service.RkeProcess
import io.cratekube.cli.service.RkeService
import io.cratekube.cli.service.SshKeypairGenerator
import io.cratekube.cli.service.WorkerNodeService
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS

class ProductionModule extends AbstractModule {
  @Override
  protected void configure() {
    bind FileSystemManager toInstance VFS.manager
    bind ConfigApi to DefaultConfigService
    bind KeypairGenerator to SshKeypairGenerator
    bind RkeApi to RkeService
    bind WorkerNodeApi to WorkerNodeService
    bind ClusterApi to AwsClusterService

    // processes
    bind ProcessExecutor annotatedWith RkeCommand to RkeProcess
    bind ProcessExecutor annotatedWith KubectlCommand to KubectlProcess

    install new AwsModule()
  }

  @Provides
  static Handlebars handlebarsProvider() {
    return new Handlebars().registerHelpers(StringHelpers)
  }

  @Provides
  static SshDslEngine sshDslEngineProvider() {
    def opts = new SshOptions()
    opts.trustUnknownHosts = true
    return new SshDslEngine(opts)
  }
}
