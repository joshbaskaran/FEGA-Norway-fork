package main

import (
	"crypto/tls"
	"crypto/x509"
	amqp "github.com/rabbitmq/amqp091-go"
	"log"
	"net/url"
	"os"
	"time"
)

func GetTLSConfig() *tls.Config {
	caCertPath := os.Getenv("CA_CERT_PATH")
	if caCertPath == "" {
		log.Fatal("CA_CERT_PATH environment variable not set")
	}
	caCert, err := os.ReadFile(caCertPath)
	if err != nil {
		log.Fatalf("Failed to read CA certificate: %v", err)
	}
	caCertPool := x509.NewCertPool()
	if !caCertPool.AppendCertsFromPEM(caCert) {
		log.Fatal("Failed to add CA certificate to pool")
	}
	return &tls.Config{
		RootCAs:            caCertPool,
		InsecureSkipVerify: false,
	}
}

// DialRabbitMQ attempts to connect to RabbitMQ up to 10 times
// with a delay between retries. It returns a connection
// instance or an error.
func DialRabbitMQ(connectionString string) (*amqp.Connection, error) {
	var conn *amqp.Connection
	var err error
	var attempts = 10
	// Parse the connection string as a URL.
	u, err := url.Parse(connectionString)
	if err != nil {
		return nil, err
	}
	log.Printf("Trying to dial host: %s [I will attempt to dial %d times with 10 seconds interval]", u.Hostname(), attempts)
	for i := 0; i < attempts; i++ {
		if os.Getenv("ENABLE_TLS") == "true" {
			conn, err = amqp.DialTLS(connectionString, GetTLSConfig())
		} else {
			conn, err = amqp.Dial(connectionString)
		}
		if err == nil {
			log.Printf("Successfully connected to host %s\n", u.Hostname())
			return conn, nil
		}
		log.Printf("Attempt %d: Failed to connect to RabbitMQ: %s\n", i+1, err)
		time.Sleep(10 * time.Second) // Wait before retrying
	}
	// After all attempts, return the last error
	return nil, err
}
