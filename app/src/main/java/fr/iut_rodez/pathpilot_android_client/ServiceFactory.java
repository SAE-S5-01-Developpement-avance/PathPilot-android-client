package fr.iut_rodez.pathpilot_android_client;

import fr.iut_rodez.pathpilot_android_client.home.routes.IRouteService;
import fr.iut_rodez.pathpilot_android_client.home.routes.RouteService;
import fr.iut_rodez.pathpilot_android_client.login.ILoginService;
import fr.iut_rodez.pathpilot_android_client.login.LoginService;

/**
 * Factory to get services
 */
public class ServiceFactory {

    static ServiceFactory instance;
    IRouteService routeService;
    ILoginService loginService;
    // TODO add all services

    private ServiceFactory() {
        this.routeService = new RouteService();
        this.loginService = new LoginService();
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

    public ILoginService loginService() {
        return loginService;
    }

    public static IRouteService getRouteService() {
        return getInstance().routeService();
    }

    public static ILoginService getLoginService() {
        return getInstance().loginService();
    }
}
