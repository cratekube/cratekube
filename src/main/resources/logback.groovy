appender('STDOUT', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%msg%n'
  }
}

root ERROR, ['STDOUT']
logger 'io.cratekube', DEBUG
