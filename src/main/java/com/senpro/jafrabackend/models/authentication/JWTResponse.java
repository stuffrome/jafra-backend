package com.senpro.jafrabackend.models.authentication;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class JWTResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwtToken;

	public String getToken() {
		return this.jwtToken;
	}
}