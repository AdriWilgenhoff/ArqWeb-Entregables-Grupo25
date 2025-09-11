package edu.tudai.arq.Factory;

import edu.tudai.arq.DAO.ClienteDAO;
import edu.tudai.arq.DAO.FacturaDAO;
import edu.tudai.arq.DAO.FacturaProductoDAO;
import edu.tudai.arq.DAO.ProductoDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDAOFactory extends AbstractFactory {

    private static MySQLDAOFactory instance = null;
    public static Connection conn;

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/integrador1";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    private MySQLDAOFactory() {}

    public static synchronized MySQLDAOFactory getInstance() {
        if (instance == null) {
            instance = new MySQLDAOFactory();
        }
        return instance;
    }


    public static Connection createConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) return conn;
            } catch (SQLException ignored) {}
        }

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de MySQL: " + DRIVER);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error conectando a MySQL con URL: " + URL);
            e.printStackTrace();
        }
        return conn;
    }


    public void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    @Override
    public ClienteDAO getClienteDAO() {
        return ClienteDAO.getInstance(createConnection());
    }

    @Override
    public FacturaDAO getFacturaDAO() {
        return FacturaDAO.getInstance(createConnection());
    }

    @Override
    public ProductoDAO getProductoDAO() {
        return ProductoDAO.getInstance(createConnection());
    }

    @Override
    public FacturaProductoDAO getFactura_ProductoDAO() {
        return FacturaProductoDAO.getInstance(createConnection());
    }
}
