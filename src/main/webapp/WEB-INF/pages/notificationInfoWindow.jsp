<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/NewsWindow.css">
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-center-area">
            <button class="back-btn" onclick="window.location.href='/notifications'">
                <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
            </button>


            <h1 class="userNumber">
                News
            </h1>
        </div>
    </div>


    <div class="main-info-container">
        <div class="new-info">
            <div class="tittle-new">${notificationTitle}</div>
            <div>${notificationDate}</div>
            <div class="content-new">${notificationContent}</div>
        </div>
    </div>

    <c:if test="${isAdmin}">
        <button class="button-func" onclick="window.location.href='/deleteNotification=${notificationId}'">
            Delete New
        </button>
    </c:if>
</div>
</body>

</html>