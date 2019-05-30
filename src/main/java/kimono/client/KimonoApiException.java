package kimono.client;

public class KimonoApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public KimonoApiException( String msg ) {
		super(msg);
	}
	public KimonoApiException( String msg, Exception cause ) {
		super(msg,cause);
	}
	public KimonoApiException( Exception cause ) {
		super(cause);
	}
	
	public KimonoApiException( String msg, kimono.api.v2.interop.ApiException ex ) {
		super(formatMessage(msg,ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( kimono.api.v2.interop.ApiException ex ) {
		super(formatMessage(ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( kimono.api.v2.interopdata.ApiException ex ) {
		super(formatMessage(ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( String msg, kimono.api.v2.interopdata.ApiException ex ) {
		super(formatMessage(msg,ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( kimono.api.v2.broker.ApiException ex ) {
		super(formatMessage(ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( String msg, kimono.api.v2.broker.ApiException ex ) {
		super(formatMessage(msg,ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( kimono.api.v2.platform.ApiException ex ) {
		super(formatMessage(ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( String msg, kimono.api.v2.platform.ApiException ex ) {
		super(formatMessage(msg,ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( kimono.api.v2.sifcloud.ApiException ex ) {
		super(formatMessage(ex.getMessage(),ex.getResponseBody()),ex);
	}
	public KimonoApiException( String msg,kimono.api.v2.sifcloud.ApiException ex ) {
		super(formatMessage(msg,ex.getMessage(),ex.getResponseBody()),ex);
	}

	public static String formatMessage( String exception, String jsonResponseBody ) {
		return formatMessage(null,exception,jsonResponseBody);
	}
	public static String formatMessage( String message, String exception, String jsonResponseBody ) {
		StringBuilder b = new StringBuilder();
		if( message != null ) {
			b.append(message);
		}
		if( b.length() > 0 ) {
			b.append(". ");
		}
		b.append(exception);
		if( jsonResponseBody != null ) {
			b.append(". ");
		}
		b.append(jsonResponseBody);
		return b.toString();
	}
}
