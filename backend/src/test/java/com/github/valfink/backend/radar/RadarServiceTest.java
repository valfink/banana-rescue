package com.github.valfink.backend.radar;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.geolocation.Location;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RadarServiceTest {
    RadarRepository radarRepository;
    MongoUserService mongoUserService;
    FoodItemService foodItemService;
    RadarService radarService;
    Principal principal;
    MongoUserDTOResponse mongoUserDTOResponse1, mongoUserDTOResponse2;
    Radar radar1;
    RadarDTORequest radarDTORequest1;
    FoodItemDTOResponse foodItem1CloseBy, foodItem2NotSoClose;

    @BeforeEach
    void setUp() {
        radarRepository = mock(RadarRepository.class);
        mongoUserService = mock(MongoUserService.class);
        foodItemService = mock(FoodItemService.class);
        radarService = new RadarService(radarRepository, mongoUserService, foodItemService);

        principal = mock(Principal.class);
        mongoUserDTOResponse1 = new MongoUserDTOResponse("u1", "user");
        mongoUserDTOResponse2 = new MongoUserDTOResponse("u2", "user2");
        radar1 = new Radar(mongoUserDTOResponse1.id(), new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599")), 200);
        radarDTORequest1 = new RadarDTORequest(radar1.center(), radar1.radiusInMeters());
        foodItem1CloseBy = new FoodItemDTOResponse(
                "f1",
                mongoUserDTOResponse2,
                "Food Item Close By",
                null,
                new Location("Very close", new Coordinate(new BigDecimal("52.51837"), new BigDecimal("13.38855"))),
                Instant.parse("2023-04-12T11:30:00Z"),
                Instant.parse("2023-04-14T12:00:00Z"),
                "This is my food item close by."
        );
        foodItem2NotSoClose = new FoodItemDTOResponse(
                "f2",
                mongoUserDTOResponse2,
                "Food Item Not So Close",
                null,
                new Location("Not So close", new Coordinate(new BigDecimal("52.51971"), new BigDecimal("13.38824"))),
                Instant.parse("2023-04-12T11:30:00Z"),
                Instant.parse("2023-04-14T12:00:00Z"),
                "This is my food item not so close, but not very far away."
        );
    }

    @Test
    void addRadar_whenUserHasNoRadarYetAndDTOIsValid_thenReturnNewRadarDTO() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsById(mongoUserDTOResponse1.id())).thenReturn(false);
        when(radarRepository.save(radar1)).thenReturn(radar1);
        when(foodItemService.getAllFoodItems()).thenReturn(List.of());

        // WHEN
        RadarDTOResponse expected = radar1.convertToDTOResponse(List.of());
        RadarDTOResponse actual = radarService.addRadar(radarDTORequest1, principal);

        // THEN
        verify(radarRepository).save(radar1);
        assertEquals(expected, actual);
    }

    @Test
    void addRadar_whenUserAlreadyHasRadius_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsById(mongoUserDTOResponse1.id())).thenReturn(true);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(radarDTORequest1, principal));
    }

    @Test
    void addRadar_whenCenterIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsById(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(null, 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }

    @Test
    void addRadar_whenCenterLatitudeIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsById(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(new Coordinate(null, new BigDecimal("50")), 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }

    @Test
    void addRadar_whenCenterLongitudeIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsById(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(new Coordinate(new BigDecimal("50"), null), 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }

    @Test
    void getRadar_whenUserHasRadar_thenReturnRadarDTO() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.findById(mongoUserDTOResponse1.id())).thenReturn(Optional.of(radar1));
        when(foodItemService.getAllFoodItems()).thenReturn(List.of());

        // WHEN
        RadarDTOResponse expected = radar1.convertToDTOResponse(List.of());
        RadarDTOResponse actual = radarService.getRadar(principal);

        // THEN
        verify(radarRepository).findById(mongoUserDTOResponse1.id());
        verify(foodItemService).getAllFoodItems();
        assertEquals(expected, actual);
    }

    @Test
    void getRadar_whenUserHasNoRadar_thenReturnNull() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.findById(mongoUserDTOResponse1.id())).thenReturn(Optional.empty());

        // WHEN
        RadarDTOResponse actual = radarService.getRadar(principal);

        // THEN
        verify(radarRepository).findById(mongoUserDTOResponse1.id());
        assertNull(actual);
    }

    @Test
    void getRadar_whenUserHasRadarAndFoodItemIsCloseBy_thenReturnRadarDTOWithItem() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.findById(mongoUserDTOResponse1.id())).thenReturn(Optional.of(radar1));
        when(foodItemService.getAllFoodItems()).thenReturn(List.of(foodItem1CloseBy, foodItem2NotSoClose));

        // WHEN
        RadarDTOResponse expected = radar1.convertToDTOResponse(List.of(foodItem1CloseBy));
        // TODO: FIX THIS!
        RadarDTOResponse actual = radarService.getRadar(principal);

        // THEN
        verify(radarRepository).findById(mongoUserDTOResponse1.id());
        verify(foodItemService).getAllFoodItems();
        assertEquals(expected, actual);
    }
}