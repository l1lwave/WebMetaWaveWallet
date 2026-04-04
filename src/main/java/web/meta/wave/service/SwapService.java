package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.model.*;
import web.meta.wave.statements.SwapStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class SwapService {
    private final BalanceService balanceService;
    private final NetworkService networkService;
    private final TokenService tokenService;
    private final TransactionService transactionService;
    private final WalletService walletService;

    private static final SwapStatements swapStatements = new SwapStatements();

    public SwapService(BalanceService balanceService,
                       NetworkService networkService,
                       TokenService tokenService,
                       TransactionService transactionService,
                       WalletService walletService) {
        this.balanceService = balanceService;
        this.networkService = networkService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    public String doSwap(String valueS,
                         String networkIdS,
                         String tokenFromIdS,
                         String tokenToIdS,
                         CustomUser customUser,
                         CustomUser gasUser) {

        if (walletService.hasInvalidParams(valueS, networkIdS, tokenFromIdS, tokenToIdS)) {
            return swapStatements.getParametersError();
        }

        long networkId = Long.parseLong(networkIdS);
        long tokenFromId = Long.parseLong(tokenFromIdS);
        long tokenToId = Long.parseLong(tokenToIdS);

        if(tokenFromId == tokenToId) {
            return swapStatements.getSameTokensError();
        }

        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> networkOp = networkService.findById(networkId);
        Optional<MetaToken> tokenFromOp = tokenService.findByTokenId(tokenFromId);
        Optional<MetaToken> tokenToOp = tokenService.findByTokenId(tokenToId);

        if (networkOp.isEmpty() || tokenFromOp.isEmpty() || tokenToOp.isEmpty()) {
            return swapStatements.getParametersError();
        }

        MetaToken tokenFrom = tokenFromOp.get();
        MetaToken tokenTo = tokenToOp.get();

        BigDecimal coef = getCoef(tokenFrom, tokenTo);
        MetaToken ethToken = walletService.getEthToken();
        BigDecimal ethValue = ethToken.getTokenValue();
        BigDecimal swapGas = walletService.countGas(gas.getSwapGas(), tokenFrom, value, ethValue);

        Optional<Balance> balanceFromOp = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkOp.get(), tokenFrom, customUser);
        Optional<Balance> balanceToOp = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(networkOp.get(), tokenTo, customUser);

        if (balanceFromOp.isEmpty() || balanceToOp.isEmpty()) {
            return swapStatements.getError();
        }

        Balance balanceFrom = balanceFromOp.get();
        Balance balanceTo = balanceToOp.get();

        BigDecimal tempBalanceFrom = balanceFrom.getBalance();

        if(tempBalanceFrom.compareTo(value) < 0) {
            transactionService.addTransaction(customUser, customUser, tokenFrom, tokenTo, value, value.multiply(coef), Status.FAILED);
            return swapStatements.getNotEnouthFundsError();
        }

        if (walletService.doGas(networkOp.get(), ethToken, customUser, gasUser, swapGas)) {
            transactionService.addTransaction(customUser, customUser, tokenFrom, tokenTo, value, value.multiply(coef), Status.FAILED);
            return swapStatements.getNotEnouthGas();
        }

        tempBalanceFrom = balanceFrom.getBalance();
        BigDecimal tempBalanceTo = balanceTo.getBalance();

        if(tempBalanceFrom.compareTo(value) < 0) {
            transactionService.addTransaction(customUser, customUser, tokenFrom, tokenTo, value, value.multiply(coef), Status.FAILED);
            return swapStatements.getNotEnouthFundsError();
        }

        balanceService.updateBalance(networkOp.get(), tokenFrom, customUser, tempBalanceFrom.subtract(value));
        balanceService.updateBalance(networkOp.get(), tokenTo, customUser, tempBalanceTo.add(value.multiply(coef)));

        transactionService.addTransaction(customUser, customUser, tokenFrom, tokenTo, value, value.multiply(coef), Status.CORRECT);
        return swapStatements.getNoError();
    }

    public BigDecimal getCoef(MetaToken tokenFrom, MetaToken tokenTo) {
        return tokenFrom.getTokenValue().divide(tokenTo.getTokenValue(), 10, RoundingMode.HALF_UP);
    }
}

