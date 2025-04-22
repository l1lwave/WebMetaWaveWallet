package web.meta.wave.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Network;
import web.meta.wave.service.BalanceService;
import web.meta.wave.service.NetworkService;
import web.meta.wave.service.TokenService;
import web.meta.wave.service.UserService;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class ImportTokenController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;

    public ImportTokenController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/{id}/importToken")
    public String newToken(@PathVariable(value = "id") long networkId,
                           @RequestParam(value = "code", required = false) String code,
                           Model model) {
        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());

        if (code == null || code.isBlank()) {
            model.addAttribute("codeErrorExist", true);
            model.addAttribute("userNumber", customUser.getWalletNumber());
            model.addAttribute("currentNetworkId", networkId);
            return "importTokenWindow";
        }

        Optional<Network> network = networkService.findById(networkId);
        if (network.isPresent()) {
            Long codeToken;

            try {
                codeToken = Long.parseLong(code);
            } catch (NumberFormatException e) {
                model.addAttribute("codeErrorExist", true);
                model.addAttribute("userNumber", customUser.getWalletNumber());
                model.addAttribute("currentNetworkId", networkId);
                return "importTokenWindow";
            }

            if (tokenService.existByTokenCode(codeToken)) {
                MetaToken token = tokenService.findByTokenCode(codeToken);

                if (balanceService.existByNetworkAndMetaTokenAndUser(network.get(), token, customUser)) {
                    model.addAttribute("codeError", true);
                    model.addAttribute("userNumber", customUser.getWalletNumber());
                    model.addAttribute("currentNetworkId", networkId);
                    return "importTokenWindow";
                }

                balanceService.addBalance(network.get(), token, customUser, BigDecimal.ZERO);
            } else {
                if (tokenService.addToken(codeToken)) {
                    MetaToken token = tokenService.findByTokenCode(codeToken);
                    balanceService.addBalance(network.get(), token, customUser, BigDecimal.ZERO);
                } else {
                    model.addAttribute("codeErrorExist", true);
                    model.addAttribute("userNumber", customUser.getWalletNumber());
                    model.addAttribute("currentNetworkId", networkId);
                    return "importTokenWindow";
                }
            }
        }

        return "redirect:/network/" + networkId;
    }

    @GetMapping(value = "/{id}/importToken")
    public String importPage(@PathVariable(value = "id") long networkId,
                             Model model) {
        User user = userService.getCurrentUser();
        CustomUser customUser = userService.findByEmail(user.getUsername());
        model.addAttribute("currentNetworkId", networkId);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        return "importTokenWindow";
    }
}
