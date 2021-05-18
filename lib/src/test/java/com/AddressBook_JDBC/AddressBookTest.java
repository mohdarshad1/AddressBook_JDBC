package com.AddressBook_JDBC;

import java.util.Arrays;
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

	@Test
	public void givenAddressBook_WhenUpdate_ShouldSyncWithDB() throws AddressBookException {
		addressBookService.updateRecord("A", "NKB");
		boolean result = addressBookService.checkUpdatedRecordSyncWithDatabase("A");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenAddressBook_WhenRetrieved_ShouldMatchCountInGivenRange() throws AddressBookException {
		List<AddressBookData> addressBookData = addressBookService.readAddressBookData(IOService.DB_IO, "2018-02-14",
				"2020-06-02");
		Assert.assertEquals(1, addressBookData.size());
	}
	
	@Test
	public void givenAddresBook_WhenRetrieved_ShouldReturnCountOfCity() throws AddressBookException {
		Assert.assertEquals(1, addressBookService.readAddressBookData("ND", "DEL"));
	}
	
	@Test
	public void givenAddresBookDetails_WhenAdded_ShouldSyncWithDB() throws AddressBookException {
		addressBookService.readAddressBookData(IOService.DB_IO);
		addressBookService.addNewContact("B", "S", "2020-11-22", "Jha", "JAM", "BH", "156262", "8975621034", "sap@gal.com");
		boolean result = addressBookService.checkUpdatedRecordSyncWithDatabase("B");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenMultipleContact_WhenAdded_ShouldSyncWithDB() throws AddressBookException {
		AddressBookData[] contactArray = {
				new AddressBookData("C", "J", "SH", "GZ", "UP", "517536", "8975658741",
						"th@ail.com", "2020-11-23"),
				new AddressBookData("N", "P", "OK", "DE", "AP", "517533", "9874563201",
						"ni@gl.com", "2020-11-23") };
		addressBookService.addMultipleContactsToDBUsingThreads(Arrays.asList(contactArray));
		boolean result1 = addressBookService.checkUpdatedRecordSyncWithDatabase("Tharun");
		boolean result2 = addressBookService.checkUpdatedRecordSyncWithDatabase("Nani");
		Assert.assertTrue(result1);
		Assert.assertTrue(result2);

	}
}