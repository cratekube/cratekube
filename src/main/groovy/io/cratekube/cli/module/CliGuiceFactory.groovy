package io.cratekube.cli.module

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import groovyjarjarpicocli.CommandLine

import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo
import static org.valid4j.Assertive.require

class CliGuiceFactory implements CommandLine.IFactory {
  Injector injector

  CliGuiceFactory(Module... modules) {
    this.injector = Guice.createInjector(
      require(modules?.toList(), allOf(notNullValue(), hasSize(greaterThanOrEqualTo(1))))
    )
  }

  @Override
  <K> K create(Class<K> aClass) throws Exception {
    return injector.getInstance(aClass)
  }
}
