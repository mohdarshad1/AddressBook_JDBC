package com.AddressBook_JDBC;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.AddressBook_JDBC.AddressBookService.IOService;


public class AddressBookTest {

	static AddressBookService addressBookService;

	@BeforeClass
	public static void createAddressBookObj() {
		addressBookService = new AddressBookService();
	}

	@Test
	public void givenAddressBookContactsInDB_WhenRetrieved_ShouldMatchContactsCount() throws AddressBookException {
		List<AddressBookData> addressBookData = addressBookService.readAddressBookData(IOService.DB_IO);
		Assert.assertEquals(1, addressBookData.size());
	}
}
