package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.model.*;
import web.meta.wave.statements.BridgeStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class BridgeService {
    private final BalanceService balanceService;
    private final NetworkService networkService;
    private final TokenService tokenService;
    private final TransactionService transactionService;
    private final WalletService walletService;

    private static final BridgeStatements bridgeStatements = new BridgeStatements();

    public BridgeService(BalanceService balanceService, NetworkService networkService, TokenService tokenService, TransactionService transactionService, WalletService gasService) {
        this.balanceService = balanceService;
        this.networkService = networkService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
        this.walletService = gasService;
    }

    public String doBridge(String valueS,
                           String networkFromIdS,
                           String networkToIdS,
                           String tokenIdS,
                           CustomUser customUser,
                           CustomUser gasUser) {

        if (walletService.hasInvalidParams(valueS, networkFromIdS, networkToIdS, tokenIdS)) {
            return bridgeStatements.getParametersError();
        }

        long networkFromId = Long.parseLong(networkFromIdS);
        long networkToId = Long.parseLong(networkToIdS);
        long tokenId = Long.parseLong(tokenIdS);
        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> networkFrom = networkService.findById(networkFromId);
        Optional<Network> networkTo = networkService.findById(networkToId);
        Optional<MetaToken> tokenOp = tokenService.findByTokenId(tokenId);


        if (networkFrom.isEmpty() || networkTo.isEmpty() || tokenOp.isEmpty()) {
            return bridgeStatements.getParametersError();
        }

        MetaToken token = tokenOp.get();

        BigDecimal minBridgeValue = BigDecimal.valueOf(bridgeStatements.getMIN_BRIDGE_AMOUNT()).divide(token.getTokenValue(), 6, RoundingMode.HALF_UP);

        if (value.compareTo(minBridgeValue) < 0) {
            transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.FAILED);
            return bridgeStatements.getMinBridgeError();
        }

        MetaToken ethToken = walletService.getEthToken();
        BigDecimal ethValue = ethToken.getTokenValue();

        BigDecimal bridgeGas = walletService.countGas(gas.getBridgeGas(), token, value, ethValue);

        Optional<Balance> balanceFrom = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkFrom.get(), token, customUser);
        Optional<Balance> balanceTo = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkTo.get(), token, customUser);

        if (balanceFrom.isEmpty()) {
            return bridgeStatements.getError();
        }

        BigDecimal tempBalanceFrom = balanceFrom.get().getBalance();

        if (tempBalanceFrom.compareTo(value) < 0) {
            transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.FAILED);
            return bridgeStatements.getNotEnouthFundsError();
        }

        if (walletService.doGas(networkFrom.get(), ethToken, customUser, gasUser, bridgeGas)) {
            transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.FAILED);
            return bridgeStatements.getNotEnouthGas();
        }

        tempBalanceFrom = balanceFrom.get().getBalance();

        if (tempBalanceFrom.compareTo(value) < 0) {
            transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.FAILED);
            return bridgeStatements.getNotEnouthFundsError();
        }

        balanceService.updateBalance(networkFrom.get(), token, customUser, tempBalanceFrom.subtract(value));

        if(balanceTo.isPresent()) {
            BigDecimal tempBalanceTo = balanceTo.get().getBalance();
            balanceService.updateBalance(networkTo.get(), token, customUser, tempBalanceTo.add(value));
            transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.CORRECT);
            return bridgeStatements.getNoError();
        }

        balanceService.addBalance(networkTo.get(), token, customUser, value);
        transactionService.addTransaction(customUser, customUser, token, token, value, value, Status.CORRECT);
        return bridgeStatements.getNoError();
    }
}
