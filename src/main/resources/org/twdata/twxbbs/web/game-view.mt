<html>

<head>
<title>TWX BBS Game ${id}</title>
<link href="/global.css" rel="stylesheet" type="text/css"></link>
</head>

<body>

<div id="errors">${msg}</div>
<h3>TWX BBS Game ${id}</h3>


    <table>
        <tr>
            <th>ID</th>
            <td>${id}</td>
        </tr>
        <tr>
            <th>Name</th>
            <td>${name}</td>
        </tr>
        <tr>
            <th>Sectors</th>
            <td>${sectors}</td>
        </tr>

    </table>
<h4>Login</h4>
<form action="/game/${id}/play" method="post">

    <table>
        <tr>
            <th>Player name</th>
            <td><input type="text" name="username" value="bob"/></td>
        </tr>
        <tr>
            <th>Password</th>
            <td><input type="text" name="password" value="bob"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Submit" />
            </td>
        </tr>
    </table>
</form>

</body>
</html>
