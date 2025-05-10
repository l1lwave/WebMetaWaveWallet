package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.model.*;
import web.meta.wave.statements.SendStatements;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SendService {
    private final BalanceService balanceService;
    private final NetworkService networkService;
    private final TokenService tokenService;
    private final TransactionService transactionService;
    private final WalletService walletService;

    private static final SendStatements sendStatements = new SendStatements();

    public SendService(BalanceService balanceService, NetworkService networkService, TokenService tokenService, TransactionService transactionService, WalletService gasService) {
        this.balanceService = balanceService;
        this.networkService = networkService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
        this.walletService = gasService;
    }

    public String doSend(String valueS, String networkIdS, String tokenIdS, CustomUser customUser, CustomUser recipientUser, CustomUser gasUser) {
        if (walletService.hasInvalidParams(valueS, networkIdS, tokenIdS)) {
            return sendStatements.getParametersError();
        }

        long networkId = Long.parseLong(networkIdS);
        long tokenId = Long.parseLong(tokenIdS);
        BigDecimal value = new BigDecimal(valueS);
        Gas gas = new Gas();

        Optional<Network> network = networkService.findById(networkId);
        Optional<MetaToken> tokenOp = tokenService.findByTokenId(tokenId);

        if(network.isEmpty() || tokenOp.isEmpty()) {
            return sendStatements.getError();
        }

        MetaToken token = tokenOp.get();

        MetaToken ethToken = walletService.getEthToken();
        BigDecimal ethValue = ethToken.getTokenValue();
        BigDecimal sendGas = walletService.countGas(gas.getSendGas(), token, value, ethValue);

        Optional<Balance> senderBalance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), token, customUser);
        Optional<Balance> recipientBalance = balanceService.getBalanceByNetworkAndMetaTokenAndCustomUser(network.get(), token, recipientUser);

        if (senderBalance.isEmpty()) {
            return sendStatements.getError();
        }

        BigDecimal tempSenderBalanceValue = senderBalance.get().getBalance();

        if (tempSenderBalanceValue.compareTo(value) < 0){
            transactionService.addTransaction(customUser, recipientUser, token, token, value, value, Status.FAILED);
            return sendStatements.getNotEnouthFundsError();
        }

        if (walletService.doGas(network.get(), ethToken, customUser, gasUser, sendGas)) {
            transactionService.addTransaction(customUser, recipientUser,  token, token, value, value, Status.FAILED);
            return sendStatements.getNotEnouthGas();
        }

        tempSenderBalanceValue = senderBalance.get().getBalance();

        if (tempSenderBalanceValue.compareTo(value) < 0){
            transactionService.addTransaction(customUser, recipientUser, token, token, value, value, Status.FAILED);
            return sendStatements.getNotEnouthFundsError();
        }

        balanceService.updateBalance(network.get(), token, customUser, tempSenderBalanceValue.subtract(value));

        if (recipientBalance.isPresent()) {
            BigDecimal tempRecipientBalanceValue = recipientBalance.get().getBalance();
            balanceService.updateBalance(network.get(), token, recipientUser, tempRecipientBalanceValue.add(value));
        } else {
            balanceService.addBalance(network.get(), token, recipientUser, value);
        }
        transactionService.addTransaction(customUser, recipientUser, token, token, value, value, Status.CORRECT);
        return sendStatements.getNoError();
    }
}
