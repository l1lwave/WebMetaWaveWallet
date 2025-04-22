package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.model.*;
import web.meta.wave.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class BridgeController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final TransactionService transactionService;

    public static final int MIN_BRIDGE_AMOUNT = 10;

    public BridgeController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService, TransactionService transactionService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
    }

    @GetMapping("/bridgeToken")
    public String bridgeToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("LabelTokenName", "Token");
        model.addAttribute("LabelNetworkFromName", "Network From");
        model.addAttribute("LabelNetworkToName", "Network To");
        model.addAttribute("selectedNetworkFromId", 0);
        model.addAttribute("selectedNetworkToId", 0);
        model.addAttribute("selectedTokenId", 0);
        model.addAttribute("bridgeGas", 0);

        return "bridgeWindow";
    }

    @GetMapping("/bridgeToken/networkFrom={networkFromId}/networkTo={networkToId}/token={tokenId}")
    public String bridgeToken(@PathVariable(value = "networkFromId") long networkFromId,
                              @PathVariable(value = "networkToId") long networkToId,
                              @PathVariable(value = "tokenId") long tokenId,
                              Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkFrom = networkService.findById(networkFromId);
        Optional<Network> networkTo = networkService.findById(networkToId);
        if (networkFrom.isPresent()) {
            List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(networkFrom.get(), customUser);
            List<MetaToken> metaTokens = new ArrayList<>();
            for (Balance balance : balances) {
                metaTokens.add(balance.getMetaToken());
            }
            model.addAttribute("LabelNetworkFromName", networkFrom.get().getName());
            model.addAttribute("tokens", metaTokens);

            Optional<MetaToken> token = tokenService.findByTokenId(tokenId);
            if (token.isPresent()) {
                model.addAttribute("LabelTokenName", token.get().getTokenName());
                model.addAttribute("minValueToken", BigDecimal.valueOf(MIN_BRIDGE_AMOUNT).divide(token.get().getTokenValue(), 6, RoundingMode.HALF_UP));
                model.addAttribute("symbolToken", token.get().getTokenSymbol());

                MetaToken ethToken = tokenService.findByTokenCode(1027L);
                model.addAttribute("ethTokenValue", ethToken.getTokenValue());


                model.addAttribute("tokenValue", token.get().getTokenValue());

                Optional<Balance> balance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom.get(), token.get(), customUser);
                if (balance.isPresent()) model.addAttribute("userTokenBalance", balance.get().getBalance());
            } else model.addAttribute("LabelTokenName", "Token");

        } else {
            model.addAttribute("LabelNetworkFromName", "Network From");
            model.addAttribute("LabelTokenName", "Token");
        }

        if (networkTo.isPresent()) {
            model.addAttribute("LabelNetworkToName", networkTo.get().getName());
        } else {
            model.addAttribute("LabelNetworkToName", "Network To");
        }

        Gas gas = new Gas();
        model.addAttribute("bridgeGas", gas.getBridgeGas());
        model.addAttribute("minBridge", MIN_BRIDGE_AMOUNT);
        model.addAttribute("selectedNetworkFromId", networkFromId);
        model.addAttribute("selectedNetworkToId", networkToId);
        model.addAttribute("selectedTokenId", tokenId);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());

        return "bridgeWindow";
    }

    @GetMapping("/bridge")
    public String swap(@RequestParam String networkFromIdS,
                       @RequestParam String networkToIdS,
                       @RequestParam String tokenIdS,
                       @RequestParam String valueS,
                       Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        CustomUser gasUser = userService.findByEmail("gas.com");

        if (valueS == null || valueS.isEmpty() ||
                networkFromIdS == null || networkFromIdS.isEmpty() ||
                networkToIdS == null || networkToIdS.isEmpty() ||
                tokenIdS == null || tokenIdS.isEmpty() ||
                networkFromIdS.equals(networkToIdS)) return Fail(model, customUser, "You don't have enough parameters");

        long networkFromId = Long.parseLong(networkFromIdS);
        long networkToId = Long.parseLong(networkToIdS);
        long tokenId = Long.parseLong(tokenIdS);
        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> networkFrom = networkService.findById(networkFromId);
        Optional<Network> networkTo = networkService.findById(networkToId);
        if (networkFrom.isPresent() && networkTo.isPresent()) {
            Optional<MetaToken> token = tokenService.findByTokenId(tokenId);
            if (token.isPresent()) {
                BigDecimal minBridgeValue = BigDecimal.valueOf(MIN_BRIDGE_AMOUNT).divide(token.get().getTokenValue(), 6, RoundingMode.HALF_UP);
                if (value.compareTo(minBridgeValue) < 0) {
                    transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.FAILED);
                    return Fail(model, customUser, "Value is less than the minimum bridge");
                }

                MetaToken ethToken = tokenService.findByTokenCode(1027L);
                if (ethToken == null) {
                    tokenService.addToken(1027L);
                    ethToken = tokenService.findByTokenCode(1027L);
                }
                BigDecimal ethValue = ethToken.getTokenValue();

                BigDecimal bridgeGasCoef = gas.getBridgeGas().divide(BigDecimal.valueOf(100));
                BigDecimal tokenFromValue = token.get().getTokenValue();
                BigDecimal tokenCost = value.multiply(tokenFromValue);
                BigDecimal calculatedGas = bridgeGasCoef.multiply(tokenCost);
                BigDecimal bridgeGas = calculatedGas.divide(ethValue, 30, RoundingMode.HALF_UP);

                Optional<Balance> balanceFrom = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom.get(), token.get(), customUser);
                Optional<Balance> balanceTo = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkTo.get(), token.get(), customUser);
                if (balanceFrom.isPresent()) {
                    BigDecimal tempBalanceFrom = balanceFrom.get().getBalance();
                    if (tempBalanceFrom.compareTo(value) < 0){
                        transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds");
                    }
                    Optional<Balance> balanceForGas = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom.get(), ethToken, customUser);
                    if (balanceForGas.isPresent()) {
                        BigDecimal tempBalanceForGas = balanceForGas.get().getBalance();
                        if (tempBalanceForGas.compareTo(bridgeGas) < 0) {
                            transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.FAILED);
                            return Fail(model, customUser, "You don't have enough funds for gas🚀");
                        }
                        balanceService.updateBalance(networkFrom.get(), ethToken, customUser, tempBalanceForGas.subtract(bridgeGas));

                        Optional<Network> gasNetwork = networkService.findById(1L);
                        if (gasNetwork.isPresent()) {
                            Optional<Balance> balanceForGasAccount = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(gasNetwork.get(), ethToken, gasUser);
                            if (balanceForGasAccount.isPresent()) {
                                BigDecimal tempBalanceForGasAccount = balanceForGasAccount.get().getBalance();
                                balanceService.updateBalance(gasNetwork.get(), ethToken, gasUser, tempBalanceForGasAccount.add(bridgeGas));
                            }
                        }
                    } else {
                        transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds for gas🚀");
                    }

                    tempBalanceFrom = balanceFrom.get().getBalance();

                    balanceService.updateBalance(networkFrom.get(), token.get(), customUser, tempBalanceFrom.subtract(value));
                    if (balanceTo.isPresent()) {
                        BigDecimal tempBalanceTo = balanceTo.get().getBalance();
                        balanceService.updateBalance(networkTo.get(), token.get(), customUser, tempBalanceTo.add(value));
                        transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.CORRECT);
                        return Correct(model, customUser);
                    } else {
                        balanceService.addBalance(networkTo.get(), token.get(), customUser, value);
                        transactionService.addTransaction(customUser, customUser, token.get(), token.get(), value, value, Status.CORRECT);
                        return Correct(model, customUser);
                    }
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
        model.addAttribute("LabelTokenName", "Token");
        model.addAttribute("LabelNetworkFromName", "Network From");
        model.addAttribute("LabelNetworkToName", "Network To");
        return "bridgeWindow";
    }

    private String Fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("transactionDone", false);
        model.addAttribute("transactionFail", true);
        model.addAttribute("LabelTokenName", "Token");
        model.addAttribute("LabelNetworkFromName", "Network From");
        model.addAttribute("LabelNetworkToName", "Network To");
        model.addAttribute("failText", failText);
        return "bridgeWindow";
    }
}
