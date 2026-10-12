package pe.edu.isil.inventario.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DevolucionStockTest {

    @Test
    void devolucionValidaIncrementaElStockExactamente() {
        // Preparación
        ProductoInventario producto = new ProductoInventario(
                "LAP-001",
                "Laptop",
                new BigDecimal("2500.00"),
                8
        );

        // Ejecución
        producto.devolverStock(2);

        // Verificación
        assertEquals(10, producto.getStock());
    }

    @Test
    void devolucionInvalidaSeRechazaYNoModificaElStock() {
        // Preparación
        ProductoInventario producto = new ProductoInventario(
                "LAP-001",
                "Laptop",
                new BigDecimal("2500.00"),
                8
        );

        // Cantidad igual a cero
        assertThrows(
                IllegalArgumentException.class,
                () -> producto.devolverStock(0)
        );

        assertEquals(8, producto.getStock());

        // Cantidad negativa
        assertThrows(
                IllegalArgumentException.class,
                () -> producto.devolverStock(-2)
        );

        assertEquals(8, producto.getStock());
    }
}