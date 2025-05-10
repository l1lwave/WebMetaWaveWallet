package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.BridgeStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Controller
public class BridgeController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final BridgeService bridgeService;
    private final WalletService walletService;

    private static final BridgeStatements bridgeStatements = new BridgeStatements();

    public BridgeController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService, BridgeService bridgeService, WalletService walletService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.bridgeService = bridgeService;
        this.walletService = walletService;
    }

    @GetMapping("/bridgeToken")
    public String bridgeToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute(bridgeStatements.getNetworks(), networkService.findAll());
        model.addAttribute(bridgeStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(bridgeStatements.getLabelTokenName(), bridgeStatements.getToken());
        model.addAttribute(bridgeStatements.getLabelNetworkFromName(), bridgeStatements.getNetworkFrom());
        model.addAttribute(bridgeStatements.getLabelNetworkToName(), bridgeStatements.getNetworkTo());
        model.addAttribute(bridgeStatements.getSelectedNetworkFromId(), bridgeStatements.getZero());
        model.addAttribute(bridgeStatements.getSelectedNetworkToId(), bridgeStatements.getZero());
        model.addAttribute(bridgeStatements.getSelectedTokenId(), bridgeStatements.getZero());
        model.addAttribute(bridgeStatements.getBridgeGas(), bridgeStatements.getZero());

        return bridgeStatements.getBridgeWindow();
    }

    @GetMapping("/bridgeToken/networkFrom={networkFromId}/networkTo={networkToId}/token={tokenId}")
    public String bridgeToken(@PathVariable(value = "networkFromId") long networkFromId,
                              @PathVariable(value = "networkToId") long networkToId,
                              @PathVariable(value = "tokenId") long tokenId,
                              Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkFrom = networkService.findById(networkFromId);
        Optional<Network> networkTo = networkService.findById(networkToId);
        Optional<MetaToken> token = tokenService.findByTokenId(tokenId);
        Gas gas = new Gas();

        networkFrom.ifPresentOrElse(networkF -> {
            model.addAttribute(bridgeStatements.getLabelNetworkFromName(), networkF.getName());
            model.addAttribute(bridgeStatements.getTokensList(), walletService.getTokensListForNetwork(networkF, customUser));

            token.ifPresentOrElse(t -> {
                model.addAttribute(bridgeStatements.getLabelTokenName(), t.getTokenName());
                model.addAttribute(bridgeStatements.getMinValueToken(), BigDecimal.valueOf(bridgeStatements.getMIN_BRIDGE_AMOUNT()).divide(t.getTokenValue(), 6, RoundingMode.HALF_UP));
                model.addAttribute(bridgeStatements.getSymbolToken(), t.getTokenSymbol());
                model.addAttribute(bridgeStatements.getEthTokenValue(), walletService.getEthToken().getTokenValue());
                model.addAttribute(bridgeStatements.getTokenValue(), t.getTokenValue());

                Optional<Balance> balance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkF, t, customUser);
                balance.ifPresent(b -> model.addAttribute(bridgeStatements.getUserTokenBalance(), b.getBalance()));
            }, () -> model.addAttribute(bridgeStatements.getLabelTokenName(), bridgeStatements.getToken()));
        }, () -> {
            model.addAttribute(bridgeStatements.getLabelNetworkFromName(), bridgeStatements.getNetworkFrom());
            model.addAttribute(bridgeStatements.getLabelTokenName(), bridgeStatements.getToken());
        });

        networkTo.ifPresentOrElse(networkT -> model.addAttribute(bridgeStatements.getLabelNetworkToName(), networkT.getName()),
                () -> model.addAttribute(bridgeStatements.getLabelNetworkToName(), bridgeStatements.getNetworkTo()));

        model.addAttribute(bridgeStatements.getBridgeGas(), gas.getBridgeGas());
        model.addAttribute(bridgeStatements.getMinBridge(), bridgeStatements.getMIN_BRIDGE_AMOUNT());
        model.addAttribute(bridgeStatements.getSelectedNetworkFromId(), networkFromId);
        model.addAttribute(bridgeStatements.getSelectedNetworkToId(), networkToId);
        model.addAttribute(bridgeStatements.getSelectedTokenId(), tokenId);
        model.addAttribute(bridgeStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(bridgeStatements.getNetworks(), networkService.findAll());

        return bridgeStatements.getBridgeWindow();
    }

    @GetMapping("/bridge")
    public String swap(@RequestParam String networkFromIdS,
                       @RequestParam String networkToIdS,
                       @RequestParam String tokenIdS,
                       @RequestParam String valueS,
                       Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        CustomUser gasUser = userService.findByEmail(bridgeStatements.getGAS_EMAIL());

        String status = bridgeService.doBridge(valueS, networkFromIdS, networkToIdS, tokenIdS, customUser, gasUser);

        if(status.equals(bridgeStatements.getNoError())) {
            return Correct(model, customUser);
        } else {
            return Fail(model, customUser, status);
        }
    }

    private String Correct(Model model, CustomUser customUser) {
        model.addAttribute(bridgeStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(bridgeStatements.getNetworks(), networkService.findAll());
        model.addAttribute(bridgeStatements.getTransactionDone(), bridgeStatements.isTrue());
        model.addAttribute(bridgeStatements.getTransactionFail(), bridgeStatements.isFalse());
        model.addAttribute(bridgeStatements.getLabelTokenName(),  bridgeStatements.getToken());
        model.addAttribute(bridgeStatements.getLabelNetworkFromName(), bridgeStatements.getNetworkFrom());
        model.addAttribute(bridgeStatements.getLabelNetworkToName(), bridgeStatements.getNetworkTo());
        return bridgeStatements.getBridgeWindow();
    }

    private String Fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute(bridgeStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(bridgeStatements.getNetworks(), networkService.findAll());
        model.addAttribute(bridgeStatements.getTransactionDone(), bridgeStatements.isFalse());
        model.addAttribute(bridgeStatements.getTransactionFail(), bridgeStatements.isTrue());
        model.addAttribute(bridgeStatements.getLabelTokenName(),  bridgeStatements.getToken());
        model.addAttribute(bridgeStatements.getLabelNetworkFromName(), bridgeStatements.getNetworkFrom());
        model.addAttribute(bridgeStatements.getLabelNetworkToName(), bridgeStatements.getNetworkTo());
        model.addAttribute(bridgeStatements.getFailText(), failText);
        return bridgeStatements.getBridgeWindow();
    }
}
