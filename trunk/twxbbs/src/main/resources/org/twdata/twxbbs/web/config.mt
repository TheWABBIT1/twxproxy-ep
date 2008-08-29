<html>

<head>
<title>TWX BBS Administration</title>
<link href="global.css" rel="stylesheet" type="text/css" />
</head>

<body>

<h3>TWX BBS Adminstration</h3>
    <p>
        Welcome to TWX BBS! The following settings allow you to configure TWX BBS without having to edit the twxbbs.ini file or restart
        the server manually.  After submitting the configuration, the affected services will be restarted automatically.
    </p>
    <p>
        The web client is an experimental web interface to TradeWars that is disabled by default as it is just in the early stages of
        development.
    </p>
    <form method="post">
        <input type="hidden" name="Global.Setup" value="1" />
    <table>
        <!-- $BeginBlock setting -->
        <tr>
            <th>${displayName}</th>
            <td>
                <!-- $BeginBlock fieldErrors -->
                <div class="error">
                    <ul>
                        <!-- $BeginBlock fieldError -->
                        <li>${fieldError}</li>
                        <!-- $EndBlock fieldError -->
                    </ul>
                </div>
                <!-- $EndBlock fieldErrors -->
                <input type="text" name="${key}" value="${value}" size="50"/></td>
        </tr>
         <!-- $EndBlock setting -->
    </table>
    <div><input type="submit" value="Submit" /></div>
    </form>

</body>
</html>
