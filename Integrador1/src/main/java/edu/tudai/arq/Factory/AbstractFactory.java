package edu.tudai.arq.Factory;

import edu.tudai.arq.DAO.ClienteDAO;
import edu.tudai.arq.DAO.FacturaDAO;
import edu.tudai.arq.DAO.FacturaProductoDAO;
import edu.tudai.arq.DAO.ProductoDAO;

public abstract class AbstractFactory {

    public static final int MYSQL_JDBC = 1;
    public static final int DERBY_JDBC = 2;

    public abstract ClienteDAO getClienteDAO();
    public abstract FacturaDAO getFacturaDAO();
    public abstract FacturaProductoDAO getFactura_ProductoDAO();
    public abstract ProductoDAO getProductoDAO();

    public static AbstractFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
            case MYSQL_JDBC:
                return MySQLDAOFactory.getInstance();
            case DERBY_JDBC:
                return null;
            default:
                return null;
        }
    }

}
