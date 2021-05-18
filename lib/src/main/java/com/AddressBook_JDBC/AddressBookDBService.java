package com.AddressBook_JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

	private static AddressBookDBService addressBookDBService;

	private AddressBookDBService() {
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<AddressBookData> readData() throws AddressBookException {
		String sql = "SELECT * FROM addressBook; ";
		return this.getAddressBookDataUsingDB(sql);
	}

	private List<AddressBookData> getAddressBookDataUsingDB(String sql) throws AddressBookException {
		List<AddressBookData> addressBookList = new ArrayList<>();
		try (Connection connection = AddressBookConnection.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			addressBookList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookList;
	}

	private List<AddressBookData> getAddressBookData(ResultSet resultSet) throws AddressBookException {
		List<AddressBookData> addressBookList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				String address = resultSet.getString("Address");
				String city = resultSet.getString("City");
				String state = resultSet.getString("State");
				String zip = resultSet.getString("Zip");
				String phoneNo = resultSet.getString("PhoneNumber");
				String email = resultSet.getString("Email");
				addressBookList.add(new AddressBookData(firstName, lastName, address, city, state, zip, phoneNo, email));
			}
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookList;
	}

}
