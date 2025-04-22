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

import java.util.List;
import java.util.Optional;

@Controller
public class MainWindowController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;

    public MainWindowController(NetworkService networkService, UserService userService, BalanceService balanceService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
    }

    @GetMapping("/main")
    public String main(Model model) {
        List<Network> networks = networkService.findAll();
        if (!networks.isEmpty()) {
            long firstNetworkId = networks.get(0).getId();
            return "redirect:/network/" + firstNetworkId;
        }

        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());
        model.addAttribute("networks", networks);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("userBalance", 0);

        return "mainWindow";
    }

    @GetMapping("/network/{id}")
    public String listByNetwork(@PathVariable(value = "id") long networkId,
                       Model model) {
        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());
        Optional<Network> network = networkService.findById(networkId);

        if (network.isPresent()) {
            List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network.get(), customUser);

            model.addAttribute("networks", networkService.findAll());
            model.addAttribute("userNumber", customUser.getWalletNumber());
            model.addAttribute("userBalance", balanceService.getTotalBalanceNetwork(network.get(), customUser));
            model.addAttribute("balances", balances);
            model.addAttribute("currentNetworkId", networkId);
            model.addAttribute("LabelNetworkName", network.get().getName());
        }

        return "mainWindow";
    }

    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        model.addAttribute("email", userService.getCurrentUser().getUsername());
        return "unauthorized";
    }
}
