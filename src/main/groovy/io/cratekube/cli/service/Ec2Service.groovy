package io.cratekube.cli.service

import groovy.util.logging.Slf4j
import io.cratekube.cli.api.Ec2Api
import org.apache.commons.vfs2.FileSystemManager
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairRequest
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsRequest
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.ImportKeyPairRequest
import software.amazon.awssdk.services.ec2.model.ImportKeyPairResponse
import software.amazon.awssdk.services.ec2.model.KeyPairInfo

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class Ec2Service implements Ec2Api {
  Ec2Client ec2
  FileSystemManager fs

  @Inject
  Ec2Service(Ec2Client ec2, FileSystemManager fs) {
    this.ec2 = require ec2, notNullValue()
    this.fs = require fs, notNullValue()
  }

  @Override
  KeyPairInfo findKeyPairByName(String keyPairName) {
    require keyPairName, notEmptyString()

    DescribeKeyPairsResponse existingKeyPair = null
    try {
      def request = DescribeKeyPairsRequest.builder().keyNames(keyPairName).build()
      existingKeyPair = ec2.describeKeyPairs(request as DescribeKeyPairsRequest)
    } catch (Ec2Exception ex) {
      log.debug 'keypair [{}] not found', keyPairName
    }

    return existingKeyPair?.keyPairs()?[0]
  }

  @Override
  ImportKeyPairResponse importKeyPair(String keyPairName, String publicKeyPath) {
    require keyPairName, notEmptyString()
    require publicKeyPath, notEmptyString()

    def publicKeyFileObject = fs.resolveFile(publicKeyPath)
    def keyMaterial = SdkBytes.fromUtf8String(publicKeyFileObject.content.inputStream.text)
    def request = ImportKeyPairRequest.builder().keyName(keyPairName).publicKeyMaterial(keyMaterial).build()
    return ec2.importKeyPair(request as ImportKeyPairRequest)
  }

  @Override
  DeleteKeyPairResponse deleteKeyPairByName(String keyPairName) {
    require keyPairName, notEmptyString()
    def request = DeleteKeyPairRequest.builder().keyName(keyPairName).build()
    return ec2.deleteKeyPair(request as DeleteKeyPairRequest)
  }
}
