package io.cratekube.cli.module

import com.aestasit.infrastructure.ssh.SshOptions
import com.aestasit.infrastructure.ssh.dsl.SshDslEngine
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.StringHelpers
import com.google.inject.AbstractModule
import com.google.inject.Provides
import io.cratekube.cli.api.CloudformationApi
import io.cratekube.cli.api.ClusterApi
import io.cratekube.cli.api.ConfigApi
import io.cratekube.cli.api.Ec2Api
import io.cratekube.cli.api.KeypairGenerator
import io.cratekube.cli.api.ProcessExecutor
import io.cratekube.cli.api.RkeApi
import io.cratekube.cli.api.WorkerNodeApi
import io.cratekube.cli.service.AwsClusterService
import io.cratekube.cli.service.CloudformationService
import io.cratekube.cli.service.DefaultConfigService
import io.cratekube.cli.service.Ec2Service
import io.cratekube.cli.service.RkeProcess
import io.cratekube.cli.service.RkeService
import io.cratekube.cli.service.SshKeypairGenerator
import io.cratekube.cli.service.WorkerNodeService
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.ec2.Ec2Client

class ProductionModule extends AbstractModule {
  @Override
  protected void configure() {
    bind FileSystemManager toInstance VFS.manager
    bind Ec2Client toInstance Ec2Client.builder().build()
    bind CloudFormationClient toInstance CloudFormationClient.builder().build()
    bind Ec2Api to Ec2Service
    bind CloudformationApi to CloudformationService
    bind ConfigApi to DefaultConfigService
    bind KeypairGenerator to SshKeypairGenerator
    bind ProcessExecutor to RkeProcess
    bind RkeApi to RkeService
    bind WorkerNodeApi to WorkerNodeService
    bind ClusterApi to AwsClusterService
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
