package web.meta.wave.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.model.CustomUser;
import web.meta.wave.service.BanUsersService;
import web.meta.wave.service.TransactionService;
import web.meta.wave.statements.AdminStatements;

import java.util.List;

@Controller
public class AdminController {
    private final TransactionService transactionService;
    private final BanUsersService banUsersService;

    private static final AdminStatements adminStatements = new AdminStatements();

    public AdminController(TransactionService transactionService,  BanUsersService banUsersService) {
        this.transactionService = transactionService;
        this.banUsersService = banUsersService;
    }

    @GetMapping("/profile/allTxns")
    @PreAuthorize("hasRole('ADMIN')")
    public String allTxns(Model model) {
        model.addAttribute(adminStatements.getTxns(), transactionService.getAllTransactions());
        return adminStatements.getAllTxnsWindow();
    }

    @GetMapping("/profile/allUsers/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) {
        List<CustomUser> users = banUsersService.getNormalUsers();
        model.addAttribute(adminStatements.getUsers(), users);
        return adminStatements.getAllUsersWindow();
    }

    @GetMapping("/profile/allUsers/banned")
    @PreAuthorize("hasRole('ADMIN')")
    public String bannedUsers(Model model) {
        List<CustomUser> users = banUsersService.getBannedUsers();
        model.addAttribute(adminStatements.getUsers(), users);
        return adminStatements.getAllUsersWindow();
    }

    @GetMapping("/profile/allUsers/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsers(Model model) {
        List<CustomUser> users = banUsersService.getAdminUsers();
        model.addAttribute(adminStatements.getUsers(), users);
        return adminStatements.getAllUsersWindow();
    }

    @PostMapping(value = "/users/banned")
    public ResponseEntity<Void> banUsers(
            @RequestParam(value = "toChangeRole[]", required = false) long[] toChangeRole) {
        if (toChangeRole != null && toChangeRole.length > 0)
            banUsersService.banUsers(toChangeRole);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/users/unbanned")
    public ResponseEntity<Void> unbanUsers(
            @RequestParam(value = "toChangeRole[]", required = false) long[] toChangeRole) {
        if (toChangeRole != null && toChangeRole.length > 0)
            banUsersService.unbanUsers(toChangeRole);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/users/giveAdmin")
    public ResponseEntity<Void> giveAdminUsers(
            @RequestParam(value = "toChangeRole[]", required = false) long[] toChangeRole) {
        if (toChangeRole != null && toChangeRole.length > 0)
            banUsersService.getAdminUsers(toChangeRole);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/users/getAdmin")
    public ResponseEntity<Void> getAdminUsers(
            @RequestParam(value = "toChangeRole[]", required = false) long[] toChangeRole) {
        if (toChangeRole != null && toChangeRole.length > 0)
            banUsersService.unAdminUser(toChangeRole);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
