package net.epsilony.mongo.codec;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MetaCodecCodeBuilder {

	private static final Logger log = LoggerFactory.getLogger(MetaCodecCodeBuilder.class);

	private String packageName = "net.epsilony.mongo.metacodec", codecName;
	@SuppressWarnings("rawtypes")
	private AnnotationToName annotationToName;
	@SuppressWarnings("rawtypes")
	private Class clazz;

	private Template template;
	{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setClassLoaderForTemplateLoading(Temp.class.getClassLoader(), "/net/epsilony/mongo/codec/");
		try {
			template = cfg.getTemplate("codec.ftl");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String build() {
		CodecDataModelBuilder producer = new CodecDataModelBuilder();
		producer.setPackageName(packageName);
		producer.setCodecName(codecName);
		producer.setAnnotationToName(annotationToName);

		CodecDataModel codecDataModel = producer.apply(clazz);

		StringWriter writer = new StringWriter();

		try {
			template.process(codecDataModel, writer);
		} catch (TemplateException | IOException e) {
			throw new IllegalStateException(e);
		}

		String javaCode = writer.toString();
		log.debug("create meta codec from {}\n{}", clazz, javaCode);
		return javaCode;
	}

	public MetaCodecCodeBuilder codecName(String codecName) {
		this.codecName = codecName;
		return this;
	}

	public MetaCodecCodeBuilder packageName(String packageName) {
		this.packageName = packageName;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public static MetaCodecCodeBuilder clazz(Class clazz) {
		MetaCodecCodeBuilder builder = new MetaCodecCodeBuilder();
		builder.clazz = clazz;
		builder.codecName = clazz.getSimpleName();
		return builder;
	}
}
