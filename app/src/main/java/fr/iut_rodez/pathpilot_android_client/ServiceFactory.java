package fr.iut_rodez.pathpilot_android_client;

import fr.iut_rodez.pathpilot_android_client.home.clients.ClientService;
import fr.iut_rodez.pathpilot_android_client.home.clients.IClientService;
import fr.iut_rodez.pathpilot_android_client.home.routes.IRouteService;
import fr.iut_rodez.pathpilot_android_client.home.routes.RouteService;
import fr.iut_rodez.pathpilot_android_client.login.ILoginService;
import fr.iut_rodez.pathpilot_android_client.login.LoginService;
import fr.iut_rodez.pathpilot_android_client.signup.ISignUpService;
import fr.iut_rodez.pathpilot_android_client.signup.SignUpService;

/**
 * Factory to get services
 */
public class ServiceFactory {

    static ServiceFactory instance;
    IRouteService routeService;
    ILoginService loginService;
    ISignUpService signUpService;
    IClientService clientService;
    // TODO add all services

    private ServiceFactory() {
        this.routeService = new RouteService();
        this.loginService = new LoginService();
        this.signUpService = new SignUpService();
        this.clientService = new ClientService();
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

    public ISignUpService signUpService() {
        return signUpService;
    }

    public IClientService clientService() {
        return clientService;
    }

    public static IRouteService getRouteService() {
        return getInstance().routeService();
    }

    public static ILoginService getLoginService() {
        return getInstance().loginService();
    }

    public static ISignUpService getSignUpService() {
        return getInstance().signUpService();
    }

    public static IClientService getClientService() {
        return getInstance().clientService();
    }
}
