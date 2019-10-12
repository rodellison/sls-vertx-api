package sls.vertx.api;

import java.util.Map;

public class Response {

	private final String message;

	public Response(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
