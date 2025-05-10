package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.model.*;
import web.meta.wave.statements.BridgeStatements;
import web.meta.wave.statements.SendStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    private final BalanceService balanceService;
    private final NetworkService networkService;
    private final TokenService tokenService;

    private static final SendStatements sendStatements = new SendStatements();
    private static final BridgeStatements bridgeStatements = new BridgeStatements();


    public WalletService(BalanceService balanceService, NetworkService networkService, TokenService tokenService) {
        this.balanceService = balanceService;
        this.networkService = networkService;
        this.tokenService = tokenService;
    }

    public boolean doGas(Network network, MetaToken ethToken, CustomUser customUser, CustomUser gasUser, BigDecimal sendGas) {
        Optional<Balance> balanceForGasOp = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, ethToken, customUser);

        if (balanceForGasOp.isEmpty()) {
            return true;
        }

        Balance balanceForGas = balanceForGasOp.get();
        BigDecimal tempBalanceForGas = balanceForGas.getBalance();

        if (tempBalanceForGas.compareTo(sendGas) < 0) {
            return true;
        }

        Optional<Network> gasNetwork = networkService.findById(sendStatements.getGasNetwork());

        if (gasNetwork.isEmpty()) {
            return true;
        }

        Optional<Balance> balanceForGasAccount = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(gasNetwork.get(), ethToken, gasUser);

        if (balanceForGasAccount.isEmpty()) {
            return true;
        }

        balanceService.updateBalance(network, ethToken, customUser, tempBalanceForGas.subtract(sendGas));

        BigDecimal tempBalanceForGasAccount = balanceForGasAccount.get().getBalance();
        balanceService.updateBalance(gasNetwork.get(), ethToken, gasUser, tempBalanceForGasAccount.add(sendGas));
        return false;
    }

    public List<MetaToken> getTokensListForNetwork(Network network, CustomUser user) {
        List<Balance> balances = balanceService.getBalanceByNetworkAndCustomUser(network, user);

        return  balances.stream()
                .map(Balance::getMetaToken)
                .toList();
    }

    public MetaToken getEthToken(){
        Optional<MetaToken> ethTokenOp = Optional.ofNullable(tokenService.findByTokenCode(bridgeStatements.getETH_CODE()));
        if(ethTokenOp.isPresent()) {
            return ethTokenOp.get();
        }
        tokenService.addToken(bridgeStatements.getETH_CODE());
        return tokenService.findByTokenCode(bridgeStatements.getETH_CODE());
    }

    public boolean hasInvalidParams(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) return true;
        }
        return false;
    }

    public BigDecimal countGas(BigDecimal gas, MetaToken tokenFrom, BigDecimal value, BigDecimal ethValue) {
        BigDecimal GasCoef = gas.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal tokenFromValue = tokenFrom.getTokenValue();
        BigDecimal tokenCost = value.multiply(tokenFromValue);
        BigDecimal calculatedGas = GasCoef.multiply(tokenCost);
        return calculatedGas.divide(ethValue, 30, RoundingMode.HALF_UP);
    }
}
