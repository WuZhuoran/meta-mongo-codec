package ${packageName};

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.BsonType;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import net.epsilony.mongo.codec.AbstractCodec;
import com.google.common.reflect.TypeToken;

<#list imports as import>
import ${import};
</#list>

public class ${codecName} extends AbstractCodec<${typeName}>{

<#list tokens as tk>
	private final static TypeToken<${tk.type}> ${tk.name} = new TypeToken<${tk.type}>() {};
</#list>

	@Override
	public void encode(BsonWriter writer, ${typeName} value, EncoderContext encoderContext) {
		writer.writeStartDocument();		
		
<#if id.category != "primitive">
		if (value.${id.getterFunc}() != null) {
</#if>
			writer.writeName("_id");
<#if id.category == "primitive" || id.category == "primitiveWrapper">
	<#switch id.typeName>
		<#case "char">
		<#case "Character">
		<#case "byte">
		<#case "Byte">
		<#case "short">
		<#case "Short">
		<#case "int">
		<#case "Integer">
			writer.writeInt32(value.${id.getterFunc}());
		<#break>
		<#case "long">
		<#case "Long">
			writer.writeInt64(value.${id.getterFunc}());
		<#break>
		<#case "boolean">
		<#case "Boolean">
			writer.writeBoolean(value.${id.getterFunc}());
		<#break>
		<#case "float">
		<#case "Float">
		<#case "double">
		<#case "Double">
			writer.writeDouble(value.${id.getterFunc}());
	</#switch>
<#else>
			writeValue(writer, encoderContext, value.${id.getterFunc}());
</#if>
<#if id.category != "primitive">
		} else if (!encoderContext.isEncodingCollectibleDocument()) {
			writer.writeNull();
		}
</#if>
		
<#list properties as p>
	<#if p.getterFunc??>
		writer.writeName("${p.name}");
		<#if p.category != "primitive">
		if(null != value.${p.getterFunc}()){
		</#if>
		<#if p.category == "primitive" || p.category == "primitiveWrapper">
			<#switch p.typeName>
				<#case "char">
				<#case "Character">
				<#case "byte">
				<#case "Byte">
				<#case "short">
				<#case "Short">
				<#case "int">
				<#case "Integer">
		writer.writeInt32(value.${p.getterFunc}());
				<#break>
				<#case "long">
				<#case "Long">
		writer.writeInt64(value.${p.getterFunc}());
				<#break>
				<#case "boolean">
				<#case "Boolean">
		writer.writeBoolean(value.${p.getterFunc}());
				<#break>
				<#case "float">
				<#case "Float">
				<#case "double">
				<#case "Double">
		writer.writeDouble(value.${p.getterFunc}());
			</#switch>
		<#else>
		writeValue(writer, encoderContext, value.${p.getterFunc}());
		</#if>
		<#if p.category != "primitive">
		} else{
			writer.writeNull();
		}
		</#if>
	</#if>
	
</#list>
	
		writer.writeEndDocument();
	}

	@Override
	public Class<${typeName}> getEncoderClass() {
		return ${typeName}.class;
	}

	@Override
	public ${typeName} decode(BsonReader reader, DecoderContext decoderContext) {

<#if isEncoderOnly()>
		return null;
<#else>
		${typeName} value = new ${typeName}();
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			String fieldName = reader.readName();
			switch (fieldName) {
	<#list allProperties as p>
		<#if p.setterFunc??>
			case "${p.name}":
				if (reader.getCurrentBsonType() == BsonType.NULL) {
					reader.readNull();
			<#if p.category!="primitive">
					value.${p.setterFunc}(null);
			</#if>
				} else {
		    <#switch p.category>
			    <#case "primitive">
			    <#case "primitiveWrapper">
			    	<#switch p.typeName>
						<#case "char">
						<#case "Character">
						<#case "byte">
						<#case "Byte">
						<#case "short">
						<#case "Short">
						<#case "int">
						<#case "Integer">
					value.${p.setterFunc}((${p.primitiveName})reader.readInt32());
						<#break>
						<#case "long">
						<#case "Long">
					value.${p.setterFunc}((${p.primitiveName})reader.readInt64());
						<#break>
						<#case "boolean">
						<#case "Boolean">
					value.${p.setterFunc}((${p.primitiveName})reader.readBoolean());
						<#break>
						<#case "float">
						<#case "Float">
						<#case "double">
						<#case "Double">
					value.${p.setterFunc}((${p.primitiveName})reader.readDouble());
					</#switch>
				<#break>
			    <#case "object">
					value.${p.setterFunc}(codecRegistry.get(${p.typeName}.class).decode(reader, decoderContext));
				<#break>
				<#case "collection">
					value.${p.setterFunc}((${p.typeName})readCollection(${p.tokenName}, reader, decoderContext));
				<#break>
				<#case "map">
					value.${p.setterFunc}(readDocument(reader,decoderContext));
				<#break>
			</#switch>
				}
				break;
		</#if>
	</#list>
			default:
				reader.skipValue();
			}
		}

		reader.readEndDocument();
		
		return value;
</#if>
	}

}