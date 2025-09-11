package edu.tudai.arq;

import edu.tudai.arq.DAO.ClienteDAO;
import edu.tudai.arq.DAO.ProductoDAO;
import edu.tudai.arq.DTO.ClienteDTO;
import edu.tudai.arq.DTO.ProductoDTO;
import edu.tudai.arq.Factory.AbstractFactory;
import edu.tudai.arq.Helpers.MySqlHelper;

import java.sql.SQLException;
import java.util.List;

public class App {
    public static void main(String[] args) throws SQLException {

        MySqlHelper helper = new MySqlHelper();

        try {
            helper.createTables();
            helper.deleteAllData();
            helper.insertData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Obtiene y muestra el producto con mayor recaudación
        AbstractFactory fact = AbstractFactory.getDAOFactory(1);
        ProductoDAO p = fact.getProductoDAO();
        ProductoDTO productoMayorRecaudacion = p.getProdMasRecaudo();
        if (productoMayorRecaudacion != null) {
            System.out.println(productoMayorRecaudacion);
        } else {
            System.out.println("No se encontraron productos.");
        }

        System.out.println("*************************************************************");

        // Obtiene y muestra la lista de clientes ordenada por facturación
        ClienteDAO c = fact.getClienteDAO();
        List<ClienteDTO> clientesFacturados = c.getClientesPorFacturacion();

        if (clientesFacturados.isEmpty()) {
            System.out.println("No se encontraron clientes.");
        } else {
            System.out.println("Clientes ordenados por mayor facturación: ");
            for (ClienteDTO cliente : clientesFacturados) {
                System.out.println(cliente);
            }
        }

        helper.closeConnection();
    }
}