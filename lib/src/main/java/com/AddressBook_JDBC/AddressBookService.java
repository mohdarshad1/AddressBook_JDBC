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

	public List<AddressBookData> readAddressBookData(IOService ioservice) throws AddressBookException {
		if (ioservice.equals(IOService.DB_IO))
			return this.addressBookList = addressBookDBService.readData();
		return this.addressBookList;
	}

	public void updateRecord(String firstname, String address) throws AddressBookException {
		int result = addressBookDBService.updateAddressBookData(firstname, address);
		if (result == 0)
			return;
		AddressBookData addressBookData = this.getAddressBookData(firstname);
		if (addressBookData != null)
			addressBookData.address = address;
	}

	public boolean checkUpdatedRecordSyncWithDatabase(String firstname) throws AddressBookException {
		try {
			List<AddressBookData> addressBookData = addressBookDBService.getAddressBookData(firstname);
			return addressBookData.get(0).equals(getAddressBookData(firstname));
		} catch (AddressBookException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	private AddressBookData getAddressBookData(String firstname) {
		return this.addressBookList.stream().filter(addressBookItem -> addressBookItem.firstName.equals(firstname))
				.findFirst().orElse(null);
	}

}