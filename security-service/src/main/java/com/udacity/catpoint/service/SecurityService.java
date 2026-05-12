package com.udacity.catpoint.service;

import com.udacity.catpoint.application.StatusListener;
import com.udacity.catpoint.data.AlarmStatus;
import com.udacity.catpoint.data.ArmingStatus;
import com.udacity.catpoint.data.SecurityRepository;
import com.udacity.catpoint.data.Sensor;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;


public class SecurityService {

    private final ImageService imageService;
    private final SecurityRepository securityRepository;

    private final Set<StatusListener> statusListeners = new HashSet<>();

    private boolean catDetected = false;

    public SecurityService(SecurityRepository securityRepository,
                           ImageService imageService) {

        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }


    public void setArmingStatus(ArmingStatus armingStatus) {

        if (armingStatus == ArmingStatus.DISARMED) {

            setAlarmStatus(AlarmStatus.NO_ALARM);

        } else {

            Set<Sensor> sensors =
                    new HashSet<>(securityRepository.getSensors());

            for (Sensor sensor : sensors) {
                sensor.setActive(false);
                securityRepository.updateSensor(sensor);
            }
        }

        securityRepository.setArmingStatus(armingStatus);

        if (armingStatus == ArmingStatus.ARMED_HOME && catDetected) {
            setAlarmStatus(AlarmStatus.ALARM);
        }
    }


    private void handleSensorActivated() {

        if (securityRepository.getArmingStatus() ==
                ArmingStatus.DISARMED) {
            return;
        }

        switch (securityRepository.getAlarmStatus()) {

            case NO_ALARM ->
                    setAlarmStatus(AlarmStatus.PENDING_ALARM);

            case PENDING_ALARM ->
                    setAlarmStatus(AlarmStatus.ALARM);

            default -> {
            }
        }
    }


    private void handleSensorDeactivated() {

        if (securityRepository.getAlarmStatus() ==
                AlarmStatus.PENDING_ALARM) {

            boolean anyActiveSensors =
                    securityRepository.getSensors()
                            .stream()
                            .anyMatch(Sensor::getActive);

            if (!anyActiveSensors) {
                setAlarmStatus(AlarmStatus.NO_ALARM);
            }
        }
    }


    public void changeSensorActivationStatus(
            Sensor sensor,
            Boolean active) {

        if (sensor.getActive() == active) {

            if (active &&
                    securityRepository.getAlarmStatus() ==
                            AlarmStatus.PENDING_ALARM) {

                setAlarmStatus(AlarmStatus.ALARM);
            }

            return;
        }

        sensor.setActive(active);
        securityRepository.updateSensor(sensor);

        if (securityRepository.getAlarmStatus() ==
                AlarmStatus.ALARM) {
            return;
        }

        if (active) {
            handleSensorActivated();
        } else {
            handleSensorDeactivated();
        }
    }


    private void catDetected(Boolean cat) {

        catDetected = cat;

        if (cat &&
                getArmingStatus() ==
                        ArmingStatus.ARMED_HOME) {

            setAlarmStatus(AlarmStatus.ALARM);

        } else if (!cat) {

            boolean anyActiveSensors =
                    securityRepository.getSensors()
                            .stream()
                            .anyMatch(Sensor::getActive);

            if (!anyActiveSensors) {
                setAlarmStatus(AlarmStatus.NO_ALARM);
            }
        }

        statusListeners.forEach(
                listener -> listener.catDetected(cat)
        );
    }


    public void processImage(BufferedImage currentCameraImage) {

        catDetected(
                imageService.imageContainsCat(
                        currentCameraImage,
                        50.0f
                )
        );
    }


    public void setAlarmStatus(AlarmStatus status) {

        securityRepository.setAlarmStatus(status);

        statusListeners.forEach(
                listener -> listener.notify(status)
        );
    }

    public void addStatusListener(StatusListener statusListener) {
        statusListeners.add(statusListener);
    }

    public void removeStatusListener(StatusListener statusListener) {
        statusListeners.remove(statusListener);
    }

    public AlarmStatus getAlarmStatus() {
        return securityRepository.getAlarmStatus();
    }

    public Set<Sensor> getSensors() {
        return securityRepository.getSensors();
    }

    public void addSensor(Sensor sensor) {
        securityRepository.addSensor(sensor);
    }

    public void removeSensor(Sensor sensor) {
        securityRepository.removeSensor(sensor);
    }

    public ArmingStatus getArmingStatus() {
        return securityRepository.getArmingStatus();
    }
}