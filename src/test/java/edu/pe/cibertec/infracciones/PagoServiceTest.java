package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.*;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 public  class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    @DisplayName("Debe aplicar 20porciento de descuento si se paga el mismo dia")
    void debeAplicarDescuento20Porciento() {

        Multa multa = new Multa();
        multa.setId(1L);
        multa.setMonto(500.0);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(30));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));


        var response = pagoService.procesarPago(1L);


        assertEquals(400.0, response.getMontoPagado());
        assertEquals(100.0, response.getDescuentoAplicado());
        assertEquals(0.0, response.getRecargo());
        assertEquals(EstadoMulta.PAGADA, multa.getEstado());

        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(multaRepository, times(1)).save(multa);
    }
    @Test
    @DisplayName("Debe aplicar recargo cuando multa está vencida")
    void debeAplicarRecargoEnMultaVencida() {


        Multa multa = new Multa();
        multa.setId(1L);
        multa.setMonto(500.0);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setFechaEmision(LocalDate.now().minusDays(12));
        multa.setFechaVencimiento(LocalDate.now().minusDays(2));

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));


        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);


        pagoService.procesarPago(1L);
        verify(pagoRepository, times(1)).save(pagoCaptor.capture());

        Pago pagoGuardado = pagoCaptor.getValue();

        assertEquals(75.0, pagoGuardado.getRecargo());
        assertEquals(0.0, pagoGuardado.getDescuentoAplicado());
        assertEquals(575.0, pagoGuardado.getMontoPagado());
    }


}