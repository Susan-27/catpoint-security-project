package com.udacity.catpoint.application;

import com.udacity.catpoint.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;


public class CatpointGui extends JFrame {

    private SecurityService securityService;

    private DisplayPanel displayPanel;
    private ControlPanel controlPanel;
    private SensorPanel sensorPanel;
    private ImagePanel imagePanel;

    public CatpointGui(SecurityService securityService) {

        this.securityService = securityService;

        displayPanel = new DisplayPanel(securityService);
        controlPanel = new ControlPanel(securityService);
        sensorPanel = new SensorPanel(securityService);
        imagePanel = new ImagePanel(securityService);

        setLocation(100, 100);
        setSize(600, 850);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());

        mainPanel.add(displayPanel, "wrap");
        mainPanel.add(imagePanel, "wrap");
        mainPanel.add(controlPanel, "wrap");
        mainPanel.add(sensorPanel);

        getContentPane().add(mainPanel);
    }
}