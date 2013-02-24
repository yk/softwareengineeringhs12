package com.xbockr.dr.repository.meta.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.meta.MetaInfo;

public class Database implements IDatabase {
	public static class NotFoundException extends Exception {
		private String name;

		private static final long serialVersionUID = 471349422227928479L;

		public NotFoundException(String name) {
			super("MetaInfo with name " + name + " not found.");
			this.name = name;

		}

		public String getName() {
			return name;
		}
	}

	public static class DBException extends Exception {
		private static final long serialVersionUID = 8594441376573813376L;
		private SQLException ex;

		public DBException(String message, SQLException ex) {
			super(message);
			this.ex = ex;
		}

		public SQLException getCause() {
			return ex;
		}
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC driver not found");
		}
	}
	private File path;
	private File file;

	public Database(File path) {
		this.path = path;
		this.file = new File(new File(path, IRepository.HIDDENDIRECTORYNAME),
				DBFILENAME);
		if (!this.file.exists()) {
			createDatabase();
		}
	}

	@Override
	public File getPath() {
		return this.path;
	}

	File getFile() {
		return this.file;
	}

	private void createDatabase() {
		try {
			this.executeStatement("create table MetaInfo(id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name string, description text, originalName string, timestamp datetime, "
					+ "numberOfFiles integer, size integer)");
		} catch (DBException e) {
			throw new RuntimeException("Error creating the database");
		}
	}

	@Override
	public void executeStatement(String statementString) throws DBException {
		Connection connection = null;
		try {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ file.getAbsolutePath());
				Statement statement = connection.createStatement();
				statement.execute(statementString);
			} catch (SQLException e) {
				throw new DBException("Error opening the database", e);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new DBException("Error closing the database", e);
				}
			}
		}
	}

	@Override
	public void executeUpdate(String statementString) throws DBException {
		Connection connection = null;
		try {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ file.getAbsolutePath());
				Statement statement = connection.createStatement();
				statement.executeUpdate(statementString);
			} catch (SQLException e) {
				throw new DBException("Error opening the database", e);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new DBException("Error closing the database", e);
				}
			}
		}
	}

	@Override
	public boolean exists(String statementString) throws DBException {
		Connection connection = null;
		try {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ file.getAbsolutePath());
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(statementString);
				boolean exists = false;
				if (rs.next()) {
					exists = true;
				}
				return exists;
			} catch (SQLException e) {
				throw new DBException("Error opening the database", e);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new DBException("Error closing the database", e);
				}
			}
		}
	}

	@Override
	public MetaInfo getMetaInfo(String name) throws DBException,
			NotFoundException {
		String statementString = "select * from MetaInfo where name = '%s'";
		statementString = String.format(statementString, name);
		Connection connection = null;
		try {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ file.getAbsolutePath());
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(statementString);
				if (rs.next()) {
					return fromResultSet(rs);
				} else {
					throw new NotFoundException(name);
				}
			} catch (SQLException e) {
				throw new DBException("Error opening the database", e);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new DBException("Error closing the database", e);
				}
			}
		}
	}

	private MetaInfo fromResultSet(ResultSet rs) throws SQLException {
		String dsname = rs.getString(2);
		String description = rs.getString(3);
		String originalName = rs.getString(4);
		long timestamp = rs.getLong(5);
		long numberOfFiles = rs.getLong(6);
		long size = rs.getLong(7);
		MetaInfo metaInfo = MetaInfo.createFromDatabase(dsname,
				description, originalName, timestamp,
				numberOfFiles, size);
		return metaInfo;
	}

	@Override
	public List<MetaInfo> list() throws DBException {
		Connection connection = null;
		try {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ file.getAbsolutePath());
				String statementString = "select * from MetaInfo";
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(statementString);
				List<MetaInfo> metaInfos = new ArrayList<MetaInfo>();
				while (rs.next()) {
					metaInfos.add(fromResultSet(rs));
				}
				return metaInfos;
			} catch (SQLException e) {
				throw new DBException("Error opening the database", e);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new DBException("Error closing the database", e);
				}
			}
		}
	}
}
