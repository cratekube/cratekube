package io.cratekube.cli.module.annotations

import javax.inject.Qualifier
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.FIELD
import static java.lang.annotation.ElementType.METHOD
import static java.lang.annotation.ElementType.PARAMETER
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Qualifier
@Target([FIELD, METHOD, PARAMETER])
@Retention(RUNTIME)
@interface RkeCommand {}

@Qualifier
@Target([FIELD, METHOD, PARAMETER])
@Retention(RUNTIME)
@interface KubectlCommand {}
