package net.epsilony.mongo.codec;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import com.google.common.reflect.TypeToken;

public abstract class AbstractCodec<T> implements Codec<T> {

	protected static final String ID_FIELD_NAME = "_id";

	protected String generatedCode;

	protected CodecRegistry codecRegistry;

	protected void beforeFields(final BsonWriter bsonWriter, final EncoderContext encoderContext,
			final Map<String, Object> document) {
		if (encoderContext.isEncodingCollectibleDocument() && document.containsKey(ID_FIELD_NAME)) {
			bsonWriter.writeName(ID_FIELD_NAME);
			writeValue(bsonWriter, encoderContext, document.get(ID_FIELD_NAME));
		}
	}

	public CodecRegistry getCodecRegistry() {
		return codecRegistry;
	}

	public void setCodecRegistry(CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	protected boolean skipField(final EncoderContext encoderContext, final String key) {
		return encoderContext.isEncodingCollectibleDocument() && key.equals(ID_FIELD_NAME);
	}

	protected void writeIterable(final BsonWriter writer, final Iterable<Object> list,
			final EncoderContext encoderContext) {
		writer.writeStartArray();
		for (final Object value : list) {
			writeValue(writer, encoderContext, value);
		}
		writer.writeEndArray();
	}

	protected void writeMap(final BsonWriter writer, final Map<String, Object> map,
			final EncoderContext encoderContext) {
		writer.writeStartDocument();

		beforeFields(writer, encoderContext, map);

		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			if (skipField(encoderContext, entry.getKey())) {
				continue;
			}
			writer.writeName(entry.getKey());
			writeValue(writer, encoderContext, entry.getValue());
		}
		writer.writeEndDocument();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final Object value) {
		if (value == null) {
			writer.writeNull();
		} else if (value instanceof Iterable) {
			writeIterable(writer, (Iterable<Object>) value, encoderContext.getChildContext());
		} else if (value instanceof Map) {
			writeMap(writer, (Map<String, Object>) value, encoderContext.getChildContext());
		} else {
			Codec codec = codecRegistry.get(value.getClass());
			encoderContext.encodeWithChildContext(codec, writer, value);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Collection readCollection(TypeToken typeToken, BsonReader reader, DecoderContext decoderContext) {
		Class rawClass = typeToken.getRawType();
		Collection result;

		if (rawClass.isInterface()) {
			if (List.class.isAssignableFrom(rawClass)) {
				result = new ArrayList();
			} else if (Set.class.isAssignableFrom(rawClass)) {
				result = new LinkedHashSet();
			} else {
				throw new IllegalStateException(rawClass.getName() + "is not supported yet!");
			}
		} else {
			try {
				result = (Collection) rawClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		reader.readStartArray();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {

			ParameterizedType ptype = (ParameterizedType) typeToken.getType();
			Type genericType = ptype.getActualTypeArguments()[0];
			Object item;
			if (genericType instanceof Class) {
				Codec codec = codecRegistry.get((Class<T>) genericType);
				item = codec.decode(reader, decoderContext);
			} else if (genericType instanceof ParameterizedType) {
				ParameterizedType pgt = (ParameterizedType) genericType;
				if (Collection.class.isAssignableFrom((Class) pgt.getRawType())) {
					item = readCollection(TypeToken.of(pgt), reader, decoderContext);
				} else if (Map.class.isAssignableFrom((Class) pgt.getRawType())) {
					item = readDocument(reader, decoderContext);
				} else {
					throw new IllegalStateException();
				}
			} else {
				throw new IllegalStateException();
			}
			result.add(item);

		}
		reader.readEndArray();
		return result;
	}

	protected Document readDocument(BsonReader reader, DecoderContext decoderContext) {
		return codecRegistry.get(Document.class).decode(reader, decoderContext);
	}

	public String getGeneratedCode() {
		return generatedCode;
	}

	public void setGeneratedCode(String generatedCode) {
		this.generatedCode = generatedCode;
	}

}
