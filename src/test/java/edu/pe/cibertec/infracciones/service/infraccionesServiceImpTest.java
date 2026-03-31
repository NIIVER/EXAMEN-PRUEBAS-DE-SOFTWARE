package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InfraccionesServiceImp - Unit Test")
public class infraccionesServiceImpTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    private Multa multa1;
    private Multa multa2;

    private Infractor infractor;
    private Vehiculo vehiculo;


    @BeforeEach
    void setUp() {

        multa1=new Multa();
        multa1.setMonto(200.0);
        multa1.setEstado(EstadoMulta.PENDIENTE);

        multa2=new Multa();
        multa2.setMonto(300.0);
        multa2.setEstado(EstadoMulta.VENCIDA);

        infractor =new Infractor();
        infractor.setId(1L);

        vehiculo=new Vehiculo();
        vehiculo.setId(1L);

        infractor.setVehiculos(new ArrayList<>());
        infractor.getVehiculos().add(vehiculo);
    }
    @Test
    @DisplayName("Debe calcular la deuda total del infractor")
    void calcularDeudaTest(){
        when(multaRepository.findByInfractor_Id(1L))
                .thenReturn(List.of(multa1,multa2));
        Double deuda=infractorService.calcularDeuda(1L);
        assertEquals(545.0,deuda);
    }
    @Test
    @DisplayName("Debe desasignar el vehículo si no tiene multas pendientes")
    void desasignarVehiculoTest(){
        when(infractorRepository.findById(1L))
                .thenReturn(Optional.of(infractor));
        when(vehiculoRepository.findById(1L))
                .thenReturn(Optional.of(vehiculo));
        when(multaRepository.findByVehiculo_IdAndEstado(1L, EstadoMulta.PENDIENTE))
                .thenReturn(List.of());
        infractorService.desasignarVehiculo(1L,1L);
        assertFalse(infractor.getVehiculos().contains(vehiculo));
        verify(infractorRepository).save(infractor);
    }


}