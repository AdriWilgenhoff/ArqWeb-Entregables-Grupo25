package edu.tudai.arq.DAO;

import edu.tudai.arq.Entidades.FacturaProducto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FacturaProductoDAO {

    private static FacturaProductoDAO instance = null;
    private static Connection conn;

    private FacturaProductoDAO() {}

    public static synchronized FacturaProductoDAO getInstance(Connection connection) {
        conn = connection;
        if (instance == null) {
            instance = new FacturaProductoDAO();
        }
        return instance;
    }

    public void insertFactura_Producto(FacturaProducto facturaProducto) throws SQLException {
        final String sql = "INSERT INTO Factura_Producto (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facturaProducto.getIdFactura());
            ps.setInt(2, facturaProducto.getIdProducto());
            ps.setInt(3, facturaProducto.getCantidad());
            ps.executeUpdate();
        }
    }
}
