#!/bin/bash

source env.sh

# Utility functions --

# Check the existence of a passed command but discard
# the outcome through redirection whether its successful
# or erroneous.
function exists() {
  command -v "$1" 1>/dev/null 2>&1
}

function escape_special_chars() {
  echo "$1" | sed -e 's/[]\/$*.^[]/\\&/g'
}

# Find and replace all the strings matching target
# in a specified file.
function frepl() {
  local search=$(escape_special_chars "$1")
  local replace=$(escape_special_chars "$2")
  sed -i.bak "s/$search/$replace/g" "$3"
  rm -rf "$3.bak"
}

# Functionalities --

function apply_configs() {

  # Check if the source template file exists
  if [ -f "docker-compose.template.yml" ]; then
    # Copy the content of docker-compose.template.yml to docker-compose.yml
    cp docker-compose.template.yml ./docker-compose.yml
    rm -rf docker-compose.yml.bak >/dev/null 2>&1
    echo "docker-compose.yml has been successfully created from the template."
  else
    echo "Error: docker-compose.template.yml does not exist."
  fi

  local f=docker-compose.yml

  # Proxy
  frepl "<<PROXY_SSL_ENABLED>>" "$PROXY_SSL_ENABLED" $f
  frepl "<<PROXY_TSD_SECURE>>" "$PROXY_TSD_SECURE" $f
  frepl "<<PROXY_DB_PORT>>" "$PROXY_DB_PORT" $f
  frepl "<<PROXY_ROOT_CERT_PASSWORD>>" "$PROXY_ROOT_CERT_PASSWORD" $f
  frepl "<<PROXY_TSD_ROOT_CERT_PASSWORD>>" "$PROXY_TSD_ROOT_CERT_PASSWORD" $f
  frepl "<<PROXY_SERVER_CERT_PASSWORD>>" "$PROXY_SERVER_CERT_PASSWORD" $f
  frepl "<<PROXY_CLIENT_ID>>" "$PROXY_CLIENT_ID" $f
  frepl "<<PROXY_CLIENT_SECRET>>" "$PROXY_CLIENT_SECRET" $f
  frepl "<<PROXY_CEGAAUTH_URL>>" "$PROXY_CEGAAUTH_URL" $f
  frepl "<<PROXY_CEGAAUTH_USERNAME>>" "$PROXY_CEGAAUTH_USERNAME" $f
  frepl "<<PROXY_CEGAAUTH_PASSWORD>>" "$PROXY_CEGAAUTH_PASSWORD" $f
  frepl "<<PROXY_TSD_HOST>>" "$PROXY_TSD_HOST" $f
  frepl "<<PROXY_TSD_ACCESS_KEY>>" "$PROXY_TSD_ACCESS_KEY" $f
  frepl "<<PROXY_POSTGRES_PASSWORD>>" "$PROXY_POSTGRES_PASSWORD" $f
  frepl "<<PROXY_ADMIN_USER>>" "$PROXY_ADMIN_USER" $f
  frepl "<<PROXY_ADMIN_PASSWORD>>" "$PROXY_ADMIN_PASSWORD" $f
  frepl "<<PROXY_TSD_MQ_HOST>>" "$PROXY_TSD_MQ_HOST" $f
  frepl "<<PROXY_TSD_MQ_PORT>>" "$PROXY_TSD_MQ_PORT" $f
  frepl "<<PROXY_TSD_MQ_VHOST>>" "$PROXY_TSD_MQ_VHOST" $f
  frepl "<<PROXY_TSD_MQ_USERNAME>>" "$PROXY_TSD_MQ_USERNAME" $f
  frepl "<<PROXY_TSD_MQ_PASSWORD>>" "$PROXY_TSD_MQ_PASSWORD" $f
  frepl "<<PROXY_TSD_MQ_EXCHANGE>>" "$PROXY_TSD_MQ_EXCHANGE" $f
  frepl "<<PROXY_TSD_MQ_EXPORT_REQUEST_ROUTING_KEY>>" "$PROXY_TSD_MQ_EXPORT_REQUEST_ROUTING_KEY" $f
  frepl "<<PROXY_TSD_MQ_INBOX_ROUTING_KEY>>" "$PROXY_TSD_MQ_INBOX_ROUTING_KEY" $f
  frepl "<<PROXY_TSD_MQ_ENABLE_TLS>>" "$PROXY_TSD_MQ_ENABLE_TLS" $f
  frepl "<<PROXY_TRUSTSTORE>>" "$PROXY_TRUSTSTORE" $f
  frepl "<<PROXY_TRUSTSTORE_PASSWORD>>" "$PROXY_TRUSTSTORE_PASSWORD" $f

  # Interceptor
  frepl "<<INTERCEPTOR_POSTGRES_CONNECTION>>" "$INTERCEPTOR_POSTGRES_CONNECTION" $f
  frepl "<<INTERCEPTOR_CEGA_MQ_CONNECTION>>" "$INTERCEPTOR_CEGA_MQ_CONNECTION" $f
  frepl "<<INTERCEPTOR_CEGA_MQ_EXCHANGE>>" "$INTERCEPTOR_CEGA_MQ_EXCHANGE" $f
  frepl "<<INTERCEPTOR_CEGA_MQ_QUEUE>>" "$INTERCEPTOR_CEGA_MQ_QUEUE" $f
  frepl "<<INTERCEPTOR_MQ_CONNECTION>>" "$INTERCEPTOR_MQ_CONNECTION" $f
  frepl "<<INTERCEPTOR_LEGA_MQ_EXCHANGE>>" "$INTERCEPTOR_LEGA_MQ_EXCHANGE" $f
  frepl "<<INTERCEPTOR_LEGA_MQ_QUEUE>>" "$INTERCEPTOR_LEGA_MQ_QUEUE" $f
  frepl "<<INTERCEPTOR_ENABLE_TLS>>" "$INTERCEPTOR_ENABLE_TLS" $f
  frepl "<<INTERCEPTOR_CA_CERT_PATH>>" "$INTERCEPTOR_CA_CERT_PATH" $f

  # Postgres
  frepl "<<POSTGRES_POSTGRES_USER>>" "$POSTGRES_POSTGRES_USER" $f
  frepl "<<POSTGRES_POSTGRES_PASSWORD>>" "$POSTGRES_POSTGRES_PASSWORD" $f
  frepl "<<POSTGRES_POSTGRES_DB>>" "$POSTGRES_POSTGRES_DB" $f

  # DB
  frepl "<<DB_POSTGRES_DB>>" "$DB_POSTGRES_DB" $f
  frepl "<<DB_PGDATA>>" "$DB_PGDATA" $f
  frepl "<<DB_POSTGRES_USER>>" "$DB_POSTGRES_USER" $f
  frepl "<<DB_POSTGRES_PASSWORD>>" "$DB_POSTGRES_PASSWORD" $f
  frepl "<<DB_POSTGRES_SERVER_CERT>>" "$DB_POSTGRES_SERVER_CERT" $f
  frepl "<<DB_POSTGRES_SERVER_KEY>>" "$DB_POSTGRES_SERVER_KEY" $f
  frepl "<<DB_POSTGRES_SERVER_CACERT>>" "$DB_POSTGRES_SERVER_CACERT" $f
  frepl "<<DB_POSTGRES_VERIFY_PEER>>" "$DB_POSTGRES_VERIFY_PEER" $f

  # SDA services (ingest, verify, finalize, mapper, intercept)
  frepl "<<SDA_ARCHIVE_TYPE>>" "$SDA_ARCHIVE_TYPE" $f
  frepl "<<SDA_ARCHIVE_LOCATION>>" "$SDA_ARCHIVE_LOCATION" $f
  frepl "<<SDA_BROKER_HOST>>" "$SDA_BROKER_HOST" $f
  frepl "<<SDA_BROKER_PORT>>" "$SDA_BROKER_PORT" $f
  frepl "<<SDA_BROKER_USER>>" "$SDA_BROKER_USER" $f
  frepl "<<SDA_BROKER_PASSWORD>>" "$SDA_BROKER_PASSWORD" $f
  frepl "<<SDA_BROKER_VHOST>>" "$SDA_BROKER_VHOST" $f
  frepl "<<SDA_BROKER_QUEUE_INGEST>>" "$SDA_BROKER_QUEUE_INGEST" $f
  frepl "<<SDA_BROKER_QUEUE_VERIFY>>" "$SDA_BROKER_QUEUE_VERIFY" $f
  frepl "<<SDA_BROKER_QUEUE_FINALIZE>>" "$SDA_BROKER_QUEUE_FINALIZE" $f
  frepl "<<SDA_BROKER_QUEUE_MAPPER>>" "$SDA_BROKER_QUEUE_MAPPER" $f
  frepl "<<SDA_BROKER_QUEUE_INTERCEPT>>" "$SDA_BROKER_QUEUE_INTERCEPT" $f
  frepl "<<SDA_BROKER_EXCHANGE>>" "$SDA_BROKER_EXCHANGE" $f
  frepl "<<SDA_BROKER_ROUTINGKEY_INGEST>>" "$SDA_BROKER_ROUTINGKEY_INGEST" $f
  frepl "<<SDA_BROKER_ROUTINGKEY_VERIFY>>" "$SDA_BROKER_ROUTINGKEY_VERIFY" $f
  frepl "<<SDA_BROKER_ROUTINGKEY_FINALIZE>>" "$SDA_BROKER_ROUTINGKEY_FINALIZE" $f
  frepl "<<SDA_BROKER_ROUTINGERROR>>" "$SDA_BROKER_ROUTINGERROR" $f
  frepl "<<SDA_BROKER_SSL>>" "$SDA_BROKER_SSL" $f
  frepl "<<SDA_BROKER_VERIFYPEER>>" "$SDA_BROKER_VERIFYPEER" $f
  frepl "<<SDA_BROKER_CACERT>>" "$SDA_BROKER_CACERT" $f
  frepl "<<SDA_BROKER_CLIENTCERT>>" "$SDA_BROKER_CLIENTCERT" $f
  frepl "<<SDA_BROKER_CLIENTKEY>>" "$SDA_BROKER_CLIENTKEY" $f
  frepl "<<SDA_C4GH_PASSPHRASE>>" "$SDA_C4GH_PASSPHRASE" $f
  frepl "<<SDA_C4GH_FILEPATH>>" "$SDA_C4GH_FILEPATH" $f
  frepl "<<SDA_DB_HOST>>" "$SDA_DB_HOST" $f
  frepl "<<SDA_DB_PORT>>" "$SDA_DB_PORT" $f
  frepl "<<SDA_DB_USER>>" "$SDA_DB_USER" $f
  frepl "<<SDA_DB_PASSWORD>>" "$SDA_DB_PASSWORD" $f
  frepl "<<SDA_DB_DATABASE>>" "$SDA_DB_DATABASE" $f
  frepl "<<SDA_DB_SSLMODE>>" "$SDA_DB_SSLMODE" $f
  frepl "<<SDA_DB_CLIENTCERT>>" "$SDA_DB_CLIENTCERT" $f
  frepl "<<SDA_DB_CLIENTKEY>>" "$SDA_DB_CLIENTKEY" $f
  frepl "<<SDA_INBOX_TYPE>>" "$SDA_INBOX_TYPE" $f
  frepl "<<SDA_INBOX_LOCATION>>" "$SDA_INBOX_LOCATION" $f
  frepl "<<SDA_LOG_LEVEL>>" "$SDA_LOG_LEVEL" $f

  # DOA
  frepl "<<DOA_SSL_MODE>>" "$DOA_SSL_MODE" $f
  frepl "<<DOA_SSL_ENABLED>>" "$DOA_SSL_ENABLED" $f
  frepl "<<DOA_ARCHIVE_PATH>>" "$DOA_ARCHIVE_PATH" $f
  frepl "<<DOA_DB_INSTANCE>>" "$DOA_DB_INSTANCE" $f
  frepl "<<DOA_POSTGRES_USER>>" "$DOA_POSTGRES_USER" $f
  frepl "<<DOA_POSTGRES_PASSWORD>>" "$DOA_POSTGRES_PASSWORD" $f
  frepl "<<DOA_POSTGRES_DB>>" "$DOA_POSTGRES_DB" $f
  frepl "<<DOA_OUTBOX_ENABLED>>" "$DOA_OUTBOX_ENABLED" $f
  frepl "<<DOA_KEYSTORE_PATH>>" "$DOA_KEYSTORE_PATH" $f
  frepl "<<DOA_KEYSTORE_PASSWORD>>" "$DOA_KEYSTORE_PASSWORD" $f

  # CEGAMQ
  frepl "<<CEGAMQ_CONFIG_FILE>>" "$CEGAMQ_CONFIG_FILE" $f
  frepl "<<CEGAMQ_ENABLED_PLUGINS_FILE>>" "$CEGAMQ_ENABLED_PLUGINS_FILE" $f
  frepl "<<CEGAMQ_NODE_PORT>>" "$CEGAMQ_NODE_PORT" $f

  # CEGAAUTH
  frepl "<<CEGAAUTH_CEGA_USERS_PASSWORD>>" "$CEGAAUTH_CEGA_USERS_PASSWORD" $f
  frepl "<<CEGAAUTH_CEGA_USERS_USER>>" "$CEGAAUTH_CEGA_USERS_USER" $f

  # Heartbeat
  frepl "<<HEARTBEAT_MODE_PUB>>" "$HEARTBEAT_MODE_PUB" $f
  frepl "<<HEARTBEAT_MODE_SUB>>" "$HEARTBEAT_MODE_SUB" $f
  frepl "<<HEARTBEAT_RABBITMQ_HOST>>" "$HEARTBEAT_RABBITMQ_HOST" $f
  frepl "<<HEARTBEAT_RABBITMQ_PORT>>" "$HEARTBEAT_RABBITMQ_PORT" $f
  frepl "<<HEARTBEAT_RABBITMQ_USER>>" "$HEARTBEAT_RABBITMQ_USER" $f
  frepl "<<HEARTBEAT_RABBITMQ_PASS>>" "$HEARTBEAT_RABBITMQ_PASS" $f
  frepl "<<HEARTBEAT_RABBITMQ_VHOST>>" "$HEARTBEAT_RABBITMQ_VHOST" $f
  frepl "<<HEARTBEAT_RABBITMQ_EXCHANGE>>" "$HEARTBEAT_RABBITMQ_EXCHANGE" $f
  frepl "<<HEARTBEAT_RABBITMQ_QUEUE>>" "$HEARTBEAT_RABBITMQ_QUEUE" $f
  frepl "<<HEARTBEAT_RABBITMQ_ROUTING_KEY>>" "$HEARTBEAT_RABBITMQ_ROUTING_KEY" $f
  frepl "<<HEARTBEAT_RABBITMQ_TLS>>" "$HEARTBEAT_RABBITMQ_TLS" $f
  frepl "<<HEARTBEAT_RABBITMQ_CA_CERT_PATH>>" "$HEARTBEAT_RABBITMQ_CA_CERT_PATH" $f
  frepl "<<HEARTBEAT_PUBLISH_INTERVAL>>" "$HEARTBEAT_PUBLISH_INTERVAL" $f
  frepl "<<HEARTBEAT_RABBITMQ_MANAGEMENT_PORT>>" "$HEARTBEAT_RABBITMQ_MANAGEMENT_PORT" $f
  frepl "<<HEARTBEAT_PUBLISHER_CONFIG_PATH>>" "$HEARTBEAT_PUBLISHER_CONFIG_PATH" $f
  frepl "<<HEARTBEAT_REDIS_HOST>>" "$HEARTBEAT_REDIS_HOST" $f
  frepl "<<HEARTBEAT_REDIS_PORT>>" "$HEARTBEAT_REDIS_PORT" $f
  frepl "<<HEARTBEAT_REDIS_DB>>" "$HEARTBEAT_REDIS_DB" $f

}

function check_requirements() {

    # Check if Docker is running
    if ! docker info &> /dev/null; then
        echo "Docker is not running"
        return 1
    fi

    # Check if the current user can execute Docker commands without sudo
    if ! docker ps &> /dev/null; then
        echo "The current user cannot execute Docker commands without sudo"
        return 1
    fi

    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        echo "Docker is not installed"
        return 1
    fi

    # Check if Docker Compose is available
    if ! docker compose version &> /dev/null; then
        echo "Docker Compose is not available"
        return 1
    fi

    echo "All requirements satisfied"
    return 0

}

# Entrypoint --

usage="[check_requirements|apply_configs]"
if [ $# -ge 1 ]; then
  # Parse the action argument and perform
  # the corresponding action
  case "$1" in
  "check_requirements")
    check_requirements
    ;;
  "apply_configs")
    apply_configs
    ;;
  *)
    echo "Invalid action. Usage: $0 $usage"
    exit 1
    ;;
  esac
fi
