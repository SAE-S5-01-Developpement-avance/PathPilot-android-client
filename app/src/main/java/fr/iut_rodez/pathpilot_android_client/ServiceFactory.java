package fr.iut_rodez.pathpilot_android_client;

import fr.iut_rodez.pathpilot_android_client.home.routes.IRouteService;
import fr.iut_rodez.pathpilot_android_client.home.routes.RouteService;

public class ServiceFactory {

    static ServiceFactory instance;
    IRouteService routeService;
    // TODO add all services

    private ServiceFactory() {
        this.routeService = new RouteService();
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public IRouteService routeService() {
        return routeService;
    }

    public static IRouteService getRouteService() {
        return getInstance().routeService();
    }
}
