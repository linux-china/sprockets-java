# Introduction:

Sprockets-Java is an implementation for Spockets with Java. For more information about Sprockets, please visit http://getsprockets.org/

## Structure

Sprockets-Java resolves js require sentenence (//= require 'model') on first line from js file, and construct a JS depedency tree to track JavaScript file relation.
Sprockets-Java will output combined js content according to js dependency.

## How to Use

### Open your pom.xml and add:

    <dependency>
         <groupId>org.mvnsearch.sprockets</groupId>
         <artifactId>sprockets-client</artifactId>
         <version>1.0.0-SNAPSHOT</version>
    </dependency>

### Open web.xml and add:

      <servlet>
            <servlet-name>Sprockets</servlet-name>
            <servlet-class>org.mvnsearch.sprockets.SprocketsServlet</servlet-class>
            <init-param>
                <param-name>env</param-name>
                <param-value>dev</param-value>
            </init-param>
            <init-param>
                <param-name>repository</param-name>
                <param-value>http://www.mvnsearch.org/sprockets</param-value>
            </init-param>
        </servlet>
        <servlet-mapping>
            <servlet-name>Sprockets</servlet-name>
            <url-pattern>*.js</url-pattern>
        </servlet-mapping>

### Use Sprockets in you html file just like this:

        <script src="/assets/javascripts/controller.js?sprockets&t=20110622.js"></script>
