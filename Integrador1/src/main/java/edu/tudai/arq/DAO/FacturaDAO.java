package edu.tudai.arq.DAO;

import edu.tudai.arq.Entidades.Factura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FacturaDAO {

    private static FacturaDAO instance = null;
    private static Connection conn;

    private FacturaDAO() {}

    public static synchronized FacturaDAO getInstance(Connection connection) {
        conn = connection;
        if (instance == null) {
            instance = new FacturaDAO();
        }
        return instance;
    }

    public void insertFactura(Factura factura) throws SQLException {
        final String sql = "INSERT INTO Factura (idFactura, idCliente) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, factura.getIdFactura());
            ps.setInt(2, factura.getIdCliente());
            ps.executeUpdate();
        }
    }
}
