package com.AddressBook_JDBC;

public class AddressBookException extends Exception {

	enum ExceptionType {
		DATABASE_EXCEPTION, NO_SUCH_CLASS
	}

	public ExceptionType type;

	public AddressBookException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}

}
