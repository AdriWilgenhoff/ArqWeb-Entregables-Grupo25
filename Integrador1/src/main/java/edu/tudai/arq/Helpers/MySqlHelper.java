package edu.tudai.arq.Helpers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import edu.tudai.arq.Entidades.Cliente;
import edu.tudai.arq.Entidades.Factura;
import edu.tudai.arq.Entidades.FacturaProducto;
import edu.tudai.arq.Entidades.Producto;
import edu.tudai.arq.Factory.MySQLDAOFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class MySqlHelper {

    MySQLDAOFactory fact = MySQLDAOFactory.getInstance();
    Connection conn = MySQLDAOFactory.createConnection();

    public void createTables() throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();

            String tableCliente =
                    "CREATE TABLE IF NOT EXISTS Cliente (" +
                            "  idCliente INT NOT NULL, " +
                            "  Nombre VARCHAR(500), " +
                            "  Email VARCHAR(150), " +
                            "  PRIMARY KEY (idCliente)" +
                            ")";

            String tableFactura =
                    "CREATE TABLE IF NOT EXISTS Factura (" +
                            "  idFactura INT NOT NULL, " +
                            "  idCliente INT NOT NULL, " +
                            "  PRIMARY KEY (idFactura), " +
                            "  FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente)" +
                            ")";

            String tableProducto =
                    "CREATE TABLE IF NOT EXISTS Producto (" +
                            "  idProducto INT NOT NULL, " +
                            "  nombre VARCHAR(45), " +
                            "  valor FLOAT NOT NULL, " +
                            "  PRIMARY KEY (idProducto)" +
                            ")";

            String tableFactura_Producto =
                    "CREATE TABLE IF NOT EXISTS Factura_Producto (" +
                            "  idFactura INT NOT NULL, " +
                            "  idProducto INT NOT NULL, " +
                            "  cantidad INT, " +
                            "  PRIMARY KEY (idFactura, idProducto)" +
                            ")";

            st.executeUpdate(tableCliente);
            st.executeUpdate(tableFactura);
            st.executeUpdate(tableProducto);
            st.executeUpdate(tableFactura_Producto);

        } finally {
            if (st != null) {
                try { st.close(); } catch (SQLException ignore) {}
            }
        }
    }


    private Iterable<CSVRecord> getData(String fileName) throws IOException {
        String resourcePath = "CSV/" + fileName;

        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (in == null) {
            throw new FileNotFoundException("No se encontró en resources: " + resourcePath);
        }

        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .parse(reader)) {

            return parser.getRecords();
        }
    }

    public void insertData() throws Exception {
        this.InsertClientData(this.getData("clientes.csv"));
        this.InsertProductData(this.getData("productos.csv"));
        this.InsertProduct_TicketData(this.getData("facturas-productos.csv"));
        this.InsertTicketData(this.getData("facturas.csv"));
    }

    public void InsertClientData(Iterable<CSVRecord> data) throws Exception {
        for (CSVRecord row : data) {
            int id = Integer.parseInt(row.get(0));
            String nombre = row.get(1);
            String email = row.get(2);

            Cliente cliente = new Cliente(id, nombre, email);
            fact.getClienteDAO().insertCliente(cliente);
        }
    }

    public void InsertProductData(Iterable<CSVRecord> data) throws Exception {
        for (CSVRecord row : data) {
            int id = Integer.parseInt(row.get(0));
            String nombre = row.get(1);
            float valor = Float.parseFloat(row.get(2));

            Producto producto = new Producto(id, nombre, valor);
            fact.getProductoDAO().insertProducto(producto);
        }
    }

    public void InsertTicketData(Iterable<CSVRecord> data) throws Exception {
        for (CSVRecord row : data) {
            int idFactura = Integer.parseInt(row.get(0));
            int idCliente = Integer.parseInt(row.get(1));

            Factura factura = new Factura(idFactura, idCliente);
            fact.getFacturaDAO().insertFactura(factura);
        }
    }

    public void InsertProduct_TicketData(Iterable<CSVRecord> data) throws Exception {
        for (CSVRecord row : data) {
            int idFactura = Integer.parseInt(row.get(0));
            int idProducto = Integer.parseInt(row.get(1));
            int cantidad = Integer.parseInt(row.get(2));

            FacturaProducto facturaProducto = new FacturaProducto(idFactura, idProducto, cantidad);
            fact.getFactura_ProductoDAO().insertFactura_Producto(facturaProducto);
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    public void deleteAllData() {
        Statement st = null;
        try {
            st = conn.createStatement();

            st.executeUpdate("DELETE FROM Factura_Producto");
            st.executeUpdate("DELETE FROM Factura");
            st.executeUpdate("DELETE FROM Producto");
            st.executeUpdate("DELETE FROM Cliente");

        } catch (SQLException e) {
            throw new RuntimeException("Error al borrar datos", e);
        } finally {
            if (st != null) {
                try { st.close(); } catch (SQLException ignore) {}
            }
        }
    }

}
