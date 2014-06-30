package com.zdawn.commons.jdbc.keygen;

import java.util.UUID;

public class UUIDGenerator extends GeneratorAdapter {

	@Override
	public String generateString(String entityName) {
		return UUID.randomUUID().toString();
	}

}
