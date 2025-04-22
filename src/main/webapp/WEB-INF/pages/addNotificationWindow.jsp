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
                <img class="button-icon" src="images/icons/fa-solid_arrow-up.svg" alt="back">
            </button>

            <h1 class="userNumber">
                News
            </h1>
        </div>
    </div>

    <form action="/addNewNotification" method="POST">
        <div class="main-info-container">
            <input type="text" class="input-title" name="title" placeholder="Title">
            <textarea type="text" class="input-content" name="content" placeholder="Content"></textarea>
            <button type="submit" class="button-func">
                Add
            </button>
        </div>
    </form>

</div>
</body>

</html>