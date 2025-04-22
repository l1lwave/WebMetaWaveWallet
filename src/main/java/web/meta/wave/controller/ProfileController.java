package web.meta.wave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Transaction;
import web.meta.wave.model.UserRole;
import web.meta.wave.service.*;

import java.util.List;

@Controller
public class ProfileController {
    private final UserService userService;
    private final TransactionService transactionService;

    public ProfileController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (customUser.getRole().equals(UserRole.ADMIN)) {
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("userNumber", customUser.getWalletNumber());

        return "profileWindow";
    }

    @GetMapping("/profile/sentTxns")
    public String profileSentTxns(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (customUser.getRole().equals(UserRole.ADMIN)) {
            model.addAttribute("isAdmin", true);
        }
        List<Transaction> transactions = transactionService.getTransactionsbyUserFrom(customUser);

        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("symbol", "-");
        model.addAttribute("sendler", true);
        model.addAttribute("txns", transactions);

        return "profileWindow";
    }

    @GetMapping("/profile/receiedTxns")
    public String profileReceiedTxns(Model model) {
        CustomUser customUser = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (customUser.getRole().equals(UserRole.ADMIN)) {
            model.addAttribute("isAdmin", true);
        }

        List<Transaction> transactions = transactionService.getTransactionsbyUserTo(customUser);

        model.addAttribute("userNumber", customUser.getWalletNumber());
        model.addAttribute("symbol", "+");
        model.addAttribute("receiver", true);
        model.addAttribute("txns", transactions);

        return "profileWindow";
    }
}
