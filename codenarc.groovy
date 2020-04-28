ruleset {
  description 'CrateKube codenarc ruleset'

  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    'NoDef' enabled: false
    'TrailingComma' enabled: false
    'VariableTypeRequired' enabled: false
    'MethodReturnTypeRequired' enabled: false
    'StaticFieldsBeforeInstanceFields' enabled: false
    'StaticMethodsBeforeInstanceMethods' enabled: false
    'PublicMethodsBeforeNonPublicMethods' enabled: false
  }
  ruleset('rulesets/design.xml')
  ruleset('rulesets/dry.xml') {
    'DuplicateListLiteral' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
    'DuplicateMapLiteral' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
    'DuplicateNumberLiteral' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
    'DuplicateStringLiteral' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
  }
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    'ClassJavadoc' enabled: false
    'LineLength' enabled: false
    'Indentation' enabled: false
    'SpaceAfterOpeningBrace' enabled: false
    'SpaceBeforeClosingBrace' enabled: false
    'SpaceAroundMapEntryColon' enabled: false
  }
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml') {
    'MisorderedStaticImports' enabled: false
    'UnusedImport' enabled: false
  }
  ruleset('rulesets/jdbc.xml')
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    'ClassName' regex: /^[A-Z][$\a-zA-Z0-9]*$/
    'FactoryMethodName' enabled: false
    'MethodName' enabled: false
  }
  ruleset('rulesets/security.xml') {
    'JavaIoPackageAccess' enabled: false
  }
  ruleset('rulesets/serialization.xml')
  ruleset('rulesets/size.xml') {
    'CrapMetric' enabled: false
    'ParameterCount' enabled: false
    'AbcMetric' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
  }
  ruleset('rulesets/unnecessary.xml') {
    'UnnecessaryReturnKeyword' enabled: false
  }
  ruleset('rulesets/unused.xml') {
    'UnusedObject' doNotApplyToFilesMatching: /.*(Spec|Test)\.groovy/
  }
}
