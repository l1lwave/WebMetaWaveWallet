<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/IndexWindow.css">
</head>
<body>
<div class="container">
    <h1 class="LableText">
    Access denied for ${email}!
    </h1>

    <button class="button-func"  onclick="window.location.href='/logout'">
        <img class="button-icon" src="/images/icons/tabler_logout.svg" alt="logout">
        Logout
    </button>
</div>
</body>
</html>