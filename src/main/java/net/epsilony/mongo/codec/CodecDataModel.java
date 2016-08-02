package net.epsilony.mongo.codec;

import java.util.Collection;

public interface CodecDataModel {
	Collection<String> getImports();

	String getTypeName();

	String getPackageName();

	String getCodecName();

	Collection<PropertyDataModel> getProperties();

	Collection<PropertyDataModel> getAllProperties();

	PropertyDataModel getId();

	Collection<TokenDataModel> getTokens();

	Boolean isEncoderOnly();

}
