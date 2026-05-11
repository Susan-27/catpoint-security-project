package com.udacity.catpoint.service;

import com.udacity.catpoint.application.StatusListener;
import com.udacity.catpoint.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SecurityServiceTest {

    private SecurityService securityService;

    private SecurityRepository securityRepository;

    private ImageService imageService;

    @BeforeEach
    void setup() {

        securityRepository =
                mock(SecurityRepository.class);

        imageService =
                mock(ImageService.class);

        securityService =
                new SecurityService(
                        securityRepository,
                        imageService
                );
    }

    @Test
    void armedSensorActivated_setsPendingAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.PENDING_ALARM
                );
    }

    @Test
    void pendingAlarmSensorActivated_setsAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void disarm_setsNoAlarm() {

        securityService.setArmingStatus(
                ArmingStatus.DISARMED
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }

    @Test
    void armedHome_catDetected_setsAlarm() {

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        securityService.processImage(null);

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void armingSystem_resetsAllSensors() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        securityService.setArmingStatus(
                ArmingStatus.ARMED_HOME
        );

        verify(securityRepository)
                .updateSensor(sensor);
    }

    @Test
    void alarmState_sensorChanges_noEffect() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                false
        );

        verify(securityRepository, never())
                .setAlarmStatus(
                        AlarmStatus.PENDING_ALARM
                );
    }
    @Test
    void pendingAlarm_allSensorsInactive_setsNoAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(false);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        securityService.changeSensorActivationStatus(
                sensor,
                false
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }

    @Test
    void inactiveSensorDeactivated_noChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(false);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                false
        );

        verify(securityRepository, never())
                .setAlarmStatus(any());
    }

    @Test
    void activeSensorActivatedWhilePending_setsAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void noCatAndNoActiveSensors_setsNoAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(false);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(false);

        securityService.processImage(null);

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }

    @Test
    void armedHomeWithCat_setsAlarm() {

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        securityService.processImage(null);

        securityService.setArmingStatus(
                ArmingStatus.ARMED_HOME
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void addStatusListener_addsListener() {

        StatusListener listener =
                mock(StatusListener.class);

        securityService.addStatusListener(listener);
    }

    @Test
    void removeStatusListener_removesListener() {

        StatusListener listener =
                mock(StatusListener.class);

        securityService.addStatusListener(listener);

        securityService.removeStatusListener(listener);
    }

    @Test
    void removeSensor_removesSensor() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        securityService.removeSensor(sensor);

        verify(securityRepository)
                .removeSensor(sensor);
    }

    @Test
    void addSensor_addsSensor() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        securityService.addSensor(sensor);

        verify(securityRepository)
                .addSensor(sensor);
    }

    @Test
    void setAlarmStatus_setsAlarm() {

        securityService.setAlarmStatus(
                AlarmStatus.ALARM
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }
    @Test
    void getAlarmStatus_returnsStatus() {

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        securityService.getAlarmStatus();
    }
    @Test
    void getArmingStatus_returnsStatus() {

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        securityService.getArmingStatus();
    }
    @Test
    void getSensors_returnsSensors() {

        Set<Sensor> sensors =
                Set.of(new Sensor("1", SensorType.DOOR));

        when(securityRepository.getSensors())
                .thenReturn(sensors);

        assertEquals(
                sensors,
                securityService.getSensors()
        );
    }

    @Test
    void getAlarmStatus_returnsAlarmStatus() {

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        assertEquals(
                AlarmStatus.ALARM,
                securityService.getAlarmStatus()
        );
    }
    @Test
    void getArmingStatus_returnsArmingStatus() {

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        assertEquals(
                ArmingStatus.ARMED_HOME,
                securityService.getArmingStatus()
        );
    }

    @Test
    void setAlarmStatus_notifiesListeners() {

        StatusListener listener =
                mock(StatusListener.class);

        securityService.addStatusListener(listener);

        securityService.setAlarmStatus(
                AlarmStatus.ALARM
        );

        verify(listener)
                .notify(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void processImage_catDetectedAway_noAlarm() {

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_AWAY);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        securityService.processImage(null);

        verify(securityRepository, never())
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void processImage_noCat_activeSensors_noChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(false);

        securityService.processImage(null);

        verify(securityRepository, never())
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }

    @Test
    void alarmState_activeSensor_noChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository, never())
                .setAlarmStatus(any());
    }

    @Test
    void inactiveSensor_noAlarm_noChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(false);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                false
        );

        verify(securityRepository, never())
                .setAlarmStatus(any());
    }

    @Test
    void armedAway_resetsSensors() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        securityService.setArmingStatus(
                ArmingStatus.ARMED_AWAY
        );

        verify(securityRepository)
                .updateSensor(sensor);
    }

    @Test
    void pendingAlarm_secondSensorActivated_alarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void catDetected_disarmed_noAlarm() {

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.DISARMED);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        securityService.processImage(null);

        verify(securityRepository, never())
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void noCat_activeSensors_noAlarmChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(false);

        securityService.processImage(null);

        verify(securityRepository, never())
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }

    @Test
    void setAlarmStatus_notifiesAllListeners() {

        StatusListener listener =
                mock(StatusListener.class);

        securityService.addStatusListener(listener);

        securityService.setAlarmStatus(
                AlarmStatus.ALARM
        );

        verify(listener)
                .notify(
                        AlarmStatus.ALARM
                );
    }


    @Test
    void pendingAlarmInactiveSensors_noAlarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(false);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        securityService.changeSensorActivationStatus(
                sensor,
                false
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.NO_ALARM
                );
    }
    @Test
    void pendingAlarmActiveSensor_alarm() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.ALARM
                );
    }

    @Test
    void disarmedSensorActivation_noAlarmChange() {

        Sensor sensor =
                new Sensor("1", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.DISARMED);

        securityService.changeSensorActivationStatus(
                sensor,
                true
        );

        verify(securityRepository)
                .setAlarmStatus(
                        AlarmStatus.PENDING_ALARM
                );
    }









}