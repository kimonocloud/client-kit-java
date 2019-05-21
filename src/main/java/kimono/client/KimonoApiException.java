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
}
