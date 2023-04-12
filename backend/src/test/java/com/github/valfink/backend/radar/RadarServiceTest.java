package com.github.valfink.backend.radar;

import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RadarServiceTest {
    RadarRepository radarRepository;
    MongoUserService mongoUserService;
    RadarService radarService;
    Principal principal;
    MongoUserDTOResponse mongoUserDTOResponse1;
    Radar radar1;
    RadarDTORequest radarDTORequest1;

    @BeforeEach
    void setUp() {
        radarRepository = mock(RadarRepository.class);
        mongoUserService = mock(MongoUserService.class);
        radarService = new RadarService(radarRepository, mongoUserService);

        principal = mock(Principal.class);
        mongoUserDTOResponse1 = new MongoUserDTOResponse("u1", "user");
        radar1 = new Radar(mongoUserDTOResponse1.id(), new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599")), 200);
        radarDTORequest1 = new RadarDTORequest(radar1.center(), radar1.radiusInMeters());
    }

    @Test
    void addRadar_whenUserHasNoRadarYetAndDTOIsValid_thenReturnNewRadarDTO() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsByUserId(mongoUserDTOResponse1.id())).thenReturn(false);
        when(radarRepository.save(radar1)).thenReturn(radar1);

        // WHEN
        RadarDTORequest expected = radarDTORequest1;
        RadarDTORequest actual = radarService.addRadar(radarDTORequest1, principal);

        // THEN
        verify(radarRepository).save(radar1);
        assertEquals(expected, actual);
    }

    @Test
    void addRadar_whenUserAlreadyHasRadius_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsByUserId(mongoUserDTOResponse1.id())).thenReturn(true);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(radarDTORequest1, principal));
    }

    @Test
    void addRadar_whenCenterIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsByUserId(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(null, 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }

    @Test
    void addRadar_whenCenterLatitudeIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsByUserId(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(new Coordinate(null, new BigDecimal("50")), 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }

    @Test
    void addRadar_whenCenterLongitudeIsNull_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(radarRepository.existsByUserId(mongoUserDTOResponse1.id())).thenReturn(false);
        RadarDTORequest badRadarDTORequest = new RadarDTORequest(new Coordinate(new BigDecimal("50"), null), 100);

        // WHEN & THEN
        assertThrows(RadarExceptionBadInputData.class, () -> radarService.addRadar(badRadarDTORequest, principal));
    }
}