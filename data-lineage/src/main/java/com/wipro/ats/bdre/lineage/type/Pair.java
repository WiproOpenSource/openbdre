package com.wipro.ats.bdre.lineage.type;

/**
 * Created by jayabroto on 12-05-2015.
 */
public class Pair {
	private String key;
	private String value;

	public Pair(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {return key;}
	public void setKey(String key) {this.key = key;}
	public String getValue() {return value;}
	public void setValue(String value) {this.value = value;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair pair = (Pair) o;
		if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
		return !(value != null ? !value.equals(pair.value) : pair.value != null);
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}