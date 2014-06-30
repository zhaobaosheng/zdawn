package com.zdawn.commons.jdbc.keygen;

public class GeneratorAdapter implements IDGenerator {

	@Override
	public String generateString(String entityName) {
		throw new UnsupportedOperationException("implementation class not support generateString method");
	}

	@Override
	public Long generateLong(String entityName) {
		throw new UnsupportedOperationException("implementation class not support generateLong method");
	}

	@Override
	public Integer generateInteger(String entityName) {
		throw new UnsupportedOperationException("implementation class not support generateInteger method");
	}

}
