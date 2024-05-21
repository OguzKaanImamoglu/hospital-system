package com.example.hospital.service;

import com.example.hospital.*;
import com.example.hospital.entity.Hospital;
import com.example.hospital.entity.Patient;
import com.example.hospital.repository.HospitalRepository;
import com.example.hospital.repository.PatientRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
public class HospitalServiceImpl extends HospitalServiceGrpc.HospitalServiceImplBase {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public void createHospital(CreateHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        Hospital savedHospital = hospitalRepository.save(hospital);

        HospitalResponse response = HospitalResponse.newBuilder()
                .setId(savedHospital.getId())
                .setName(savedHospital.getName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void modifyHospital(ModifyHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getId());
        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            hospital.setName(request.getName());
            Hospital updatedHospital = hospitalRepository.save(hospital);

            HospitalResponse response = HospitalResponse.newBuilder()
                    .setId(updatedHospital.getId())
                    .setName(updatedHospital.getName())
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Hospital not found"));
        }
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void deleteHospital(DeleteHospitalRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getId());

        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            hospital.getPatients().forEach(patient -> patient.getHospitals().remove(hospital));
            hospitalRepository.delete(hospital);

            responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Hospital not found"));
        }
    }

    @Override
    public void createPatient(CreatePatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        Patient patient = new Patient();
        patient.setName(request.getName());
        Patient savedPatient = patientRepository.save(patient);

        PatientResponse response = PatientResponse.newBuilder()
                .setId(savedPatient.getId())
                .setName(savedPatient.getName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void modifyPatient(ModifyPatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        Optional<Patient> patientOpt = patientRepository.findById(request.getId());
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setName(request.getName());
            Patient updatedPatient = patientRepository.save(patient);

            PatientResponse response = PatientResponse.newBuilder()
                    .setId(updatedPatient.getId())
                    .setName(updatedPatient.getName())
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Patient not found"));
        }
        responseObserver.onCompleted();
    }


    @Transactional
    @Override
    public void deletePatient(DeletePatientRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        Optional<Patient> patientOpt = patientRepository.findById(request.getId());

        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.getHospitals().forEach(hospital -> hospital.getPatients().remove(patient));
            patientRepository.delete(patient);

            responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Patient not found"));
        }
    }

    @Transactional
    @Override
    public void registerPatient(RegisterPatientRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getHospitalId());
        Optional<Patient> patientOpt = patientRepository.findById(request.getPatientId());

        if (hospitalOpt.isPresent() && patientOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            Patient patient = patientOpt.get();
            hospital.getPatients().add(patient);
            hospitalRepository.save(hospital);
        } else {
            responseObserver.onError(new RuntimeException("Hospital or Patient not found"));
            return;
        }

        responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void listPatientsOfHospital(ListPatientsOfHospitalRequest request, StreamObserver<ListPatientsResponse> responseObserver) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getHospitalId());

        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            ListPatientsResponse response = ListPatientsResponse.newBuilder()
                    .addAllPatients(hospital.getPatients().stream()
                            .map(patient -> PatientResponse.newBuilder()
                                    .setId(patient.getId())
                                    .setName(patient.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Hospital not found"));
        }
    }

    @Transactional
    @Override
    public void listHospitalsOfPatient(ListHospitalsOfPatientRequest request, StreamObserver<ListHospitalsResponse> responseObserver) {
        Optional<Patient> patientOpt = patientRepository.findById(request.getPatientId());

        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            ListHospitalsResponse response = ListHospitalsResponse.newBuilder()
                    .addAllHospitals(patient.getHospitals().stream()
                            .map(hospital -> HospitalResponse.newBuilder()
                                    .setId(hospital.getId())
                                    .setName(hospital.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Patient not found"));
        }
    }


    @Transactional
    @Override
    public void listAllPatients(com.google.protobuf.Empty request, StreamObserver<ListPatientsResponse> responseObserver) {
        ListPatientsResponse response = ListPatientsResponse.newBuilder()
                .addAllPatients(patientRepository.findAll().stream()
                        .map(patient -> PatientResponse.newBuilder()
                                .setId(patient.getId())
                                .setName(patient.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void listAllHospitals(com.google.protobuf.Empty request, StreamObserver<ListHospitalsResponse> responseObserver) {
        ListHospitalsResponse response = ListHospitalsResponse.newBuilder()
                .addAllHospitals(hospitalRepository.findAll().stream()
                        .map(hospital -> HospitalResponse.newBuilder()
                                .setId(hospital.getId())
                                .setName(hospital.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}