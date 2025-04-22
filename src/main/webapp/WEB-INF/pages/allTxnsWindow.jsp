<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/ProfileWindow.css">
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-center-area">
            <button class="back-btn" onclick="window.location.href='/profile'">
                <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
            </button>

            <h1 class="userNumber">
                All Txns
            </h1>
        </div>
    </div>

    <div class="main-info-container">

        <div class="tokens-list">
            <div class="token">
                <span class="token-name">From</span>
                <span class="token-name">To</span>
                <span class="token-name">AmountFrom</span>
                <span class="token-name">AmountTo</span>
                <span class="token-name">Status/Date</span>
            </div>

            <c:forEach items="${txns}" var="txn">
                <div class="token">
                    <div>
                        <span class="token-name">
                                ${txn.userFrom.getWalletNumber().substring(0, 3)}...${txn.userFrom.getWalletNumber().substring(txn.userFrom.getWalletNumber().length() - 3)}
                        </span>
                        <br>
                        <span class="token-value">${txn.tokenFrom.getTokenSymbol()}</span>
                    </div>
                    <div>
                        <span class="token-name">
                                ${txn.userTo.getWalletNumber().substring(0, 3)}...${txn.userTo.getWalletNumber().substring(txn.userTo.getWalletNumber().length() - 3)}
                        </span>
                        <br>
                        <span class="token-value">${txn.tokenTo.getTokenSymbol()}</span>
                    </div>
                    <span class="token-name">${txn.amountFrom}</span>
                    <div>
                    </div>
                    <span class="token-name">${txn.amountTo}</span>
                    <div>
                        <span class="token-value">${txn.status}</span>
                        <br>
                        <span class="token-name">${txn.date}</span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>

</html>