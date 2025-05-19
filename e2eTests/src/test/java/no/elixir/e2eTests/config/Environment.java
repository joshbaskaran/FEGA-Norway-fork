package no.elixir.e2eTests.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class Environment {

  private final String cegaAuthUsername;
  private final String cegaAuthPassword;
  private final String cegaConnString;
  private final String proxyHost;
  private final String proxyPort;
  private final String sdaDbUsername;
  private final String sdaDbPassword;
  private final String sdaDbHost;
  private final String sdaDbPort;
  private final String sdaDoaHost;
  private final String sdaDoaPort;
  private final String sdaDbDatabaseName;
  private final String truststorePassword;
  private final String runtime;

  // Static Dotenv instance for accessing environment variables
  private static final Dotenv dotenv = Dotenv.load();

  // Constructor
  public Environment() {
    this.runtime = dotenv.get("E2E_RUNTIME");
    this.cegaAuthUsername = dotenv.get("E2E_CEGAAUTH_USERNAME");
    this.cegaAuthPassword = dotenv.get("E2E_CEGAAUTH_PASSWORD");
    this.cegaConnString = dotenv.get("E2E_CEGAMQ_CONN_STR");
    this.proxyHost = dotenv.get("E2E_PROXY_HOST");
    this.proxyPort = dotenv.get("E2E_PROXY_PORT");
    this.sdaDbHost = dotenv.get("E2E_SDA_DB_HOST");
    this.sdaDbPort = dotenv.get("E2E_SDA_DB_PORT");
    this.sdaDbUsername = dotenv.get("E2E_SDA_DB_USERNAME");
    this.sdaDbPassword = dotenv.get("E2E_SDA_DB_PASSWORD");
    this.sdaDbDatabaseName = dotenv.get("E2E_SDA_DB_DATABASE_NAME");
    this.sdaDoaHost = dotenv.get("E2E_SDA_DOA_HOST");
    this.sdaDoaPort = dotenv.get("E2E_SDA_DOA_PORT");
    this.truststorePassword = dotenv.get("E2E_TRUSTSTORE_PASSWORD");
  }

  // Method to construct and return the broker connection string
  public String getBrokerConnectionString() {
    return cegaConnString;
  }
}
