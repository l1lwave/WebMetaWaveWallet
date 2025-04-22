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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-center-area">
            <button class="back-btn">
                <a href="/network/1">
                    <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
                </a>
            </button>

            <button class="back-btn">
                <a href="/notifications">
                    <img class="button-icon" src="/images/icons/tabler_bell-filled.svg" alt="news">
                </a>
            </button>


        </div>
    </div>

    <h1 class="accountText">Account</h1>
    <div class="userNumber">${userNumber}</div>

    <div class="buttons-function">
        <c:if test="${isAdmin}">
            <button class="button-func"  onclick="window.location.href='/profile/allTxns'">
                <img class="button-icon" src="/images/icons/icon-park-outline_transaction-order.svg" alt="txns">
                Txns
            </button>
            <button class="button-func"  onclick="window.location.href='/profile/allUsers/users'">
                <img class="button-icon" src="/images/icons/mdi_users-outline.svg" alt="users">
                Users
            </button>
        </c:if>
        <button class="button-func"  onclick="window.location.href='/logout'">
            <img class="button-icon" src="/images/icons/tabler_logout.svg" alt="logout">
            Logout
        </button>
    </div>

    <div class="main-info-container">
        <div class="paralelButtons">
            <button class="import-token" onclick="window.location.href='/profile/sentTxns'">
                Sent
            </button>
            <button class="import-token"  onclick="window.location.href='/profile/receiedTxns'">
                Received
            </button>
        </div>

        <div class="tokens-list">
            <div class="token">
                <span class="token-name">Token</span>
                <div><span class="token-cost">Amount</span>
                </div>
                <div>
                    <span class="token-cost">Date</span>
                </div>
                <div>
                    <span class="token-cost">Status</span>
                </div>
            </div>

            <c:forEach items="${txns}" var="txn">
                <div class="token">
                    <c:if test="${sendler}">
                        <span class="token-name">${txn.tokenFrom.getTokenSymbol()}</span>
                    </c:if>
                    <c:if test="${receiver}">
                        <span class="token-name">${txn.tokenTo.getTokenSymbol()}</span>
                    </c:if>
                    <c:if test="${sendler}">
                        <div>
                            <span class="token-cost">${symbol}${txn.amountFrom}</span>
                        </div>
                    </c:if>
                    <c:if test="${receiver}">
                        <div>
                            <span class="token-cost">${symbol}${txn.amountTo}</span>
                        </div>
                    </c:if>
                    <div>
                        <span class="token-cost">${txn.date}</span>
                    </div>
                    <div>
                        <span class="token-cost">${txn.status}</span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>

<script>
    $(document).ready(function() {
        $('.userNumber').click(function() {
            const text = $(this).text();
            const tempInput = $('<input>');
            $('body').append(tempInput);
            tempInput.val(text).select();
            document.execCommand('copy');
            tempInput.remove();

            alert("Copied: " + text);
        });
    });
</script>

</html>