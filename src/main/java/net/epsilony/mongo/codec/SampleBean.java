package net.epsilony.mongo.codec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.common.collect.Sets;

public class SampleBean {

	byte byteV;
	char charV;
	short shortV;
	int intV;
	long longV;
	float floatV;
	double doubleV;
	boolean boolV;

	Byte byteW;
	Character charW;
	Short shortW;
	Integer intW;
	Long longW;
	Float floatW;
	Double doubleW;
	Boolean boolW;

	Map<String, Object> mapV = new LinkedHashMap<>();

	ObjectId id = new ObjectId();

	List<ObjectId> idList = Arrays.asList(new ObjectId());

	Set<ObjectId> idSet = Sets.newLinkedHashSet(Arrays.asList(new ObjectId()));

	List<List<ObjectId>> complexList = new ArrayList<>();
	{
		complexList.add(Arrays.asList(new ObjectId()));
		complexList.add(Arrays.asList(new ObjectId()));
	}

	public byte getByteV() {
		return byteV;
	}

	public void setByteV(byte byteV) {
		this.byteV = byteV;
	}

	public char getCharV() {
		return charV;
	}

	public void setCharV(char charV) {
		this.charV = charV;
	}

	public short getShortV() {
		return shortV;
	}

	@AlterName("shortVVV")
	public void setShortV(short shortV) {
		this.shortV = shortV;
	}

	public int getIntV() {
		return intV;
	}

	public void setIntV(int intV) {
		this.intV = intV;
	}

	public long getLongV() {
		return longV;
	}

	public void setLongV(long longV) {
		this.longV = longV;
	}

	public float getFloatV() {
		return floatV;
	}

	public void setFloatV(float floatV) {
		this.floatV = floatV;
	}

	public double getDoubleV() {
		return doubleV;
	}

	public void setDoubleV(double doubleV) {
		this.doubleV = doubleV;
	}

	public boolean isBoolV() {
		return boolV;
	}

	public void setBoolV(boolean boolV) {
		this.boolV = boolV;
	}

	public Byte getByteW() {
		return byteW;
	}

	public void setByteW(Byte byteW) {
		this.byteW = byteW;
	}

	public Character getCharW() {
		return charW;
	}

	public void setCharW(Character charW) {
		this.charW = charW;
	}

	public Short getShortW() {
		return shortW;
	}

	public void setShortW(Short shortW) {
		this.shortW = shortW;
	}

	public Integer getIntW() {
		return intW;
	}

	public void setIntW(Integer intW) {
		this.intW = intW;
	}

	public Long getLongW() {
		return longW;
	}

	public void setLongW(Long longW) {
		this.longW = longW;
	}

	public Float getFloatW() {
		return floatW;
	}

	public void setFloatW(Float floatW) {
		this.floatW = floatW;
	}

	public Double getDoubleW() {
		return doubleW;
	}

	public void setDoubleW(Double doubleW) {
		this.doubleW = doubleW;
	}

	public Boolean getBoolW() {
		return boolW;
	}

	public void setBoolW(Boolean boolW) {
		this.boolW = boolW;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<ObjectId> getIdList() {
		return idList;
	}

	public void setIdList(List<ObjectId> idList) {
		this.idList = idList;
	}

	public Set<ObjectId> getIdSet() {
		return idSet;
	}

	public void setIdSet(Set<ObjectId> idSet) {
		this.idSet = idSet;
	}

	public List<List<ObjectId>> getComplexList() {
		return complexList;
	}

	public void setComplexList(List<List<ObjectId>> complexList) {
		this.complexList = complexList;
	}

	public Map<String, Object> getMapV() {
		return mapV;
	}

	public void setMapV(Map<String, Object> mapV) {
		this.mapV = mapV;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (boolV ? 1231 : 1237);
		result = prime * result + ((boolW == null) ? 0 : boolW.hashCode());
		result = prime * result + byteV;
		result = prime * result + ((byteW == null) ? 0 : byteW.hashCode());
		result = prime * result + charV;
		result = prime * result + ((charW == null) ? 0 : charW.hashCode());
		result = prime * result + ((complexList == null) ? 0 : complexList.hashCode());
		long temp;
		temp = Double.doubleToLongBits(doubleV);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((doubleW == null) ? 0 : doubleW.hashCode());
		result = prime * result + Float.floatToIntBits(floatV);
		result = prime * result + ((floatW == null) ? 0 : floatW.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idList == null) ? 0 : idList.hashCode());
		result = prime * result + ((idSet == null) ? 0 : idSet.hashCode());
		result = prime * result + intV;
		result = prime * result + ((intW == null) ? 0 : intW.hashCode());
		result = prime * result + (int) (longV ^ (longV >>> 32));
		result = prime * result + ((longW == null) ? 0 : longW.hashCode());
		result = prime * result + ((mapV == null) ? 0 : mapV.hashCode());
		result = prime * result + shortV;
		result = prime * result + ((shortW == null) ? 0 : shortW.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleBean other = (SampleBean) obj;
		if (boolV != other.boolV)
			return false;
		if (boolW == null) {
			if (other.boolW != null)
				return false;
		} else if (!boolW.equals(other.boolW))
			return false;
		if (byteV != other.byteV)
			return false;
		if (byteW == null) {
			if (other.byteW != null)
				return false;
		} else if (!byteW.equals(other.byteW))
			return false;
		if (charV != other.charV)
			return false;
		if (charW == null) {
			if (other.charW != null)
				return false;
		} else if (!charW.equals(other.charW))
			return false;
		if (complexList == null) {
			if (other.complexList != null)
				return false;
		} else if (!complexList.equals(other.complexList))
			return false;
		if (Double.doubleToLongBits(doubleV) != Double.doubleToLongBits(other.doubleV))
			return false;
		if (doubleW == null) {
			if (other.doubleW != null)
				return false;
		} else if (!doubleW.equals(other.doubleW))
			return false;
		if (Float.floatToIntBits(floatV) != Float.floatToIntBits(other.floatV))
			return false;
		if (floatW == null) {
			if (other.floatW != null)
				return false;
		} else if (!floatW.equals(other.floatW))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idList == null) {
			if (other.idList != null)
				return false;
		} else if (!idList.equals(other.idList))
			return false;
		if (idSet == null) {
			if (other.idSet != null)
				return false;
		} else if (!idSet.equals(other.idSet))
			return false;
		if (intV != other.intV)
			return false;
		if (intW == null) {
			if (other.intW != null)
				return false;
		} else if (!intW.equals(other.intW))
			return false;
		if (longV != other.longV)
			return false;
		if (longW == null) {
			if (other.longW != null)
				return false;
		} else if (!longW.equals(other.longW))
			return false;
		if (mapV == null) {
			if (other.mapV != null)
				return false;
		} else if (!mapV.equals(other.mapV))
			return false;
		if (shortV != other.shortV)
			return false;
		if (shortW == null) {
			if (other.shortW != null)
				return false;
		} else if (!shortW.equals(other.shortW))
			return false;
		return true;
	}

}
