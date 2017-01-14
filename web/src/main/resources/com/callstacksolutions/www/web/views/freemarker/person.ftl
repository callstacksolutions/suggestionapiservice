<#-- @ftlvariable name="" type="com.callstacksolutions.www.web.views.PersonView" -->
<html>
    <head>
      <link rel="stylesheet" href="/assets/pure-min.css">
    </head>
    <body>
        <!-- calls getPerson().getFullName() and sanitizes it -->
        <h1>Hello, ${person.fullName?html}!</h1>
        You are an awesome ${person.jobTitle?html}.
    </body>
</html>