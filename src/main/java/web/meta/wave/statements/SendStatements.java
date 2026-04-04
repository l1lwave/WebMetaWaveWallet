package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SendStatements {
    private final String networksList = "networks";
    private final String tokensList = "tokens";
    private final String userNumber = "userNumber";
    private final String labelNetworkName = "LabelNetworkName";
    private final String network = "Network";
    private final String labelTokenName = "LabelTokenName";
    private final String token = "Token";
    private final String coef = "coef";
    private final String sendGas = "sendGas";
    private final int zero = 0;
    private final String selectedNetworkId = "selectedNetworkId";
    private final String selectedTokenId = "selectedTokenId";
    private final boolean trueChoose = true;
    private final boolean falseChoose = false;
    private final String transactionDone = "transactionDone";
    private final String transactionFail = "transactionFail";
    private final String failText = "failText";
    private final String ethTokenValue = "ethTokenValue";
    private final long ETH_CODE = 1027L;
    private final String tokenValue = "tokenValue";
    private final String userTokenBalance = "userTokenBalance";
    private final String GAS_EMAIL = "gas@a.com";
    private final String parametersError = "You don't have enough parameters";
    private final String sameTokensError = "You choose same tokens";
    private final String noSuchWalletFound = "No such wallet found";
    private final String notEnouthFundsError = "You don't have enough funds💵❌";
    private final String notEnouthGas = "You don't have enough funds for gas🚀";
    private final String error = "Parameters error";
    private final long gasNetwork = 1L;
    private final String noError = "OK";
    private final String walletNumberInput = "walletInput";

    //Windows
    private final String sendWindow = "sendWindow";

}
