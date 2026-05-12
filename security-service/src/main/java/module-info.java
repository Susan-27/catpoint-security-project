module security.service {

    requires java.desktop;
    requires java.prefs;

    requires com.google.gson;
    requires com.google.common;

    requires org.slf4j;

    requires image.service;

    opens com.udacity.catpoint.data to com.google.gson;

    exports com.udacity.catpoint.application;
    exports com.udacity.catpoint.data;
    exports com.udacity.catpoint.service;
}