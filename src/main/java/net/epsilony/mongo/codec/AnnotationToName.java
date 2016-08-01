package net.epsilony.mongo.codec;

public interface AnnotationToName<T> {

	public Class<T> getAnnotationClass();

	public String getName(T annotation);
}
