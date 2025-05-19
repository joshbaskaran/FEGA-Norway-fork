package main

import (
	"fmt"
	"os"
	"strconv"
)

// Config holds all configuration parameters for the application
type Config struct {
	// Database configuration
	PostgresConnection string
	// TLS configuration
	EnableTLS  bool
	CaCertPath string
	// LEGA MQ configuration
	LegaMQConnection string
	LegaMQExchange   string
	LegaMQQueue      string
	// CEGA MQ configuration
	CegaMQConnection string
	CegaMQExchange   string
	CegaMQQueue      string
	// HTTPS Server
	ServerCertPath string
	ServerKeyPath  string
	ServerPort     string
}

// NewConfig creates a new Config instance with values from environment variables
func NewConfig() (*Config, error) {
	config := &Config{
		PostgresConnection: os.Getenv("POSTGRES_CONNECTION"),
		EnableTLS:          os.Getenv("ENABLE_TLS") == "true",
		CaCertPath:         os.Getenv("CA_CERT_PATH"),
		LegaMQConnection:   os.Getenv("LEGA_MQ_CONNECTION"),
		LegaMQExchange:     os.Getenv("LEGA_MQ_EXCHANGE"),
		LegaMQQueue:        os.Getenv("LEGA_MQ_QUEUE"),
		CegaMQConnection:   os.Getenv("CEGA_MQ_CONNECTION"),
		CegaMQExchange:     os.Getenv("CEGA_MQ_EXCHANGE"),
		CegaMQQueue:        os.Getenv("CEGA_MQ_QUEUE"),
		ServerCertPath:     os.Getenv("SERVER_CERT_PATH"),
		ServerKeyPath:      os.Getenv("SERVER_KEY_PATH"),
		ServerPort:         os.Getenv("SERVER_PORT"),
	}

	// Validate required configuration
	if err := config.validate(); err != nil {
		return nil, err
	}

	return config, nil
}

// validate ensures that all required configuration parameters are present
func (c *Config) validate() error {
	// Check for required environment variables
	required := map[string]string{
		"POSTGRES_CONNECTION": c.PostgresConnection,
		"CA_CERT_PATH":        c.CaCertPath,
		"LEGA_MQ_CONNECTION":  c.LegaMQConnection,
		"LEGA_MQ_EXCHANGE":    c.LegaMQExchange,
		"LEGA_MQ_QUEUE":       c.LegaMQQueue,
		"CEGA_MQ_CONNECTION":  c.CegaMQConnection,
		"CEGA_MQ_EXCHANGE":    c.CegaMQExchange,
		"CEGA_MQ_QUEUE":       c.CegaMQQueue,
	}

	for name, value := range required {
		if value == "" {
			return fmt.Errorf("required environment variable %s is not set", name)
		}
	}

	return nil
}

// String returns a string representation of the config for logging (with sensitive data masked)
func (c *Config) String() string {
	return fmt.Sprintf(
		"Config{\n"+
			"  PostgresConnection: \"***\",\n"+
			"  EnableTLS: %s,\n"+
			"  LegaMQConnection: \"***\",\n"+
			"  LegaMQExchange: %q,\n"+
			"  LegaMQQueue: %q,\n"+
			"  CegaMQConnection: \"***\",\n"+
			"  CegaMQExchange: %q,\n"+
			"  CegaMQQueue: %q\n"+
			"}",
		strconv.FormatBool(c.EnableTLS),
		c.LegaMQExchange,
		c.LegaMQQueue,
		c.CegaMQExchange,
		c.CegaMQQueue,
	)
}































3.14

   3
   3.14













