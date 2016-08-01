package net.epsilony.mongo.codec;

public class AlterNameTransformer implements AnnotationToName<AlterName> {

	@Override
	public Class<AlterName> getAnnotationClass() {
		return AlterName.class;
	}

	@Override
	public String getName(AlterName annotation) {
		return annotation.value().toString();
	}

}
