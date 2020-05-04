package io.cratekube.cli.api

import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse
import software.amazon.awssdk.services.ec2.model.ImportKeyPairResponse
import software.amazon.awssdk.services.ec2.model.KeyPairInfo

/**
 * Interface for interacting with the AWS EC2 API.
 */
interface Ec2Api {
  /**
   * Finds an EC2 keypair by name.  Returns null if no keypair is found
   *
   * @param keyPairName {@code non-empty} keypair name
   * @return keypair object if found, otherwise null
   */
  KeyPairInfo findKeyPairByName(String keyPairName)

  /**
   * Imports a keypair using the public key path.
   *
   * @param keyPairName {@code non-empty} keypair name
   * @param publicKeyPath {@code non-empty} path to keypair public key
   * @return the import response from AWS
   */
  ImportKeyPairResponse importKeyPair(String keyPairName, String publicKeyPath)

  /**
   * Deletes an EC2 keypair by name.
   *
   * @param keyPairName {@code non-empty} keypair name
   * @return delete response from EC2
   */
  DeleteKeyPairResponse deleteKeyPairByName(String keyPairName)
}
