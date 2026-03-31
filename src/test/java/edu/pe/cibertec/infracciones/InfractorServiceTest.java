package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.*;
import edu.pe.cibertec.infracciones.repository.*;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test de InfractorService - verificarBloqueo")
 public class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    @DisplayName("No debe bloquear infractor si tiene menos de 3 multas vencidas")
    void noDebeBloquearInfractorSiTieneMenosDe3MultasVencidas() {


        Infractor infractor = new Infractor();
        infractor.setId(1L);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));

        List<Multa> multasVencidas = List.of(new Multa(), new Multa());
        when(multaRepository.findByInfractor_IdAndEstado(1L, EstadoMulta.VENCIDA))
                .thenReturn(multasVencidas);


        infractorService.verificarBloqueo(1L);


        assertFalse(infractor.isBloqueado());
        verify(infractorRepository, never()).save(any());
    }
}