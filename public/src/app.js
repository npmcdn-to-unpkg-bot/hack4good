var Header = React.createClass({
    render: function () {
        return (
            <nav className="light-blue lighten-1" role="navigation">
                <div className="nav-wrapper container"><a id="logo-container" href="#" className="brand-logo">Logo</a>
                    <ul className="right hide-on-med-and-down">
                        <li><a href="#">Navbar Link</a></li>
                    </ul>

                    <ul id="nav-mobile" className="side-nav">
                        <li><a href="#">Navbar Link</a></li>
                    </ul>
                    <a href="#" data-activates="nav-mobile" className="button-collapse"><i className="material-icons">menu</i></a>
                </div>
            </nav>
        )
    }
});

var LanguageFlag = React.createClass({
    getDefaultProps: function () {
        return {
            baseUrl: 'assets/flags/512/',
            country: 'Germany'
        }
    },

    getFullUrl: function () {
        return this.props.baseUrl + this.props.country + ".png"
    },

    render: function () {
        return <img src={this.getFullUrl()} width="100%"/>;
    }
});

var LanguageTable = React.createClass({
    getDefaultProps: function () {
        return {
            speaking: ['Germany', 'United-kingdom'],
            learning: ['Albania', 'Bosnian', 'Kosovo']
        };
    },

    render: function () {
        return (
            <div>
                <div className="row">
                    <div className="col s6">You speak</div>
                    <div className="col s6">You want to learn</div>
                </div>
                <div className="row">
                    <div className="col s6"><LanguageFlag country="Germany" /></div>
                    <div className="col s6"><LanguageFlag country="Albania" /></div>
                </div>
            </div>);
    }
});

var Content = React.createClass({

    getDefaultProps: function() {
        return {
            roles: {
                helper: 1,
                refugee: 0
            },
            role: null
        }
    },

    getInitialState: function () {
        return {
            role: this.props.role
        }
    },

    printRole: function () {
        if (this.state.role == this.props.roles.helper) {
            return 'You\'re a helper';
        } else if (this.state.role == this.props.roles.refugee) {
            return 'You\'re a refugee';
        } else {
            return "Nothing";
        }
    },

    setHelper: function () {
        console.log("helper");
        this.setState({role: this.props.helper});
    },

    setRefugee: function() {
        console.log("refugee");
        this.setState({role: this.props.refugee});
    },

    setLanguages: function () {

    },

    render: function() {
        return (
            <div>
                <h2>Are you a <a href="#" onClick={this.setHelper}>Helper</a> or <a href="#" onClick={this.setRefugee}> Refugee</a></h2>

                <LanguageTable onClick={this.setLanguages}/>
            </div>
        );
    }
});

var Footer = React.createClass({
    render: function() {
        return (
            <footer className="page-footer orange">
                <div className="container">
                    <div className="row">
                    </div>
                </div>
            </footer>
        );
    }
});

var App = React.createClass({
    render: function () {
        $('.button-collapse').sideNav();
        return (
            <div>
                <Header/>
                <Content/>
                <Footer/>
            </div>)
    }
});

ReactDOM.render(<App/>, document.getElementById('app'));
