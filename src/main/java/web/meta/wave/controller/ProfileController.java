package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Transaction;
import web.meta.wave.service.*;
import web.meta.wave.statements.ProfileStatements;

import java.util.List;

@Controller
public class ProfileController {
    private final UserService userService;
    private final TransactionService transactionService;

    private static final ProfileStatements profileStatements = new ProfileStatements();

    public ProfileController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (userService.isAdmin(customUser))
            model.addAttribute(profileStatements.getIsAdmin(), profileStatements.isTrue());

        model.addAttribute(profileStatements.getUserNumber(), customUser.getWalletNumber());
        return profileStatements.getProfileWindow();
    }

    @GetMapping("/profile/sentTxns")
    public String profileSentTxns(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (userService.isAdmin(customUser))
            model.addAttribute(profileStatements.getIsAdmin(), profileStatements.isTrue());

        List<Transaction> transactions = transactionService.getTransactionsbyUserFrom(customUser);

        model.addAttribute(profileStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(profileStatements.getSymbol(), profileStatements.getMinus());
        model.addAttribute(profileStatements.getSendler(), profileStatements.isTrue());
        model.addAttribute(profileStatements.getTxns(), transactions);

        return profileStatements.getProfileWindow();
    }

    @GetMapping("/profile/receivedTxns")
    public String profileReceiedTxns(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (userService.isAdmin(customUser))
            model.addAttribute(profileStatements.getIsAdmin(), profileStatements.isTrue());

        List<Transaction> transactions = transactionService.getTransactionsbyUserTo(customUser);

        model.addAttribute(profileStatements.getUserNumber(), customUser.getWalletNumber());
        model.addAttribute(profileStatements.getSymbol(),profileStatements.getPlus());
        model.addAttribute(profileStatements.getReceiver(), profileStatements.isTrue());
        model.addAttribute(profileStatements.getTxns(), transactions);

        return profileStatements.getProfileWindow();
    }
}
