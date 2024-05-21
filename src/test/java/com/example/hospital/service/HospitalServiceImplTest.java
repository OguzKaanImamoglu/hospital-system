package com.example.hospital.service;

import com.example.hospital.*;
import com.example.hospital.entity.Hospital;
import com.example.hospital.entity.Patient;
import com.example.hospital.repository.HospitalRepository;
import com.example.hospital.repository.PatientRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HospitalServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("General Hospital");

        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        @SuppressWarnings("unchecked")
        StreamObserver<HospitalResponse> responseObserver = mock(StreamObserver.class);
        ArgumentCaptor<HospitalResponse> captor = ArgumentCaptor.forClass(HospitalResponse.class);

        CreateHospitalRequest request = CreateHospitalRequest.newBuilder()
                .setName("General Hospital")
                .build();

        hospitalService.createHospital(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        HospitalResponse response = captor.getValue();
        assertEquals(1L, response.getId());
        assertEquals("General Hospital", response.getName());
    }

    @Test
    public void testCreatePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        @SuppressWarnings("unchecked")
        StreamObserver<PatientResponse> responseObserver = mock(StreamObserver.class);
        ArgumentCaptor<PatientResponse> captor = ArgumentCaptor.forClass(PatientResponse.class);

        CreatePatientRequest request = CreatePatientRequest.newBuilder()
                .setName("John Doe")
                .build();

        hospitalService.createPatient(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        PatientResponse response = captor.getValue();
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
    }

    @Test
    public void testRegisterPatient() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("General Hospital");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        @SuppressWarnings("unchecked")
        StreamObserver<com.google.protobuf.Empty> responseObserver = mock(StreamObserver.class);

        RegisterPatientRequest request = RegisterPatientRequest.newBuilder()
                .setHospitalId(1L)
                .setPatientId(1L)
                .build();

        hospitalService.registerPatient(request, responseObserver);

        verify(responseObserver).onNext(com.google.protobuf.Empty.getDefaultInstance());
        verify(responseObserver).onCompleted();
        assertTrue(hospital.getPatients().contains(patient));
    }

    @Test
    public void testListAllPatients() {
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("John Doe");

        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Jane Doe");

        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        @SuppressWarnings("unchecked")
        StreamObserver<ListPatientsResponse> responseObserver = mock(StreamObserver.class);
        ArgumentCaptor<ListPatientsResponse> captor = ArgumentCaptor.forClass(ListPatientsResponse.class);

        hospitalService.listAllPatients(com.google.protobuf.Empty.getDefaultInstance(), responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ListPatientsResponse response = captor.getValue();
        assertEquals(2, response.getPatientsCount());
        assertEquals("John Doe", response.getPatients(0).getName());
        assertEquals("Jane Doe", response.getPatients(1).getName());
    }

    @Test
    public void testListAllHospitals() {
        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setName("General Hospital");

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setName("City Hospital");

        when(hospitalRepository.findAll()).thenReturn(List.of(hospital1, hospital2));

        @SuppressWarnings("unchecked")
        StreamObserver<ListHospitalsResponse> responseObserver = mock(StreamObserver.class);
        ArgumentCaptor<ListHospitalsResponse> captor = ArgumentCaptor.forClass(ListHospitalsResponse.class);

        hospitalService.listAllHospitals(com.google.protobuf.Empty.getDefaultInstance(), responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ListHospitalsResponse response = captor.getValue();
        assertEquals(2, response.getHospitalsCount());
        assertEquals("General Hospital", response.getHospitals(0).getName());
        assertEquals("City Hospital", response.getHospitals(1).getName());
    }
}
