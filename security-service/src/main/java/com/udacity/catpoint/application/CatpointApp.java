package com.udacity.catpoint.application;

import com.udacity.catpoint.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.service.FakeImageService;
import com.udacity.catpoint.service.SecurityService;

public class CatpointApp {

    public static void main(String[] args) {

        SecurityService securityService = new SecurityService(
                new PretendDatabaseSecurityRepositoryImpl(),
                new FakeImageService()
        );

        CatpointGui gui = new CatpointGui(securityService);

        gui.setVisible(true);
    }
}