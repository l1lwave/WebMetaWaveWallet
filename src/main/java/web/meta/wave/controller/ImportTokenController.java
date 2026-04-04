package web.meta.wave.controller;

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
import web.meta.wave.statements.ImportTokenStatements;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class ImportTokenController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;

    private static final ImportTokenStatements importTokenStatements = new ImportTokenStatements();

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
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());

        if (code == null || code.isBlank()) {
            model.addAttribute(importTokenStatements.getCodeErrorExist(), importTokenStatements.isTrue());
            model.addAttribute(importTokenStatements.getUserNumber(), customUser.getWalletNumber());
            model.addAttribute(importTokenStatements.getCurrentNetworkId(), networkId);
            model.addAttribute(importTokenStatements.getIdInput(), code);
            return importTokenStatements.getImportTokenWindow();
        }

        Optional<Network> network = networkService.findById(networkId);
        if (network.isEmpty()) {
            return importTokenStatements.getRedirect() + networkId;
        }

        Long codeToken;

        try {
            codeToken = Long.parseLong(code);
        } catch (NumberFormatException e) {
            model.addAttribute(importTokenStatements.getCodeErrorExist(), importTokenStatements.isTrue());
            model.addAttribute(importTokenStatements.getUserNumber(), customUser.getWalletNumber());
            model.addAttribute(importTokenStatements.getCurrentNetworkId(), networkId);
            model.addAttribute(importTokenStatements.getIdInput(), code);
            return importTokenStatements.getImportTokenWindow();
        }

        if (tokenService.existByTokenCode(codeToken)) {
            MetaToken token = tokenService.findByTokenCode(codeToken);

            if (balanceService.existByNetworkAndMetaTokenAndUser(network.get(), token, customUser)) {
                model.addAttribute(importTokenStatements.getCodeErrorExist(), importTokenStatements.isTrue());
                model.addAttribute(importTokenStatements.getUserNumber(), customUser.getWalletNumber());
                model.addAttribute(importTokenStatements.getCurrentNetworkId(), networkId);
                model.addAttribute(importTokenStatements.getIdInput(), code);
                return importTokenStatements.getImportTokenWindow();
            }
            balanceService.addBalance(network.get(), token, customUser, BigDecimal.ZERO);
        } else {
            if (tokenService.addToken(codeToken)) {
                MetaToken token = tokenService.findByTokenCode(codeToken);
                balanceService.addBalance(network.get(), token, customUser, BigDecimal.ZERO);
            } else {
                model.addAttribute(importTokenStatements.getCodeErrorExist(), importTokenStatements.isTrue());
                model.addAttribute(importTokenStatements.getUserNumber(), customUser.getWalletNumber());
                model.addAttribute(importTokenStatements.getCurrentNetworkId(), networkId);
                model.addAttribute(importTokenStatements.getIdInput(), code);
                return importTokenStatements.getImportTokenWindow();
            }
        }

        return importTokenStatements.getRedirect() + networkId;
    }

    @GetMapping(value = "/{id}/importToken")
    public String importPage(@PathVariable(value = "id") long networkId,
                             Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute(importTokenStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(importTokenStatements.getCurrentNetworkId(), networkId);
        return importTokenStatements.getImportTokenWindow();
    }
}
