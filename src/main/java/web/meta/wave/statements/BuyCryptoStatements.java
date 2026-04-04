package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BuyCryptoStatements {
    private final String networksList = "networks";
    private final String tokensList = "tokens";
    private final String userNumber = "userNumber";
    private final String labelNetworkName = "LabelNetworkName";
    private final String networks = "Networks";
    private final String labelTokenName = "LabelTokenName";
    private final String token = "Token";
    private final int zero = 0;
    private final String selectedNetworkId = "selectedNetworkId";
    private final String selectedTokenId = "selectedTokenId";
    private final String tokenPriceUsd = "tokenPriceUsd";
    private final String privatSaleRate = "privatSaleRate";
    private final String transactionDone = "transactionDone";
    private final String transactionFail = "transactionFail";
    private final String failText = "failText";
    private final boolean trueChoose = true;
    private final boolean falseChoose = false;
    private final String parametersError = "Not enough parameters for purchase";
    private final String wrongAmountError = "Purchase amount must be greater than zero";
    private final String tokenUnavailableError = "Selected token is unavailable for this network";
    private final String privatBankError = "PrivatBank rate is unavailable right now";
    private final String noError = "OK";
    private final String fiatTokenName = "USD";
    private final String fiatTokenSymbol = "USD";
    private final long fiatTokenCode = -980L;

    private final String buyWindow = "buyWindow";
}
