package com.AddressBook_JDBC;

import java.util.List;

public class AddressBookService {

	public enum IOService {
		DB_IO
	}

	private List<AddressBookData> addressBookList;
	private static AddressBookDBService addressBookDBService;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<AddressBookData> addresBookList) {
		this();
		this.addressBookList = addressBookList;
	}

	public List<AddressBookData> readAddressBookData(IOService ioservice) throws AddressBookException {
		if (ioservice.equals(IOService.DB_IO))
			this.addressBookList = addressBookDBService.readData();
		return this.addressBookList;
	}

}
