package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.meta.wave.model.*;
import web.meta.wave.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class SwapController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final TransactionService transactionService;


    public SwapController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService, TransactionService transactionService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
    }

    @GetMapping("/swapToken")
    public String swapToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("LabelNetworkName", "Networks");
        model.addAttribute("LabelTokenFromName", "Token From");
        model.addAttribute("LabelTokenToName", "Token To");
        model.addAttribute("coef", 0);
        model.addAttribute("swapGas", 0);


        return "swapWindow";
    }

    @GetMapping("/swapToken/network={networkId}")
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> network = networkService.findById(networkId);
        if (network.isPresent()) {
            List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network.get(), customUser);
            List<MetaToken> metaTokens = new ArrayList<>();
            for (Balance balance : balances) {
                metaTokens.add(balance.getMetaToken());
            }
            model.addAttribute("LabelNetworkName", network.get().getName());
            model.addAttribute("tokens", metaTokens);
        }

        model.addAttribute("selectedNetworkId", networkId);
        model.addAttribute("selectedTokenFromId", 0);
        model.addAttribute("selectedTokenToId", 0);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("LabelTokenFromName", "Token From");
        model.addAttribute("LabelTokenToName", "Token To");
        model.addAttribute("coef", 0);
        model.addAttribute("swapGas", 0);


        return "swapWindow";
    }

    @GetMapping("/swapToken/network={networkId}/tokenFrom={tokenFromId}/tokenTo={tokenToId}" )
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            @PathVariable(value = "tokenFromId") long tokenFromId,
                            @PathVariable(value = "tokenToId") long tokenToId,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> network = networkService.findById(networkId);
        if (network.isPresent()) {
            List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network.get(), customUser);
            List<MetaToken> metaTokens = new ArrayList<>();
            for (Balance balance : balances) {
                metaTokens.add(balance.getMetaToken());
            }
            model.addAttribute("LabelNetworkName", network.get().getName());
            model.addAttribute("tokens", metaTokens);

            Optional<MetaToken> tokenFrom = tokenService.findByTokenId(tokenFromId);
            Optional<MetaToken> tokenTo = tokenService.findByTokenId(tokenToId);
            BigDecimal coef = BigDecimal.ZERO;

            if (tokenFrom.isPresent()) {
                Optional<Balance> balance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), tokenFrom.get(), customUser);
                balance.ifPresent(value -> model.addAttribute("userTokenBalance", value.getBalance()));

                model.addAttribute("LabelTokenFromName", tokenFrom.get().getTokenName());

                Optional<MetaToken> ethToken = Optional.ofNullable(tokenService.findByTokenCode(1027L));
                model.addAttribute("ethTokenValue", ethToken.get().getTokenValue());

                model.addAttribute("tokenFromValue", tokenFrom.get().getTokenValue());
            } else {
                model.addAttribute("LabelTokenFromName", "Token From");
            }

            if (tokenTo.isPresent()) {
                model.addAttribute("LabelTokenToName", tokenTo.get().getTokenName());
            } else {
                model.addAttribute("LabelTokenToName", "Token To");
            }

            if(tokenFrom.isPresent() && tokenTo.isPresent()) {
                coef = tokenFrom.get().getTokenValue().divide(tokenTo.get().getTokenValue(), 10, RoundingMode.HALF_UP);
                model.addAttribute("coef", coef);
            }
        }

        Gas gas = new Gas();
        model.addAttribute("swapGas", gas.getSwapGas());
        model.addAttribute("selectedNetworkId", networkId);
        model.addAttribute("selectedTokenFromId", tokenFromId);
        model.addAttribute("selectedTokenToId", tokenToId);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());

        return "swapWindow";
    }

    @GetMapping("/swap")
    public String swap(@RequestParam String networkIdS,
            @RequestParam String tokenFromIdS,
            @RequestParam String tokenToIdS,
            @RequestParam String valueS,
            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        CustomUser gasUser = userService.findByEmail("gas.com");

        if (valueS == null || valueS.isEmpty() ||
            networkIdS == null || networkIdS.isEmpty() ||
            tokenFromIdS == null || tokenFromIdS.isEmpty() ||
            tokenToIdS == null || tokenToIdS.isEmpty()) return Fail(model, customUser, "You don't have enough parameters");


        long networkId = Long.parseLong(networkIdS);
        long tokenFromId = Long.parseLong(tokenFromIdS);
        long tokenToId = Long.parseLong(tokenToIdS);
        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> network = networkService.findById(networkId);
        if (network.isPresent()) {
            Optional<MetaToken> tokenFrom = tokenService.findByTokenId(tokenFromId);
            Optional<MetaToken> tokenTo = tokenService.findByTokenId(tokenToId);
            if (tokenFrom.isPresent() && tokenTo.isPresent()) {
                if(tokenFrom.get().getTokenCode().equals(tokenTo.get().getTokenCode())) return Fail(model, customUser, "You choose same tokens♊️");

                BigDecimal coef = tokenFrom.get().getTokenValue().divide(tokenTo.get().getTokenValue(), 10, RoundingMode.HALF_UP);

                MetaToken ethToken = tokenService.findByTokenCode(1027L);
                if (ethToken == null) {
                    tokenService.addToken(1027L);
                    ethToken = tokenService.findByTokenCode(1027L);
                }
                BigDecimal ethValue = ethToken.getTokenValue();

                BigDecimal swapGasCoef = gas.getSwapGas().divide(BigDecimal.valueOf(100));
                BigDecimal tokenFromValue = tokenFrom.get().getTokenValue();
                BigDecimal tokenCost = value.multiply(tokenFromValue);
                BigDecimal calculatedGas = swapGasCoef.multiply(tokenCost);
                BigDecimal swapGas = calculatedGas.divide(ethValue, 30, RoundingMode.HALF_UP);


                Optional<Balance> balanceFrom = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), tokenFrom.get(), customUser);
                Optional<Balance> balanceTo = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), tokenTo.get(), customUser);
                if (balanceFrom.isPresent() && balanceTo.isPresent()) {
                    BigDecimal tempBalanceFrom = balanceFrom.get().getBalance();
                    BigDecimal tempBalanceTo = balanceTo.get().getBalance();

                    if(tempBalanceFrom.compareTo(value) < 0) {
                        transactionService.addTransaction(customUser, customUser, tokenFrom.get(), tokenTo.get(), value, value.multiply(coef), Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds💵❌");
                    }

                    Optional<Balance> balanceForGas = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), ethToken, customUser);
                    if (balanceForGas.isPresent()) {
                        BigDecimal tempBalanceForGas = balanceForGas.get().getBalance();
                        if (tempBalanceForGas.compareTo(swapGas) < 0) {
                            transactionService.addTransaction(customUser, customUser, tokenFrom.get(), tokenTo.get(), value, value.multiply(coef), Status.FAILED);
                            return Fail(model, customUser, "You don't have enough funds for gas🚀");
                        }
                        balanceService.updateBalance(network.get(), ethToken, customUser, tempBalanceForGas.subtract(swapGas));

                        Optional<Network> gasNetwork = networkService.findById(1L);
                        if (gasNetwork.isPresent()) {
                            Optional<Balance> balanceForGasAccount = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(gasNetwork.get(), ethToken, gasUser);
                            if (balanceForGasAccount.isPresent()) {
                                BigDecimal tempBalanceForGasAccount = balanceForGasAccount.get().getBalance();
                                balanceService.updateBalance(gasNetwork.get(), ethToken, gasUser, tempBalanceForGasAccount.add(swapGas));
                            }
                        }
                    } else {
                        transactionService.addTransaction(customUser, customUser, tokenFrom.get(), tokenTo.get(), value, value.multiply(coef), Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds for gas🚀");
                    }

                    tempBalanceFrom = balanceFrom.get().getBalance();
                    tempBalanceTo = balanceTo.get().getBalance();

                    balanceService.updateBalance(network.get(), tokenFrom.get(), customUser, tempBalanceFrom.subtract(value));
                    balanceService.updateBalance(network.get(), tokenTo.get(), customUser, tempBalanceTo.add(value.multiply(coef)));

                    transactionService.addTransaction(customUser, customUser, tokenFrom.get(), tokenTo.get(), value, value.multiply(coef), Status.CORRECT);
                    return Correct(model, customUser);

                }

            }
        }
        return Fail(model, customUser, "Parameters error");
    }

    private String Correct(Model model, CustomUser customUser) {
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("transactionDone", true);
        model.addAttribute("transactionFail", false);
        model.addAttribute("LabelTokenFromName", "Token From");
        model.addAttribute("LabelTokenToName", "Token To");
        model.addAttribute("LabelNetworkName", "Network");
        return "swapWindow";
    }

    private String Fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("transactionDone", false);
        model.addAttribute("transactionFail", true);
        model.addAttribute("LabelTokenFromName", "Token From");
        model.addAttribute("LabelTokenToName", "Token To");
        model.addAttribute("LabelNetworkName", "Network");
        model.addAttribute("failText", failText);
        return "swapWindow";
    }
}
