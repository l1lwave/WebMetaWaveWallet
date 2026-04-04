package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BridgeStatements {
    private final String networks = "networks";
    private final String tokensList = "tokens";
    private final int MIN_BRIDGE_AMOUNT = 10;
    private final String userNumber = "userNumber";
    private final String labelTokenName = "LabelTokenName";
    private final String token = "Token";
    private final String labelNetworkFromName = "LabelNetworkFromName";
    private final String labelNetworkToName = "LabelNetworkToName";
    private final String networkFrom = "Network From";
    private final String networkTo = "Network To";
    private final String selectedNetworkFromId = "selectedNetworkFromId";
    private final String selectedNetworkToId = "selectedNetworkToId";
    private final String selectedTokenId = "selectedTokenId";
    private final String bridgeGas = "bridgeGas";
    private final String minValueToken = "minValueToken";
    private final String symbolToken = "symbolToken";
    private final String ethTokenValue = "ethTokenValue";
    private final String tokenValue = "tokenValue";
    private final String userTokenBalance = "userTokenBalance";
    private final String minBridge = "minBridge";
    private final String transactionDone = "transactionDone";
    private final String transactionFail = "transactionFail";
    private final String failText = "failText";
    private final String parametersError = "You don't have enough parameters";
    private final String error = "Parameters error";
    private final String notEnouthGas = "You don't have enough funds for gas🚀";
    private final String notEnouthFundsError = "You don't have enough funds💵❌";
    private final String minBridgeError = "Value is less than the minimum bridge";
    private final String noError = "OK";
    private final String GAS_EMAIL = "gas@a.com";
    private final int zero = 0;
    private final long ETH_CODE = 1027L;
    private final long gasNetwork = 1L;
    private final boolean isTrue = true;
    private final boolean isFalse = false;

    //Windows
    private final String bridgeWindow = "bridgeWindow";


}
