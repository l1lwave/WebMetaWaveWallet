package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Network;
import web.meta.wave.service.*;
import web.meta.wave.statements.BuyCryptoStatements;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Controller
public class BuyCryptoController {
    private final NetworkService networkService;
    private final UserService userService;
    private final WalletService walletService;
    private final BuyCryptoService buyCryptoService;
    private final LiqPayService liqPayService;

    private static final BuyCryptoStatements buyCryptoStatements = new BuyCryptoStatements();

    public BuyCryptoController(NetworkService networkService,
                               UserService userService,
                               WalletService walletService,
                               BuyCryptoService buyCryptoService,
                               LiqPayService liqPayService) {
        this.networkService = networkService;
        this.userService = userService;
        this.walletService = walletService;
        this.buyCryptoService = buyCryptoService;
        this.liqPayService = liqPayService;
    }

    @GetMapping("/buyToken")
    public String buyToken(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        model.addAttribute(buyCryptoStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(buyCryptoStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(buyCryptoStatements.getLabelNetworkName(), buyCryptoStatements.getNetworks());
        model.addAttribute(buyCryptoStatements.getLabelTokenName(), buyCryptoStatements.getToken());
        model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), BigDecimal.ZERO);
        model.addAttribute(buyCryptoStatements.getSelectedNetworkId(), buyCryptoStatements.getZero());
        model.addAttribute(buyCryptoStatements.getSelectedTokenId(), buyCryptoStatements.getZero());
        return buyCryptoStatements.getBuyWindow();
    }

    @GetMapping("/buyToken/network={networkId}")
    public String buyTokenByNetwork(@PathVariable("networkId") long networkId, Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkOp = networkService.findById(networkId);

        networkOp.ifPresent(network -> {
            model.addAttribute(buyCryptoStatements.getLabelNetworkName(), network.getName());
            model.addAttribute(buyCryptoStatements.getTokensList(), walletService.getTokensListForNetwork(network, customUser));
        });

        model.addAttribute(buyCryptoStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(buyCryptoStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(buyCryptoStatements.getLabelTokenName(), buyCryptoStatements.getToken());
        model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), BigDecimal.ZERO);
        model.addAttribute(buyCryptoStatements.getSelectedNetworkId(), networkId);
        model.addAttribute(buyCryptoStatements.getSelectedTokenId(), buyCryptoStatements.getZero());
        return buyCryptoStatements.getBuyWindow();
    }

    @GetMapping("/buyToken/network={networkId}/token={tokenId}")
    public String buyTokenByNetworkAndToken(@PathVariable("networkId") long networkId,
                                            @PathVariable("tokenId") long tokenId,
                                            Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        Optional<Network> networkOp = networkService.findById(networkId);

        networkOp.ifPresent(network -> {
            model.addAttribute(buyCryptoStatements.getLabelNetworkName(), network.getName());
            model.addAttribute(buyCryptoStatements.getTokensList(), walletService.getTokensListForNetwork(network, customUser));
        });

        networkOp.flatMap(network -> walletService.getTokensListForNetwork(network, customUser).stream()
                .filter(token -> Objects.equals(token.getId(), tokenId))
                .findFirst()).ifPresentOrElse(token -> {
            model.addAttribute(buyCryptoStatements.getLabelTokenName(), token.getTokenName());
            model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), token.getTokenValue());
        }, () -> {
            model.addAttribute(buyCryptoStatements.getLabelTokenName(), buyCryptoStatements.getToken());
            model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), BigDecimal.ZERO);
        });

        model.addAttribute(buyCryptoStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(buyCryptoStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(buyCryptoStatements.getSelectedNetworkId(), networkId);
        model.addAttribute(buyCryptoStatements.getSelectedTokenId(), tokenId);
        return buyCryptoStatements.getBuyWindow();
    }

    @GetMapping(value = "/buy", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String buy(@RequestParam String amountUsdS,
                      @RequestParam String networkIdS,
                      @RequestParam String tokenIdS) {
        try {
            double amount = Double.parseDouble(amountUsdS);
            if (amount <= 0) {
                return "<html><body><h2>ERROR: AMOUNT MUST BE GREATER THAN 0</h2><a href='/buyToken'>BACK</a></body></html>";
            }

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            return liqPayService.generatePaymentForm(amount, "USD", networkIdS, tokenIdS, baseUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h2>ERROR WAS OCCURRING PAYMENT</h2><a href='/buyToken'>BACK</a></body></html>";
        }
    }

        @GetMapping("/payment-success")
        public String paymentSuccess(@RequestParam String amountUsdS,
                                     @RequestParam String networkIdS,
                                     @RequestParam String tokenIdS,
                                     Model model) {
            CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());

            String buyStatus = buyCryptoService.buyCrypto(amountUsdS, networkIdS, tokenIdS, customUser);

            if (buyCryptoStatements.getNoError().equals(buyStatus)) {
                return correct(model, customUser);
            }

            return fail(model, customUser, buyStatus);
        }


    private String correct(Model model, CustomUser customUser) {
        model.addAttribute(buyCryptoStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(buyCryptoStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(buyCryptoStatements.getTransactionDone(), buyCryptoStatements.isTrueChoose());
        model.addAttribute(buyCryptoStatements.getTransactionFail(), buyCryptoStatements.isFalseChoose());
        model.addAttribute(buyCryptoStatements.getLabelNetworkName(), buyCryptoStatements.getNetworks());
        model.addAttribute(buyCryptoStatements.getLabelTokenName(), buyCryptoStatements.getToken());
        model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), BigDecimal.ZERO);
        return buyCryptoStatements.getBuyWindow();
    }

    private String fail(Model model, CustomUser customUser, String failText) {
        model.addAttribute(buyCryptoStatements.getNetworksList(), networkService.findAll());
        model.addAttribute(buyCryptoStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(buyCryptoStatements.getTransactionDone(), buyCryptoStatements.isFalseChoose());
        model.addAttribute(buyCryptoStatements.getTransactionFail(), buyCryptoStatements.isTrueChoose());
        model.addAttribute(buyCryptoStatements.getLabelNetworkName(), buyCryptoStatements.getNetworks());
        model.addAttribute(buyCryptoStatements.getLabelTokenName(), buyCryptoStatements.getToken());
        model.addAttribute(buyCryptoStatements.getFailText(), failText);
        model.addAttribute(buyCryptoStatements.getTokenPriceUsd(), BigDecimal.ZERO);
        return buyCryptoStatements.getBuyWindow();
    }
}