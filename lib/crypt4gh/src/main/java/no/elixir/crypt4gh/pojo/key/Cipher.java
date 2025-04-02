package no.elixir.crypt4gh.pojo.key;

/** Supported ciphers for private key encryption */
public enum Cipher {
  /** Private key file encrypted with ChaCha20-Poly1305 */
  CHACHA20_POLY1305,
  /** Private key file is not encrypted */
  NONE
}
