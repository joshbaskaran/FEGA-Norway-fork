package main

import (
	"testing"
	"os"
	"io/ioutil"
	"encoding/json"
	"database/sql"
	"errors"
	"time"
	"reflect"
	_ "github.com/proullon/ramsql/driver"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"github.com/spf13/viper"
        "github.com/NeowayLabs/wabbit"
	"github.com/NeowayLabs/wabbit/amqptest"
	"github.com/NeowayLabs/wabbit/amqptest/server"
        amqp091 "github.com/rabbitmq/amqp091-go"
//	amqp "github.com/NeowayLabs/wabbit/amqp"	
)

type customChannel struct {
     Channel *amqp091.Channel
}

var settings = map[string]string{
	"POSTGRES_CONNECTION" : "postgres://postgres:p0stgres_passw0rd@postgres:5432/postgres?sslmode=disable",
	"LEGA_MQ_CONNECTION" : "amqps://admin:guest@mq:5671/test",
	"LEGA_MQ_EXCHANGE" : "sda",
	"LEGA_MQ_QUEUE" : "files",
	"CEGA_MQ_CONNECTION" : "amqps://test:test@cegamq:5671/lega?cacertfile=/etc/ega/ssl/CA.cert",
	"CEGA_MQ_EXCHANGE" : "localega.v1",
	"CEGA_MQ_QUEUE" : "v1.files",
	"VERIFY_CERT" : "false",
}

func failTestOnError(err error, t *testing.T) {
     if (err != nil) {
        t.Fatalf("Fatal error: %s", err)
     }
}

func setupDatabase(connection string) (*sql.DB, error) {
	db_content := []string{
		`CREATE TABLE mapping (ega_id TEXT, elixir_id TEXT);`,
		`INSERT INTO mapping (ega_id, elixir_id) VALUES ('alice@ega.org', 'alice@elixir.org');`,
		`INSERT INTO mapping (ega_id, elixir_id) VALUES ('bob@ega.org', 'bob@elixir.org');`,
		`INSERT INTO mapping (ega_id, elixir_id) VALUES ('carol@ega.org', 'carol@elixir.org');`,
	}

	db, err := sql.Open("ramsql", connection) // The connection string is just ignored
	if err != nil {
		return nil, err
	}
	for _, statement := range db_content {
		_, err = db.Exec(statement)
		if err != nil {
			return nil, err
		}
	}
	return db, nil
}

func readJSON(filename string) (map[string]any,error) {
    jsonFile, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer jsonFile.Close()
    byteValue, _ := ioutil.ReadAll(jsonFile)
    var result map[string]any
    json.Unmarshal([]byte(byteValue), &result)
    return result, nil
}

func setupMQserver(connectionString string, configfile string, t *testing.T) {
	t.Logf("Reading MQ config: %s", configfile)
	config,err := readJSON(configfile)
	failTestOnError(err,t)

	mqServer := server.NewServer(connectionString)
	err = mqServer.Start()
	failTestOnError(err,t)
	t.Logf("Started server at: %s",connectionString)

	connection, err := amqptest.Dial(connectionString)
	failTestOnError(err,t)

	channel, err := connection.Channel()
	failTestOnError(err,t)

	// now configure the AMQP server

	/* // It seems that it is not possible to configure new Vhosts, so I just have to use the default
	vhosts := config["vhosts"]
	for _, vhost_list := range vhosts.([]any) {
		vhost := vhost_list.(map[string]any)
		t.Logf("Vhost: name=%s,\n", vhost["name"])
	}
	*/

	exchanges := config["exchanges"].([]any)
	for _, exchange_list := range exchanges {
		exchange := exchange_list.(map[string]any)
		t.Logf("Exhange: name=%s, vhost=%s, type=%s, durable=%t, auto_delete=%t, internal=%t\n",
				 exchange["name"], exchange["vhost"], exchange["type"], exchange["durable"],
				 exchange["auto_delete"], exchange["internal"])
		err = channel.ExchangeDeclare(
		exchange["name"].(string),
		exchange["type"].(string),
		wabbit.Option{
			"durable": exchange["durable"].(bool),
			"delete": exchange["auto_delete"].(bool),
			"internal": exchange["internal"].(bool),
			"noWait": false,
			},
		)
		failTestOnError(err,t)
	}
	queues := config["queues"].([]any)
	for _, queue_list := range queues {
		queue := queue_list.(map[string]any)
		t.Logf("Queue: name=%s, vhost=%s, durable=%t, auto_delete=%t\n",
			       queue["name"], queue["vhost"], queue["durable"], queue["auto_delete"])
		_ , err = channel.QueueDeclare(
		queue["name"].(string),
		wabbit.Option{
			"durable": queue["durable"].(bool),
			"delete": queue["auto_delete"].(bool),
			"exclusive": false,
			"noWait": false,
		},
		)
		failTestOnError(err,t)
	}
	bindings := config["bindings"].([]any)
	for _, binding_list := range bindings {
		binding := binding_list.(map[string]any)
		t.Logf("Binding: vhost=%s, source=%s, destination=%s, routing_key=%s, dest_type=%s\n",
				 binding["vhost"], binding["source"], binding["destination"],
				 binding["routing_key"], binding["destination_type"])
		err = channel.QueueBind(
		    binding["destination"].(string),
		    binding["routing_key"].(string),
		    binding["source"].(string),
		    wabbit.Option{"noWait": false,},
		)
		failTestOnError(err,t)
	}
}


// --------------


func testImplementation(val any, t *testing.T) {
    t.Logf("testImplementation: %#v", val)
    t.Logf("Reflect: %s", reflect.TypeOf((*server.Channel)(nil)))
//    inter := reflect.TypeOf((*amqp091.Channel)(nil)).Elem()
/*    
    if reflect.TypeOf((*server.Channel)(nil)).Implements(inter) {
        t.Logf("server.channel implements amqp091.channel")
    } else {
        t.Logf("server.channel DOES NOT implement ampq091.channel")
    }
*/
    if r, ok := val.(amqp091.Channel); ok {
        t.Logf("server.channel implements amqp091.channel. %#v",r)
    } else {
        t.Logf("server.channel DOES NOT implement amqp091.channel")
    }
}



// --------------------------------------------------------------------------

func mainmock(t *testing.T) { // copy of the first part of "main()" that sets up the connections
        t.Log("-------------- main ------------")
	var err error
	// db, err = sql.Open("postgres", os.Getenv("POSTGRES_CONNECTION"))
	// failOnError(err, "Failed to connect to DB")
	
	legaMQ, err := amqptest.Dial(os.Getenv("LEGA_MQ_CONNECTION"))
	failOnError(err, "Failed to connect to LEGA RabbitMQ")
	legaConsumeChannel, err := legaMQ.Channel()
	failOnError(err, "Failed to create LEGA consume RabbitMQ channel")
	t.Logf("Created channel: %+v", legaConsumeChannel)
	legaPublishChannel, err := legaMQ.Channel()
	failOnError(err, "Failed to create LEGA publish RabbitMQ channel")
	t.Logf("Created channel: %+v", legaPublishChannel)
	legaNotifyCloseChannel := legaMQ.NotifyClose(make(chan wabbit.Error))
	go func() {
		err := <-legaNotifyCloseChannel
		t.Fatal(err)
	}()
	t.Logf("Connecting to CEGA at: %s",os.Getenv("CEGA_MQ_CONNECTION"))
	cegaMQ, err := amqptest.Dial(os.Getenv("CEGA_MQ_CONNECTION"))
	failOnError(err, "Failed to connect to CEGA RabbitMQ")
	cegaConsumeChannel, err := cegaMQ.Channel()
	failOnError(err, "Failed to create CEGA consume RabbitMQ channel")
	t.Logf("Created channel: %+v", cegaConsumeChannel)
	t.Logf("BEFORE: %T => %+v", cegaPublishChannel, cegaPublishChannel)
	cegaPublishChannel, err := cegaMQ.Channel()
	failOnError(err, "Failed to create CEGA publish RabbitMQ channel")
	t.Logf("AFTER: %T",cegaPublishChannel)
	testImplementation(cegaPublishChannel, t)
	t.Logf("Created channel: %T => %+v", cegaPublishChannel, cegaPublishChannel)
	// cegaPublishChannel = cegaPublishChannelTemp.(*amqp091.Channel)
	cegaNotifyCloseChannel := cegaMQ.NotifyClose(make(chan wabbit.Error))
	go func() {
		err := <-cegaNotifyCloseChannel
		t.Fatal(err)
	}()
        t.Log("-------------- main end ------------")	
}

// -------------------------------------------------------

type MQinterceptorTests struct {
	suite.Suite	
}

func TestMQinterceptorTests(t *testing.T) {
	suite.Run(t, new(MQinterceptorTests))
}

func (testsuite *MQinterceptorTests) SetupSuite() {
        var err error
        t := testsuite.T()
	viper.Set("log.level", "debug")
	for k, v := range settings {
	    t.Logf("OS.env: %s = %s\n", k, v)
	    t.Setenv(k,v)
	}
        t.Log("\n---------- CEGA MQ ----------")
        setupMQserver(os.Getenv("CEGA_MQ_CONNECTION"),"test/cegamq.json",t)
        t.Log("\n---------- LEGA MQ ----------")
        setupMQserver(os.Getenv("LEGA_MQ_CONNECTION"),"test/legamq.json",t)
	t.Log("\n---------- Database ----------")
 	db, err = setupDatabase(os.Getenv("POSTGRES_CONNECTION")) // "db" is a global variable in "main.go" 
	if (err!=nil) {
	   t.Fatalf("Error setting up database: %s", err)
	}
	mainmock(t)
}

func (testsuite *MQinterceptorTests) TearDownSuite() {
        t := testsuite.T()
	t.Log("**** TEARDOWN ****")
	db.Close()
}

func (testsuite *MQinterceptorTests) Test_selectElixirIdByEGAId() {
        t := testsuite.T()
	var elixirID string
	var err error
	
        elixirID, err = selectElixirIdByEGAId("alice@ega.org")
	assert.Equal(t, elixirID, "alice@elixir.org", "EGA ID not mapped to correct Elixir ID")
	assert.Nil(t, err, "EGA<->Elixir ID mapping not found in database")
	
        elixirID, err = selectElixirIdByEGAId("carol@ega.org")
        assert.Equal(t, elixirID, "carol@elixir.org", "EGA ID not mapped to correct Elixir ID")
        assert.Nil(t, err, "EGA<->Elixir ID mapping not found in database")

        elixirID, err = selectElixirIdByEGAId("not_a_real_user") // this should return an empty string and an error
        assert.Equal(t, elixirID, "", "Database returned non-empty Elixir ID for non-existing user")
	assert.NotNil(t, err, "No error for non-existing Elixir ID when mapping")
}

func (testsuite *MQinterceptorTests) Test_selectEgaIdByElixirId() {
        t := testsuite.T()
        var egaID string
        var err error
//	var restoredb *sql.DB

        egaID, err = selectEgaIdByElixirId("alice@elixir.org")
        assert.Equal(t, egaID, "alice@ega.org", "Elixir ID not mapped to correct EGA ID")
        assert.Nil(t, err, "Elixir<->EGA ID mapping not found in database")

        egaID, err = selectEgaIdByElixirId("carol@elixir.org")
        assert.Equal(t, egaID, "carol@ega.org", "Elixir ID not mapped to correct EGA ID")
        assert.Nil(t, err, "Elixir<->EGA ID mapping not found in database")

        egaID, err = selectEgaIdByElixirId("not_a_real_user") // this should return an empty string and an error
        assert.Equal(t, egaID, "", "Database returned non-empty EGA ID for non-existing user")
        assert.NotNil(t, err, "No error for non-existing EGA ID when mapping")
/*
	restoredb = db
	db = nil // what happens if the database is not correctly instantiated?
        egaID, err = selectEgaIdByElixirId("carol@elixir.org")
        assert.Equal(t, egaID, "carol@ega.org", "Elixir ID not mapped to correct EGA ID")
        assert.Nil(t, err, "Elixir<->EGA ID mapping not found in database")
	db = restoredb
*/
}


func (testsuite *MQinterceptorTests) Test_publishError() {
     t := testsuite.T()
     original_error := errors.New("something went wrong")
     t.Logf("Original error: %+v",original_error)
     delivery := amqp091.Delivery{     
		Acknowledger: cegaPublishChannel, // (*amqp091.Channel)(0xc00028c480),
		Headers: amqp091.Table(nil),
		ContentType: "application/json",
		ContentEncoding: "UTF-8",
		DeliveryMode: 0x2,
		Priority: 0x0,
		CorrelationId: "7835fd97-68bd-4baf-b0a3-1e6dd05688e1",
		ReplyTo: "",
		Expiration: "",
		MessageId: "",
		Timestamp:time.Now(),
		Type: "",
		UserId: "",
		AppId: "",
		ConsumerTag: "ctag-/mq-interceptor-1",
		MessageCount: 0x0,
		DeliveryTag: 0x4,
		Redelivered: false,
		Exchange: "localega.v1",
		RoutingKey: "files",
 	        Body: []byte("This is a mock delivery"),
     }
     t.Logf("==> TEST publishError - BEGIN")
     err := publishError(delivery, original_error)
     t.Logf("==> TEST publishError - END")
     t.Logf("##ERROR: %s", err)
     assert.Nil(t, err, "publishError (to CEGA) returned an unexpected error")
     // check that the error post has ended up the right queue
}
