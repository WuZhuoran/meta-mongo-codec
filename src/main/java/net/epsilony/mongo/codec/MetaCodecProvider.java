package net.epsilony.mongo.codec;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import net.epsilony.mongo.codec.demo.SampleBean;
import net.openhft.compiler.CompilerUtils;

public class MetaCodecProvider implements CodecProvider {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MetaCodecProvider.class);

	@SuppressWarnings("rawtypes")
	private ListMultimap<String, Class> nameToType = Multimaps
			.synchronizedListMultimap(MultimapBuilder.hashKeys().arrayListValues().build());
	private String packageName;
	@SuppressWarnings("rawtypes")
	private Map<Class, Codec> codecs = Collections.synchronizedMap(new LinkedHashMap<>());
	@SuppressWarnings("rawtypes")
	private AnnotationToName annotationToName;

	@SuppressWarnings("rawtypes")
	private Map<Class, Class> fatherToActual = Collections.synchronizedMap(new LinkedHashMap<>());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	synchronized public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {

		Codec codec = codecs.get(clazz);
		if (codec != null) {
			return codec;
		}
		String simpleName = clazz.getSimpleName();
		List<Class> list = nameToType.get(simpleName);

		String codecName = simpleName + "Codec" + (list.isEmpty() ? "" : list.size());
		nameToType.put(simpleName, clazz);

		String javaCode = MetaCodecCodeBuilder.clazz(clazz).codecName(codecName).packageName(packageName)
				.annotationToName(annotationToName).build();

		Class<? extends AbstractCodec<SampleBean>> loadFromJava;
		try {
			loadFromJava = CompilerUtils.CACHED_COMPILER.loadFromJava(packageName + "." + codecName, javaCode);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}

		AbstractCodec abstractCodec;
		try {
			abstractCodec = loadFromJava.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		abstractCodec.setCodecRegistry(registry);
		abstractCodec.setGeneratedCode(javaCode);

		Class actualClazz = fatherToActual.get(clazz);
		if (null == actualClazz) {
			codecs.put(clazz, abstractCodec);
			return abstractCodec;
		}

		Codec actualCodec = get(actualClazz, registry);
		ForceDecoderCodec forceCodec = new ForceDecoderCodec(abstractCodec, clazz, actualCodec);
		codecs.put(clazz, forceCodec);
		return forceCodec;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;

	}

	@SuppressWarnings("rawtypes")
	public AnnotationToName getAnnotationToName() {
		return annotationToName;
	}

	@SuppressWarnings("rawtypes")
	public void setAnnotationToName(AnnotationToName annotationToName) {
		this.annotationToName = annotationToName;
	}

	@SuppressWarnings("rawtypes")
	public void add(Class key, Codec value) {
		codecs.put(key, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void force(Class father, Class actual) {
		if (!father.isAssignableFrom(actual) || father.equals(actual)) {
			throw new IllegalArgumentException();
		}
		fatherToActual.put(father, actual);
	}
}
