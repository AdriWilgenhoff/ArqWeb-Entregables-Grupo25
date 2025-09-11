package edu.tudai.arq.DAO;

import edu.tudai.arq.DTO.ProductoDTO;
import edu.tudai.arq.Entidades.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductoDAO {

    private static ProductoDAO instance = null;
    private static Connection conn;

    private ProductoDAO() {}

    public static synchronized ProductoDAO getInstance(Connection connection) {
        conn = connection;
        if (instance == null) {
            instance = new ProductoDAO();
        }
        return instance;
    }

    public void insertProducto(Producto producto) throws SQLException {
        final String sql = "INSERT INTO Producto (idProducto, nombre, valor) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setFloat(3, producto.getValor());
            ps.executeUpdate();
        }
    }

    public Producto findById(int idProducto) throws SQLException {
        final String sql = "SELECT idProducto, nombre, valor FROM Producto WHERE idProducto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idProducto");
                    String nombre = rs.getString("nombre");
                    float valor = rs.getFloat("valor");
                    return new Producto(id, nombre, valor);
                }
                return null;
            }
        }
    }

    public void update(Producto producto) throws SQLException {
        final String sql = "UPDATE Producto SET nombre = ?, valor = ? WHERE idProducto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setFloat(2, producto.getValor());
            ps.setInt(3, producto.getId());
        }
    }

    public void deleteById(int idProducto) throws SQLException {
        final String sql = "DELETE FROM Producto WHERE idProducto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
        }
    }

    public ProductoDTO getProdMasRecaudo() throws SQLException {
        final String sql =
                "SELECT p.idProducto, p.nombre, SUM(fp.cantidad * p.valor) AS total_recaudado " +
                        "FROM Producto p " +
                        "JOIN Factura_Producto fp ON p.idProducto = fp.idProducto " +
                        "GROUP BY p.idProducto, p.nombre " +
                        "ORDER BY total_recaudado DESC " +
                        "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("idProducto");
                String nombre = rs.getString("nombre");
                float total = rs.getFloat("total_recaudado");
                return new ProductoDTO(id, nombre, total);
            }
            return null;
        }
    }
}
