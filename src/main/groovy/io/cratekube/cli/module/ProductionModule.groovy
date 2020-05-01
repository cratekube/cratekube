package io.cratekube.cli.module

import com.aestasit.infrastructure.ssh.SshOptions
import com.aestasit.infrastructure.ssh.dsl.SshDslEngine
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.StringHelpers
import com.google.inject.AbstractModule
import com.google.inject.Provides

class ProductionModule extends AbstractModule {
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
