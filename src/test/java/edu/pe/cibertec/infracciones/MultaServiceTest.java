package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Test
    @DisplayName("Debe cambiar multa pendiente a vencida si fechaVencimiento es 2026-01-01")
    void debeActualizarMultaAVencidaSegunEscenario() {


        Multa multa = new Multa();
        multa.setId(1L);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setFechaVencimiento(LocalDate.of(2026, 1, 1));
        when(multaRepository.findByEstado(EstadoMulta.PENDIENTE))
                .thenReturn(List.of(multa));

        multaService.actualizarEstados();
        verify(multaRepository, times(1)).save(multa);
    }
}