/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.acesinc.data.json.generator.log;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.acesinc.data.json.generator.serializer.JsonSerializer;
import net.acesinc.data.json.util.JsonUtils;

/**
 * @author kmandalas
 */
public class KafkaJsonLogger implements EventLogger {

	private static final Logger log = LogManager.getLogger(KafkaJsonLogger.class);
	public static final String BROKER_SERVER_PROP_NAME = "broker.server";
	public static final String BROKER_PORT_PROP_NAME = "broker.port";

	private final Properties props = new Properties();
	private final KafkaProducer<String, JsonNode> producer;
	private final String topic;
	private final String keyAttribute;
	private JsonUtils jsonUtils;
	private boolean sync;

	public KafkaJsonLogger(Map<String, Object> props) {
		String brokerHost = (String) props.get(BROKER_SERVER_PROP_NAME);
		Integer brokerPort = (Integer) props.get(BROKER_PORT_PROP_NAME);

		this.props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost + ":" + brokerPort.toString());
		this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

		producer = new KafkaProducer<>(this.props);

		this.topic = (String) props.get("topic");
		this.keyAttribute = (String) props.get("keyAttribute");
		if (props.get("sync") != null) {
			this.sync = (Boolean) props.get("sync");
		} else {
			this.sync = false;
		}

		this.jsonUtils = new JsonUtils();
	}

	@Override
	public void logEvent(String event, Map<String, Object> producerConfig) {
		logEvent(event);
	}

	private void logEvent(String event) {
		boolean sync = false;

		JsonNode jsonNode = null;
		try {
			jsonNode = new ObjectMapper().readValue(event, JsonNode.class);
		} catch (IOException e) {
			log.error(e);
			System.exit(0);
		}

		ProducerRecord<String, JsonNode> producerRecord;
		if (StringUtils.isNotBlank(this.keyAttribute)) {
			JsonNode key = jsonNode.findValue(this.keyAttribute);
			producerRecord = new ProducerRecord<>(topic, key.toString(), jsonNode);
		} else {
			producerRecord = new ProducerRecord<>(topic, jsonNode);
		}

		if (sync) {
			try {
				producer.send(producerRecord).get();
			} catch (InterruptedException | ExecutionException ex) {
				//got interrupted while waiting
				log.warn("Thread interrupted while waiting for synchronous response from producer", ex);
			}
		} else {
			log.debug("Sending event to Kafka: [ " + jsonNode + " ]");
			producer.send(producerRecord);
		}
	}

	@Override
	public void shutdown() {
		producer.close();
	}
}
