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
            <button class="back-btn" onclick="window.location.href='/profile'">
                <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
            </button>

            <h1 class="userNumber">
                News
            </h1>

            <c:if test="${isAdmin}">
                <button class="back-btn" onclick="window.location.href='/addNotification'">
                    <img class="button-icon" src="/images/icons/zondicons_add-outline.svg" alt="add">
                </button>
            </c:if>


        </div>
    </div>


    <div class="main-info-container">
        <c:forEach items="${notifications}" var="notification">
            <button class="news" onclick="window.location.href='/notifications/${notification.id}'">
                <div class="title-new">${notification.title}</div>
                <div class="content-new">${notification.content.substring(0, 25)}...</div>
            </button>
        </c:forEach>
    </div>
</div>
</body>
</html>