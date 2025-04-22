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
public class SendController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final TransactionService transactionService;

    public SendController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService, TransactionService transactionService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
    }

    @GetMapping("/sendToken")
    public String swapToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("LabelNetworkName", "Network");
        model.addAttribute("LabelTokenName", "Token");
        model.addAttribute("selectedNetworkId", 0);
        model.addAttribute("selectedTokenId", 0);
        model.addAttribute("swapGas", 0);

        return "sendWindow";
    }

    @GetMapping("/sendToken/network={networkId}/token={tokenId}")
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            @PathVariable(value = "tokenId") long tokenId,
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

            Optional<MetaToken> token = tokenService.findByTokenId(tokenId);
            if (token.isPresent()) {
                model.addAttribute("LabelTokenName", token.get().getTokenName());

                MetaToken ethToken = tokenService.findByTokenCode(1027L);
                model.addAttribute("ethTokenValue", ethToken.getTokenValue());
                model.addAttribute("tokenValue", token.get().getTokenValue());

                Optional<Balance> balance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), token.get(), customUser);
                if (balance.isPresent()) model.addAttribute("userTokenBalance", balance.get().getBalance());
            } else model.addAttribute("LabelTokenName", "Token");

        } else {
            model.addAttribute("LabelNetworkName", "Network");
            model.addAttribute("LabelTokenName", "Token");
        }

        Gas gas = new Gas();
        model.addAttribute("sendGas", gas.getSendGas());
        model.addAttribute("selectedNetworkId", networkId);
        model.addAttribute("selectedTokenId", tokenId);
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());

        return "sendWindow";
    }


    @GetMapping("/send")
    public String swapToken(@RequestParam String walletNumber,
                            @RequestParam String networkIdS,
                            @RequestParam String tokenIdS,
                            @RequestParam String valueS,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<CustomUser> recipientUser = Optional.ofNullable(userService.findByWalletNumber(walletNumber));
        CustomUser gasUser = userService.findByEmail("gas.com");

        if (recipientUser.isEmpty()) {
            return Fail(model, customUser, "No such wallet found");
        }

        if (valueS == null || valueS.isEmpty() ||
                networkIdS == null || networkIdS.isEmpty() ||
                tokenIdS == null || tokenIdS.isEmpty()) return Fail(model,
                                                                    customUser,
                                                            "You don't have enough parameters");

        long networkId = Long.parseLong(networkIdS);
        long tokenId = Long.parseLong(tokenIdS);
        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> network = networkService.findById(networkId);
        if (network.isPresent()) {
            Optional<MetaToken> token = tokenService.findByTokenId(tokenId);
            if (token.isPresent()) {

                MetaToken ethToken = tokenService.findByTokenCode(1027L);
                if (ethToken == null) {
                    tokenService.addToken(1027L);
                    ethToken = tokenService.findByTokenCode(1027L);
                }
                BigDecimal ethValue = ethToken.getTokenValue();

                BigDecimal sendGasCoef = gas.getSendGas().divide(BigDecimal.valueOf(100));
                BigDecimal tokenFromValue = token.get().getTokenValue();
                BigDecimal tokenCost = value.multiply(tokenFromValue);
                BigDecimal calculatedGas = sendGasCoef.multiply(tokenCost);
                BigDecimal sendGas = calculatedGas.divide(ethValue, 30, RoundingMode.HALF_UP);

                Optional<Balance> senderBalance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), token.get(), customUser);
                Optional<Balance> recipientBalance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), token.get(), recipientUser.get());
                if (senderBalance.isPresent()) {
                    BigDecimal tempSenderBalanceValue = senderBalance.get().getBalance();
                    if (tempSenderBalanceValue.compareTo(value) < 0){
                        transactionService.addTransaction(customUser, recipientUser.get(), token.get(), token.get(), value, value, Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds");
                    }
                    Optional<Balance> balanceForGas = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), ethToken, customUser);
                    if (balanceForGas.isPresent()) {
                        BigDecimal tempBalanceForGas = balanceForGas.get().getBalance();
                        if (tempBalanceForGas.compareTo(sendGas) < 0) {
                            transactionService.addTransaction(customUser, recipientUser.get(), token.get(), token.get(), value, value, Status.FAILED);
                            return Fail(model, customUser, "You don't have enough funds for gas🚀");
                        }
                        balanceService.updateBalance(network.get(), ethToken, customUser, tempBalanceForGas.subtract(sendGas));

                        Optional<Network> gasNetwork = networkService.findById(1L);
                        if (gasNetwork.isPresent()) {
                            Optional<Balance> balanceForGasAccount = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(gasNetwork.get(), ethToken, gasUser);
                            if (balanceForGasAccount.isPresent()) {
                                BigDecimal tempBalanceForGasAccount = balanceForGasAccount.get().getBalance();
                                balanceService.updateBalance(gasNetwork.get(), ethToken, gasUser, tempBalanceForGasAccount.add(sendGas));
                            }
                        }

                    } else {
                        transactionService.addTransaction(customUser, recipientUser.get(), token.get(), token.get(), value, value, Status.FAILED);
                        return Fail(model, customUser, "You don't have enough funds for gas🚀");
                    }
                    tempSenderBalanceValue = senderBalance.get().getBalance();

                    balanceService.updateBalance(network.get(), token.get(), customUser, tempSenderBalanceValue.subtract(value));
                    if (recipientBalance.isPresent()) {
                        BigDecimal tempRecipientBalanceValue = recipientBalance.get().getBalance();
                        balanceService.updateBalance(network.get(), token.get(), recipientUser.get(), tempRecipientBalanceValue.add(value));
                        transactionService.addTransaction(customUser, recipientUser.get(), token.get(), token.get(), value, value, Status.CORRECT);
                        return Correct(model, customUser);
                    } else {
                        balanceService.addBalance(network.get(), token.get(), recipientUser.get(), value);
                        transactionService.addTransaction(customUser, recipientUser.get(), token.get(), token.get(), value, value, Status.CORRECT);
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
        model.addAttribute("LabelNetworkName", "Network");
        return "sendWindow";
    }

    private String Fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("networks", networkService.findAll());
        model.addAttribute("transactionDone", false);
        model.addAttribute("transactionFail", true);
        model.addAttribute("LabelTokenName", "Token");
        model.addAttribute("LabelNetworkName", "Network");
        model.addAttribute("failText", failText);
        return "sendWindow";
    }
}
