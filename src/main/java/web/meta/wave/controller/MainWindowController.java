package web.meta.wave.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import web.meta.wave.model.Balance;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Network;
import web.meta.wave.service.BalanceService;
import web.meta.wave.service.NetworkService;
import web.meta.wave.service.UserService;
import web.meta.wave.statements.MainWindowStatements;

import java.util.List;
import java.util.Optional;

@Controller
public class MainWindowController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;

    private static final MainWindowStatements mainWindowStatements = new MainWindowStatements();

    public MainWindowController(NetworkService networkService, UserService userService, BalanceService balanceService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
    }

    @GetMapping("/main")
    public String mainWindow(Model model) {
        List<Network> networks = networkService.findAll();
        if (!networks.isEmpty()) {
            long firstNetworkId = networks.get(0).getId();
            return mainWindowStatements.getRedirect() + firstNetworkId;
        }

        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());
        model.addAttribute(mainWindowStatements.getNetworksList(), networks);
        model.addAttribute(mainWindowStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(mainWindowStatements.getUserBalance(), mainWindowStatements.getZero());

        return mainWindowStatements.getMainWindow();
    }

    @GetMapping("/network/{id}")
    public String listByNetwork(@PathVariable(value = "id") long networkId,
                       Model model) {
        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());
        Optional<Network> networkOp = networkService.findById(networkId);

        networkOp.ifPresent(network -> {
            List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network, customUser);

            model.addAttribute(mainWindowStatements.getNetworksList(), networkService.findAll());
            model.addAttribute(mainWindowStatements.getUserNumber(), customUser.getWalletNumber());
            model.addAttribute(mainWindowStatements.getUserBalance(), balanceService.getTotalBalanceNetwork(network, customUser));
            model.addAttribute(mainWindowStatements.getBalances(), balances);
            model.addAttribute(mainWindowStatements.getCurrentNetworkId(), networkId);
            model.addAttribute(mainWindowStatements.getLabelNetworkName(), network.getName());
        });

        return mainWindowStatements.getMainWindow();
    }

    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        model.addAttribute(mainWindowStatements.getEmail(), userService.getCurrentUser().getUsername());
        return mainWindowStatements.getUnauthorized();
    }
}
