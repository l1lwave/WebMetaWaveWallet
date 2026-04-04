package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.SwapStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Controller
public class SwapController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final SwapService swapService;
    private final WalletService walletService;

    private static final SwapStatements swapStatements = new SwapStatements();

    public SwapController(NetworkService networkService,
                          UserService userService,
                          BalanceService balanceService,
                          TokenService tokenService,
                          SwapService swapService, WalletService walletService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.swapService = swapService;
        this.walletService = walletService;
    }

    @GetMapping("/swapToken")
    public String swapToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute(swapStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(swapStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(swapStatements.getLabelNetworkName(), swapStatements.getNetworks());
        model.addAttribute(swapStatements.getLabelTokenFromName(), swapStatements.getTokenFrom());
        model.addAttribute(swapStatements.getLabelTokenToName(), swapStatements.getTokenTo());
        model.addAttribute(swapStatements.getCoef(), swapStatements.getZero());
        model.addAttribute(swapStatements.getSwapGas(), swapStatements.getZero());

        return swapStatements.getSwapWindow();
    }

    @GetMapping("/swapToken/network={networkId}")
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkOptional = networkService.findById(networkId);
        networkOptional.ifPresent(network -> {
            model.addAttribute(swapStatements.getLabelNetworkName(), network.getName());
            model.addAttribute(swapStatements.getTokensList(), walletService.getTokensListForNetwork(network, customUser));
        });

        model.addAttribute(swapStatements.getSelectedNetworkId(), networkId);
        model.addAttribute(swapStatements.getSelectedTokenFromId(), swapStatements.getZero());
        model.addAttribute(swapStatements.getSelectedTokenToId(), swapStatements.getZero());
        model.addAttribute(swapStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(swapStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(swapStatements.getLabelTokenFromName(), swapStatements.getTokenFrom());
        model.addAttribute(swapStatements.getLabelTokenToName(), swapStatements.getTokenTo());
        model.addAttribute(swapStatements.getCoef(), swapStatements.getZero());
        model.addAttribute(swapStatements.getSwapGas(), swapStatements.getZero());

        return swapStatements.getSwapWindow();
    }

    @GetMapping("/swapToken/network={networkId}/tokenFrom={tokenFromId}/tokenTo={tokenToId}" )
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            @PathVariable(value = "tokenFromId") long tokenFromId,
                            @PathVariable(value = "tokenToId") long tokenToId,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());

        Optional<Network> networkOp = networkService.findById(networkId);
        networkOp.ifPresent(network -> {
            List<MetaToken> metaTokens = walletService.getTokensListForNetwork(network, customUser);

            model.addAttribute(swapStatements.getLabelNetworkName(), network.getName());
            model.addAttribute(swapStatements.getTokensList(), metaTokens);

            Optional<MetaToken> tokenFrom = tokenService.findByTokenId(tokenFromId);
            Optional<MetaToken> tokenTo = tokenService.findByTokenId(tokenToId);
            BigDecimal coef;

            tokenFrom.ifPresentOrElse(token -> {
                Optional<Balance> balance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, customUser);
                balance.ifPresent(value -> model.addAttribute(swapStatements.getUserTokenBalance(), value.getBalance()));

                model.addAttribute(swapStatements.getLabelTokenFromName(), token.getTokenName());
                model.addAttribute(swapStatements.getEthTokenValue(), walletService.getEthToken().getTokenValue());
                model.addAttribute(swapStatements.getTokenFromValue(), token.getTokenValue());
            }, () -> model.addAttribute(swapStatements.getLabelTokenFromName(), swapStatements.getTokenFrom()));

            tokenTo.ifPresentOrElse(token -> model.addAttribute(swapStatements.getLabelTokenToName(), token.getTokenName()),
                    () -> model.addAttribute(swapStatements.getLabelTokenToName(), swapStatements.getTokenTo()));

            if (tokenFrom.isPresent() && tokenTo.isPresent()) {
                coef = tokenFrom.get().getTokenValue().divide(tokenTo.get().getTokenValue(), 10, RoundingMode.HALF_UP);
                model.addAttribute(swapStatements.getCoef(), coef);
            }
        });

        Gas gas = new Gas();
        model.addAttribute(swapStatements.getSwapGas(), gas.getSwapGas());
        model.addAttribute(swapStatements.getSelectedNetworkId(), networkId);
        model.addAttribute(swapStatements.getSelectedTokenFromId(), tokenFromId);
        model.addAttribute(swapStatements.getSelectedTokenToId(), tokenToId);
        model.addAttribute(swapStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(swapStatements.getNetworksList(), networkService.findAll());

        return swapStatements.getSwapWindow();
    }

    @GetMapping("/swap")
    public String swap(@RequestParam String networkIdS,
            @RequestParam String tokenFromIdS,
            @RequestParam String tokenToIdS,
            @RequestParam String valueS,
            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        CustomUser gasUser = userService.findByEmail(swapStatements.getGAS_EMAIL());

        String swapStatus = swapService.doSwap(valueS, networkIdS, tokenFromIdS, tokenToIdS, customUser, gasUser);

        if(swapStatus.equals(swapStatements.getNoError())){
            return Correct(model, customUser);
        }

        return Fail(model, customUser, swapStatus);
    }

    private String Correct(Model model, CustomUser customUser) {
        model.addAttribute(swapStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(swapStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(swapStatements.getTransactionDone(), swapStatements.isTrueChoose());
        model.addAttribute(swapStatements.getTransactionFail(), swapStatements.isFalseChoose());
        model.addAttribute(swapStatements.getLabelTokenFromName(), swapStatements.getTokenFrom());
        model.addAttribute(swapStatements.getLabelTokenToName(), swapStatements.getTokenTo());
        model.addAttribute(swapStatements.getLabelNetworkName(), swapStatements.getNetworks());
        return swapStatements.getSwapWindow();
    }

    private String Fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute(swapStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(swapStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(swapStatements.getTransactionDone(), swapStatements.isFalseChoose());
        model.addAttribute(swapStatements.getTransactionFail(), swapStatements.isTrueChoose());
        model.addAttribute(swapStatements.getLabelTokenFromName(), swapStatements.getTokenFrom());
        model.addAttribute(swapStatements.getLabelTokenToName(), swapStatements.getTokenTo());
        model.addAttribute(swapStatements.getLabelNetworkName(), swapStatements.getNetworks());
        model.addAttribute(swapStatements.getFailText(), failText);
        return swapStatements.getSwapWindow();
    }
}
