(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
var Header = React.createClass({
    displayName: "Header",

    render: function () {
        return React.createElement(
            "nav",
            { "class": "light-blue lighten-1", role: "navigation" },
            React.createElement(
                "div",
                { "class": "nav-wrapper container" },
                React.createElement(
                    "a",
                    { id: "logo-container", href: "#", "class": "brand-logo" },
                    "Logo"
                ),
                React.createElement(
                    "ul",
                    { "class": "right hide-on-med-and-down" },
                    React.createElement(
                        "li",
                        null,
                        React.createElement(
                            "a",
                            { href: "#" },
                            "Navbar Link"
                        )
                    )
                ),
                React.createElement(
                    "ul",
                    { id: "nav-mobile", "class": "side-nav" },
                    React.createElement(
                        "li",
                        null,
                        React.createElement(
                            "a",
                            { href: "#" },
                            "Navbar Link"
                        )
                    )
                ),
                React.createElement(
                    "a",
                    { href: "#", "data-activates": "nav-mobile", "class": "button-collapse" },
                    React.createElement(
                        "i",
                        { "class": "material-icons" },
                        "menu"
                    )
                )
            )
        );
    }
});

var Content = React.createClass({
    displayName: "Content",

    render: function () {
        return React.createElement(
            "div",
            null,
            "#CONETENT"
        );
    }
});

var Footer = React.createClass({
    displayName: "Footer",

    render: function () {
        return React.createElement(
            "div",
            null,
            "#FOOTER"
        );
    }
});

var App = React.createClass({
    displayName: "App",

    render: function () {
        $('.button-collapse').sideNav();
        return React.createElement(
            "div",
            null,
            React.createElement(Header, null),
            React.createElement(Content, null),
            React.createElement(Footer, null)
        );
    }
});

ReactDOM.render(React.createElement(App, null), document.getElementById('app'));

},{}]},{},[1]);
