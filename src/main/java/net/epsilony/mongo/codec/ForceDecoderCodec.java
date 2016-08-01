package net.epsilony.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;

public class ForceDecoderCodec<T> implements Codec<T> {

	private Decoder<? extends T> decoder;
	private Encoder<T> encoder;
	private Class<T> clazz;

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		encoder.encode(writer, value, encoderContext);
	}

	@Override
	public Class<T> getEncoderClass() {
		return clazz;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		return decoder.decode(reader, decoderContext);
	}

	public ForceDecoderCodec(Encoder<T> encoder, Class<T> clazz, Decoder<? extends T> decoder) {
		this.decoder = decoder;
		this.encoder = encoder;
		this.clazz = clazz;
	}

}
