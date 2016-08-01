package net.epsilony.mongo.codec;

public class SimplePropertyDataModel implements PropertyDataModel {
	private String getterFunc, setterFunc, typeName, category, name, primitiveName;

	@Override
	public String getGetterFunc() {
		return getterFunc;
	}

	public void setGetterFunc(String getterFunc) {
		this.getterFunc = getterFunc;
	}

	@Override
	public String getSetterFunc() {
		return setterFunc;
	}

	public void setSetterFunc(String setterFunc) {
		this.setterFunc = setterFunc;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTokenName() {
		return name + "Token";
	}

	@Override
	public String getPrimitiveName() {
		return primitiveName;
	}

	public void setPrimitiveName(String primitiveName) {
		this.primitiveName = primitiveName;
	}

}
