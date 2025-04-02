package no.elixir.crypt4gh.pojo.key;

/** Supported formats for private keys */
public enum Format {
  /** Key file in OpenSSL format */
  OPENSSL,
  /**
   * Key file in Crypt4GH format
   *
   * @see <a href="https://crypt4gh.readthedocs.io/en/latest/keys.html">Crypt4GH key format</a>
   */
  CRYPT4GH
}
