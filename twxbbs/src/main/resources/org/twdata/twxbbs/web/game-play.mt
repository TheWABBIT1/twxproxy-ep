<html>

<head>
<title>TWX BBS Game ${name}</title>
<link href="/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/jquery-1.2.6.js"></script>

<script type="text/javascript" src="/jclient.js"></script>
</head>

<body>
<div id="game">
<div id="cockpit">
<div id="ceiling">
    <span class="btn">Trade</span>
    <span class="btn">Steal</span>
    <span class="btn">Keep Alive</span>
    <span class="btn">Kill All Scripts</span>
</div>
<!--[if !IE]> -->
<div id="viewport">
    <img src="/tw_port.png" alt="Port" />
</div>
<div id="dashboard">
<div id="panel-status">
    <table>
        <tr>
            <th>Sector</th>
            <td><span id="sector-status">Unknown</span></td>
        </tr>
        <tr>
            <th>Turns</th>
            <td><span id="sector-status">Unknown</span></td>
        </tr>
        <tr>
            <th>Experience</th>
            <td><span id="sector-status">Unknown</span></td>
        </tr>
        <tr>
            <th>Alignment</th>
            <td><span id="sector-status">Unknown</span></td>
        </tr>
        <tr>
            <th>Credits</th>
            <td><span id="sector-status">Unknown</span></td>
        </tr>
    </table>
</div>
<div id="panel-map">
   <img src="/map.png" alt="Map" />
</div>
<div id="telnet">
<object classid="java:de/mud/jta/Applet.class"
              type="application/x-java-applet"
              archive="/jclient-1.0-dev.jar"
              height="360" width="590"
              id="jclient">
  <param name="code" value="de/mud/jta/Applet.class" />
  <!-- For Konqueror -->
  <param name="archive" value="/jclient-1.0-dev.jar" />
  <param name="persistState" value="false" />
  <param name="session" value="${session}" />
  <param name="proxyHost" value="${proxyHost}" />
  <param name="proxyPort" value="${proxyPort}" />
  <center>
    <p><strong>TWX BBS content requires Java 1.5 or higher, which your browser does not appear to have.</strong></p>

    <p><a href="http://www.java.com/en/download/index.jsp">Get the latest Java Plug-in.</a></p>
  </center>
</object>
<!--<![endif]-->
<!--[if IE]>
<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"
                codebase="http://java.sun.com/products/plugin/autodl/jinstall-1_4-windows-i586.cab#Version=1,4,0,0"
                height="350" width="550"
                id="jclient">
  <param name="code" value="de/mud/jta/Applet.class" />
  <param name="archive" value="/jclient-1.0-dev.jar" />
  <param name="persistState" value="false" />
  <param name="cache_option" value="No">
  <center>
    <p><strong>TWX BBS client requires Java 1.5 or higher, which your browser does not appear to have.</strong></p>
    <p><a href="http://www.java.com/en/download/index.jsp">Get the latest Java Plug-in.</a></p>
  </center>
</object>
<![endif]-->
</div>
<div id="panel-chat">
    <form action="foo">
        <div>
            <textarea rows="4" cols="80">Some great chat</textarea>
        </div>
        <div>
            <input type="text" size="60">&nbsp;&nbsp;<input type="submit" value="Submit" />
        </div>
    </form>
</div>
</div>
</div>
</div>
</body>
</html>
