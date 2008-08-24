<html>

<head>
<title>TWX BBS Administration</title>
<link href="global.css" rel="stylesheet" type="text/css"></link>
</head>

<body>

<h3>TWX BBS Adminstration</h3>

    <!-- $BeginBlock errors -->
    <div id="errors">
        There were errors:
        <ul>
        <!-- $BeginBlock error -->
            <li>${msg}</li>
        <!-- $EndBlock error -->
        </ul>
    </div>
    <!-- $EndBlock errors -->
    <form method="post">
        <input type="hidden" name="Global.Setup" value="1" />
    <table>
        <!-- $BeginBlock setting -->
        <tr>
            <th>${displayName}</th>
            <td><input type="text" name="${key}" value="${value}" size="50"/></td>
        </tr>
         <!-- $EndBlock setting -->
    </table>
    <div><input type="submit" value="Submit" /></div>
    </form>

</body>
</html>
