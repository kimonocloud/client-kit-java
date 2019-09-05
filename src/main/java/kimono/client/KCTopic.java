package kimono.client;

import org.apache.commons.lang3.StringUtils;

public class KCTopic {

	private static final String DEFAULT_SCHEMA="RDM";
	
	private String schema;
	
	private String name;
	
	private KCTopic( String schema, String name ) {
		this.schema=schema;
		this.name=name;
	}
	
	/**
	 * Create a {@link KCTopic} by parsing a string in the form
	 * {@code schema:name}. If the string does not contain a colon delimiter,
	 * the schema defaults to {@link #DEFAULT_SCHEMA}.
	 * @param schemaAndName The string to parse
	 */
	public static KCTopic parse( String schemaAndName ) {
		return parse(schemaAndName,DEFAULT_SCHEMA);
	}
	
	/**
	 * Create a {@link KPEntityName} by parsing a string in the form
	 * {@code schema:name}. If the string does not contain a colon delimiter,
	 * the schema defaults to {@code defaultSchema}.
	 * @param schemaAndName The string to parse
	 * @param defaultSchema The schema to use if none is specified in {@code schemaAndTopic}
	 */
	public static KCTopic parse( String schemaAndName, String defaultSchemaId ) {
		String[] d = StringUtils.split(schemaAndName,":");
		if( d.length == 2 ) {
			return new KCTopic(d[0],d[1]);
		} 
		return new KCTopic(defaultSchemaId,d[0]);
	}
	
	public String getSchema() {
		return schema;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return name.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if( obj instanceof KCTopic ) {
			KCTopic otherTopic = (KCTopic)obj;
			boolean schemaEquals = schema.equals(otherTopic.getSchema());
			return schemaEquals && name.equalsIgnoreCase(otherTopic.getName());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return schema.toString()+":"+name;
	}}
