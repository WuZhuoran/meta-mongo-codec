package net.epsilony.mongo.codec;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.google.common.primitives.Primitives;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;

import java8.util.function.Function;

public class CodecDataModelBuilder implements Function<Class<?>, CodecDataModel> {
	private static final Map<String, String> DEFAULT_SIMPLE_TO_FULL_CLASS_NAMES = new HashMap<>();
	static {
		for (Class<?> type : Arrays.asList(BsonType.class, BsonReader.class, BsonWriter.class, DecoderContext.class,
				EncoderContext.class, AbstractCodec.class, TypeToken.class))
			DEFAULT_SIMPLE_TO_FULL_CLASS_NAMES.put(type.getSimpleName(), type.getName());
	}

	private String packageName;
	private String codecName;
	private List<String> imports = new ArrayList<>();
	private List<PropertyDataModel> allProperties;
	private Map<String, String> simpleToFullClassNames = new LinkedHashMap<>(DEFAULT_SIMPLE_TO_FULL_CLASS_NAMES);
	private SimplePropertyDataModel id;;
	private Collection<TokenDataModel> typeTokens = new ArrayList<>();
	@SuppressWarnings("rawtypes")
	private AnnotationToName annotationToName;

	@Override
	public CodecDataModel apply(Class<?> type) {

		List<PropertyDescriptor> propertyDescriptors;
		try {
			propertyDescriptors = fetchProperties(type);
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}

		Set<String> processedPropertyNames = new HashSet<>();
		allProperties = new ArrayList<>(propertyDescriptors.size());
		for (PropertyDescriptor pd : propertyDescriptors) {
			if (processedPropertyNames.contains(pd.getName())) {
				continue;
			}
			SimplePropertyDataModel pdm = createPropertyDescriptor(pd);
			processedPropertyNames.add(pd.getName());
			if (null == pdm) {
				continue;
			}
			if (pdm.getName().equals("_id")) {
				id = pdm;
			}
			allProperties.add(pdm);
		}
		if (id == null) {
			for (PropertyDataModel pdm : allProperties) {
				SimplePropertyDataModel spdm = (SimplePropertyDataModel) pdm;
				if ("id".equals(spdm.getName())) {
					spdm.setName("_id");
					id = spdm;
					break;
				}
			}
		}

		String typeName = settleTypeName(type);

		return new CodecDataModel() {

			@Override
			public String getTypeName() {
				return typeName;
			}

			@Override
			public Collection<PropertyDataModel> getProperties() {
				List<PropertyDataModel> result = new ArrayList<>(allProperties);
				result.remove(id);
				return result;
			}

			@Override
			public String getPackageName() {
				return packageName;
			}

			@Override
			public Collection<String> getImports() {
				return imports;
			}

			@Override
			public PropertyDataModel getId() {
				return id;
			}

			@Override
			public String getCodecName() {
				return codecName;
			}

			@Override
			public Collection<PropertyDataModel> getAllProperties() {
				return allProperties;
			}

			@Override
			public Collection<TokenDataModel> getTokens() {
				return typeTokens;
			}

			@Override
			public Boolean isEncoderOnly() {
				return type.isInterface() || Modifier.isAbstract(type.getModifiers());
			}
		};
	}

	private List<PropertyDescriptor> fetchProperties(Class<?> type) throws IntrospectionException {
		List<PropertyDescriptor> propertyDescriptors;
		if (type.isInterface()) {
			propertyDescriptors = new ArrayList<>();
			propertyDescriptors.addAll(Arrays.asList(Introspector.getBeanInfo(type).getPropertyDescriptors()));
			Class<?>[] interfaces = type.getInterfaces();
			if (null == interfaces) {
				return propertyDescriptors;
			}
			for (Class<?> superInterface : interfaces) {
				propertyDescriptors.addAll(fetchProperties(superInterface));
			}
			return propertyDescriptors;
		} else {
			propertyDescriptors = Arrays.asList(Introspector.getBeanInfo(type, Object.class).getPropertyDescriptors());
			return propertyDescriptors;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private SimplePropertyDataModel createPropertyDescriptor(PropertyDescriptor pd) {
		Class<?> propertyType = pd.getPropertyType();
		getClass();

		if (propertyType.isArray()) {
			throw new IllegalStateException("Java Array properties are not supported! Take Collection instead!");
		}
		if (propertyType.isLocalClass()) {
			throw new IllegalStateException("Java local class properties are not supported!");
		}
		if (propertyType.isMemberClass()) {
			throw new IllegalStateException("Java member class properties are not supported!");
		}
		String typeName = settleTypeName(propertyType);
		String name = pd.getName();
		Method writeMethod = pd.getWriteMethod();
		Method readMethod = pd.getReadMethod();

		if (null != writeMethod) {
			IgnoreInCodec ignoreInCodec = writeMethod.getAnnotation(IgnoreInCodec.class);
			if (null != ignoreInCodec) {
				writeMethod = null;
				if (ignoreInCodec.both()) {
					readMethod = null;
				}
			}
		}
		if (null != readMethod) {
			IgnoreInCodec ignoreInCodec = readMethod.getAnnotation(IgnoreInCodec.class);
			if (null != ignoreInCodec) {
				readMethod = null;
				if (ignoreInCodec.both()) {
					writeMethod = null;
				}
			}
		}

		if (readMethod == null && readMethod == null) {
			return null;
		}

		String setterFunc = writeMethod == null ? null : writeMethod.getName();
		String getterFunc = readMethod == null ? null : readMethod.getName();
		SimplePropertyDataModel pdm = new SimplePropertyDataModel();
		pdm.setGetterFunc(getterFunc);
		pdm.setSetterFunc(setterFunc);
		pdm.setTypeName(typeName);

		if (writeMethod != null) {
			AlterName alterName = writeMethod.getAnnotation(AlterName.class);
			if (null != alterName) {
				name = alterName.value();
			}
			if (null != annotationToName) {
				Object annotation = writeMethod.getAnnotation(annotationToName.getAnnotationClass());
				if (null != annotation) {
					name = annotationToName.getName(annotation);
				}
			}
		}
		if (readMethod != null) {
			AlterName alterName = readMethod.getAnnotation(AlterName.class);
			if (null != alterName) {
				name = alterName.value();
			}
			if (null != annotationToName) {
				Object annotation = readMethod.getAnnotation(annotationToName.getAnnotationClass());
				if (null != annotation) {
					name = annotationToName.getName(annotation);
				}
			}
		}
		pdm.setName(name);

		String category = "object";
		if (propertyType.isPrimitive()) {
			category = "primitive";
			pdm.setPrimitiveName(typeName);
		} else if (Primitives.isWrapperType(propertyType)) {
			category = "primitiveWrapper";
			pdm.setPrimitiveName(Primitives.unwrap(propertyType).getName());
		} else if (Collection.class.isAssignableFrom(propertyType)) {
			category = "collection";
			ParameterizedType genericParameterType = null;
			if (null != writeMethod) {
				Type parameterType = writeMethod.getGenericParameterTypes()[0];
				if (parameterType != null && parameterType instanceof ParameterizedType) {
					genericParameterType = (ParameterizedType) parameterType;
				}
			}
			if (null == genericParameterType && null != readMethod) {
				Type returnType = readMethod.getGenericReturnType();
				if (returnType instanceof ParameterizedType) {
					genericParameterType = (ParameterizedType) returnType;
				}
			}
			if (null != genericParameterType) {
				TokenDataModel tokenDataModel = new TokenDataModel();
				tokenDataModel.setName(pdm.getTokenName());
				tokenDataModel.setType(genericParameterType.toString());
				typeTokens.add(tokenDataModel);
			}
			if (genericParameterType == null) {
				throw new IllegalStateException();
			}
		} else if (Map.class.isAssignableFrom(propertyType)) {
			category = "map";
		}
		pdm.setCategory(category);
		return pdm;
	}

	private String settleTypeName(Class<?> type) {
		String oldFullName = simpleToFullClassNames.get(type.getSimpleName());
		String propertyPackage = Reflection.getPackageName(type);
		String typeName = type.getSimpleName();
		if (!type.isPrimitive() && !propertyPackage.equals("java.lang") && !propertyPackage.equals(packageName)) {
			if (oldFullName != null) {
				if (!oldFullName.equals(type.getName())) {
					typeName = type.getName();
				}
			} else {
				simpleToFullClassNames.put(typeName, type.getName());
				imports.add(type.getName());
			}
		}
		return typeName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCodecName() {
		return codecName;
	}

	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}

	@SuppressWarnings("rawtypes")
	public AnnotationToName getAnnotationToName() {
		return annotationToName;
	}

	@SuppressWarnings("rawtypes")
	public void setAnnotationToName(AnnotationToName annotationToName) {
		this.annotationToName = annotationToName;
	}

}
