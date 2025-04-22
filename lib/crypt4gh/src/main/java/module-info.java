/**
 * This module provides functionality for encrypting and decrypting data files with the Crypt4GH
 * encryption standard developed by the Global Alliance for Genomics and Health (GA4GH)
 */
module no.elixir.crypt4gh {
  requires com.rfksystems.blake2b;
  requires org.apache.commons.lang3;
  requires org.apache.commons.io;
  requires org.apache.commons.cli;
  requires org.slf4j;
  requires lombok;

  exports no.elixir.crypt4gh.stream;
  exports no.elixir.crypt4gh.pojo;
  exports no.elixir.crypt4gh.util;
}
