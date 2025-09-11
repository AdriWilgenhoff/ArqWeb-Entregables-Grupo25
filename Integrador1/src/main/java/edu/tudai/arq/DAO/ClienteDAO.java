package edu.tudai.arq.DAO;

import edu.tudai.arq.DTO.ClienteDTO;
import edu.tudai.arq.Entidades.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static ClienteDAO instance = null;
    private static Connection conn;

    private ClienteDAO() {}

    public static synchronized ClienteDAO getInstance(Connection connection) {
        conn = connection;
        if (instance == null) {
            instance = new ClienteDAO();
        }
        return instance;
    }

    public void insertCliente(Cliente cliente) throws SQLException {
        final String sql = "INSERT INTO Cliente (idCliente, nombre, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getEmail());
            ps.executeUpdate();
        }
    }

    public List<ClienteDTO> getClientesPorFacturacion() throws SQLException {
        final String sql =
                "SELECT c.idCliente, c.nombre, SUM(p.valor * fp.cantidad) AS total_facturado " +
                        "FROM Cliente c " +
                        "JOIN Factura f ON c.idCliente = f.idCliente " +
                        "JOIN Factura_Producto fp ON f.idFactura = fp.idFactura " +
                        "JOIN Producto p ON fp.idProducto = p.idProducto " +
                        "GROUP BY c.idCliente, c.nombre " +
                        "ORDER BY total_facturado DESC";

        List<ClienteDTO> clientes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("idCliente");
                String nombre = rs.getString("nombre");
                float totalFacturado = rs.getFloat("total_facturado");
                clientes.add(new ClienteDTO(id, nombre, totalFacturado));
            }
        }
        return clientes;
    }

    public Cliente findById(int idCliente) throws SQLException {
        final String sql = "SELECT idCliente, nombre, email FROM Cliente WHERE idCliente = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idCliente");
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");
                    return new Cliente(id, nombre, email);
                }
                return null;
            }
        }
    }

    public void update(Cliente cliente) throws SQLException {
        final String sql = "UPDATE Cliente SET nombre = ?, email = ? WHERE idCliente = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getEmail());
            ps.setInt(3, cliente.getId());
        }
    }

    public void deleteById(int idCliente) throws SQLException {
        final String sql = "DELETE FROM Cliente WHERE idCliente = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
        }
    }
}
