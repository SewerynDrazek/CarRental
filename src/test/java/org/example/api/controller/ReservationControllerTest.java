package org.example.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.error.model.ErrorResponse;
import org.example.api.request.ReservationRequest;
import org.example.domain.model.CarType;
import org.example.infrastructure.model.CarEntity;
import org.example.infrastructure.model.ReservationEntity;
import org.example.infrastructure.repository.jpa.JpaCarRepository;
import org.example.infrastructure.repository.jpa.JpaReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    private static final String URL = "/api/v1/reservation";
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = LocalDate.now().plusDays(5);

    private final CarEntity SEDAN_CAR = new CarEntity(CarType.SEDAN);
    private final CarEntity SEDAN_CAR_2 = new CarEntity(CarType.SEDAN);
    private final CarEntity VAN_CAR = new CarEntity(CarType.VAN);
    private final CarEntity SUV_CAR = new CarEntity(CarType.SUV);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaCarRepository carRepository;

    @Autowired
    private JpaReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        carRepository.saveAllAndFlush(List.of(SEDAN_CAR, VAN_CAR, SUV_CAR, SEDAN_CAR_2));
    }

    @Test
    void shouldCreateReservations() throws Exception {
        //given:
        var request = new ReservationRequest(CarType.SEDAN,
                START_DATE,
                END_DATE);
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        var carId = result.getResponse().getContentAsString();
        assertNotNull(carId);

        List<ReservationEntity> reservations = reservationRepository.findAll();
        ReservationEntity reservation = reservations.get(0);
        assertEquals(1, reservations.size());
        assertEquals(CarType.SEDAN, reservation.getCar().getCarType());
        assertEquals(request.startDate(), reservation.getStartDate());
        assertEquals(request.endDate(), reservation.getEndDate());
    }

    @Test
    void shouldCreateReservationsWhenAtLeastOneCarIsAvailable() throws Exception {
        //given:
        reservationRepository.save(new ReservationEntity(SEDAN_CAR, START_DATE, END_DATE));
        var request = new ReservationRequest(CarType.SEDAN,
                START_DATE,
                END_DATE);
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        var carId = result.getResponse().getContentAsString();
        assertNotNull(carId);

        List<ReservationEntity> reservations = reservationRepository.findAll();
        ReservationEntity reservation = reservations.get(1);
        assertEquals(2, reservations.size());
        assertEquals(CarType.SEDAN, reservation.getCar().getCarType());
        assertEquals(request.startDate(), reservation.getStartDate());
        assertEquals(request.endDate(), reservation.getEndDate());
    }

    @Test
    void shouldCreateReservationsForAnotherDates() throws Exception {
        //given:
        reservationRepository.save(new ReservationEntity(SEDAN_CAR, START_DATE, END_DATE));
        reservationRepository.save(new ReservationEntity(SEDAN_CAR_2, START_DATE.plusDays(6), END_DATE.plusDays(8)));
        var request = new ReservationRequest(CarType.SEDAN,
                START_DATE.plusDays(10),
                END_DATE.plusDays(12));
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        var carId = result.getResponse().getContentAsString();
        assertNotNull(carId);

        List<ReservationEntity> reservations = reservationRepository.findAll();
        ReservationEntity reservation = reservations.get(2);
        assertEquals(3, reservations.size());
        assertEquals(CarType.SEDAN, reservation.getCar().getCarType());
        assertEquals(request.startDate(), reservation.getStartDate());
        assertEquals(request.endDate(), reservation.getEndDate());
    }

    @Test
    void shouldReturnNotFoundErrorWhenNoCarsInDatabase() throws Exception {
        //given:
        carRepository.deleteAll();
        var request = new ReservationRequest(CarType.SEDAN,
                START_DATE,
                END_DATE);
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andReturn();

        //then:
        var response = result.getResponse().getContentAsString();
        assertNotNull(response);
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
        assertEquals("Car not found", errorResponse.message());
    }

    @Test
    void shouldReturnNotFoundErrorWhenNoRequiredCarIsRequested() throws Exception {
        //given:
        reservationRepository.save(new ReservationEntity(SEDAN_CAR, START_DATE, END_DATE));
        reservationRepository.save(new ReservationEntity(SEDAN_CAR_2, START_DATE, END_DATE));
        var request = new ReservationRequest(CarType.SEDAN,
                START_DATE,
                END_DATE);
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andReturn();

        //then:
        var response = result.getResponse().getContentAsString();
        assertNotNull(response);
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
        assertEquals("Car not found", errorResponse.message());
    }

    @ParameterizedTest
    @MethodSource("invalidStartDates")
    void shouldReturnBadRequestWhenDateIsOutOfRange(LocalDate startDay) throws Exception {
        //given:
        var request = new ReservationRequest(CarType.SEDAN,
                startDay,
                END_DATE);
        var jsonRequest = objectMapper.writeValueAsString(request);

        //when:
        var result = mockMvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        var response = result.getResponse().getContentAsString();
        assertNotNull(response);
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals("Date is out of range", errorResponse.message());
    }

    private static Stream<Arguments> invalidStartDates() {
        return Stream.of(
                Arguments.of(START_DATE.minusDays(1)),
                Arguments.of(START_DATE.plusDays(6))
        );
    }

}