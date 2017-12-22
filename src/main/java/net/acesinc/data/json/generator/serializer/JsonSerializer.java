package net.acesinc.data.json.generator.serializer;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer<T> implements Serializer<T> {
	protected final ObjectMapper objectMapper;

	public JsonSerializer() {
		this(new ObjectMapper());
		this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public JsonSerializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	public byte[] serialize(String topic, T data) {
		try {
			byte[] ex = null;
			if (data != null) {
				ex = this.objectMapper.writeValueAsBytes(data);
			}

			return ex;
		} catch (IOException var4) {
			throw new SerializationException("Can\'t serialize data [" + data + "] for topic [" + topic + "]", var4);
		}
	}

	public void close() {
	}
}