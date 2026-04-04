package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.Balance;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.Network;
import web.meta.wave.model.Status;
import web.meta.wave.statements.BuyCryptoStatements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class BuyCryptoService {
    private final BalanceService balanceService;
    private final NetworkService networkService;
    private final TokenService tokenService;
    private final TransactionService transactionService;

    private static final BuyCryptoStatements buyCryptoStatements = new BuyCryptoStatements();

    public BuyCryptoService(BalanceService balanceService,
                            NetworkService networkService,
                            TokenService tokenService,
                            TransactionService transactionService) {
        this.balanceService = balanceService;
        this.networkService = networkService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
    }

    @Transactional
    public String buyCrypto(String amountUahS,
                            String networkIdS,
                            String tokenIdS,
                            CustomUser customUser) {
        if (amountUahS == null || amountUahS.isBlank() || networkIdS == null || networkIdS.isBlank()
                || tokenIdS == null || tokenIdS.isBlank()) {
            return buyCryptoStatements.getParametersError();
        }

        BigDecimal amountUah;
        long networkId;
        long tokenId;

        try {
            amountUah = new BigDecimal(amountUahS);
            networkId = Long.parseLong(networkIdS);
            tokenId = Long.parseLong(tokenIdS);
        } catch (NumberFormatException exception) {
            return buyCryptoStatements.getParametersError();
        }

        if (amountUah.compareTo(BigDecimal.ZERO) <= 0) {
            return buyCryptoStatements.getWrongAmountError();
        }

        Optional<Network> networkOp = networkService.findById(networkId);
        Optional<MetaToken> tokenOp = tokenService.findByTokenId(tokenId);

        if (networkOp.isEmpty() || tokenOp.isEmpty()) {
            return buyCryptoStatements.getParametersError();
        }

        Network network = networkOp.get();
        MetaToken token = tokenOp.get();
        if (token.getTokenValue() == null || token.getTokenValue().compareTo(BigDecimal.ZERO) <= 0) {
            return buyCryptoStatements.getTokenUnavailableError();
        }

        Optional<Balance> balanceOp = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network, token, customUser);
        if (balanceOp.isEmpty()) {
            return buyCryptoStatements.getTokenUnavailableError();
        }

        try {
            BigDecimal cryptoAmount = calculateCryptoAmount(amountUah, token.getTokenValue());

            Balance balance = balanceOp.get();
            balanceService.updateBalance(network, token, customUser, balance.getBalance().add(cryptoAmount));

            MetaToken fiatToken = tokenService.findOrCreateCustomToken(
                    buyCryptoStatements.getFiatTokenName(),
                    buyCryptoStatements.getFiatTokenSymbol(),
                    buyCryptoStatements.getFiatTokenCode(),
                    BigDecimal.ZERO
            );

            transactionService.addTransaction(customUser, customUser, fiatToken, token, amountUah, cryptoAmount, Status.CORRECT);
            return buyCryptoStatements.getNoError();
        } catch (Exception exception) {
            return buyCryptoStatements.getPrivatBankError();
        }
    }

    public BigDecimal calculateCryptoAmount(BigDecimal usdAmount, BigDecimal tokenPriceUsd) {
        return usdAmount.divide(tokenPriceUsd, 10, RoundingMode.HALF_UP);
    }
}
