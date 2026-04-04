package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.model.*;
import web.meta.wave.service.*;
import web.meta.wave.statements.SendStatements;

import java.util.Optional;

@Controller
public class SendController {
    private final NetworkService networkService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TokenService tokenService;
    private final SendService sendService;

    private static final SendStatements sendStatements = new SendStatements();
    private final WalletService walletService;

    public SendController(NetworkService networkService, UserService userService, BalanceService balanceService, TokenService tokenService, SendService sendService, WalletService walletService) {
        this.networkService = networkService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.tokenService = tokenService;
        this.sendService = sendService;
        this.walletService = walletService;
    }

    @GetMapping("/sendToken")
    public String swapToken(@RequestParam(value = "walletinput", required = false) String walletinput,
                            Model model) {
        if (walletinput != null
        && !walletinput.trim().isEmpty()){
            model.addAttribute(sendStatements.getWalletNumberInput(), walletinput);
        }
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute(sendStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(sendStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(sendStatements.getLabelNetworkName(), sendStatements.getNetwork());
        model.addAttribute(sendStatements.getLabelTokenName(), sendStatements.getToken());
        model.addAttribute(sendStatements.getSelectedNetworkId(), sendStatements.getZero());
        model.addAttribute(sendStatements.getSelectedTokenId(), sendStatements.getZero());
        model.addAttribute(sendStatements.getSendGas(), sendStatements.getZero());
        return sendStatements.getSendWindow();
    }

    @GetMapping("/sendToken/network={networkId}/token={tokenId}")
    public String swapToken(@PathVariable(value = "networkId") long networkId,
                            @PathVariable(value = "tokenId") long tokenId,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkOp = networkService.findById(networkId);
        networkOp.ifPresentOrElse(network -> {
            model.addAttribute(sendStatements.getLabelNetworkName(), network.getName());
            model.addAttribute(sendStatements.getTokensList(), walletService.getTokensListForNetwork(network, customUser));

            Optional<MetaToken> tokenOp = tokenService.findByTokenId(tokenId);
            tokenOp.ifPresentOrElse(token -> {
                model.addAttribute(sendStatements.getLabelTokenName(), token.getTokenName());
                MetaToken ethToken = tokenService.findByTokenCode(sendStatements.getETH_CODE());
                model.addAttribute(sendStatements.getEthTokenValue(), ethToken.getTokenValue());
                model.addAttribute(sendStatements.getTokenValue(), token.getTokenValue());

                Optional<Balance> balanceOp = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, customUser);
                balanceOp.ifPresent(balance -> model.addAttribute(sendStatements.getUserTokenBalance(), balance.getBalance()));
            }, () -> model.addAttribute(sendStatements.getLabelTokenName(), sendStatements.getToken()));
        }, () ->{
            model.addAttribute(sendStatements.getLabelNetworkName(), sendStatements.getNetwork());
            model.addAttribute(sendStatements.getLabelTokenName(), sendStatements.getToken());
        });

        Gas gas = new Gas();
        model.addAttribute(sendStatements.getSendGas(), gas.getSendGas());
        model.addAttribute(sendStatements.getSelectedNetworkId(), networkId);
        model.addAttribute(sendStatements.getSelectedTokenId(), tokenId);
        model.addAttribute(sendStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(sendStatements.getNetworksList(), networkService.findAll());

        return sendStatements.getSendWindow();
    }

    @GetMapping("/send")
    public String sendToken(@RequestParam String walletNumber,
                            @RequestParam String networkIdS,
                            @RequestParam String tokenIdS,
                            @RequestParam String valueS,
                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        CustomUser gasUser = userService.findByEmail(sendStatements.getGAS_EMAIL());

        Optional<CustomUser> recipientUser = Optional.ofNullable(userService.findByWalletNumber(walletNumber));

        if (recipientUser.isEmpty()) {
            return Fail(model, walletNumber, customUser, sendStatements.getNoSuchWalletFound());
        }

        String sendStatus = sendService.doSend(valueS, networkIdS, tokenIdS, customUser, recipientUser.get(), gasUser);

        if(sendStatus.equals(sendStatements.getNoError())){
            return Correct(model, walletNumber, customUser);
        }

        return Fail(model, walletNumber, customUser, sendStatus);
    }

    private String Correct(Model model, String walletNumber, CustomUser customUser) {
        model.addAttribute(sendStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(sendStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(sendStatements.getWalletNumberInput(), walletNumber);
        model.addAttribute(sendStatements.getTransactionDone(), sendStatements.isTrueChoose());
        model.addAttribute(sendStatements.getTransactionFail(), sendStatements.isFalseChoose());
        model.addAttribute(sendStatements.getLabelNetworkName(), sendStatements.getNetwork());
        model.addAttribute(sendStatements.getLabelTokenName(), sendStatements.getToken());
        return sendStatements.getSendWindow();
    }

    private String Fail(Model model, String walletNumber, CustomUser customUser, String failText) {
        model.addAttribute(sendStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(sendStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(sendStatements.getWalletNumberInput(), walletNumber);
        model.addAttribute(sendStatements.getTransactionDone(), sendStatements.isFalseChoose());
        model.addAttribute(sendStatements.getTransactionFail(), sendStatements.isTrueChoose());
        model.addAttribute(sendStatements.getLabelNetworkName(), sendStatements.getNetwork());
        model.addAttribute(sendStatements.getLabelTokenName(), sendStatements.getToken());
        model.addAttribute(sendStatements.getFailText(), failText);
        return sendStatements.getSendWindow();
    }
}
