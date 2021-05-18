package com.AddressBook_JDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookDBService {

	private PreparedStatement addressBookPreparedStatement;
	private static AddressBookDBService addressBookDBService;
	private List<AddressBookData> addressBookData;

	private AddressBookDBService() {
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/addressBook?useSSL=false";
		String username = "root";
		String password = "13041997@Mda";
		Connection con;
		System.out.println("Connecting to database:" + jdbcURL);
		con = DriverManager.getConnection(jdbcURL, username, password);
		System.out.println("Connection is successful:" + con);
		return con;

	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<AddressBookData> readData() throws AddressBookException {
		String query = null;
		query = "select * from addressBook";
		return getAddressBookDataUsingDB(query);
	}

	private List<AddressBookData> getAddressBookDataUsingDB(String sql) throws AddressBookException {
		List<AddressBookData> addressBookData = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			addressBookData = this.getAddressBookDetails(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookData;
	}

	private void prepareAddressBookStatement() throws AddressBookException {
		try {
			Connection connection = this.getConnection();
			String query = "select * from addressBook where FirstName = ?";
			addressBookPreparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	private List<AddressBookData> getAddressBookDetails(ResultSet resultSet) throws AddressBookException {
		List<AddressBookData> addressBookData = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				String Start = resultSet.getString("Start");
				String address = resultSet.getString("Address");
				String city = resultSet.getString("City");
				String state = resultSet.getString("State");
				String zip = resultSet.getString("Zip");
				String phoneNo = resultSet.getString("PhoneNo");
				String email = resultSet.getString("Email");
			
				addressBookData
						.add(new AddressBookData(firstName, lastName, start, address, city, state, zip, phoneNo, email));
			}
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookData;
	}

	public int updateAddressBookData(String firstname, String address) throws AddressBookException {
		try (Connection connection = this.getConnection()) {
			String query = String.format("update addressBook set Address = '%s' where FirstName = '%s';", address,
					firstname);
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			return preparedStatement.executeUpdate(query);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.CONNECTION_FAILED);
		}
	}

	public List<AddressBookData> getAddressBookData(String firstname) throws AddressBookException {
		if (this.addressBookPreparedStatement == null)
			this.prepareAddressBookStatement();
		try {
			addressBookPreparedStatement.setString(1, firstname);
			ResultSet resultSet = addressBookPreparedStatement.executeQuery();
			addressBookData = this.getAddressBookDetails(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.CONNECTION_FAILED);
		}
		System.out.println(addressBookData);
		return addressBookData;
	}

	public List<AddressBookData> readData(LocalDate start, LocalDate end) throws AddressBookException {
		String query = null;
		if (start != null)
			query = String.format("select * from addressBook where Date between '%s' and '%s';", start, end);
		if (start == null)
			query = "select * from addressBook";
		List<AddressBookData> addressBookList = new ArrayList<>();
		try (Connection con = this.getConnection();) {
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(query);
			addressBookList = this.getAddressBookDetails(rs);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookList;
	}

	public int readDataBasedOnCity(String total, String city) throws AddressBookException {
		int count = 0;
		String query = String.format("select %s(state) from addressBook where city = '%s' group by city;", total, city);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			count = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return count;
	}

	public AddressBookData addNewContact(String firstName, String lastName, String start, String address, String city, String state,
			String zip, String phoneNo, String email) throws AddressBookException {
		int id = -1;
		AddressBookData addressBookData = null;
		String query = String.format(
				"insert into addressBook(FirstName, LastName, Start, Address, City, State, Zip, PhoneNo, Email) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s','%s')",
				firstName, lastName, start, address, city, state, zip, phoneNo, email);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowChanged = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowChanged == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			addressBookData = new AddressBookData(firstName, lastName, address, city, state, zip, phoneNo, email, date);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DATABASE_EXCEPTION);
		}
		return addressBookData;
	}

	public void addMultipleContactsToDBUsingThread(List<AddressBookData> record) {
		Map<Integer, Boolean> addressAdditionStatus = new HashMap<Integer, Boolean>();
		record.forEach(addressbookdata -> {
			Runnable task = () -> {
				addressAdditionStatus.put(addressbookdata.hashCode(), false);
				System.out.println("Contact Being Added:" + Thread.currentThread().getName());
				try {
					this.addNewContact(addressbookdata.getFirstName(), addressbookdata.getLastName(),
							addressbookdata.getAddress(), addressbookdata.getCity(), addressbookdata.getState(),
							addressbookdata.getZip(), addressbookdata.getPhoneNo(), addressbookdata.getEmail(),
							addressbookdata.getDate());
				} catch (AddressBookException e) {
					e.printStackTrace();
				}
				addressAdditionStatus.put(addressbookdata.hashCode(), true);
				System.out.println("Contact Added:" + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, addressbookdata.getFirstName());
			thread.start();
		});
		while (addressAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}