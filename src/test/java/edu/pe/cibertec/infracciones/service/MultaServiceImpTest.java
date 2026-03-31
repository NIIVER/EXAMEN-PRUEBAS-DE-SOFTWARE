package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceImpTest {
    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    private Vehiculo vehiculo;

    @BeforeEach
    void setup() {
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC-123");
    }

    @Test
    @DisplayName("Debe transferir la multa a otro infractor correctamente")
    void transferirMulta() {

        Infractor infractorA = new Infractor();
        infractorA.setId(1L);
        infractorA.setVehiculos(new ArrayList<>());
        infractorA.getVehiculos().add(vehiculo);

        Infractor infractorB = new Infractor();
        infractorB.setId(2L);
        infractorB.setBloqueado(false);
        infractorB.setVehiculos(new ArrayList<>());
        infractorB.getVehiculos().add(vehiculo);

        Multa multa = new Multa();
        multa.setId(1L);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setInfractor(infractorA);
        multa.setVehiculo(vehiculo);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));

        multaService.transferirMultas(1L, 2L);

        assertEquals(infractorB, multa.getInfractor());
        verify(multaRepository).save(multa);
    }

    @Test
    @DisplayName("No debe transferir multa si infractor destino está bloqueado")
    void transferirMulta_InfractorBloqueado() {

        Infractor infractorA = new Infractor();
        infractorA.setId(1L);
        infractorA.setVehiculos(new ArrayList<>());
        infractorA.getVehiculos().add(vehiculo);


        Infractor infractorB = new Infractor();
        infractorB.setId(2L);
        infractorB.setBloqueado(true);
        infractorB.setVehiculos(new ArrayList<>());
        infractorB.getVehiculos().add(vehiculo);

        Multa multa = new Multa();
        multa.setId(1L);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setInfractor(infractorA);
        multa.setVehiculo(vehiculo);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));

        assertThrows(InfractorBloqueadoException.class, () ->
                multaService.transferirMultas(1L, 2L)
        );

        ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

        verify(multaRepository, never()).save(multaCaptor.capture());
    }
}
