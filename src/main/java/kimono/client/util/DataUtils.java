package kimono.client.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import kimono.api.v2.interopdata.model.NameType;

public class DataUtils {

	private DataUtils() {
	}

	public static String asString(Map<String, Object> map, String key) {
		Object value = map == null ? null : map.get(key);
		return value == null ? null : value.toString();
	}
	
	/**
	 * Combine the components of a Kimono {@code NameType} into a single string
	 * in the form {@code last, first middle}
	 * @param name
	 * @return
	 */
	public static String combine(NameType name) {
		StringBuilder str = new StringBuilder();
		if (!StringUtils.isBlank(name.getLast()) ) {
			str.append(name.getLast());
		}
		if (!StringUtils.isBlank(name.getFirst()) ) {
			if( str.length() > 0 ) {
				str.append(", ");
			}
			str.append(name.getFirst());
		}
		if (!StringUtils.isBlank(name.getMiddle()) ) {
			if( str.length() > 0 ) {
				str.append(" ");
			}
			str.append(name.getMiddle());
		}
		return str.toString();
	}
}
